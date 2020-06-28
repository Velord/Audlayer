package velord.university.application.settings.miniPlayer

import android.content.Context
import velord.university.application.settings.PreferencesDelegate

class MiniPlayerUIPreference(context: Context) {

    companion object {
        private const val PREF_MINI_PLAYER_UI_LAYOUT_STATE = "miniPlayerUILayoutState"
    }

    var state: Int by PreferencesDelegate(
        context,
        PREF_MINI_PLAYER_UI_LAYOUT_STATE,
        -1
    )
}