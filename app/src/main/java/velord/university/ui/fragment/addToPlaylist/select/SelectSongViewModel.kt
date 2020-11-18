package velord.university.ui.fragment.addToPlaylist.select

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import velord.university.application.permission.PermissionChecker.checkReadWriteExternalStoragePermission
import velord.university.application.settings.SortByPreference
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.model.entity.music.song.main.AudlayerSong.Companion.filterByQuery

class SelectSongViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    val TAG = "SelectSongViewModel"

    lateinit var songList: List<AudlayerSong>

    val checked = mutableListOf<AudlayerSong>()

    lateinit var currentQuery: String

    fun filterAndSortFiles(context: Context,
                           searchTerm: String): List<AudlayerSong> {
        val songs = songList.filterByQuery(searchTerm)
        // sort by name or artist or date added
        val sorted = when(SortByPreference(context).sortBySelectSongFragment) {
            0 ->  songs.sortedBy { it.title }
            1 ->  songs.sortedBy { it.artist }
            2 -> songs.sortedBy { it.dateAdded }
            else -> songs
        }
        // sort by ascending or descending order
        return when(SortByPreference(context).ascDescSelectSongFragment) {
            0 -> sorted
            1 ->  sorted.reversed()
            else -> sorted
        }
    }

    fun checkPermission(activity: Activity): Boolean =
        app.checkReadWriteExternalStoragePermission(activity)
}