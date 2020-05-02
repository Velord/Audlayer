package velord.university.application.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val ORDER_ASC_DEC_FOLDER = "AscendingDescendingFolder"
private const val ORDER_ASC_DEC_SELECT_SONG = "AscendingDescendingSelect"
private const val ORDER_ASC_DEC_ALBUM = "AscendingDescendingAlbum"
private const val ORDER_ASC_DEC_SONG = "AscendingDescendingSong"
private const val ORDER_ASC_DEC_VK = "AscendingDescendingVK"
private const val ORDER_ASC_DEC_RADIO = "AscendingDescendingRadio"

private const val NAME_ARTIST_DATE_ADDED_FOLDER = "NameArtistDateAdded"
private const val NAME_ARTIST_DATE_ADDED_SELECT = "NameArtistDateAdded"
private const val ALBUM_ARTIST_YEAR_NUMBER_OF_TRACKS = "AlbumArtistYearNumberOfTracks"
private const val NAME_ARTIST_DURATION_SIZE_DATE_ADDED_SONG = "NameArtistDurationSizeDateAddedSong"
private const val NAME_ARTIST_DURATION_SIZE_DATE_ADDED_VK = "NameArtistDurationSizeDateAddedVk"
private const val NAME_ARTIST_RADIO = "NameArtistRadio"


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

    fun setSortByFolderFragment(
        context: Context,
        sortBy: Int,
        key: String = NAME_ARTIST_DATE_ADDED_FOLDER
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, sortBy)
            }

    fun getSortByFolderFragment(
        context: Context,
        key: String = NAME_ARTIST_DATE_ADDED_FOLDER
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

    fun setSortBySelectSongFragment(
        context: Context,
        sortBy: Int,
        key: String = NAME_ARTIST_DATE_ADDED_SELECT
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, sortBy)
            }

    fun getSortBySelectSongFragment(
        context: Context,
        key: String = NAME_ARTIST_DATE_ADDED_SELECT
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setSortByAlbumFragment(
        context: Context,
        sortBy: Int,
        key: String = ALBUM_ARTIST_YEAR_NUMBER_OF_TRACKS
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, sortBy)
            }

    fun getSortByAlbumFragment(
        context: Context,
        key: String = ALBUM_ARTIST_YEAR_NUMBER_OF_TRACKS
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

    fun setAscDescSongFragment(
        context: Context,
        order: Int,
        key: String = ORDER_ASC_DEC_SONG
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescSongFragment(
        context: Context,
        key: String = ORDER_ASC_DEC_SONG
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setSortBySongFragment(
        context: Context,
        sortBy: Int,
        key: String = NAME_ARTIST_DURATION_SIZE_DATE_ADDED_SONG
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, sortBy)
            }

    fun getSortBySongFragment(
        context: Context,
        key: String = NAME_ARTIST_DURATION_SIZE_DATE_ADDED_SONG
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setSortByVkFragment(
        context: Context,
        sortBy: Int,
        key: String = NAME_ARTIST_DURATION_SIZE_DATE_ADDED_VK
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, sortBy)
            }

    fun getSortByVkFragment(
        context: Context,
        key: String = NAME_ARTIST_DURATION_SIZE_DATE_ADDED_VK
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setAscDescVkFragment(
        context: Context,
        order: Int,
        key: String = ORDER_ASC_DEC_VK
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescVkFragment(
        context: Context,
        key: String = ORDER_ASC_DEC_VK
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setSortByRadioFragment(
        context: Context,
        sortBy: Int,
        key: String = NAME_ARTIST_RADIO
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, sortBy)
            }

    fun getSortByRadioFragment(
        context: Context,
        key: String = NAME_ARTIST_RADIO
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setAscDescRadioFragment(
        context: Context,
        order: Int,
        key: String = ORDER_ASC_DEC_RADIO
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, order)
            }

    fun getAscDescRadioFragment(
        context: Context,
        key: String = ORDER_ASC_DEC_RADIO
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)
}