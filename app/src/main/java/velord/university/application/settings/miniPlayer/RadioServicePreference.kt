package velord.university.application.settings.miniPlayer

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_RADIO_SERVICE_ID = "radioServiceID"

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
}