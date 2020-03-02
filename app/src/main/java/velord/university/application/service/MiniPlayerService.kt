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
import velord.university.application.broadcast.MiniPlayerBroadcastPlay.sendBroadcastPlayUI
import velord.university.application.broadcast.MiniPlayerBroadcastRewind
import velord.university.application.broadcast.MiniPlayerBroadcastRewind.sendBroadcastRewindUI
import velord.university.application.broadcast.MiniPlayerBroadcastShow
import velord.university.application.broadcast.MiniPlayerBroadcastSongArtist.sendBroadcastSongArtistUI
import velord.university.application.broadcast.MiniPlayerBroadcastSongDuration
import velord.university.application.broadcast.MiniPlayerBroadcastSongName.sendBroadcastSongNameUI
import velord.university.application.broadcast.MiniPlayerBroadcastStop.sendBroadcastStopUI
import velord.university.application.settings.AppPreference
import velord.university.application.settings.MiniPlayerServicePreferences
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.model.QueueResolver
import velord.university.model.SongPlaylist
import velord.university.model.converter.SongTimeConverter
import velord.university.model.entity.MiniPlayerServiceSong
import velord.university.repository.AppDatabase
import java.io.File

abstract class MiniPlayerService : Service() {

    abstract val TAG: String

    private lateinit var player: MediaPlayer

    private val playlist = SongPlaylist()

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)
    private lateinit var  rewindJob: Job

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()
        retrieveInfo()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        //store player state
        storeCurrentPlayerState()
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
        clearSongQueue()
        val songs = FileFilter.filterOnlyAudio(File(pathFolder))
        val info = if (songs.isNotEmpty()) {
            addToSongQueue(songs)
            playNext(playlist.songs[0].path)
            "will play: ${songs.size}\n" +
                    "first: ${songs[0].name}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playNextAllInFolder(pathFolder: String) {
        val songs = FileFilter.filterOnlyAudio(File(pathFolder))
        addToSongQueue(songs)
        val info = "added to queue: ${songs.size}"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun shuffleAndPlayAllInFolder(pathFolder: String) {
        clearSongQueue()
        val songs = FileFilter.filterOnlyAudio(File(pathFolder))
        val info = if (songs.isNotEmpty()) {
            addToSongQueue(songs)
            playlist.shuffle()
            playNext(playlist.songs[0].path)
            "will play: ${songs.size}\n" +
                    "first: ${playlist.songs[0].name}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playByPath(path: String) {
        clearSongQueue()
        addToSongQueue(SongPlaylistInteractor.songs.toList())
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
            storeCurrentPlayerState()
        } else {
            if (playlist.songs.isNotEmpty()) {
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
                playlist.songs[playlist.songs.lastIndex]
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
        playlist.notShuffled.apply {
            clear()
            addAll(playlist.songs)
        }
        playlist.songs.shuffle()
    }

    protected fun shuffleOff() {
        QueueResolver.shuffleState = false
        playlist.songs.apply {
            clear()
            addAll(playlist.notShuffled)
        }
    }

    protected fun addToQueue(path: String) {
        val newSong = File(path)
        playlist.songs.add(newSong)
        val info = "in queue at ${playlist.getSongPos(newSong) + 1}"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    private fun retrieveInfo() {
        scope.launch {
            AudlayerApp.db?.let { db ->
                val songsFromDb = retrievePlaylist(db)
                val songsToPlaylist = songsFromDb.sortedBy {
                    it.pos
                }.map {
                    File(it.path)
                }

                if (songsToPlaylist.isNotEmpty()) {
                    addToSongQueue(songsToPlaylist)
                    //restore duration
                    val duration =
                        MiniPlayerServicePreferences
                            .getCurrentDuration(this@MiniPlayerService)
                    //what song should play
                    val posWasPlayedSong =
                        MiniPlayerServicePreferences
                            .getCurrentPos(this@MiniPlayerService)
                    val path =
                        playlist.getSong(posWasPlayedSong).path
                    val isPlaying =
                        MiniPlayerServicePreferences
                            .getIsPlaying(this@MiniPlayerService)
                    val appWasDestroyed =
                        AppPreference.getAppIsDestroyed(this@MiniPlayerService)
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
    }

    private suspend fun retrievePlaylist(db: AppDatabase) =
        scope.async { db.serviceDao().getAll() }.await()

    private fun addToSongQueue(list: List<File>) {
        playlist.songs.addAll(list)
        //restore shuffle state
        if (QueueResolver.shuffleState)
            shuffleOn()
        storeQueue()
    }

    private fun clearSongQueue() {
        playlist.songs.clear()
    }

    private fun sendSongNameAndArtist(file: File) {
        val songArtist = FileNameParser.getSongArtist(file)
        val songName = FileNameParser.getSongName(file)
        sendBroadcastSongArtistUI(songArtist)
        sendBroadcastSongNameUI(songName)
    }

    private fun sendDurationSong(player: MediaPlayer) {
        val duration = player.duration
        MiniPlayerBroadcastSongDuration.apply {
            sendBroadcastSongDurationUI(duration)
        }
    }

    private fun sendIsHQ() {

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
                QueueResolver.loop == true -> {
                    playNext(playlist.getSongPath())
                }
                QueueResolver.loopAll == true -> {
                    playNext()
                }
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
        createPlayer(song.path)
        playSongAfterCreatedPlayer()
        //send info
        sendInfoToUI(song)
        //store pos
        storeSongPositionInQueue()
    }

    private fun createPlayer(path: String) {
        val uri = Uri.parse(path)
        player = MediaPlayer.create(baseContext, uri)
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

    private fun storeCurrentPlayerState() {
        if (player.isPlaying)
            MiniPlayerServicePreferences
                .setIsPlaying(this@MiniPlayerService, true)
        else
            MiniPlayerServicePreferences
                .setIsPlaying(this, false)
    }

    private fun storeSongPositionInQueue() {
        val pos = playlist.getSongPos()
        MiniPlayerServicePreferences
            .setCurrentPos(this, pos)
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
            val songsToDb = playlist.songs.map {
                val path = it.path
                val pos = playlist.getSongPos(path)
                MiniPlayerServiceSong(path, pos)
            }.toTypedArray()
            AudlayerApp.db?.let {
                it.serviceDao().updateData(songsToDb)
            }
        }
        Log.d(TAG, "store queue")
    }

    private fun stopSendRewind() {
        rewindJob.cancel()
    }

    private fun sendInfoToUI(song: File = playlist.getSong()) {
        //send info
        sendSongNameAndArtist(song)
        sendDurationSong(player)
        sendIsHQ()
    }
}
