package velord.university.ui.fragment.vk

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import velord.university.model.coroutine.onDef
import kotlinx.coroutines.*
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.application.settings.VkPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.music.song.Song
import velord.university.model.entity.fileType.file.FileFilter
import velord.university.model.entity.fileType.json.general.Loadable
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.newGeneration.song.AudlayerSong.Companion.filterByQuery
import velord.university.model.entity.music.song.download.DownloadSong
import velord.university.repository.hub.FolderRepository
import velord.university.repository.db.transaction.hub.HubTransaction
import velord.university.repository.hub.HubRepository.vkRepository
import velord.university.ui.util.RVSelection
import java.io.File

class VkViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val TAG = "VkViewModel"

    var songList: Loadable<Array<AudlayerSong>> = Loadable {
        HubTransaction.songTransaction("vkSongList") {
            getAll().toTypedArray()
        }
    }

    lateinit var ordered: Array<AudlayerSong>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<AudlayerSong>

    fun orderedIsInitialized() = ::ordered.isInitialized

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences(app).storedQueryVk = currentQuery
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences(app).storedQueryVk

    fun playAudioNext(song: AudlayerSong) {
        //don't remember for SongQuery Interactor it will be used between this and service
        addToInteractor()
        //add to queue one song
        AppBroadcastHub.apply {
            app.addToQueueService(song.path)
        }
    }

    //path must be not blank and file can be created by that path
    fun needDownload(vkSong: AudlayerSong): Boolean =
        (vkSong.path.isBlank()) and (File(vkSong.path).exists().not())

    fun playAudioAndAllSong(song: AudlayerSong) {
        viewModelScope.launch {
            addToInteractor()

            AppBroadcastHub.apply {
                app.playByPathService(song.path)
                app.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
                //sendIcon
                sendIconToMiniPlayer(song)
            }
        }
    }

    fun shuffle(): Array<AudlayerSong> {
        ordered.shuffle()
        return ordered
    }

    fun sendIconToMiniPlayer(song: AudlayerSong) {
        //toDO()
    }

    fun checkAuth(): Boolean {
        val token = VkPreference(app).accessToken
        return (token.isNotEmpty())
    }

    fun logout() {
        viewModelScope.launch { deleteAll() }
        VkPreference(app).accessToken = ""
    }

    suspend fun needDownloadList(): Array<DownloadSong> =
        songList.get()
            .filter { needDownload(it) }
            .reversed()
            .map { it.toDownloadSong() }
            .toTypedArray()

    suspend fun pathIsWrong(path: String) {
        songList.get().find { it.path == path }?.let {
            applyNewPath(it, "")
        }
    }

    suspend fun deleteSong(vkSong: AudlayerSong) {
        FolderRepository.getApplicationVkDir()
            .listFiles()?.find { it.path == vkSong.path }.let {
                if (it != null) {
                    applyNewPath(vkSong, "")
                    it.delete()
                }
                else withContext(Dispatchers.Main) {
                    Toast.makeText(app, "Can't delete", Toast.LENGTH_SHORT).show()
                }
        }
    }

    suspend fun refreshByCredential() = onDef {
        try {
            app.vkRepository {
                refreshPlaylistViaCredential(it)
            }
            refresh()
        }
        catch(e: Exception) {
            Log.d(TAG, e.message.toString())
        }
    }

    private suspend fun load() { songList.load() }

    suspend fun deleteAll() {
        //todo()
        songList = Loadable { arrayOf() }
        load()
    }

    suspend fun filterByQuery(query: String): Array<AudlayerSong> = onDef {
        val filtered: List<AudlayerSong> = songList
            .get()
            .filterByQuery(query)
        //sort by name or artist or date added or duration or size
        val sortBy = SortByPreference(app).sortByVkFragment
        val sorted: List<AudlayerSong> = when(sortBy) {
            //name
            0 -> filtered.sortedBy {
                FileFilter.getName(File(it.path))
            }
            //artist
            1 -> filtered.sortedBy {
                FileFilter.getArtist(File(it.path))
            }
            //date added
            2 -> filtered.sortedBy {
                it.dateAdded
            }
            3 -> filtered.sortedBy { it.duration }
            //file size
            4 -> filtered.sortedBy { FileFilter.getSize(File(it.path)) }
            else -> filtered
        }
        // sort by ascending or descending order
        val ascDescOrder = SortByPreference(app).ascDescVkFragment
        ordered = when(ascDescOrder) {
            0 -> sorted
            1 ->  sorted.reversed()
            else -> sorted
        }.toTypedArray()

        ordered
    }

    private fun addToInteractor() {
        SongPlaylistInteractor.songs = ordered
            .filter { it.path.isNotBlank() }
            .map {
                Song(File(it.path), "")
            }
            .toTypedArray()
    }

    suspend fun applyNewPath(downloaded: DownloadSong) {
        val find = songList.get().find {
            it.artist == downloaded.artist &&
                    it.title == downloaded.title
        }
        if (find == null) return

        applyNewPath(find, downloaded.path)
    }

    private suspend fun applyNewPath(vkSong: AudlayerSong,
                                     path: String) {
        val index = songList.get().indexOf(vkSong)
        val song = songList.get()[index]
        HubTransaction.songTransaction("applyNewPath") {
            update(song.getWithNewPath(path))
        }

        refresh()
    }

    private suspend fun refresh() {
        songList.load()
        filterByQuery(currentQuery)
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
    }
}