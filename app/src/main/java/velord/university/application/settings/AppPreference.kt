package velord.university.application.settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val AUDLAYER_APP_DESTROYED = "appIsDestroyed"

object AppPreference {

    fun setAppIsDestroyed(
        context: Context,
        isDestroyed: Boolean,
        key: String = AUDLAYER_APP_DESTROYED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(key, isDestroyed)
            }

    fun getAppIsDestroyed(
        context: Context,
        key: String = AUDLAYER_APP_DESTROYED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key, false)
}