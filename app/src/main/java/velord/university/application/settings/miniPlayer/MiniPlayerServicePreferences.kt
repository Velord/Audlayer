package velord.university.application.settings.miniPlayer

import android.content.Context
import velord.university.application.settings.PreferencesDelegate

class MiniPlayerServicePreferences(context: Context) {

    companion object {
        private const val PREF_MINI_PLAYER_SERVICE_CURRENT_POS = "serviceCurrentPos"
        private const val PREF_MINI_PLAYER_SERVICE_IS_PLAYING = "serviceIsPlaying"
        private const val PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION = "serviceCurrentDuration"
        private const val PREF_MINI_PLAYER_SERVICE_IS_SHUFFLE = "serviceIsShuffle"
        private const val PREF_MINI_PLAYER_SERVICE_IS_LOOP = "serviceIsLoop"
    }

    var currentPos: Int by PreferencesDelegate(
        context,
        PREF_MINI_PLAYER_SERVICE_CURRENT_POS,
        -1
    )

    var currentDuration: Int by PreferencesDelegate(
        context,
        PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION,
        -1
    )

    var isPlaying: Boolean by PreferencesDelegate(
        context,
        PREF_MINI_PLAYER_SERVICE_IS_PLAYING,
        false
    )

    var isShuffle: Boolean by PreferencesDelegate(
        context,
        PREF_MINI_PLAYER_SERVICE_IS_SHUFFLE,
        false
    )

    var loopState: Int by PreferencesDelegate(
        context,
        PREF_MINI_PLAYER_SERVICE_IS_LOOP,
        -1
    )
}