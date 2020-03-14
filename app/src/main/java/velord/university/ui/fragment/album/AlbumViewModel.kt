package velord.university.ui.fragment.album

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import velord.university.application.AudlayerApp
import velord.university.application.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.entity.Album
import velord.university.model.entity.Playlist
import java.io.File

const val MAX_LAST_PLAYED: Int = 50
const val MAX_MOST_PLAYED: Int = 50

class AlbumViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "AlbumViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    lateinit var currentQuery: String

    private lateinit var playlist: List<Playlist>
    private lateinit var recentlyModified: List<String>
    private lateinit var lastPlayed: List<String>
    private lateinit var mostPlayed: List<String>
    private lateinit var favourite: List<String>
    private lateinit var other: List<Playlist>

    private lateinit var allSongRemovedDuplicate: List<File>

    lateinit var albums: List<Album>

    fun getSearchQuery(): String =
        SearchQueryPreferences.getStoredQueryAlbum(app)

    fun albumsIsInitialized(): Boolean = ::albums.isInitialized

    fun playlistIsInitialized(): Boolean = ::playlist.isInitialized

    fun filterByQueryPlaylist(query: String): List<Playlist> {
        val newOther = other.filter { it.name.contains(query) }

        // sort by album or artist or year or number of tracks
        val sortedPlaylist = when(SortByPreference.getSortByAlbumFragment(app)) {
            //album TODO()
            0 -> newOther
            //artist
            1 -> newOther.sortedBy {
                FileFilter.getArtist(File(it.songs[0]))
            }
            //year TODO()
            2 -> newOther
            //number of tracks
            3 -> newOther.sortedBy { it.songs.size }
            else -> newOther
        }
        // sort by ascending or descending order
        val orderedPlaylist = when(SortByPreference.getAscDescAlbumFragment(app)) {
            0 -> sortedPlaylist
            1 ->  sortedPlaylist.reversed()
            else -> sortedPlaylist
        }

        return collectPlaylist(
            recentlyModified,
            lastPlayed,
            mostPlayed,
            favourite,
            orderedPlaylist
        )
    }

    fun playSongs(songs: Array<String>) {
        //don't remember for SongPlaylistInteractor
        SongPlaylistInteractor.songs =
            songs.map { File(it) }.toTypedArray()
        MiniPlayerBroadcastPlayByPath.apply {
            app.sendBroadcastPlayByPath(songs[0])
        }
    }

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQueryAlbum(app, currentQuery)
        val check = SearchQueryPreferences.getStoredQueryAlbum(app)
        Log.d(TAG, "retrieved: $check")
        Log.d(TAG, "stored: $currentQuery")
    }

    suspend fun retrievePlaylistFromDb() = withContext(Dispatchers.IO) {
        playlist = getDefaultAndUserPlaylist()
        Log.d(TAG, "all playlist collected")
    }

    suspend fun retrieveAlbumFromDb() = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            albums = albumDao().getAll()
        }
        Log.d(TAG, "all albums collected")
    }

    suspend fun refreshAllAlbum() = withContext(Dispatchers.IO) {
        albums = getAlbumBasedOnAllSong()
        scope.launch { saveAlbum(albums) }
        Log.d(TAG, "all album collected")
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        AudlayerApp.db?.let {
            it.playlistDao().deletePlaylistByName(playlist.name)
            //refresh playlist
            getDefaultAndUserPlaylist()
        }
    }

    private fun collectPlaylist(recentlyModified: List<String>,
                                lastPlayed: List<String>,
                                mostPlayed: List<String>,
                                favourite: List<String>,
                                otherPlaylist: List<Playlist>): List<Playlist> {
        return listOf(
            Playlist("Recently Modified", recentlyModified),
            Playlist("Last Played", lastPlayed),
            Playlist("Most Played", mostPlayed),
            Playlist("Favourite", favourite),
            *otherPlaylist.map {
                Playlist(it.name, it.songs)
            }.toTypedArray()
        )
    }

    private fun otherPlaylist(playlist: List<Playlist>): List<Playlist> =
        playlist.filter {
            it.name != "Favourite" && it.name != "Played"
        }

    private suspend fun getFavourite(): List<String> = withContext(Dispatchers.IO) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getByName("Favourite").songs.reversed().filter { it.isNotEmpty() }
        }
    } ?: listOf()

    private suspend fun getPlayed(): List<String> = withContext(Dispatchers.IO) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getByName("Played").songs.reversed().filter { it.isNotEmpty() }
        }
    } ?: listOf()

    private fun allSongFromPlaylist(playlist: List<Playlist>): List<File> =
        playlist.asSequence()
            .map { it.songs }
            .fold(mutableListOf<String>()) { joined, fromDB ->
                joined.addAll(fromDB)
                joined
            }
            .distinct()
            .map { File(it) }
            .filter { it.path.isNotEmpty() }.toList()

    private suspend fun getAllPlaylist(): List<Playlist> = withContext(Dispatchers.IO) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getAll()
        }
    } ?: listOf()

    private suspend fun getDefaultAndUserPlaylist(): List<Playlist> {
        val allPlaylist = getAllPlaylist()
        Log.d(TAG, "all playlist retrieved")
        //unique songs
        allSongRemovedDuplicate = allSongFromPlaylist(allPlaylist)
        Log.d(TAG, "all song retrieved")
        //1 month
        recentlyModified =
            FileFilter.recentlyModified(allSongRemovedDuplicate)
                .map { it.path }
        Log.d(TAG, "last modified playlist retrieved")
        //lastPlayed
        val played = getPlayed()
        //last 50
        lastPlayed = played.take(MAX_LAST_PLAYED)
        Log.d(TAG, "last play playlist retrieved")
        //most played
        mostPlayed = played
            .fold(HashMap<String, Int>()) { mostPlayed, song ->
                if (song.isNotEmpty()) {
                    mostPlayed += if (mostPlayed.containsKey(song).not())
                        Pair(song, 1)
                    else {
                        val count = mostPlayed[song]
                        Pair(song, count!!.plus(1))
                    }
                }
                mostPlayed
            }
            .toList()
            .sortedBy { it.second }
            .reversed()
            .map { it.first }
            .take(MAX_MOST_PLAYED)
        Log.d(TAG, "most played playlist retrieved")
        //favourite
        favourite = getFavourite()
        Log.d(TAG, "favourite playlist retrieved")
        //other
        other = otherPlaylist(allPlaylist)
        Log.d(TAG, "other playlist retrieved")
        //collect all to one list
        return collectPlaylist(
            recentlyModified,
            lastPlayed,
            mostPlayed,
            favourite,
            other
        )
    }

//    private fun getAlbumImage(path: String): Bitmap? {
//        val mmr = MediaMetadataRetriever()
//        mmr.setDataSource(path)
//        val data = mmr.embeddedPicture
//        return if (data != null)
//            BitmapFactory.decodeByteArray(data, 0, data.size)
//        else null
//    }
    //something wrong
    private suspend fun getAlbumBasedOnAllSong(): List<Album> {
        val metaRetriever = MediaMetadataRetriever()
        return allSongRemovedDuplicate
            .fold(hashMapOf()) { albums: HashMap<String, Album>, song: File ->
                metaRetriever.setDataSource(song.path)
                scope.launch {
                    val name = withContext(Dispatchers.IO) {
                        metaRetriever
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    }
                    name?.let {
                        val genre = metaRetriever
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

                        val album = Album(
                            name,
                            genre,
                            listOf(song.path)
                        )
                        if (albums.containsKey(name)) {
                            albums[name]!!.songs += song.path
                        } else albums += Pair(name, album)

                        Log.d(TAG, "album name: $name on ${song.path}")
                    }
                }

                Log.d(TAG, "check album on ${song.path}")
                albums
            }.toList().map { it.second }
    }

    private suspend fun saveAlbum(album: List<Album>) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            albumDao().nukeTable()
            albumDao().insertAll(*(album.toTypedArray()))
        }
    }
}
