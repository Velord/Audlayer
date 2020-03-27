package velord.university.application.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import velord.university.application.AudlayerApp
import velord.university.application.broadcast.*
import velord.university.application.broadcast.MiniPlayerBroadcastLoop.sendBroadcastLoopUI
import velord.university.application.broadcast.MiniPlayerBroadcastLoopAll.sendBroadcastLoopAllUI
import velord.university.application.broadcast.MiniPlayerBroadcastNotLoop.sendBroadcastNotLoopUI
import velord.university.application.broadcast.MiniPlayerBroadcastPlay.sendBroadcastPlayUI
import velord.university.application.broadcast.MiniPlayerBroadcastRewind.sendBroadcastRewindUI
import velord.university.application.broadcast.MiniPlayerBroadcastShuffle.sendBroadcastShuffleUI
import velord.university.application.broadcast.MiniPlayerBroadcastSongArtist.sendBroadcastSongArtistUI
import velord.university.application.broadcast.MiniPlayerBroadcastSongName.sendBroadcastSongNameUI
import velord.university.application.broadcast.MiniPlayerBroadcastStop.sendBroadcastStopUI
import velord.university.application.broadcast.MiniPlayerBroadcastUnShuffle.sendBroadcastUnShuffleUI
import velord.university.application.settings.AppPreference
import velord.university.application.settings.MiniPlayerServicePreferences
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.model.QueueResolver
import velord.university.model.ServicePlaylist
import velord.university.model.converter.SongTimeConverter
import velord.university.model.entity.MiniPlayerServiceSong
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.repository.transaction.ServiceTransaction
import java.io.File

abstract class MiniPlayerService : Service() {

    abstract val TAG: String

    private lateinit var player: MediaPlayer

    private val playlist = ServicePlaylist()

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)
    private lateinit var rewindJob: Job

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()
        scope.launch {
            AudlayerApp.checkDbTableColumn()
            restoreState()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        //store player state
        storeIsPlayingState()
        stopPlayer()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind called")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        return START_STICKY
    }

    protected fun playAllInFolder(pathFolder: String) {
        playlist.clearQueue()
        val songs = FileFilter.filterOnlyAudio(File(pathFolder))
        val info = if (songs.isNotEmpty()) {
            addToQueue(songs)
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
            playNext(firstInQueue.path)
            "will play: ${songs.size}\n" + "first: ${firstInQueue.name}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playByPath(path: String) {
        playlist.clearQueue()
        addToQueue(SongPlaylistInteractor.songs.toList())
        playNext(path)
        //showUI
        MiniPlayerBroadcastShow.apply { sendBroadcastShow() }
    }

    protected fun getInfoFromServiceToUI() {
        if (::player.isInitialized) {
            val restoreStatePlayer: () -> Unit =
                if (player.isPlaying) { {} }
                else { { pausePlayer() } }
            pausePlayer()
            sendInfoToUI()
            playSongAfterCreatedPlayer()
            restoreStatePlayer()
        }
    }

    protected fun pausePlayer() = stopOrPausePlayer {
            player.pause()
        }

    protected fun playSongAfterCreatedPlayer() {
        if (::player.isInitialized) {
            if (player.currentPosition != player.duration) {
                player.start()
                val seconds =
                    SongTimeConverter.millisecondsToSeconds(player.currentPosition)
                startSendRewind(player, seconds)
            }
            //store player state
            storeIsPlayingState()
        } else {
            if (playlist.notShuffled.isNotEmpty()) {
                playNext()
            }
        }
        //send command to change ui
        sendBroadcastPlayUI()
    }

    protected fun skipSongAndPlayNext() {
        if (::player.isInitialized) {
            playNext()
        }
    }

    protected fun skipSongAndPlayPrevious() {
        if (::player.isInitialized) {
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
        if (::player.isInitialized) {
            player.seekTo(milliseconds)
            //to apply current duration
            if (player.isPlaying) {
                pausePlayer()
                playSongAfterCreatedPlayer()
            }
            storeCurrentDuration(milliseconds)
        }
        sendBroadcastRewindUI(seconds)
    }

    protected fun shuffleOn() {
        QueueResolver.shuffleState = true
        //store before shuffle
        playlist.shuffle()
        MiniPlayerServicePreferences
            .setIsShuffle(this, QueueResolver.shuffleState)
        sendBroadcastShuffleUI()
    }

    protected fun shuffleOff() {
        QueueResolver.shuffleState = false
        playlist.notShuffle()
        MiniPlayerServicePreferences
            .setIsShuffle(this, QueueResolver.shuffleState)
        sendBroadcastUnShuffleUI()
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
            PlaylistTransaction.updateFavourite {
                it.filter { it != songPath }
            }
        }
    }

    protected fun loopState() {
        QueueResolver.loopState()
        MiniPlayerServicePreferences
            .setLoopState(this, 1)
        sendBroadcastLoopUI()
    }

    protected fun notLoopState() {
        QueueResolver.notLoopState()
        MiniPlayerServicePreferences
            .setLoopState(this, 0)
        sendBroadcastNotLoopUI()
    }

    protected fun loopAllState() {
        QueueResolver.loopAllState()
        MiniPlayerServicePreferences
            .setLoopState(this, 2)
        sendBroadcastLoopAllUI()
    }

    private fun restoreState() {
        scope.launch {
            val songsFromDb = ServiceTransaction.getPlaylist()
            val songsToPlaylist = MiniPlayerServiceSong.getSongsToPlaylist(songsFromDb)

            if (songsToPlaylist.isNotEmpty()) {
                playlist.addToQueue(*songsToPlaylist.toTypedArray())
                //restore shuffle and loop state
                val isShuffle = MiniPlayerServicePreferences
                    .getIsShuffle(this@MiniPlayerService)
                val loopState = MiniPlayerServicePreferences
                    .getLoopState(this@MiniPlayerService)
                //restore duration
                val duration = MiniPlayerServicePreferences
                        .getCurrentDuration(this@MiniPlayerService)
                //what song should play
                var posWasPlayedSong = MiniPlayerServicePreferences
                        .getCurrentPos(this@MiniPlayerService)
                if (posWasPlayedSong > songsToPlaylist.lastIndex)
                    posWasPlayedSong = songsToPlaylist.lastIndex
                val path = playlist.notShuffled[posWasPlayedSong].path
                //restore isPlaying state
                val isPlaying = MiniPlayerServicePreferences
                        .getIsPlaying(this@MiniPlayerService)
                val appWasDestroyed = AppPreference
                    .getAppIsDestroyed(this@MiniPlayerService)
                //apply shuffle state player
                if (isShuffle) shuffleOn()
                else shuffleOff()
                //apply loop state player
                when(loopState) {
                    0 -> notLoopState()
                    1 -> loopState()
                    2 -> loopAllState()
                }
                //after all info got -> start player ->  set current time -> stop
                playNext(path)
                rewindPlayer(duration)
                pausePlayer()
                //this means ui have been destroyed with service after destroy main activity
                //but app is still working -> after restoration we should play song
                if (isPlaying && appWasDestroyed.not()) {
                    playSongAfterCreatedPlayer()
                }
            }
        }
    }

    private fun addToQueue(list: List<File>) {
        playlist.addToQueue(*list.toTypedArray())
        //restore shuffle state
        if (QueueResolver.shuffleState) shuffleOn()
        storeQueue()
    }

    private fun stopOrPausePlayer(f: () -> Unit) {
        if (::player.isInitialized) {
            f()
            stopSendRewind()
        }
        sendBroadcastStopUI()
    }

    private fun stopPlayer() {
        stopOrPausePlayer {
            player.stop()
        }
    }

    private fun songIsOver() {
        if (::player.isInitialized) {
            when {
                QueueResolver.loop -> playNext(playlist.getSongPath())

                QueueResolver.loopAll -> playNext()
                //reset current song ui
                else -> {
                    playNext(playlist.getSongPath())
                    pausePlayer()
                }
            }
        }
    }

    private fun playNext(path: String? = null) {
        //check if song now is playing
        stopPlayer()
        //start playing
        val song = if (path == null)
             playlist.getNext()
        else
            playlist.getSongAndResetQuery(path)
        createPlayer(song.path)?.let {
            player = it
            playSongAfterCreatedPlayer()
            //update db
            scope.launch {
                PlaylistTransaction.updatePlayedSong(song.path)
            }
            //send info
            sendInfoToUI(song)
            //store pos
            storeSongPositionInQueue()
        }
    }

    private fun createPlayer(path: String): MediaPlayer? {
        val uri = Uri.parse(path)
        return MediaPlayer.create(baseContext, uri)
    }

    private fun startSendRewind(player: MediaPlayer, startFrom: Int = 0) {
        //this is how much we must change ui
        val durationInSeconds =
            SongTimeConverter.millisecondsToSeconds(player.duration)

        var rewindValue = startFrom
        rewindJob = scope.launch {
            while (isActive) {
                while (rewindValue <= durationInSeconds) {
                    MiniPlayerBroadcastRewind.apply {
                        sendBroadcastRewindUI(rewindValue++)
                    }
                    //store current duration cause onDestroy invoke only when view is destroy
                    storeCurrentDuration()
                    delay(1000)
                }
                songIsOver()
            }
        }
    }

    private fun stopSendRewind() {
        rewindJob.cancel()
    }

    private fun storeIsPlayingState() {
        if (player.isPlaying)
            MiniPlayerServicePreferences
                .setIsPlaying(this@MiniPlayerService, true)
        else
            MiniPlayerServicePreferences
                .setIsPlaying(this, false)
    }

    private fun storeSongPositionInQueue() {
        val posInQueue = playlist.getSong()
        val pos = playlist.notShuffled.indexOf(posInQueue)
        MiniPlayerServicePreferences.setCurrentPos(this, pos)
        Log.d(TAG, "store pos: $pos")
    }

    private fun storeCurrentDuration(duration: Int = player.currentPosition) {
        //store current duration cause onDestroy invoke only when view is destroy
        MiniPlayerServicePreferences
            .setCurrentDuration(this@MiniPlayerService, duration)
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
        scope.launch {
            sendLoopState()
            sendShuffleState()
            sendPath(song)
            sendSongNameAndArtist(song)
            sendDurationSong(player)
            sendIsHQ(song)
            sendIsLoved(song)
        }
    }

    private fun sendLoopState() {
        when(MiniPlayerServicePreferences.getLoopState(this)) {
            0 -> sendBroadcastNotLoopUI()
            1 -> sendBroadcastLoopUI()
            2 -> sendBroadcastLoopAllUI()
        }
    }

    private fun sendShuffleState() {
        if (MiniPlayerServicePreferences.getIsShuffle(this))
            sendBroadcastShuffleUI()
        else sendBroadcastUnShuffleUI()
    }

    private fun sendPath(song: File) {
        MiniPlayerBroadcastSongPath.apply {
            sendBroadcastSongPathUI(song.path)
        }
    }

    private fun sendDurationSong(player: MediaPlayer) {
        val duration = player.duration
        MiniPlayerBroadcastSongDuration.apply {
            sendBroadcastSongDurationUI(duration)
        }
    }

    private fun sendIsHQ(song: File) {

    }

    private fun sendSongNameAndArtist(file: File) {
        val songArtist = FileNameParser.getSongArtist(file)
        val songName = FileNameParser.getSongName(file)
        sendBroadcastSongArtistUI(songArtist)
        sendBroadcastSongNameUI(songName)
    }

    private suspend fun sendIsLoved(song: File) = withContext(Dispatchers.IO) {
        val favourite = PlaylistTransaction.getFavouriteSongs()
        if (favourite.contains(song.path))
            MiniPlayerBroadcastLike.apply {
                sendBroadcastLikeUI()
            }
        else MiniPlayerBroadcastUnlike.apply {
            sendBroadcastUnlikeUI()
        }
    }
}
