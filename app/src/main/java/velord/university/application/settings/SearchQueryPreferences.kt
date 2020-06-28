package velord.university.application.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SearchQueryPreferences(context: Context) {

    companion object {
        private const val PREF_SEARCH_QUERY_ALBUM = "searchQueryAlbum"
        private const val PREF_SEARCH_QUERY_ALL_SONG = "searchQuerySong"
        private const val PREF_SEARCH_QUERY_VK = "searchQueryVk"
        private const val PREF_SEARCH_QUERY_RADIO = "searchQueryRadio"

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
    }

    var storedQueryAlbum: String by PreferencesDelegate(
        context,
        PREF_SEARCH_QUERY_ALBUM,
    "-1"
    )

    var storedQueryAllSong: String by PreferencesDelegate(
        context,
        PREF_SEARCH_QUERY_ALL_SONG,
        "-1"
    )

    var storedQueryVk: String by PreferencesDelegate(
        context,
        PREF_SEARCH_QUERY_VK,
        "-1"
    )

    var storedQueryRadio: String by PreferencesDelegate(
        context,
        PREF_SEARCH_QUERY_RADIO,
        "-1"
    )
}