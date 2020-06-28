package velord.university.application.settings

import android.content.Context

class SortByPreference(context: Context) {

    companion object {
        private const val ORDER_ASC_DEC_FOLDER = "AscendingDescendingFolder"
        private const val ORDER_ASC_DEC_SELECT_SONG = "AscendingDescendingSelect"
        private const val ORDER_ASC_DEC_ALBUM = "AscendingDescendingAlbum"
        private const val ORDER_ASC_DEC_ALL_SONG = "AscendingDescendingSong"
        private const val ORDER_ASC_DEC_VK = "AscendingDescendingVK"
        private const val ORDER_ASC_DEC_RADIO = "AscendingDescendingRadio"

        private const val NAME_ARTIST_DATE_ADDED_FOLDER = "NameArtistDateAdded"
        private const val NAME_ARTIST_DATE_ADDED_SELECT = "NameArtistDateAdded"
        private const val ALBUM_ARTIST_YEAR_NUMBER_OF_TRACKS = "AlbumArtistYearNumberOfTracks"
        private const val NAME_ARTIST_DURATION_SIZE_DATE_ADDED_ALL_SONG = "NameArtistDurationSizeDateAddedSong"
        private const val NAME_ARTIST_DURATION_SIZE_DATE_ADDED_VK = "NameArtistDurationSizeDateAddedVk"
        private const val NAME_ARTIST_RADIO = "NameArtistRadio"
    }

    var ascDescFolderFragment: Int by PreferencesDelegate(
        context,
        ORDER_ASC_DEC_FOLDER,
        -1
    )

    var sortByFolderFragment: Int by PreferencesDelegate(
        context,
        NAME_ARTIST_DATE_ADDED_FOLDER,
        -1
    )

    var ascDescSelectSongFragment: Int by PreferencesDelegate(
        context,
        ORDER_ASC_DEC_SELECT_SONG,
        -1
    )

    var sortBySelectSongFragment: Int by PreferencesDelegate(
        context,
        NAME_ARTIST_DATE_ADDED_SELECT,
        -1
    )

    var ascDescAlbumFragment: Int by PreferencesDelegate(
        context,
        ORDER_ASC_DEC_ALBUM,
        -1
    )

    var sortByAlbumFragment: Int by PreferencesDelegate(
        context,
        ALBUM_ARTIST_YEAR_NUMBER_OF_TRACKS,
        -1
    )

    var ascDescAllSongFragment: Int by PreferencesDelegate(
        context,
        ORDER_ASC_DEC_ALL_SONG,
        -1
    )

    var sortByAllSongFragment: Int by PreferencesDelegate(
        context,
        NAME_ARTIST_DURATION_SIZE_DATE_ADDED_ALL_SONG,
        -1
    )

    var ascDescVkFragment: Int by PreferencesDelegate(
        context,
        ORDER_ASC_DEC_VK,
        -1
    )

    var sortByVkFragment: Int by PreferencesDelegate(
        context,
        NAME_ARTIST_DURATION_SIZE_DATE_ADDED_VK,
        -1
    )

    var ascDescRadioFragment: Int by PreferencesDelegate(
        context,
        ORDER_ASC_DEC_RADIO,
        -1
    )

    var sortByRadioFragment: Int by PreferencesDelegate(
        context,
        NAME_ARTIST_RADIO,
        -1
    )
}