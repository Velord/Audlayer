package velord.university.application.service.audioFocus

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import velord.university.application.broadcast.MediaButtonEventReceiver

abstract class AudioFocusListenerService : Service(),
    MediaPlayer.OnCompletionListener {

    abstract val TAG: String
    //how service should react to focus change
    abstract val onAudioFocusChange: AudioFocusChangeF

    private lateinit var audioManager: AudioManager
    private lateinit var afListenerMusic: AudioFocusListener
    protected lateinit var player: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (mp == player) {
            Log.d(TAG, "Abandon focus")
            audioManager.abandonAudioFocus(afListenerMusic)
        }
    }

    override fun onCreate() {
        super.onCreate()
        //audioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onDestroy() {
        super.onDestroy()
        //audioManager
        if (::player.isInitialized) {
            if (player.isPlaying.not())
                audioManager.abandonAudioFocus(afListenerMusic)
        }
    }

    protected fun playerIsInitialized(): Boolean = ::player.isInitialized

    protected fun setAudioFocusMusicListener() {
        afListenerMusic =
            AudioFocusListener(
                onAudioFocusChange,
                TAG
            )
        val requestResult = audioManager.requestAudioFocus(
            afListenerMusic,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        registerMediaButtonEventReceiver()
        Log.d(TAG, "Music request focus, result: $requestResult")
    }

    protected fun registerMediaButtonEventReceiver() {
        val component = ComponentName(this, MediaButtonEventReceiver::class.java)
        audioManager.registerMediaButtonEventReceiver(component)
    }

    protected fun unregisterMediaButtonEventReceiver() {
        val component = ComponentName(this, MediaButtonEventReceiver::class.java)
        audioManager.unregisterMediaButtonEventReceiver(component)
    }
}