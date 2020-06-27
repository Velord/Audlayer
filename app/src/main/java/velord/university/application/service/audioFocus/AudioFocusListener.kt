package velord.university.application.service.audioFocus

import android.media.AudioManager
import android.util.Log

data class AudioFocusListener(private val audioFocusChangeF: AudioFocusChangeF,
                              private val tag: String
) : AudioManager.OnAudioFocusChangeListener {

    override fun onAudioFocusChange(focusChange: Int) {
        var event = ""
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                event = "AUDIOFOCUS_LOSS"
                audioFocusChangeF.focusLossF()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                event = "AUDIOFOCUS_LOSS_TRANSIENT"
                audioFocusChangeF.focusLossTransientF()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"
                audioFocusChangeF.focusLossTransientCanDuckF()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                event = "AUDIOFOCUS_GAIN"
                audioFocusChangeF.focusGainF()
            }
        }
        Log.d(tag, "onAudioFocusChange: $event")
    }
}