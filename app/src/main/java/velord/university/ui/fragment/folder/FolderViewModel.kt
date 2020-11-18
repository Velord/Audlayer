package velord.university.ui.fragment.folder

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.service.hub.player.toAudlayerSong
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.fileType.directory.DirectoryResolver
import velord.university.model.entity.fileType.file.FileExtension
import velord.university.model.entity.fileType.file.FileFilter
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.model.entity.music.song.main.AudlayerSong.Companion.filterByQuery
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.RVSelection
import java.io.File

class FolderViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    val TAG = "FolderViewModel"

    lateinit var songList: Array<AudlayerSong>
    lateinit var ordered: List<AudlayerSong>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<AudlayerSong>

    val directory: DirectoryResolver = DirectoryResolver()

    val mediaMetadataRetriever = MediaMetadataRetriever()

    fun sendIconToMiniPlayer(song: AudlayerSong) =
        AppBroadcastHub.apply { app.iconUI(song.imgUrl) }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences.getStoredQueryFolder(app, directory.getPath())

    fun onlyAudio(file: File = directory.getDirectory()): Array<AudlayerSong> =
        FileFilter.filterOnlyAudio(file)
            .map { it.toAudlayerSong(mediaMetadataRetriever) }
            .toTypedArray()

    fun playAllInFolder(value: AudlayerSong) {
        //don't remember for SongPlaylistInteractor
        AppBroadcastHub.apply {
            app.playAllInFolderService(value.path)
            app.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
        }
    }

    fun playAllInFolderNext(value: AudlayerSong) {
        //add to queue
        AppBroadcastHub.apply {
            app.playNextAllInFolderService(value.path)
        }
    }

    fun shuffleAndPlayAllInFolder(value: AudlayerSong) {
        AppBroadcastHub.apply {
            app.shuffleAndPlayAllInFolderService(value.path)
            app.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
        }
    }

    fun playAudio(value: AudlayerSong) {
        //don't remember for SongPlaylistInteractor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(value)

        AppBroadcastHub.apply {
            app.playByPathService(value.path)
            app.doAction(BroadcastActionType.LOOP_PLAYER_SERVICE)
        }
    }

    fun playAudioNext(value: AudlayerSong) {
        //don't remember for SongQueryInteractor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(value)
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

    fun playAudioFile(value: AudlayerSong) {
        AppBroadcastHub.apply {
           SongPlaylistInteractor.songs = songList

           app.playByPathService(value.path)
       }
    }

    fun filterAndSortFiles(searchTerm: String = currentQuery): Array<AudlayerSong> {
        val filesInFolder = getSongListInCurrentFolder()
        //if you would see not compatible format
        //just remove or comment 2 lines bottom
        val compatibleFileFormat = filesInFolder.filterByQuery(searchTerm)
        // sort by name or artist or date added
        val sortedFiles = when(SortByPreference(app).sortByFolderFragment) {
            //title
            0 -> compatibleFileFormat.sortedBy {  it.title }
            //artist
            1 -> compatibleFileFormat.sortedBy { it.artist }
            //last date modified
            2 -> compatibleFileFormat.sortedBy { it.dateAdded }
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

    fun isAudio(value: AudlayerSong): Boolean =
        FileExtension.isAudio(File(value.path).extension)

    private fun getSongListInCurrentFolder(): Array<AudlayerSong> =
        directory.getFilesInDirectory()
            .map { it.toAudlayerSong(mediaMetadataRetriever) }
            .toTypedArray()
}