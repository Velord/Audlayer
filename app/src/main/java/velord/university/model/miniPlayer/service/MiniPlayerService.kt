package velord.university.model.miniPlayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.IBinder
import android.util.Log
import velord.university.model.FileNameParser
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSongArtist.sendBroadcastSongArtistUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSongName.sendBroadcastSongNameUI
import java.io.File

abstract class MiniPlayerService : Service() {

    abstract val TAG: String

    protected val player: MediaPlayer = MediaPlayer()

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
        player.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        return START_STICKY
    }

    protected fun playByPath(path: String) {
        val file = File(path)
        val songArtist = FileNameParser.getSongArtist(file)
        val songName = FileNameParser.getSongName(file)
        sendBroadcastSongArtistUI(songArtist)
        sendBroadcastSongNameUI(songName)


        val soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        val soundId = soundPool.load(path, 1)
        soundPool.play(soundId, 1.0f,
            1.0f, 1, 0 , 1.0f)
    }

}
