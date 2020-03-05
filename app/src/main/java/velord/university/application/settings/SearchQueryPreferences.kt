package velord.university.application.settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_SEARCH_QUERY_FOLDER = "searchQuery"
private const val PREF_SEARCH_QUERY_ALBUM = "searchQueryAlbum"

object SearchQueryPreferences {

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

    fun getStoredQueryAlbum(context: Context,
                            key: String = PREF_SEARCH_QUERY_ALBUM): String  =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(key, "")!!

    fun setStoredQueryAlbum(context: Context,
                            query: String,
                            key: String = PREF_SEARCH_QUERY_ALBUM) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(key, query)
            }
}