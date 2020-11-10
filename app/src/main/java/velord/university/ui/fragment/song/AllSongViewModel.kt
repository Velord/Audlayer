package velord.university.ui.fragment.song

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.coroutine.onDef
import velord.university.model.entity.music.playlist.Playlist
import velord.university.model.entity.music.song.Song
import velord.university.model.entity.fileType.file.FileFilter
import velord.university.model.entity.fileType.json.general.Loadable
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.ui.util.RVSelection

class AllSongViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val TAG = "AllSongViewModel"

    val allPlaylist: Loadable<Array<Playlist>> = Loadable {
        Log.d(TAG, "get all playlist retrieved")
        PlaylistTransaction.getAllPlaylist().toTypedArray()
    }
    val allSong: Loadable<Array<Song>> = Loadable {
        Log.d(TAG, "get all song retrieved")
        Playlist.allSongFromPlaylist(allPlaylist.get().toList()).map {
            Song(it)
        }.toTypedArray()
    }
    lateinit var ordered: Array<Song>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<Song>

    suspend fun filterByQuery(query: String): Array<Song> = onDef {
        val filtered = allSong.get().filter {
            FileFilter.filterFileBySearchQuery(it.file, query)
        }
        //sort by name or artist or date added or duration or size
        val sortByOrder = SortByPreference(app).sortByAllSongFragment
        val sorted = when(sortByOrder) {
            //name
            0 -> filtered.sortedBy {
                FileFilter.getName(it.file)
            }
            //artist
            1 -> filtered.sortedBy {
                FileFilter.getArtist(it.file)
            }
            //date added
            //todo()
            2 -> filtered
            //duration TODO()
            3 -> {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                filtered.sortedBy {
                    mediaMetadataRetriever
                        .setDataSource(it.file.absolutePath)

                    val durationStr = mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                    durationStr.toLong()
                }
            }
            //file size
            4 -> filtered.sortedBy { FileFilter.getSize(it.file) }
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

    fun sendIconToMiniPlayer(song: Song) =
        AppBroadcastHub.apply { app.iconUI(song.icon.toString()) }

    fun shuffle(): Array<Song> {
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

    fun playAudioAndAllSong(song: Song) {
        SongPlaylistInteractor.songs = ordered

        AppBroadcastHub.apply {
            app.showGeneralUI()
            app.playByPathService(song.file.path)
            app.loopAllService()
            sendIconToMiniPlayer(song)
        }
    }

    fun playAudio(song: Song) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(song)
        AppBroadcastHub.apply {
            app.showGeneralUI()
            app.playByPathService(song.file.path)
            sendIconToMiniPlayer(song)
            app.loopService()
        }
    }

    fun playAudioNext(song: Song) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(song)
        //add to queue one song
        AppBroadcastHub.apply {
            app.addToQueueService(song.file.path)
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
    }
}
