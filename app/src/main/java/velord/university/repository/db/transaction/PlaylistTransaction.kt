package velord.university.repository.db.transaction

import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.newGeneration.playlist.Playlist
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPos
import velord.university.repository.db.transaction.hub.BaseTransaction
import velord.university.repository.db.transaction.hub.HubTransaction.playlistTransaction

object PlaylistTransaction : BaseTransaction() {

    override val TAG: String = "PlaylistTransaction"

    suspend fun getAllPlaylist(): List<Playlist> =
        playlistTransaction("getAllPlaylist") { getAll() }

    suspend fun update(playlist: Playlist) =
        playlistTransaction("update") { update(playlist) }

    suspend fun convertPlaylist(playlist: Playlist): Playlist {
        //get all SongWitPos
        val songWithPos = playlist.songIdList.map {
            transaction("convertPlaylist") {
                songWithPosDao().getById(it)
            }
        }
        //convert SongWithPos
        songWithPos.forEach {
            it.song = transaction("convertPlaylist") {
                songDao().getById(it.songId)
            }
        }
        playlist.songs = songWithPos
        return playlist
    }

    suspend fun getPlayed(): Playlist =
        convertPlaylist(playlistTransaction("getPlayed") {
            (getByName("Played"))
        })

    suspend fun getFavourite(): Playlist =
        convertPlaylist(playlistTransaction("getFavourite") {
            (getByName("Favourite"))
        })

    suspend fun getVk(): Playlist =
        convertPlaylist(playlistTransaction("getFavourite") {
            (getByName("Vk"))
        })

    suspend fun createNewPlaylist(name: String, songs: List<Int>) =
        playlistTransaction("createNewPlaylist") {
            val playlist = Playlist(name, songs)
            insertAll(playlist)
        }

    suspend fun updatePlayedSong(song: SongWithPos) = onIO {
        //retrieve from Db
        val playedSongs = getPlayed()
        //add new path
        //secure from duplicate last
        if (playedSongs.songs.last() != song)
            playedSongs.songs += song
        //update column
        update(playedSongs)
    }

    suspend fun checkDbTableColumn() =
        transaction("checkDbTableColumn") {
            val playlist = getAllPlaylist().map { it.name }

            val favouriteExist = playlist.contains("Favourite")
            val playedSongExist = playlist.contains("Played")
            val downloadedExist = playlist.contains("Downloaded")
            val vkExist = playlist.contains("Vk")
            val currentExist = playlist.contains("Current")

            playlistTransaction("checkDbTableColumn") {
                if (favouriteExist.not())
                    insertAll(Playlist("Favourite", listOf()))

                if (playedSongExist.not())
                    insertAll(Playlist("Played", listOf()))

                if (downloadedExist.not())
                    insertAll(Playlist("Downloaded", listOf()))

                if (vkExist.not())
                    insertAll(Playlist("Vk", listOf()))

                if (currentExist.not())
                    insertAll(Playlist("Current", listOf()))
            }
        }
}