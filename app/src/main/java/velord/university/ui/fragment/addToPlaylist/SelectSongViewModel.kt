package velord.university.ui.fragment.addToPlaylist

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import velord.university.application.permission.PermissionChecker
import velord.university.application.settings.SortByPreference
import velord.university.model.file.FileFilter
import java.io.File

class SelectSongViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "AddSongViewModel"

    lateinit var fileList: Array<File>

    val checked = mutableListOf<String>()

    lateinit var currentQuery: String

    fun filterAndSortFiles(context: Context,
                           filter: (File, String) -> Boolean,
                           searchTerm: String): Array<File> {
        val songs =
            fileList.filter { filter(it, searchTerm) }
        // sort by name or artist or date added
        val sorted = when(SortByPreference(context).sortBySelectSongFragment) {
            0 ->  {
                songs.sortedBy {  FileFilter.getName(it)  }
            }
            1 ->  {
                songs.sortedBy { FileFilter.getArtist(it) }
            }
            2 ->  {
                songs.sortedBy { FileFilter.getLastDateModified(it) }
            }
            else -> songs
        }
        // sort by ascending or descending order
        return when(SortByPreference(context).ascDescSelectSongFragment) {
            0 -> sorted
            1 ->  sorted.reversed()
            else -> sorted
        }.toTypedArray()
    }

    fun checkPermission(activity: Activity): Boolean =
        PermissionChecker
            .checkReadWriteExternalStoragePermission(app, activity)
}