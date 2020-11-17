package velord.university.ui.fragment.album

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.song.Song
import velord.university.model.entity.fileType.file.FileFilter
import velord.university.model.entity.music.newGeneration.playlist.Playlist
import velord.university.repository.hub.FolderRepository
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.hub.DB
import java.io.File

const val MAX_LAST_PLAYED: Int = 50
const val MAX_MOST_PLAYED: Int = 50

class AlbumViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val TAG = "AlbumViewModel"

    lateinit var currentQuery: String

    private lateinit var playlist: List<Playlist>
    private lateinit var lastPlayed: Playlist
    private lateinit var favourite: Playlist
    private lateinit var other: List<Playlist>

    fun getSearchQuery(): String =
        SearchQueryPreferences(app).storedQueryAlbum

    fun playlistIsInitialized(): Boolean = ::playlist.isInitialized

    fun playSongs(songs: Array<String>) {
        if (songs.isNotEmpty()) {
            //don't remember for SongPlaylistInteractor
            SongPlaylistInteractor.songs = songs
                .map { Song(File(it)) }
                .toTypedArray()

            AppBroadcastHub.apply {
                app.playByPathService(songs[0])
            }
            AppBroadcastHub.apply {
                app.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
            }
        }
        else Toast.makeText(app,
            "Playlist is empty", Toast.LENGTH_SHORT).show()
    }

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences(app).storedQueryAlbum = currentQuery
        val check = SearchQueryPreferences(app).storedQueryAlbum
        Log.d(TAG, "retrieved: $check")
        Log.d(TAG, "stored: $currentQuery")
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        if (playlist.isDefault()) return
        //update db
        DB.playlistTransaction("deletePlaylist") {
            deletePlaylistById(playlist.id)
        }
        //refresh playlist
        getDefaultAndUserPlaylist()
    }

    suspend fun retrievePlaylistFromDb() = onIO {
        playlist = getDefaultAndUserPlaylist()
        Log.d(TAG, "all playlist collected")
    }

    private suspend fun getDefaultAndUserPlaylist(): List<Playlist> {
        val allPlaylist =  PlaylistTransaction.getAllPlaylist()
        Log.d(TAG, "all playlist retrieved")
        Log.d(TAG, "all song retrieved")
        //1 month TODO()
        Log.d(TAG, "last modified playlist retrieved")
        //lastPlayed
        val played = PlaylistTransaction.getPlayed()
        //last 50
        lastPlayed = played.take(MAX_LAST_PLAYED)
        Log.d(TAG, "last play playlist retrieved")
        //most played TODO()
        Log.d(TAG, "most played playlist retrieved")
        //favourite
        favourite =  PlaylistTransaction.getFavourite()
        Log.d(TAG, "favourite playlist retrieved")
        //downloaded TODO()
        Log.d(TAG, "downloaded playlist retrieved")
        //other
        other = Playlist.other(allPlaylist)
        Log.d(TAG, "other playlist retrieved")

        return listOf()
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
    }
}
