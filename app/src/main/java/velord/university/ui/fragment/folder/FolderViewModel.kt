package velord.university.ui.fragment.folder

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.fileType.directory.DirectoryResolver
import velord.university.model.entity.fileType.file.FileExtension
import velord.university.model.entity.fileType.file.FileRetrieverConverter
import velord.university.model.entity.fileType.file.FileRetrieverConverter.filterBySearchQuery
import velord.university.model.entity.fileType.file.FileRetrieverConverter.getArtist
import velord.university.model.entity.fileType.file.FileRetrieverConverter.getLastDateModified
import velord.university.model.entity.fileType.file.FileRetrieverConverter.getTitle
import velord.university.model.entity.fileType.file.FileRetrieverConverter.isAudio
import velord.university.model.entity.fileType.file.FileRetrieverConverter.toAudlayer
import velord.university.model.entity.fileType.file.FileRetrieverConverter.toAudlayerSong
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.ui.util.RVSelection
import java.io.File

class FolderViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    val TAG = "FolderViewModel"

    lateinit var fileList: Array<File>
    lateinit var ordered: List<File>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<File>

    val directory: DirectoryResolver = DirectoryResolver()

    val mediaRetriever = MediaMetadataRetriever()

    fun sendIconToMiniPlayer(value: File) = AppBroadcastHub.apply {
        app.iconUI(value.toAudlayerSong(mediaRetriever).imgUrl)
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences.getStoredQueryFolder(app, directory.getPath())

    fun onlyAudio(file: File = directory.getDirectory()): List<File> =
        FileRetrieverConverter.filterOnlyAudio(file)

    fun playAllInFolder(value: File) {
        //don't remember for SongPlaylistInteractor
        AppBroadcastHub.apply {
            app.playAllInFolderService(value.path)
            app.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
        }
    }

    fun playAllInFolderNext(value: File) {
        //add to queue
        AppBroadcastHub.apply {
            app.playNextAllInFolderService(value.path)
        }
    }

    fun shuffleAndPlayAllInFolder(value: File) {
        AppBroadcastHub.apply {
            app.shuffleAndPlayAllInFolderService(value.path)
            app.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
        }
    }

    fun playAudio(value: File) {
        //don't remember for SongPlaylistInteractor it will be used between this and service
        SongPlaylistInteractor.songList = listOf(value.toAudlayerSong(mediaRetriever))

        AppBroadcastHub.apply {
            app.playByPathService(value.path)
            app.doAction(BroadcastActionType.LOOP_PLAYER_SERVICE)
        }
    }

    fun playAudioNext(value: File) {
        //don't remember for SongQueryInteractor it will be used between this and service
        SongPlaylistInteractor.songList = listOf(value.toAudlayerSong(mediaRetriever))
        //add to queue one song
        AppBroadcastHub.apply {
            app.addToQueueService(value.path)
        }
    }

    fun storeCurrentFolderSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        val folderPath = directory.getPath()
        SearchQueryPreferences.setStoredQueryFolder(app, folderPath, currentQuery)
        Log.d(TAG, "query: $currentQuery path: $folderPath")
    }

    fun playAudioFile(value: File) {
        AppBroadcastHub.apply {
           SongPlaylistInteractor.songList = fileList.map {
               it.toAudlayerSong(mediaRetriever)
           }

           app.playByPathService(value.path)
       }
    }

    fun filterAndSortFiles(searchTerm: String = currentQuery): Array<File> {
        val filesInFolder = getFilesInFolder()
        //if you would see not compatible format
        //just remove or comment 2 lines bottom
        val compatibleFileFormat = filesInFolder.filter {
            it.filterBySearchQuery(searchTerm)
        }
        // sort by name or artist or date added
        val sortedFiles = when(SortByPreference(app).sortByFolderFragment) {
            //title
            0 -> compatibleFileFormat.sortedBy {  it.getTitle() }
            //artist
            1 -> compatibleFileFormat.sortedBy { it.getArtist() }
            //last date modified
            2 -> compatibleFileFormat.sortedBy { it.getLastDateModified() }
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

    fun isAudio(value: File): Boolean = value.isAudio()

    private fun getFilesInFolder(): Array<File> = directory.getFilesInDirectory()

    fun toAudlayer(list: List<File>): List<AudlayerSong> = list.toAudlayer(mediaRetriever)
}