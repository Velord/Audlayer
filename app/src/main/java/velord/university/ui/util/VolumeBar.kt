package velord.university.ui.util

import android.content.Context
import android.media.AudioManager

fun hideDefaultChangeVolumeBar(context: Context,
                               volumeEvent: VolumeEvent) {
    val manager = context
        .getSystemService(Context.AUDIO_SERVICE) as AudioManager
    manager.adjustStreamVolume(
        AudioManager.STREAM_MUSIC,
        volumeEvent.get(),
        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
    )
}

sealed class VolumeEvent {

    abstract fun getAudioManagerEvent(): Int

    internal object INCREASE: VolumeEvent() {

        override fun getAudioManagerEvent(): Int = AudioManager.ADJUST_RAISE
    }

    internal object DECREASE: VolumeEvent() {

        override fun getAudioManagerEvent(): Int = AudioManager.ADJUST_LOWER
    }

    fun get(): Int = getAudioManagerEvent()
}