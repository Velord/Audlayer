package velord.university.application.settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_SORT_BY_ORDER_ASC_DEC = "AscendingDescending"
private const val PREF_SORT_BY_NAME_ARTIST_DATEADDED = "NameArtistDateAdded"

object SortByPreference {

    fun setAscDesc(
        context: Context,
        order: Int,
        key: String = PREF_SORT_BY_ORDER_ASC_DEC
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDesc(
        context: Context,
        key: String = PREF_SORT_BY_ORDER_ASC_DEC
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setNameArtistDateAdded(
        context: Context,
        pos: Int,
        key: String = PREF_SORT_BY_NAME_ARTIST_DATEADDED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getNameArtistDateAdded(
        context: Context,
        key: String = PREF_SORT_BY_NAME_ARTIST_DATEADDED
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)
}