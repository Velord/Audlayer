package velord.university.application.settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val ORDER_ASC_DEC_FOLDER = "AscendingDescendingFolder"
private const val ORDER_ASC_DEC_SELECT_SONG = "AscendingDescendingSelect"
private const val ORDER_ASC_DEC_ALBUM = "AscendingDescendingAlbum"

private const val NAME_ARTIST_DATEADDED_FOLDER = "NameArtistDateAdded"
private const val NAME_ARTIST_DATEADED_SELECT = "NameArtistDateAdded"
private const val ALBUM_ARTIST_YEAR_NUMBEROFTRACKS = "AlbumArtistYearNumberOfTracks"

object SortByPreference {

    fun setAscDescFolderFragment(
        context: Context,
        order: Int,
        key: String = ORDER_ASC_DEC_FOLDER
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescFolderFragment(
        context: Context,
        key: String = ORDER_ASC_DEC_FOLDER
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setNameArtistDateAddedFolderFragment(
        context: Context,
        pos: Int,
        key: String = NAME_ARTIST_DATEADDED_FOLDER
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getNameArtistDateAddedFolderFragment(
        context: Context,
        key: String = NAME_ARTIST_DATEADDED_FOLDER
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setAscDescSelectSongFragment(
        context: Context,
        order: Int,
        key: String = ORDER_ASC_DEC_SELECT_SONG
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescSelectSongFragment(
        context: Context,
        key: String = ORDER_ASC_DEC_SELECT_SONG
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setNameArtistDateAddedSelectSongFragment(
        context: Context,
        pos: Int,
        key: String = NAME_ARTIST_DATEADED_SELECT
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getNameArtistDateAddedSelectSongFragment(
        context: Context,
        key: String = NAME_ARTIST_DATEADED_SELECT
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setAlbumArtistYearNumberAlbumFragment(
        context: Context,
        pos: Int,
        key: String = ALBUM_ARTIST_YEAR_NUMBEROFTRACKS
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getAlbumArtistYearNumberAlbumFragment(
        context: Context,
        key: String = ALBUM_ARTIST_YEAR_NUMBEROFTRACKS
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setAscDescAlbumFragment(
        context: Context,
        order: Int,
        key: String = ORDER_ASC_DEC_ALBUM
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescAlbumFragment(
        context: Context,
        key: String = ORDER_ASC_DEC_ALBUM
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)
}