package velord.university.application.settings.miniPlayer

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_RADIO_SERVICE_ID = "radioServiceID"
private const val PREF_RADIO_SERVICE_IS_PLAYING = "radioServiceIsPlaying"

object RadioServicePreference {

    fun setCurrentRadioId(
        context: Context,
        value: Int,
        key: String = PREF_RADIO_SERVICE_ID
    ) = PreferenceManager.getDefaultSharedPreferences(context)
        .edit { putInt(key, value) }

    fun getCurrentRadioId(
        context: Context,
        key: String = PREF_RADIO_SERVICE_ID
    ) = PreferenceManager.getDefaultSharedPreferences(context)
        .getInt(key, -1)

    fun setIsPlaying(
        context: Context,
        isPlaying: Boolean,
        key: String = PREF_RADIO_SERVICE_IS_PLAYING
    ) = PreferenceManager.getDefaultSharedPreferences(context)
        .edit {
            putBoolean(key, isPlaying)
        }

    fun getIsPlaying(
        context: Context,
        key: String = PREF_RADIO_SERVICE_IS_PLAYING
    ) = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(key, false)
}