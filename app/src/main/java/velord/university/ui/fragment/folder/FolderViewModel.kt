package velord.university.ui.fragment.folder

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import velord.university.application.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import java.io.File

class FolderViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "FolderViewModel"

    lateinit var fileList: Array<File>

    lateinit var currentQuery: String

    var currentFolder: File

    init {
        currentFolder = Environment.getExternalStorageDirectory()
    }

    fun storeCurrentFolderSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        val folderPath = currentFolder.path
        SearchQueryPreferences.setStoredQueryFolder(app, folderPath, currentQuery)
        Log.d(TAG, "query: $currentQuery path: $folderPath")
    }

    fun getFilesInCurrentFolder(): Array<File> {
        val path = currentFolder.path
        val file = File(path)
        val filesInFolder = file.listFiles()
        return filesInFolder ?: arrayOf()
    }

    fun playAudioFile(file: File) {
       MiniPlayerBroadcastPlayByPath.apply {
           SongPlaylistInteractor.songs = fileList
           app.sendBroadcastPlayByPath(file.path)
       }
    }

    fun filterAndSortFiles(context: Context,
                           filter: (File, String) -> Boolean,
                           searchTerm: String): Array<File> {
        val filesInFolder = getFilesInCurrentFolder()
        //if you would see not compatible format
        //just remove or comment 2 lines bottom
        val compatibleFileFormat =
            filesInFolder.filter { filter(it, searchTerm) }
        // sort by name or artist or date added
        val sortedFiles = when(SortByPreference.getNameArtistDateAddedFolderFragment(context)) {
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
        return when(SortByPreference.getAscDescFolderFragment(context)) {
            0 -> sortedFiles
            1 ->  sortedFiles.reversed()
            else -> sortedFiles
        }.toTypedArray()
    }
}