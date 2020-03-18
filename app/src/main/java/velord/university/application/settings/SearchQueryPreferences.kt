package velord.university.application.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_QUERY_ALBUM = "searchQueryAlbum"
private const val PREF_SEARCH_QUERY_SONG = "searchQuerySong"
private const val PREF_SEARCH_QUERY_VK = "searchQueryVk"

object SearchQueryPreferences {

    fun getStoredQueryFolder(context: Context,
                             folder: String): String  =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(folder, "-1")!!

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
            .getString(key, "-1")!!

    fun setStoredQueryAlbum(context: Context,
                            query: String,
                            key: String = PREF_SEARCH_QUERY_ALBUM) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(key, query)
            }

    fun getStoredQuerySong(context: Context,
                            key: String = PREF_SEARCH_QUERY_SONG): String  =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(key, "-1")!!

    fun setStoredQuerySong(context: Context,
                            query: String,
                            key: String = PREF_SEARCH_QUERY_SONG) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(key, query)
            }

    fun getStoredQueryVk(context: Context,
                           key: String = PREF_SEARCH_QUERY_VK): String  =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(key, "-1")!!

    fun setStoredQueryVk(context: Context,
                           query: String,
                           key: String = PREF_SEARCH_QUERY_VK) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(key, query)
            }
}