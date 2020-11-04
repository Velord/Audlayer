package velord.university.repository.db.transaction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.Playlist

object PlaylistTransaction {

    private val context = Dispatchers.IO

    suspend fun getAllPlaylist(): List<Playlist> = withContext(context) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getAll()
        }
    } ?: listOf()

    suspend fun getPlayedSongs(): List<String> = withContext(context) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getByName("Played").songs.reversed().filter { it.isNotEmpty() }
        }
    } ?: listOf()

    suspend fun getPlayed(): Playlist = withContext(Dispatchers.IO) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getByName("Played")
        }
    } ?: Playlist("Played", listOf())

    suspend fun getFavouriteSongs(): List<String> = withContext(Dispatchers.IO) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getByName("Favourite").songs.reversed().filter { it.isNotEmpty() }
        }
    } ?: listOf()

    suspend fun getFavourite(): Playlist = withContext(Dispatchers.IO) {
        return@withContext AudlayerApp.db?.run {
            playlistDao().getByName("Favourite")
        }
    } ?: Playlist("Favourite", listOf())

    suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            playlistDao().deletePlaylistByName(playlist.name)
        }
    }

    suspend fun createNewPlaylist(name: String, songs: List<String>) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            val playlist = Playlist(name, songs)
            playlistDao().insertAll(playlist)
        }
    }

    suspend fun update(playlist: Playlist) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            playlistDao().update(playlist)
        }
    }

    suspend fun updateFavourite(changeSongsF: (List<String>) -> List<String>) {
        val favourite = getFavourite()
        favourite.songs += changeSongsF(favourite.songs)
        update(favourite)
    }

    suspend fun updatePlayedSong(path: String) = withContext(Dispatchers.IO) {
        //retrieve from Db
        val playedSongs = getPlayed()
        //add new path
        //secure from duplicate last
        if (playedSongs.songs.last() != path)
            playedSongs.songs += path
        //update column
        update(playedSongs)
    }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            playlistDao().deletePlaylistById(id)
        }
    }

    suspend fun whichAlbum(path: String): String = withContext(Dispatchers.IO) {
        Playlist.other(getAllPlaylist()).forEach {
            if(it.songs.contains(path))
                return@withContext it.name
        }
        return@withContext ""
    }

    suspend fun checkDbTableColumn() = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            val playlist = getAllPlaylist()

            var favouriteExist = false
            var playedSongExist = false
            var downloadedExist = false

            playlist.forEach {
                if (it.name == "Favourite")
                    favouriteExist = true
                if (it.name == "Played")
                    playedSongExist = true
                if (it.name == "Downloaded")
                    downloadedExist = true
            }

            if (favouriteExist.not())
                playlistDao().insertAll(Playlist("Favourite", listOf()))

            if (playedSongExist.not())
                playlistDao().insertAll(Playlist("Played", listOf()))

            if (downloadedExist.not())
                playlistDao().insertAll(Playlist("Downloaded", listOf()))
        }
    }
}