package velord.university.application.settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_SORT_BY_ORDER_ASC_DEC = "AscendingDescending"
private const val PREF_SORT_BY_NAME_ARTIST_DATEADDED = "NameArtistDateAdded"

object SortByPreference {

    fun setAscDescFolderFragment(
        context: Context,
        order: Int,
        key: String = PREF_SORT_BY_ORDER_ASC_DEC
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescFolderFragment(
        context: Context,
        key: String = PREF_SORT_BY_ORDER_ASC_DEC
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setNameArtistDateAddedFolderFragment(
        context: Context,
        pos: Int,
        key: String = PREF_SORT_BY_NAME_ARTIST_DATEADDED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getNameArtistDateAddedFolderFragment(
        context: Context,
        key: String = PREF_SORT_BY_NAME_ARTIST_DATEADDED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setAscDescSongAddFragment(
        context: Context,
        order: Int,
        key: String = PREF_SORT_BY_ORDER_ASC_DEC
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescSongAddFragment(
        context: Context,
        key: String = PREF_SORT_BY_ORDER_ASC_DEC
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setNameArtistDateAddedSongAddFragment(
        context: Context,
        pos: Int,
        key: String = PREF_SORT_BY_NAME_ARTIST_DATEADDED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getNameArtistDateAddedSongAddFragment(
        context: Context,
        key: String = PREF_SORT_BY_NAME_ARTIST_DATEADDED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)
}