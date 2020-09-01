package velord.university.ui.fragment.song

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.Playlist
import velord.university.model.entity.Song
import velord.university.model.file.FileFilter
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.ui.util.RVSelection


class AllSongViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "SongViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    lateinit var allPlaylist: List<Playlist>
    lateinit var songs: List<Song>
    lateinit var ordered: List<Song>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<Song>

    init {
        scope.launch {
            retrieveSongsFromDb()
        }
    }

    private suspend fun retrieveSongsFromDb() =
        withContext(Dispatchers.IO) {
            allPlaylist = PlaylistTransaction.getAllPlaylist()
            Log.d(TAG, "all playlist retrieved")
            //unique songs
            songs = Playlist.allSongFromPlaylist(allPlaylist).map {
                Song(it)
            }
            Log.d(TAG, "all song retrieved")
        }

    suspend fun filterByQuery(query: String): List<Song> =
        withContext(Dispatchers.Default) {
            val filtered = songs.filter {
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
                2 -> filtered.sortedBy {
                    FileFilter.getLastDateModified(it.file)
                }
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
            }
            // sort by ascending or descending order
            val ascDescOrder = SortByPreference(app).ascDescAllSongFragment
            ordered = when(ascDescOrder) {
                0 -> sorted
                1 ->  sorted.reversed()
                else -> sorted
            }

            return@withContext ordered
        }

    fun sendIconToMiniPlayer(song: Song) =
        AppBroadcastHub.apply { app.iconUI(song.icon.toString()) }

    fun shuffle(): Array<Song> {
        ordered = ordered.shuffled()
        return ordered.toTypedArray()
    }

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences(app).storedQueryAllSong = currentQuery
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun songsIsInitialized() = ::songs.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences(app).storedQueryAllSong

    fun playAudioAndAllSong(song: Song) {
        SongPlaylistInteractor.songs = ordered
            .toTypedArray()

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
}
