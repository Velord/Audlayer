package velord.university.application.settings.miniPlayer

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_MINI_PLAYER_UI_LAYOUT_STATE = "miniPlayerUILayoutState"

object MiniPlayerUIPreference {

    fun setState(
        context: Context,
        pos: Int,
        key: String = PREF_MINI_PLAYER_UI_LAYOUT_STATE
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putInt(key, pos) }

    fun getState(
        context: Context,
        key: String = PREF_MINI_PLAYER_UI_LAYOUT_STATE
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)
}