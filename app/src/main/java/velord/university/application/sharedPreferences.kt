package velord.university.application

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_SEARCH_QUERY = "searchQuery"

object QueryPreferences {

    fun getStoredQueryFolder(context: Context,
                             folder: String): String  =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(folder, "")!!

    fun setStoredQueryFolder(context: Context,
                             folder: String,
                             query: String) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(folder, query)
            }
}