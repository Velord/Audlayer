package velord.university.ui.fragment.album

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.Album
import velord.university.model.entity.Playlist
import velord.university.model.entity.music.Song
import velord.university.model.file.FileFilter
import velord.university.repository.hub.FolderRepository
import velord.university.repository.db.transaction.AlbumTransaction
import velord.university.repository.db.transaction.PlaylistTransaction
import java.io.File

const val MAX_LAST_PLAYED: Int = 50
const val MAX_MOST_PLAYED: Int = 50

class AlbumViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val TAG = "AlbumViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    lateinit var currentQuery: String

    private lateinit var playlist: List<Playlist>
    private lateinit var recentlyModified: List<String>
    private lateinit var lastPlayed: List<String>
    private lateinit var mostPlayed: List<String>
    private lateinit var favourite: List<String>
    private lateinit var downloaded: List<String>
    private lateinit var other: List<Playlist>

    private lateinit var allSongRemovedDuplicate: List<File>

    lateinit var albums: List<Album>

    fun getSearchQuery(): String =
        SearchQueryPreferences(app).storedQueryAlbum

    fun albumsIsInitialized(): Boolean = ::albums.isInitialized

    fun playlistIsInitialized(): Boolean = ::playlist.isInitialized

    fun filterByQueryPlaylist(query: String): List<Playlist> {
        val newOther = other.filter { it.name.contains(query) }
        // sort by album or artist or year or number of tracks
        val sortedPlaylist = when(SortByPreference(app).sortByAlbumFragment) {
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
        val orderedPlaylist = when(SortByPreference(app).ascDescAlbumFragment) {
            0 -> sortedPlaylist
            1 ->  sortedPlaylist.reversed()
            else -> sortedPlaylist
        }

        return collect(orderedPlaylist)
    }

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
                app.loopAllService()
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

    suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        PlaylistTransaction.deletePlaylist(playlist)
        //refresh playlist
        getDefaultAndUserPlaylist()
    }

    suspend fun retrievePlaylistFromDb() = withContext(Dispatchers.IO) {
        playlist = getDefaultAndUserPlaylist()
        Log.d(TAG, "all playlist collected")
    }

    suspend fun retrieveAlbumFromDb() = withContext(Dispatchers.IO) {
        albums = AlbumTransaction.getAlbums()
        Log.d(TAG, "all albums collected")
    }

    suspend fun refreshAllAlbum() = withContext(Dispatchers.IO) {
        albums = getAlbumBasedOnAllSong()
        scope.launch { AlbumTransaction.saveAlbum(albums) }
        Log.d(TAG, "all album collected")
    }

    private fun collect(otherPlaylist: List<Playlist>): List<Playlist> {
        return listOf(
            Playlist("Recently Modified", recentlyModified),
            Playlist("Last Played", lastPlayed),
            Playlist("Most Played", mostPlayed),
            Playlist("Favourite", favourite),
            Playlist("Downloaded", downloaded),
            *otherPlaylist.map {
                Playlist(it.name, it.songs)
            }.toTypedArray()
        )
    }

    private suspend fun getDefaultAndUserPlaylist(): List<Playlist> {
        val allPlaylist =  PlaylistTransaction.getAllPlaylist()
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
        val played = PlaylistTransaction.getPlayedSongs()
        //last 50
        lastPlayed = played.take(MAX_LAST_PLAYED)
        Log.d(TAG, "last play playlist retrieved")
        //most played
        mostPlayed = Playlist.getMostPlayed(played)
        Log.d(TAG, "most played playlist retrieved")
        //favourite
        favourite =  PlaylistTransaction.getFavouriteSongs()
        Log.d(TAG, "favourite playlist retrieved")
        //downloaded
        val filesAppDir: Array<out File> = FolderRepository
            .getApplicationDir()
            .listFiles() ?: arrayOf()
        val filesVkDir: Array<out File> = FolderRepository
            .getApplicationVkDir()
            .listFiles() ?: arrayOf()
        val allFiles = (filesAppDir.toList() + filesVkDir).toTypedArray()
        downloaded =  FileFilter.filterOnlyAudio(allFiles).map { it.path }
        Log.d(TAG, "downloaded playlist retrieved")
        //other
        other = Playlist.other(allPlaylist)
        Log.d(TAG, "other playlist retrieved")
        //collect all to one list
        return collect(other)
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
}
