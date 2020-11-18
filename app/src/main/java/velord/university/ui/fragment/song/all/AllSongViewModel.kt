package velord.university.ui.fragment.song.all

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.coroutine.onDef
import velord.university.model.entity.fileType.file.FileRetrieverConverter
import velord.university.model.entity.fileType.file.FileRetrieverConverter.getSize
import velord.university.model.entity.fileType.json.general.Loadable
import velord.university.model.entity.music.playlist.Playlist
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.model.entity.music.song.main.AudlayerSong.Companion.filterByQuery
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.ui.util.RVSelection
import java.io.File

class AllSongViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val TAG = "AllSongViewModel"

    val allPlaylist: Loadable<List<Playlist>> = Loadable {
        Log.d(TAG, "get all playlist retrieved")
        PlaylistTransaction.getAllPlaylist()
    }
    val allSong: Loadable<List<AudlayerSong>> = Loadable {
        val songList = mutableListOf<AudlayerSong>()
        allPlaylist.get().map { songList.addAll(it.songList) }
        songList
    }

    lateinit var ordered: Array<AudlayerSong>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<AudlayerSong>

    suspend fun filterByQuery(query: String): Array<AudlayerSong> = onDef {
        val filtered = allSong.get().filterByQuery(query)
        //sort by name or artist or date added or duration or size
        val sortByOrder = SortByPreference(app).sortByAllSongFragment
        val sorted = when(sortByOrder) {
            //title
            0 -> filtered.sortedBy { it.title }
            //artist
            1 -> filtered.sortedBy { it.artist }
            //date added
            2 -> filtered.sortedBy { it.dateAdded }
            //duration
            3 -> filtered.sortedBy { it.duration }
            //file size
            4 -> filtered.sortedBy { File(it.path).getSize() }
            else -> filtered
        }.toTypedArray()
        // sort by ascending or descending order
        val ascDescOrder = SortByPreference(app).ascDescAllSongFragment
        ordered = when(ascDescOrder) {
            0 -> sorted
            1 ->  sorted.reversed().toTypedArray()
            else -> sorted
        }

        return@onDef ordered
    }

    fun sendIconToMiniPlayer(song: AudlayerSong) =
        AppBroadcastHub.apply { app.iconUI(song.imgUrl) }

    fun shuffle(): Array<AudlayerSong> {
        ordered.shuffle()
        return ordered
    }

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences(app).storedQueryAllSong = currentQuery
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences(app).storedQueryAllSong

    fun playAudioAndAllSong(song: AudlayerSong) {
        SongPlaylistInteractor.songList = ordered.toList()

        AppBroadcastHub.apply {
            app.apply {
                doAction(BroadcastActionType.SHOW_PLAYER_UI)
                playByPathService(song.path)
                doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
            }
            sendIconToMiniPlayer(song)
        }
    }

    fun playAudio(song: AudlayerSong) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songList = listOf(song)
        AppBroadcastHub.apply {
            app.apply {
                doAction(BroadcastActionType.SHOW_PLAYER_UI)
                playByPathService(song.path)
                doAction(BroadcastActionType.LOOP_PLAYER_SERVICE)
            }
            sendIconToMiniPlayer(song)
        }
    }

    fun playAudioNext(song: AudlayerSong) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songList = listOf(song)
        //add to queue one song
        AppBroadcastHub.apply {
            app.addToQueueService(song.path)
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
    }
}
