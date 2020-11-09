package velord.university.ui.fragment.folder

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.music.Song
import velord.university.model.entity.file.FileExtension
import velord.university.model.entity.file.FileFilter
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.RVSelection
import java.io.File

class FolderViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    val TAG = "FolderViewModel"

    lateinit var fileList: Array<Song>
    lateinit var ordered: List<Song>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<Song>

    lateinit var currentFile: File

    fun initViewModel() {
        currentFile = Environment.getExternalStorageDirectory()
    }

    fun sendIconToMiniPlayer(song: Song) =
        AppBroadcastHub.apply { app.iconUI(song.icon.toString()) }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences.getStoredQueryFolder(app, currentFile.path)

    fun onlyAudio(file: File = currentFile): Array<Song> =
        FileFilter
            .filterOnlyAudio(file)
            .map { Song(it) }
            .toTypedArray()

    fun playAllInFolder(value: Song) {
        //don't remember for SongPlaylistInteractor
        AppBroadcastHub.apply {
            app.playAllInFolderService(value.file.path)
            app.loopAllService()
        }
    }

    fun playAllInFolderNext(value: Song) {
        //add to queue
        AppBroadcastHub.apply {
            app.playNextAllInFolderService(value.file.path)
        }
    }

    fun shuffleAndPlayAllInFolder(value: Song) {
        AppBroadcastHub.apply {
            app.shuffleAndPlayAllInFolderService(value.file.path)
            app.loopAllService()
        }
    }

    fun playAudio(value: Song) {
        //don't remember for SongPlaylistInteractor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(value)


        AppBroadcastHub.apply {
            app.playByPathService(value.file.path)
            app.loopService()
        }
    }

    fun playAudioNext(value: Song) {
        //don't remember for SongQueryInteractor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(value)
        //add to queue one song
        AppBroadcastHub.apply {
            app.addToQueueService(value.file.path)
        }
    }

    fun storeCurrentFolderSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        val folderPath = currentFile.path
        SearchQueryPreferences.setStoredQueryFolder(app, folderPath, currentQuery)
        Log.d(TAG, "query: $currentQuery path: $folderPath")
    }

    fun playAudioFile(value: Song) {
        AppBroadcastHub.apply {
           SongPlaylistInteractor.songs = fileList
               .filter { FileExtension.isAudio(it.file.extension) }
               .toTypedArray()

           app.playByPathService(value.file.path)
       }
    }

    fun filterAndSortFiles(filter: FileFilter.TYPE = FileFilter.TYPE.SEARCH,
                           searchTerm: String = currentQuery
    ): Array<Song> {
        val filesInFolder = getFilesInCurrentFolder()
        //if you would see not compatible format
        //just remove or comment 2 lines bottom
        val compatibleFileFormat = filesInFolder.filter {
            when (filter) {
                FileFilter.TYPE.EMPTY_SEARCH ->
                    FileFilter.filterByEmptySearchQuery(it.file, searchTerm)
                FileFilter.TYPE.SEARCH ->
                    FileFilter.filterFileBySearchQuery(it.file, searchTerm)
            }
        }
        // sort by name or artist or date added
        val sortedFiles = when(SortByPreference(app).sortByFolderFragment) {
            0 ->  {
                compatibleFileFormat.sortedBy {  FileFilter.getName(it.file)  }
            }
            1 ->  {
                compatibleFileFormat.sortedBy { FileFilter.getArtist(it.file) }
            }
            2 ->  {
                compatibleFileFormat.sortedBy { FileFilter.getLastDateModified(it.file) }
            }
            else -> compatibleFileFormat
        }
        // sort by ascending or descending order
        ordered = when(SortByPreference(app).ascDescFolderFragment) {
            0 -> sortedFiles
            1 ->  sortedFiles.reversed()
            else -> sortedFiles
        }
        return ordered.toTypedArray()
    }

    fun isAudio(value: Song): Boolean =
        FileExtension.isAudio(value.file.extension)

    private fun getFilesInCurrentFolder(): Array<Song> {
        val path = currentFile.path
        val file = File(path)
        val filesInFolder: Array<File> = file.listFiles() ?: arrayOf()
        return filesInFolder
            .map {
                Song(it, "", DrawableIcon.getRandomFolderSongIconName)
            }
            .toTypedArray()

    }
}