package velord.university.model.miniPlayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import velord.university.model.FileNameParser
import velord.university.model.SongTimeConverter
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastRewind
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastShow
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSongArtist.sendBroadcastSongArtistUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSongDuration
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSongName.sendBroadcastSongNameUI
import java.io.File

abstract class MiniPlayerService : Service() {

    abstract val TAG: String

    private lateinit var player: MediaPlayer

    private lateinit var song: File

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
    }

    protected fun pausePlayer() {
        if (::player.isInitialized) {
            player.pause()
             stopSendRewind()
        }
    }

    protected fun playSong() {
        if (::player.isInitialized) {
            if (::song.isInitialized) {
                player.start()
                val seconds =
                    SongTimeConverter.millisecondsToSeconds(player.currentPosition)
                startSendRewind(player, seconds)
            }
        }
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
                repeat(durationInSeconds) {
                    MiniPlayerBroadcastRewind.apply {
                        sendBroadcastRewindUI(rewindValue++)
                    }
                    delay(1000)
                }
            }
        }
    }

    protected fun rewindPlayer(seconds: Int) {
        if (::player.isInitialized) {
            if (::song.isInitialized) {
                val milliseconds = SongTimeConverter.secondsToMilliseconds(seconds)
                player.seekTo(milliseconds)
                pausePlayer()
                playSong()
            }
        }
    }

    private fun stopSendRewind() {
        rewindJob.cancel()
    }

    protected fun playByPath(path: String) {
        //check if song now is playing
        if (::player.isInitialized)
            stopPlayer()
        //get file and set current song file
        song = File(path)
        //start playing
        val path = song.path
        createPlayer(path)
        playSong()
        //showUI
        MiniPlayerBroadcastShow.apply {
            sendBroadcastShow()
        }
        //send info
        sendSongNameAndArtist(song)
        sendDurationSong(player)
        sendIsHQ()
    }
}
