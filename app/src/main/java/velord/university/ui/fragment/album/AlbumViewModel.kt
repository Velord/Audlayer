package velord.university.ui.fragment.album

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import velord.university.application.AudlayerApp
import velord.university.application.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.application.settings.SearchQueryPreferences
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.entity.Album
import velord.university.model.entity.Playlist
import java.io.File


const val MAX_FILEAGE: Long = 2678400000L // 1 month in milliseconds
const val MAX_LAST_PLAYED: Int = 50
const val MAX_MOST_PLAYED: Int = 50

class AlbumViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "AlbumViewModel"

    val scope = CoroutineScope(Job() + Dispatchers.IO)

    lateinit var currentQuery: String

    lateinit var playlist: List<Playlist>
    lateinit var recentlyModified: List<String>
    lateinit var lastPlayed: List<String>
    lateinit var mostPlayed: List<String>
    lateinit var favourite: List<String>
    lateinit var other: List<Playlist>

    lateinit var allSongRemovedDuplicate: List<File>

    lateinit var albums: List<Album>

    fun albumsIsInitialized(): Boolean =
        ::albums.isInitialized

    fun playlistIsInitialized(): Boolean =
        ::playlist.isInitialized

    suspend fun retrievePlaylistFromDb() = withContext(Dispatchers.IO) {
        playlist = getPlaylists()
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
            getPlaylists()
        }
    }

    private suspend fun getPlaylists(): List<Playlist> {
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
                    if (mostPlayed.containsKey(song).not())
                        mostPlayed += Pair(song, 1)
                    else {
                        val count = mostPlayed[song]
                        mostPlayed += Pair(song, count!!.plus(1))
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

    fun filterByQueryPlaylist(query: String): List<Playlist> {
        val newOther = other.filter { it.name.contains(query) }

        return collectPlaylist(
            recentlyModified,
            lastPlayed,
            mostPlayed,
            favourite,
            newOther
        )
    }

    private fun otherPlaylist(playlist: List<Playlist>): List<Playlist> =
        playlist.filter {
            it.name != "Favourite" && it.name != "Played"
        }

    fun playSongs(songs: Array<String>) {
        //don't remember for SongPlaylistInteractor
        SongPlaylistInteractor.songs =
            songs.map { File(it) }.toTypedArray()
        MiniPlayerBroadcastPlayByPath.apply {
            app.sendBroadcastPlayByPath(songs[0])
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
        playlist.map { it.songs }
            .fold(mutableListOf<String>()) { joined, fromDB ->
            joined.addAll(fromDB)
            joined
        }
            .distinct()
            .map { File(it) }
            .filter { it.path.isNotEmpty() }

    private suspend fun getAllPlaylist(): List<Playlist> = withContext(Dispatchers.IO) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getAll()
        }
    } ?: listOf()

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQueryAlbum(app, currentQuery)
        Log.d(TAG, "query: $currentQuery")
    }

    private fun getAlbumImage(path: String): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val data = mmr.embeddedPicture
        return if (data != null)
            BitmapFactory.decodeByteArray(data, 0, data.size)
        else null
    }
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
