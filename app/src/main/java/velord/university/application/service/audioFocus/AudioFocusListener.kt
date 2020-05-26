package velord.university.application.service.audioFocus

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log

data class AudioFocusListener(private val player: MediaPlayer,
                              private val tag: String
) : AudioManager.OnAudioFocusChangeListener {

    override fun onAudioFocusChange(focusChange: Int) {
        var event = ""
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                event = "AUDIOFOCUS_LOSS"
                player.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                event = "AUDIOFOCUS_LOSS_TRANSIENT"
                player.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"
                player.setVolume(0.5f, 0.5f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                event = "AUDIOFOCUS_GAIN"
                if (!player.isPlaying) player.start()
                player.setVolume(1.0f, 1.0f)
            }
        }
        Log.d(tag, "onAudioFocusChange: $event")
    }
}