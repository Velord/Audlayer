package velord.university.repository.db.transaction

import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.playlist.Playlist
import velord.university.repository.db.dao.PlaylistDao
import velord.university.repository.db.transaction.hub.BaseTransaction
import velord.university.repository.db.transaction.hub.HubTransaction.playlistTransaction

object PlaylistTransaction : BaseTransaction() {

    override val TAG: String = "PlaylistTransaction"

    suspend fun getAllPlaylist(): List<Playlist> =
        playlistTransaction("getAllPlaylist") { getAll() }

    suspend fun update(playlist: Playlist) =
        playlistTransaction("update") { update(playlist) }

    suspend fun getPlayedSongs(): List<String> =
        playlistTransaction("getPlayedSongs") {
            getByName("Played")
                .songs.reversed()
                .filter { it.isNotEmpty() }
        }

    suspend fun getPlayed(): Playlist =
        playlistTransaction("getPlayed") {
            getByName("Played")
        }

    suspend fun getFavouriteSongs(): List<String> =
        playlistTransaction("getFavouriteSongs") {
            getByName("Favourite")
                .songs.reversed()
                .filter { it.isNotEmpty() }
        }

    suspend fun getFavourite(): Playlist =
        playlistTransaction("getFavourite") {
            getByName("Favourite")
        }

    suspend fun createNewPlaylist(name: String, songs: List<String>) =
        playlistTransaction("createNewPlaylist") {
            val playlist = Playlist(name, songs)
            insertAll(playlist)
        }

    suspend fun updateFavourite(changeSongsF: (List<String>) -> List<String>) =
        transaction("updateFavourite") {
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

    suspend fun whichAlbum(path: String): String =
        transaction("whichAlbum") {
            Playlist.other(getAllPlaylist()).forEach {
                if(it.songs.contains(path))
                    return@transaction it.name
            }
            return@transaction ""
        }

    suspend fun checkDbTableColumn() =
        transaction("checkDbTableColumn") {
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

            playlistTransaction("checkDbTableColumn") {
                if (favouriteExist.not())
                    insertAll(Playlist("Favourite", listOf()))

                if (playedSongExist.not())
                    insertAll(Playlist("Played", listOf()))

                if (downloadedExist.not())
                    insertAll(Playlist("Downloaded", listOf()))
            }
        }
}