package velord.university.application.service

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.restarter.RestarterMiniPlayerGeneralService
import velord.university.application.service.audioFocus.AudioFocusChangeF
import velord.university.application.service.audioFocus.AudioFocusListenerService
import velord.university.application.settings.AppPreference
import velord.university.application.settings.miniPlayer.MiniPlayerServicePreferences
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.QueueResolver
import velord.university.model.ServicePlaylist
import velord.university.model.converter.SongBitrate
import velord.university.model.converter.SongTimeConverter
import velord.university.model.entity.MiniPlayerServiceSong
import velord.university.model.entity.Song
import velord.university.model.file.FileFilter
import velord.university.model.file.FileNameParser
import velord.university.repository.MiniPlayerRepository
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.repository.transaction.ServiceTransaction
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import java.io.File

abstract class MiniPlayerService : AudioFocusListenerService() {

    private val playlist = ServicePlaylist()

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var rewindJob: Job

    override val onAudioFocusChange: AudioFocusChangeF =
        AudioFocusChangeF(
            {
                if (player.isPlaying) {
                    pausePlayer()
                    //rearward playing state
                    storeIsPlayingStateTrue()
                }

                unregisterMediaButtonEventReceiver()
            },
            {
                if (player.isPlaying) {
                    pausePlayer()
                    //rearward playing state
                    storeIsPlayingStateTrue()
                }
            },
            {
                player.setVolume(0.5f, 0.5f)
            },
            {
                if (MiniPlayerServicePreferences(this).isPlaying)
                    playSongAfterCreatedPlayer()

                player.setVolume(1.0f, 1.0f)
            }
        )

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()

        scope.launch {
            PlaylistTransaction.checkDbTableColumn()
            restoreState()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        whenServiceKilled()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()

        whenServiceKilled()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind called")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?,
                                flags: Int,
                                startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    protected fun playOrStopService() {
        if (playerIsInitialized()) {
            if (player.isPlaying) pausePlayer()
            else playSongAfterCreatedPlayer()
        }
    }

    protected fun playAllInFolder(pathFolder: String) {
        playlist.clearQueue()
        val songs = FileFilter.filterOnlyAudio(File(pathFolder))
        val info = if (songs.isNotEmpty()) {
            addToQueue(songs)
            stopElseService()
            playNext(playlist.firstInQueue().path)
            "will play: ${songs.size}\n" +
                    "first: ${songs[0].name}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playNextAllInFolder(pathFolder: String) {
        val songs = FileFilter.filterOnlyAudio(File(pathFolder))
        addToQueue(songs)
        val info = "added to queue: ${songs.size}"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun shuffleAndPlayAllInFolder(pathFolder: String) {
        playlist.clearQueue()
        val songs = FileFilter.filterOnlyAudio(File(pathFolder))
        val info = if (songs.isNotEmpty()) {
            addToQueue(songs)
            playlist.shuffle()
            val firstInQueue = playlist.firstInQueue()
            stopElseService()
            playNext(firstInQueue.path)
            "will play: ${songs.size}\n" + "first: ${firstInQueue.name}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playByPath(path: String) {
        playlist.clearQueue()
        addToQueue(SongPlaylistInteractor.songs.map { it.file }.toList())
        playNext(path)
        //showUI
        stopElseService()
    }

    protected fun getInfoFromServiceToUI() {
        if (playerIsInitialized()) sendInfoToUI()
        else AppBroadcastHub.run { playerUnavailableUI() }
    }

    protected fun pausePlayer() = stopOrPausePlayer {
        player.pause()
    }

    protected fun playSongAfterCreatedPlayer() {
        if (playerIsInitialized()) {
            if (player.currentPosition != player.duration) {
                player.start()
                val seconds = SongTimeConverter
                    .millisecondsToSeconds(player.currentPosition)
                startSendRewind(seconds)
            }
            //store player state
            storeIsPlayingState()
        } else {
            if (playlist.notShuffled.isNotEmpty()) {
                playNext()
            }
        }
        sendInfoWhenPlay()
    }

    protected fun skipSongAndPlayNext() {
        if (playerIsInitialized()) {
            playNext()
        }
    }

    protected fun skipSongAndPlayPrevious() {
        if (playerIsInitialized()) {
            val currentPos = playlist.getSongPos()
            //current position minus 1 else first from end
            val newSong = if (currentPos != 0)
                playlist.getSong(currentPos - 1)
            else
                playlist.lastInQueue()
            //just need got path to new file
            playNext(newSong.path)
        }
    }

    protected fun rewindPlayer(milliseconds: Int) {
        val seconds = SongTimeConverter.millisecondsToSeconds(milliseconds)
        if (playerIsInitialized()) {
            player.seekTo(milliseconds)
            //to apply current duration
            if (player.isPlaying) {
                pausePlayer()
                playSongAfterCreatedPlayer()
            }
            storeCurrentDuration(milliseconds)
        }
        mayInvoke {
            AppBroadcastHub.run { rewindUI(seconds) }
        }
    }

    protected fun shuffleOn() {
        QueueResolver.shuffleState = true
        //store before shuffle
        playlist.shuffle()
        MiniPlayerServicePreferences(this).isShuffle =
            QueueResolver.shuffleState
        mayInvoke {
            AppBroadcastHub.run { shuffleUI() }
        }
    }

    protected fun shuffleOff() {
        QueueResolver.shuffleState = false
        playlist.notShuffle()
        MiniPlayerServicePreferences(this).isShuffle =
            QueueResolver.shuffleState
        mayInvoke {
            AppBroadcastHub.run { unShuffleUI() }
        }
    }

    protected fun addToQueueOneSong(path: String) {
        val song = File(path)
        val index = playlist.addToQueue(song)
        val info = "in queue at $index"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun likeSong() {
        scope.launch {
            val songPath = playlist.getSongPath()
            PlaylistTransaction.updateFavourite {
                val updated = it + songPath
                updated
            }
        }
    }

    protected fun unlikeSong() {
        scope.launch {
            val songPath = playlist.getSongPath()
            PlaylistTransaction.updateFavourite { it ->
                it.filter { it != songPath }
            }
        }
    }

    protected fun loopState() {
        QueueResolver.loopState()
        MiniPlayerServicePreferences(this).loopState = 1
        mayInvoke {
            AppBroadcastHub.apply { this@MiniPlayerService.loopUI() }
        }
    }

    protected fun notLoopState() {
        QueueResolver.notLoopState()
        MiniPlayerServicePreferences(this).loopState = 0
        mayInvoke {
            AppBroadcastHub.apply { this@MiniPlayerService.notLoopUI() }
        }
    }

    protected fun loopAllState() {
        QueueResolver.loopAllState()
        MiniPlayerServicePreferences(this).loopState = 2
        mayInvoke {
            AppBroadcastHub.apply { this@MiniPlayerService.loopAllUI() }
        }
    }

    private fun playlistIsInitialized(): Boolean =
        playlist.notShuffled.isNotEmpty()

    private fun whenServiceKilled() {
        //store player state
        storeSongPositionInQueue()
        storeQueue()
        storeIsPlayingState()
        stopPlayer()
        restartService()
    }

    private fun restartService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "RestartMiniPlayerGeneralService"
        broadcastIntent.setClass(this, RestarterMiniPlayerGeneralService::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private fun sendInfoWhenPlay() {
        //send command to change ui
        mayInvoke {
            AppBroadcastHub.run { playUI() }
        }
    }

    private fun stopElseService() {
        AppBroadcastHub.run { stopRadioService() }
        sendShowGeneralUI()
    }

    private suspend fun restoreState() {
        val songsFromDb = ServiceTransaction.getPlaylist()
        val songsToPlaylist = MiniPlayerServiceSong.getSongsToPlaylist(songsFromDb)

        if (songsToPlaylist.isNotEmpty()) {
            applyInteractor(songsToPlaylist)
            playlist.addToQueue(*songsToPlaylist.toTypedArray())
            //restore isPlaying state
            val isPlaying = MiniPlayerServicePreferences(this).isPlaying
            val appWasDestroyed = AppPreference(this).appIsDestroyed
            //apply shuffle state player
            applyShuffleState()
            //apply loop state player
            applyLoopState()
            //restore duration
            //what song should play
            //after all info got -> start player ->  set current time -> stop
            applySong(songsToPlaylist)
            //this means ui have been destroyed with service after destroy main activity
            //but app is still working -> after restoration we should play song
            //TODO() does need this lines ?
            if (isPlaying && appWasDestroyed.not()) {
                playSongAfterCreatedPlayer()
            }
        }
    }

    private fun applyInteractor(songsToPlaylist: List<File>) {
        SongPlaylistInteractor.songs = songsToPlaylist
            .map { Song(it) }
            .toTypedArray()
    }

    private fun applySong(songsToPlaylist: List<File>) {
        val duration = MiniPlayerServicePreferences(this).currentDuration
        val path = restoreSongPath(songsToPlaylist)
        playNext(path, true)
        rewindPlayer(duration)
        pausePlayer()
        player.setVolume(1.0f, 1.0f)
    }

    private fun restoreSongPath(songsToPlaylist: List<File>): String {
        var posWasPlayedSong = MiniPlayerServicePreferences(this).currentPos
        if (posWasPlayedSong > songsToPlaylist.lastIndex)
            posWasPlayedSong = songsToPlaylist.lastIndex
        return playlist.notShuffled[posWasPlayedSong].path
    }

    private fun applyShuffleState(
        state: Boolean = MiniPlayerServicePreferences(this).isShuffle) {
        if (state) shuffleOn()
        else shuffleOff()
    }

    private fun applyLoopState(
        state: Int = MiniPlayerServicePreferences(this).loopState) {
        when(state) {
            0 -> notLoopState()
            1 -> loopState()
            2 -> loopAllState()
        }
    }

    private fun addToQueue(list: List<File>) {
        playlist.addToQueue(*list.toTypedArray())
        //restore shuffle state
        if (QueueResolver.shuffleState) shuffleOn()
        storeQueue()
    }

    private fun stopOrPausePlayer(f: () -> Unit) {
        if (playerIsInitialized()) {
            f()
            storeIsPlayingState()
            stopSendRewind()
        }
        //send command to change ui
        mayInvoke {
            AppBroadcastHub.apply { stopUI() }
        }
    }

    private fun stopPlayer() {
        stopOrPausePlayer {
            player.pause()
        }
    }

    private fun songIsOver() {
        if (playerIsInitialized()) {
            when {
                QueueResolver.loop -> playNext(playlist.getSongPath())
                QueueResolver.loopAll -> playNext()
                //reset current song ui
                //not loop
                else -> {
                    playNext(playlist.getSongPath())
                    pausePlayer()
                }
            }
        }
    }

    private fun playNext(
        path: String? = null,
        silence: Boolean = false) {
        //check if song now is playing
        stopPlayer()
        //start playing
        val song = if (path == null) playlist.getNext()
        else playlist.getSongAndResetQuery(path)
        createPlayer(song.absoluteFile)?.let {
            player = it
            //low volume
            if (silence) player.setVolume(0.0f, 0.0f)
            //focus
            setAudioFocusMusicListener()
            //play
            playSongAfterCreatedPlayer()
            //update db
            scope.launch {
                PlaylistTransaction.updatePlayedSong(song.path)
            }
            //send info
            sendInfoToUI(song)
            //store pos
            storeSongPositionInQueue()
        } ?: pathIsWrong(path ?: "")
    }

    private fun pathIsWrong(path: String) {
        Log.d(TAG, "Path: $path is incorrect")
        sendPathIsWrong(path)
        scope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@MiniPlayerService,
                    "Path: $path is unavailable",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createPlayer(file: File): MediaPlayer? {
        Log.d(TAG, file.path)
        val uri = Uri.fromFile(file)
        return MediaPlayer.create(baseContext, uri)
    }

    private fun startSendRewind(startFrom: Int = 0) {
        if (playerIsInitialized()) {
            player.setOnCompletionListener { songIsOver() }

            var rewindValue = startFrom
            rewindJob = scope.launch {
                while (isActive) {
                    mayInvoke {
                        AppBroadcastHub.run { rewindUI(rewindValue++) }
                    }
                    //store current duration cause onDestroy invoke only when view is destroy
                    storeCurrentDuration()
                    delay(1000)
                }
            }
        }
    }

    private fun stopSendRewind() {
        rewindJob.cancel()
    }

    private fun storeIsPlayingState() {
        if (playerIsInitialized()) {
            if (player.isPlaying) storeIsPlayingStateTrue()
            else storeIsPlayingStateFalse()
        }
    }

    private fun storeIsPlayingStateTrue() {
        MiniPlayerServicePreferences(this).isPlaying = true
    }

    private fun storeIsPlayingStateFalse() {
        MiniPlayerServicePreferences(this).isPlaying = false
    }

    private fun storeSongPositionInQueue() {
        val posInQueue = playlist.getSong()
        val pos = playlist.notShuffled.indexOf(posInQueue)
        MiniPlayerServicePreferences(this).currentPos = pos
        Log.d(TAG, "store pos: $pos")
    }

    private fun storeCurrentDuration(duration: Int = player.currentPosition) {
        //store current duration cause onDestroy invoke only when view is destroy
        MiniPlayerServicePreferences(this).currentDuration = duration
        Log.d(TAG, "store duration: $duration")
    }

    private fun storeQueue() {
        scope.launch {
            val songsToDb =
                MiniPlayerServiceSong.getSongsToDb(playlist.notShuffled)
            ServiceTransaction.clearAndInsert(songsToDb)
        }
        Log.d(TAG, "store queue")
    }

    private fun sendInfoToUI(song: File = playlist.getSong()) {
        //send info
        mayInvoke {
            sendShowGeneralUI()
            sendIsPlayed()
            sendLoopState()
            sendShuffleState()
            sendPath(song)
            sendSongNameAndArtist(song)
            sendSongDuration(player)
            sendIsHQ(song)
            sendCurrentDuration()
            scope.launch {
                sendIsLoved(song)
            }
        }
    }

    private fun sendCurrentDuration() {
        val duration = 
            MiniPlayerServicePreferences(this).currentDuration
        val seconds = SongTimeConverter.millisecondsToSeconds(duration)
        AppBroadcastHub.run { rewindUI(seconds) }
    }

    private fun sendIsPlayed() {
        mayInvoke {
            if (playerIsInitialized()) {
                if(player.isPlaying)
                    AppBroadcastHub.apply { playUI() }
                else
                    AppBroadcastHub.run { stopUI() }
            }
        }
    }

    private fun sendShowGeneralUI() {
        AppBroadcastHub.run { showGeneralUI() }
    }

    private fun sendLoopState() =
        when(MiniPlayerServicePreferences(this).loopState) {
            0 -> mayInvoke { AppBroadcastHub.run { notLoopUI() } }
            1 -> mayInvoke { AppBroadcastHub.run { loopUI() } }
            2 -> mayInvoke { AppBroadcastHub.run { loopAllUI() } }
            else -> Unit
        }

    private fun sendShuffleState() {
        if (MiniPlayerServicePreferences(this).isShuffle)
            mayInvoke { AppBroadcastHub.run { shuffleUI() } }
        else mayInvoke { AppBroadcastHub.run { unShuffleUI() } }
    }

    private fun sendPath(song: File) =
        AppBroadcastHub.run { songPathUI(song.absolutePath) }

    private fun sendSongDuration(player: MediaPlayer) =
        AppBroadcastHub.run { songDurationUI(player.duration) }

    private fun sendIsHQ(song: File) {
        mayInvoke {
            val bitrate = SongBitrate.getKbps(song)

            if (bitrate > 190) AppBroadcastHub.run { songHQUI(true) }
            else AppBroadcastHub.run { songHQUI(false) }
        }
    }

    private fun sendSongNameAndArtist(file: File) {
        val songArtist = FileNameParser.getSongArtist(file)
        val songName = FileNameParser.getSongTitle(file)
        mayInvoke {
            AppBroadcastHub.run { songArtistUI(songArtist) }
            AppBroadcastHub.run { songNameUI(songName) }
        }
    }

    private suspend fun sendIsLoved(song: File) = withContext(Dispatchers.IO) {
        val favourite = PlaylistTransaction.getFavouriteSongs()
        if (favourite.contains(song.path))
            mayInvoke { AppBroadcastHub.run { likeUI() } }
        else
            mayInvoke { AppBroadcastHub.run { unlikeUI() } }
    }

    private fun sendPathIsWrong(path: String) =
        AppBroadcastHub.run { pathIsWrongUI(path) }
    //when mini player is radio no need send info
    private fun mayInvoke(f: () -> Unit) =
        MiniPlayerRepository.mayDoAction(
            this, MiniPlayerLayoutState.GENERAL, f)
}
