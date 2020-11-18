package velord.university.repository.db.transaction

import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.playlist.Playlist
import velord.university.model.entity.music.song.withPos.SongWithPos
import velord.university.repository.db.transaction.hub.BaseTransaction
import velord.university.repository.db.transaction.hub.DB.playlistTransaction

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
        playlist.songWithPosList = songWithPos
        return playlist
    }

    suspend fun getPlayed(): Playlist =
        convertPlaylist(playlistTransaction("getPlayed") {
            (getByName("Played"))
        })

    suspend fun getCurrent(): Playlist =
        convertPlaylist(playlistTransaction("getCurrent") {
            (getByName("Current"))
        })

    suspend fun getFavourite(): Playlist =
        convertPlaylist(playlistTransaction("getFavourite") {
            (getByName("Favourite"))
        })

    suspend fun getVk(): Playlist =
        convertPlaylist(playlistTransaction("getVk") {
            (getByName("Vk"))
        })

    suspend fun createNewPlaylist(name: String, songs: List<Int>) =
        playlistTransaction("createNewPlaylist") {
            val playlist = Playlist(name, songs.toMutableList())
            insertAll(playlist)
        }

    suspend fun checkDbTableColumn() = transaction("checkDbTableColumn") {
        val playlist = getAllPlaylist().map { it.name }

        Playlist.defaultPlaylist.forEach {
            val exist = playlist.contains(it)
            if (exist.not()) playlistTransaction("checkDbTableColumn") {
                insertAll(Playlist(it, mutableListOf()))
            }
        }
    }
}