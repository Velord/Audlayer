package velord.university.ui.fragment.folder

import android.app.Activity
import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import velord.university.application.broadcast.*
import velord.university.application.permission.PermissionChecker
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.ui.util.RecyclerViewSelectItemResolver
import java.io.File

class FolderViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "FolderViewModel"

    lateinit var fileList: Array<File>

    lateinit var currentQuery: String

    lateinit var rvResolver: RecyclerViewSelectItemResolver<String>

    var currentFolder: File = Environment.getExternalStorageDirectory()

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences.getStoredQueryFolder(app, currentFolder.path)

    fun onlyAudio(file: File): Array<File> =
        FileFilter.filterOnlyAudio(file).toTypedArray()

    fun playAllInFolder(file: File) {
        //don't remember for SongPlaylistInteractor
        MiniPlayerBroadcastPlayAllInFolder.apply {
            app.sendBroadcastPlayAllInFolder(file.path)
        }
        MiniPlayerBroadcastLoopAll.apply {
            app.sendBroadcastLoopAll()
        }
    }

    fun playAllInFolderNext(file: File) {
        //add to queue
        MiniPlayerBroadcastPlayNextAllInFolder.apply {
            app.sendBroadcastPlayNextAllInFolder(file.path)
        }
    }

    fun shuffleAndPlayAllInFolder(file: File) {
        MiniPlayerBroadcastShuffleAndPlayAllInFolder.apply {
            app.sendBroadcastShuffleAndPlayAllInFolder(file.path)
        }
        MiniPlayerBroadcastLoopAll.apply {
            app.sendBroadcastLoopAll()
        }
    }

    fun playAudio(file: File) {
        //don't remember for SongPlaylistInteractor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        MiniPlayerBroadcastPlayByPath.apply {
            app.sendBroadcastPlayByPath(file.path)
        }
        MiniPlayerBroadcastLoop.apply {
            app.sendBroadcastLoop()
        }
    }

    fun playAudioNext(file: File) {
        //don't remember for SongQueryInteractor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        //add to queue one song
        MiniPlayerBroadcastAddToQueue.apply {
            app.sendBroadcastAddToQueue(file.path)
        }
    }

    fun storeCurrentFolderSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        val folderPath = currentFolder.path
        SearchQueryPreferences.setStoredQueryFolder(app, folderPath, currentQuery)
        Log.d(TAG, "query: $currentQuery path: $folderPath")
    }

    fun playAudioFile(file: File) {
       MiniPlayerBroadcastPlayByPath.apply {
           SongPlaylistInteractor.songs = fileList
           app.sendBroadcastPlayByPath(file.path)
       }
    }

    fun filterAndSortFiles(filter: (File, String) -> Boolean,
                           searchTerm: String): Array<File> {
        val filesInFolder = getFilesInCurrentFolder()
        //if you would see not compatible format
        //just remove or comment 2 lines bottom
        val compatibleFileFormat =
            filesInFolder.filter { filter(it, searchTerm) }
        // sort by name or artist or date added
        val sortedFiles = when(SortByPreference.getSortByFolderFragment(app)) {
            0 ->  {
                compatibleFileFormat.sortedBy {  FileFilter.getName(it)  }
            }
            1 ->  {
                compatibleFileFormat.sortedBy { FileFilter.getArtist(it) }
            }
            2 ->  {
                compatibleFileFormat.sortedBy { FileFilter.getLastDateModified(it) }
            }
            else -> compatibleFileFormat
        }
        // sort by ascending or descending order
        return when(SortByPreference.getAscDescFolderFragment(app)) {
            0 -> sortedFiles
            1 ->  sortedFiles.reversed()
            else -> sortedFiles
        }.toTypedArray()
    }

    fun checkPermission(activity: Activity): Boolean =
        PermissionChecker
            .checkThenRequestReadWriteExternalStoragePermission(app, activity)

    private fun getFilesInCurrentFolder(): Array<File> {
        val path = currentFolder.path
        val file = File(path)
        val filesInFolder = file.listFiles()
        return filesInFolder ?: arrayOf()
    }
}