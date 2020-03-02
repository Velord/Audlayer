package velord.university.ui.fragment.addToPlaylist

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import velord.university.application.settings.SortByPreference
import velord.university.model.FileFilter
import java.io.File

class SelectSongViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "AddSongViewModel"

    lateinit var fileList: Array<File>

    lateinit var currentQuery: String

    fun filterAndSortFiles(context: Context,
                           filter: (File, String) -> Boolean,
                           searchTerm: String)
            : Array<File> {
        //if you would see not compatible format
        //just remove or comment 2 lines bottom
        val compatibleFileFormat =
            fileList.filter { filter(it, searchTerm) }
        // sort by name or artist or date added
        val sortedFiles =
            when(SortByPreference.getNameArtistDateAddedSongAddFragment(context)) {
                0 ->  {
                    compatibleFileFormat.sortedBy {  FileFilter.orderByName(it)  }
                }
                1 ->  {
                    compatibleFileFormat.sortedBy { FileFilter.orderByArtist(it) }
                }
                2 ->  {
                    compatibleFileFormat.sortedBy { FileFilter.orderByDateAdded(it) }
                }
                else -> compatibleFileFormat
            }
        // sort by ascending or descending order
        return when(SortByPreference.getAscDescSongAddFragment(context)) {
            0 -> sortedFiles
            1 ->  sortedFiles.reversed()
            else -> sortedFiles
        }.toTypedArray()
    }
}