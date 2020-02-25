package velord.university.ui.fragment.folder

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import velord.university.application.QueryPreferences
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.interactor.SongQueryInteractor
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
        QueryPreferences.setStoredQueryFolder(app, folderPath, currentQuery)
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
           SongQueryInteractor.songs = fileList
           app.sendBroadcastPlayByPath(file.path)
       }
    }
}