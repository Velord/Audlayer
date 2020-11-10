package velord.university.repository.db.transaction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.playlist.Playlist

object PlaylistTransaction : BaseTransaction() {

    suspend fun getAllPlaylist(): List<Playlist> =
        makeTransaction { playlistDao().getAll() }

    suspend fun getPlayedSongs(): List<String> =
        makeTransaction {
            playlistDao().getByName("Played")
                .songs.reversed()
                .filter { it.isNotEmpty() }
        }

    suspend fun getPlayed(): Playlist =
        makeTransaction {
            playlistDao().getByName("Played")
        }

    suspend fun getFavouriteSongs(): List<String> =
        makeTransaction {
            playlistDao().getByName("Favourite")
                .songs.reversed()
                .filter { it.isNotEmpty() }
        }

    suspend fun getFavourite(): Playlist =
        makeTransaction { playlistDao().getByName("Favourite") }

    suspend fun deletePlaylist(playlist: Playlist) =
        makeTransaction {
            playlistDao().deletePlaylistByName(playlist.name)
        }

    suspend fun createNewPlaylist(name: String, songs: List<String>) =
        makeTransaction {
            val playlist = Playlist(name, songs)
            playlistDao().insertAll(playlist)
        }

    suspend fun update(playlist: Playlist) =
        makeTransaction { playlistDao().update(playlist) }


    suspend fun updateFavourite(changeSongsF: (List<String>) -> List<String>) {
        val favourite = getFavourite()
        favourite.songs += changeSongsF(favourite.songs)
        update(favourite)
    }

    suspend fun updatePlayedSong(path: String) = onIO {
        //retrieve from Db
        val playedSongs = getPlayed()
        //add new path
        //secure from duplicate last
        if (playedSongs.songs.last() != path)
            playedSongs.songs += path
        //update column
        update(playedSongs)
    }

    suspend fun delete(id: Long) =
        makeTransaction { playlistDao().deletePlaylistById(id) }

    suspend fun whichAlbum(path: String): String = onIO {
        Playlist.other(getAllPlaylist()).forEach {
            if(it.songs.contains(path))
                return@onIO it.name
        }
        return@onIO ""
    }

    suspend fun checkDbTableColumn() =
        makeTransaction {
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