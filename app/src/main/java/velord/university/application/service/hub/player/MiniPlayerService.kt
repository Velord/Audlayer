package velord.university.application.service.hub.player

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import velord.university.application.AudlayerApp
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.broadcast.restarter.RestarterMiniPlayerGeneralService
import velord.university.application.service.audioFocus.AudioFocusChangeF
import velord.university.application.service.audioFocus.AudioFocusListenerService
import velord.university.application.settings.AppPreference
import velord.university.application.settings.miniPlayer.MiniPlayerServicePreferences
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.music.song.QueueResolver
import velord.university.model.entity.music.playlist.ServicePlaylist
import velord.university.model.converter.SongBitrate
import velord.university.model.converter.SongTimeConverter
import velord.university.model.coroutine.getScope
import velord.university.model.entity.fileType.file.FileFilter
import velord.university.model.entity.fileType.json.general.Loadable
import velord.university.model.entity.music.playlist.Playlist
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.repository.hub.RadioRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import java.io.File

fun File.toAudlayerSong(
    mediaMetadataRetriever: MediaMetadataRetriever
): AudlayerSong = AudlayerSong(
    FileFilter.getArtist(this),
    FileFilter.getTitle(this),
    FileFilter.getDuration(mediaMetadataRetriever, this).toInt()
)

abstract class MiniPlayerService : AudioFocusListenerService() {

    private val currentPlaylist: Loadable<Playlist> = Loadable {
        PlaylistTransaction.getCurrent()
    }

    private lateinit var playlistResolver: ServicePlaylist

    private val metaRetriever = MediaMetadataRetriever()

    private val scope: CoroutineScope = getScope()

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
            try {
                PlaylistTransaction.checkDbTableColumn()
                restoreState()
            }
            catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
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
        playlistResolver.clearQueue()
        val songs = FileFilter.filterOnlyAudio(File(pathFolder)).map {
            it.toAudlayerSong(metaRetriever)
        }
        val info = if (songs.isNotEmpty()) {
            addToQueue(songs)
            stopElseService()
            playNext(playlistResolver.firstInQueue())
            "will play: ${songs.size}\n" +
                    "first: ${songs[0]}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playNextAllInFolder(pathFolder: String) {
        val songs = FileFilter.filterOnlyAudio(File(pathFolder)).map {
            it.toAudlayerSong(metaRetriever)
        }
        addToQueue(songs)
        val info = "added to queue: ${songs.size}"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun shuffleAndPlayAllInFolder(pathFolder: String) {
        playlistResolver.clearQueue()
        val songs = FileFilter.filterOnlyAudio(File(pathFolder)).map {
            it.toAudlayerSong(metaRetriever)
        }
        val info = if (songs.isNotEmpty()) {
            addToQueue(songs)
            playlistResolver.shuffle()
            val firstInQueue = playlistResolver.firstInQueue()
            stopElseService()
            playNext(firstInQueue)
            "will play: ${songs.size}\n" + "first: $firstInQueue"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playByPath(path: String) {
        playlistResolver.clearQueue()
        addToQueue(SongPlaylistInteractor.songs.toList())

        playNext(File(path).toAudlayerSong(metaRetriever))
        //showUI
        stopElseService()
    }

    protected fun getInfoFromServiceToUI() {
        if (playerIsInitialized()) sendInfoToUI()
        else AppBroadcastHub.run {
            doAction(BroadcastActionType.UNAVAILABLE_PLAYER_UI)
        }
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
            if (playlistResolver.currentPlaylist.songList.isNotEmpty()) {
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
            val currentPos = playlistResolver.getSongPos()
            //current position minus 1 else first from end
            val newSong = if (currentPos != 0)
                playlistResolver.getSong(currentPos - 1)
            else
                playlistResolver.lastInQueue()
            //just need got path to new file
            playNext(newSong)
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
        playlistResolver.shuffle()
        MiniPlayerServicePreferences(this).isShuffle =
            QueueResolver.shuffleState
        mayInvoke {
            AppBroadcastHub.run {
                doAction(BroadcastActionType.SHUFFLE_PLAYER_UI)
            }
        }
    }

    protected fun shuffleOff() {
        QueueResolver.shuffleState = false
        playlistResolver.notShuffle()
        MiniPlayerServicePreferences(this).isShuffle =
            QueueResolver.shuffleState
        mayInvoke {
            AppBroadcastHub.run {
                doAction(BroadcastActionType.UN_SHUFFLE_PLAYER_UI)
            }
        }
    }

    protected fun addToQueueOneSong(path: String) {
        val song = File(path).toAudlayerSong(metaRetriever)
        val index = playlistResolver.addToQueue(song)
        val info = "in queue at $index"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun likeSong() {
        scope.launch {
            //TODO()
        }
    }

    protected fun unlikeSong() {
        scope.launch {
            //TODO()
        }
    }

    protected fun loopState() {
        QueueResolver.loopState()
        MiniPlayerServicePreferences(this).loopState = 1
        mayInvoke {
            AppBroadcastHub.apply {
                doAction(BroadcastActionType.LOOP_PLAYER_UI)
            }
        }
    }

    protected fun notLoopState() {
        QueueResolver.notLoopState()
        MiniPlayerServicePreferences(this).loopState = 0
        mayInvoke {
            AppBroadcastHub.apply {
                doAction(BroadcastActionType.LOOP_NOT_PLAYER_UI)
            }
        }
    }

    protected fun loopAllState() {
        QueueResolver.loopAllState()
        MiniPlayerServicePreferences(this).loopState = 2
        mayInvoke {
            AppBroadcastHub.apply {
                doAction(BroadcastActionType.LOOP_ALL_PLAYER_UI)
            }
        }
    }

    private fun playlistIsInitialized(): Boolean =
        playlistResolver.currentPlaylist.songList.isNotEmpty()

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
            AppBroadcastHub.run {
                doAction(BroadcastActionType.PLAY_PLAYER_UI)
            }
        }
    }

    private fun stopElseService() {
        AppBroadcastHub.run {
            doAction(BroadcastActionType.STOP_RADIO_SERVICE)
            doAction(BroadcastActionType.SHOW_PLAYER_UI)
        }
    }

    private suspend fun restoreState() {
        playlistResolver = ServicePlaylist(currentPlaylist.get())

        if (currentPlaylist.getUnsafe().songList.isNotEmpty()) {
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
            applySong()
            //this means ui have been destroyed with service after destroy main activity
            //but app is still working -> after restoration we should play song
            //TODO() does need this lines ?
            if (isPlaying && appWasDestroyed.not()) {
                playSongAfterCreatedPlayer()
            }
        }
    }

    private fun applySong() {
        val duration = MiniPlayerServicePreferences(this).currentDuration
        val path = restoreSongPath()
        if (path != null) return

        playNext(path, true)
        rewindPlayer(duration)
        pausePlayer()
        if (playerIsInitialized())
            player.setVolume(1.0f, 1.0f)
    }

    private fun restoreSongPath(): AudlayerSong? {
        var posWasPlayedSong = MiniPlayerServicePreferences(this).currentPos
        if (posWasPlayedSong == -1) return null

        val lastIndex = currentPlaylist.getUnsafe().songList.lastIndex
        if (posWasPlayedSong > lastIndex) posWasPlayedSong = lastIndex
        return playlistResolver.currentPlaylist.songList[posWasPlayedSong]
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

    private fun addToQueue(list: List<AudlayerSong>) {
        playlistResolver.addToQueue(*list.toTypedArray())
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
        if (::rewindJob.isInitialized)
            stopSendRewind()
        //send command to change ui
        mayInvoke {
            AppBroadcastHub.run {
                doAction(BroadcastActionType.STOP_PLAYER_UI)
            }
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
                QueueResolver.loop -> playNext(playlistResolver.getSong())
                QueueResolver.loopAll -> playNext()
                //reset current song ui
                //not loop
                else -> {
                    playNext(playlistResolver.getSong())
                    pausePlayer()
                }
            }
        }
    }

    private fun playNext(
        song: AudlayerSong? = null,
        silence: Boolean = false) {
        //check if song now is playing
        stopPlayer()
        //start playing
        val song = if (song == null) playlistResolver.getNext()
        else playlistResolver.getSongAndResetQuery(song)
        createPlayer(song)?.let {
            player = it
            //low volume
            if (silence) player.setVolume(0.0f, 0.0f)
            //focus
            setAudioFocusMusicListener()
            //play
            playSongAfterCreatedPlayer()
            //update db
            //todo()
//            scope.launch {
//                PlaylistTransaction.updatePlayedSong(song.path)
//            }
            //send info
            sendInfoToUI(song)
            //store pos
            storeSongPositionInQueue()
        } ?: pathIsWrong(song.path)
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

    private fun createPlayer(song: AudlayerSong): MediaPlayer? {
        Log.d(TAG, song.toString())
        val uri = Uri.fromFile(File(song.path))
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
        if (playlistIsInitialized()) {
            val songInQueue = playlistResolver.getSong()
            val pos = playlistResolver.currentPlaylist
                .songList
                .indexOf(songInQueue)
            MiniPlayerServicePreferences(this).currentPos = pos
            Log.d(TAG, "store pos: $pos")
        }
    }

    private fun storeCurrentDuration(duration: Int = player.currentPosition) {
        //store current duration cause onDestroy invoke only when view is destroy
        MiniPlayerServicePreferences(this).currentDuration = duration
        Log.d(TAG, "store duration: $duration")
    }

    private fun storeQueue() {
        scope.launch {
            //db
            PlaylistTransaction.update(currentPlaylist.getUnsafe())
        }
        Log.d(TAG, "store queue")
    }

    private fun sendInfoToUI(song: AudlayerSong = playlistResolver.getSong()) {
        //send info
        mayInvoke {
            AppBroadcastHub.run {
                doAction(BroadcastActionType.SHOW_PLAYER_UI)
            }
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
                if(player.isPlaying) AppBroadcastHub.run {
                    doAction(BroadcastActionType.PLAY_PLAYER_UI)
                }
                else AppBroadcastHub.run {
                    doAction(BroadcastActionType.STOP_PLAYER_UI)
                }
            }
        }
    }

    private fun sendLoopState() =
        mayInvoke {
            when (MiniPlayerServicePreferences(this).loopState) {
                0 -> AppBroadcastHub.apply {
                    doAction(BroadcastActionType.LOOP_NOT_PLAYER_UI)
                }
                1 -> AppBroadcastHub.run {
                    doAction(BroadcastActionType.LOOP_PLAYER_UI)
                }

                2 -> AppBroadcastHub.run {
                    doAction(BroadcastActionType.LOOP_ALL_PLAYER_UI)
                }
                else -> Unit
            }
        }

    private fun sendShuffleState() {
        if (MiniPlayerServicePreferences(this).isShuffle)
            mayInvoke {
                AppBroadcastHub.run {
                    doAction(BroadcastActionType.SHUFFLE_PLAYER_UI)
                }
            }
        else mayInvoke {
            AppBroadcastHub.run {
                AppBroadcastHub.run {
                    doAction(BroadcastActionType.UN_SHUFFLE_PLAYER_UI)
                }
            }
        }
    }

    private fun sendPath(song: AudlayerSong) =
        AppBroadcastHub.run { playByPathUI(song.path) }

    private fun sendSongDuration(player: MediaPlayer) =
        AppBroadcastHub.run { songDurationUI(player.duration) }

    private fun sendIsHQ(song: AudlayerSong) {
        mayInvoke {
            val bitrate = SongBitrate.getKbps(File(song.path))

            if (bitrate > 190) AppBroadcastHub.run { songHQUI(true) }
            else AppBroadcastHub.run { songHQUI(false) }
        }
    }

    private fun sendSongNameAndArtist(song: AudlayerSong) {
        mayInvoke {
            AppBroadcastHub.run { songArtistUI(song.artist) }
            AppBroadcastHub.run { songTitleUI(song.title) }
        }
    }

    private suspend fun sendIsLoved(song: AudlayerSong) {
        //todo()
    }

    private fun sendPathIsWrong(path: String) =
        AppBroadcastHub.run { pathIsWrongUI(path) }
    //when mini player is radio no need send info
    private fun mayInvoke(f: () -> Unit) =
        MiniPlayerRepository.mayDoAction(
            this, MiniPlayerLayoutState.DEFAULT, f)
}
