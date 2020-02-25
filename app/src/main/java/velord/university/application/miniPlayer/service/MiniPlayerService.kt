package velord.university.application.miniPlayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastPlay.sendBroadcastPlayUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastRewind
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastRewind.sendBroadcastRewindUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastShow
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSongArtist.sendBroadcastSongArtistUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSongDuration
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSongName.sendBroadcastSongNameUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastStop.sendBroadcastStopUI
import velord.university.interactor.SongQueryInteractor
import velord.university.model.*
import java.io.File

abstract class MiniPlayerService : Service() {

    abstract val TAG: String

    private lateinit var player: MediaPlayer

    private val songQueue = SongQueue()

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)
    private lateinit var  rewindJob: Job

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        if (::player.isInitialized)
            player.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        return START_STICKY
    }

    protected fun playAllInFolder(pathFolder: String) {
        clearSongQueue()
        val songs = FileExtension.filterOnlyAudio(File(pathFolder))
        val info = if (songs.isNotEmpty()) {
            addToSongQueue(songs)
            playNext(songQueue.songs[0].path)
            "will play: ${songs.size}\n" +
                    "first: ${songs[0].name}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playNextAllInFolder(pathFolder: String) {
        val songs = FileExtension.filterOnlyAudio(File(pathFolder))
        addToSongQueue(songs)
        val info = "added to queue: ${songs.size}"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun shuffleAndPlayAllInFolder(pathFolder: String) {
        clearSongQueue()
        val songs = FileExtension.filterOnlyAudio(File(pathFolder))
        val info = if (songs.isNotEmpty()) {
            addToSongQueue(songs)
            songQueue.shuffle()
            playNext(songQueue.songs[0].path)
            "will play: ${songs.size}\n" +
                    "first: ${songQueue.songs[0].name}"
        } else { "no one song" }
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    protected fun playByPath(path: String) {
        //filter songs by received filter after that play it
        clearSongQueue()
        addToSongQueue(SongQueryInteractor.songs.toList())
        playNext(path)
        //showUI
        MiniPlayerBroadcastShow.apply { sendBroadcastShow() }
    }

    protected fun pausePlayer() {
        if (::player.isInitialized) {
            player.pause()
            stopSendRewind()
        }
        sendBroadcastStopUI()
    }

    protected fun playSongAfterCreatedPlayer() {
        if (::player.isInitialized) {
            //was current player check
            if (player.currentPosition != player.duration) {
                player.start()
                val seconds =
                    SongTimeConverter.millisecondsToSeconds(player.currentPosition)
                startSendRewind(player, seconds)
            }
        } else {
            if (songQueue.songs.isNotEmpty()) {
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
            val currentPos = songQueue.getSongPos()
            //current position minus 1 else first from end
            val newSong = if (currentPos != 0)
                songQueue.getSong(currentPos - 1)
            else
                songQueue.songs[songQueue.songs.lastIndex]
            //just need got path to new file
            playNext(newSong.path)
        }
    }

    protected fun rewindPlayer(seconds: Int) {
        if (::player.isInitialized) {
            //was current player check
            val milliseconds = SongTimeConverter.secondsToMilliseconds(seconds)
            player.seekTo(milliseconds)
            pausePlayer()
            playSongAfterCreatedPlayer()
        }
        sendBroadcastRewindUI(seconds)
    }

    protected fun shuffleOn() {
        QueueResolver.shuffleState = true
        //store before shuffle
        songQueue.notShuffled.apply {
            clear()
            addAll(songQueue.songs)
        }
        songQueue.songs.shuffle()
    }

    protected fun shuffleOff() {
        QueueResolver.shuffleState = false
        songQueue.songs.apply {
            clear()
            addAll(songQueue.notShuffled)
        }
    }

    protected fun addToQueue(path: String) {
        val newSong = File(path)
        songQueue.songs.add(newSong)
        val info = "in queue at ${songQueue.getSongPos(newSong) + 1}"
        Log.d(TAG, info)
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }

    private fun addToSongQueue(list: List<File>) {
        songQueue.songs.addAll(list)
    }

    private fun clearSongQueue() {
        songQueue.songs.clear()
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

    private fun stopPlayer() {
        if (::player.isInitialized) {
            player.stop()
            stopSendRewind()
        }
        sendBroadcastStopUI()
    }

    private fun songIsOver() {
        if (::player.isInitialized) {
            when {
                QueueResolver.loop == true -> {
                    playNext(songQueue.getSongPath())
                }
                QueueResolver.loopAll == true -> {
                    playNext()
                }
                //reset current song ui
                else -> {
                    playNext(songQueue.getSongPath())
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
             songQueue.getNext()
        else
            songQueue.getSongAndResetQuery(path)
        createPlayer(song.path)
        playSongAfterCreatedPlayer()
        //send info
        sendInfoToUI(song)
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
                    delay(1000)
                }
                songIsOver()
            }
        }
    }

    private fun stopSendRewind() {
        rewindJob.cancel()
    }

    private fun sendInfoToUI(song: File) {
        //send info
        sendSongNameAndArtist(song)
        sendDurationSong(player)
        sendIsHQ()
    }

}
