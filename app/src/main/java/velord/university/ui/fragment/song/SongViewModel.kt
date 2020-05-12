package velord.university.ui.fragment.song

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.entity.Playlist
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.ui.util.RvSelectionOld
import java.io.File


class SongViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "SongViewModel"

    lateinit var allPlaylist: List<Playlist>
    lateinit var songs: List<File>
    lateinit var ordered: List<File>

    lateinit var currentQuery: String

    lateinit var rvResolver: RvSelectionOld<String>

    suspend fun retrieveSongsFromDb() = withContext(Dispatchers.IO) {
        allPlaylist = PlaylistTransaction.getAllPlaylist()
        Log.d(TAG, "all playlist retrieved")
        //unique songs
        songs = Playlist.allSongFromPlaylist(allPlaylist)
        Log.d(TAG, "all song retrieved")
    }

    suspend fun filterByQuery(query: String): List<File> = withContext(Dispatchers.Default) {
        val filtered = songs.filter {
            FileFilter.filterFileBySearchQuery(it, query)
        }
        //sort by name or artist or date added or duration or size
        val sorted = when(SortByPreference.getSortBySongFragment(app)) {
            //name
            0 -> filtered.sortedBy {
                FileFilter.getName(it)
            }
            //artist
            1 -> filtered.sortedBy {
                FileFilter.getArtist(it)
            }
            //date added
            2 -> filtered.sortedBy {
                FileFilter.getLastDateModified(it)
            }
            //duration TODO()
            3 -> {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                filtered.sortedBy {
                    mediaMetadataRetriever.setDataSource(it.absolutePath)
                    val durationStr = mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    durationStr.toLong()
                }
            }
            //file size
            4 -> filtered.sortedBy { FileFilter.getSize(it) }
            else -> filtered
        }
        // sort by ascending or descending order
        ordered = when(SortByPreference.getAscDescSongFragment(app)) {
            0 -> sorted
            1 ->  sorted.reversed()
            else -> sorted
        }

        return@withContext ordered
    }

    fun shuffle(): List<File> = ordered.shuffled()

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQuerySong(app, currentQuery)
        val check = SearchQueryPreferences.getStoredQuerySong(app)
        Log.d(TAG, "retrieved: $check")
        Log.d(TAG, "stored: $currentQuery")
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun songsIsInitialized() = ::songs.isInitialized

    fun getSearchQuery(): String = SearchQueryPreferences.getStoredQuerySong(app)

    fun playAudioAndAllSong(file: File) {
        AppBroadcastHub.apply {
            SongPlaylistInteractor.songs = ordered.toTypedArray()
            app.playByPathService(file.path)
        }
        AppBroadcastHub.apply {
            app.loopAllService()
        }
    }

    fun playAudio(file: File) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        AppBroadcastHub.apply {
            app.playByPathService(file.path)
        }
        AppBroadcastHub.apply {
            app.loopService()
        }
    }

    fun playAudioNext(file: File) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        //add to queue one song
        AppBroadcastHub.apply {
            app.addToQueueService(file.path)
        }
    }
}
