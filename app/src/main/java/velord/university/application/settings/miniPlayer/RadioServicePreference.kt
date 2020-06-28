package velord.university.application.settings.miniPlayer

import android.content.Context
import velord.university.application.settings.PreferencesDelegate

class RadioServicePreference(context: Context) {

    companion object {
        private const val PREF_RADIO_SERVICE_ID = "radioServiceID"
        private const val PREF_RADIO_SERVICE_IS_PLAYING = "radioServiceIsPlaying"
    }

    var currentRadioId: Int by PreferencesDelegate(
        context,
        PREF_RADIO_SERVICE_ID,
        -1
    )

    var isPlaying: Boolean by PreferencesDelegate(
        context,
        PREF_RADIO_SERVICE_IS_PLAYING,
        false
    )
}