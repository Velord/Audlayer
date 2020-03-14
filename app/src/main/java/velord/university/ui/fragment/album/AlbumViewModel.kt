package velord.university.ui.fragment.album

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import velord.university.application.broadcast.MiniPlayerBroadcastLoopAll
import velord.university.application.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.entity.Album
import velord.university.model.entity.Playlist
import velord.university.repository.transaction.AlbumDb
import velord.university.repository.transaction.PlaylistDb
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

        return Playlist.collect(
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
        MiniPlayerBroadcastLoopAll.apply {
            app.sendBroadcastLoopAll()
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

    suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        PlaylistDb.deletePlaylist(playlist)
        //refresh playlist
        getDefaultAndUserPlaylist()
    }

    suspend fun retrievePlaylistFromDb() = withContext(Dispatchers.IO) {
        playlist = getDefaultAndUserPlaylist()
        Log.d(TAG, "all playlist collected")
    }

    suspend fun retrieveAlbumFromDb() = withContext(Dispatchers.IO) {
        albums = AlbumDb.getAlbums()
        Log.d(TAG, "all albums collected")
    }

    suspend fun refreshAllAlbum() = withContext(Dispatchers.IO) {
        albums = getAlbumBasedOnAllSong()
        scope.launch { AlbumDb.saveAlbum(albums) }
        Log.d(TAG, "all album collected")
    }

    private suspend fun getDefaultAndUserPlaylist(): List<Playlist> {
        val allPlaylist =  PlaylistDb.getAllPlaylist()
        Log.d(TAG, "all playlist retrieved")
        //unique songs
        allSongRemovedDuplicate = Playlist.allSongFromPlaylist(allPlaylist)
        Log.d(TAG, "all song retrieved")
        //1 month
        recentlyModified = FileFilter
                .recentlyModified(allSongRemovedDuplicate)
                .map { it.path }
        Log.d(TAG, "last modified playlist retrieved")
        //lastPlayed
        val played = PlaylistDb.getPlayedSongs()
        //last 50
        lastPlayed = played.take(MAX_LAST_PLAYED)
        Log.d(TAG, "last play playlist retrieved")
        //most played
        mostPlayed = Playlist.getMostPlayed(played)
        Log.d(TAG, "most played playlist retrieved")
        //favourite
        favourite =  PlaylistDb.getFavouriteSongs()
        Log.d(TAG, "favourite playlist retrieved")
        //other
        other = Playlist.other(allPlaylist)
        Log.d(TAG, "other playlist retrieved")
        //collect all to one list
        return Playlist.collect(
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
}
