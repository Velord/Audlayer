package velord.university.model.entity.music.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaylistDao {

    @Query("Select *, `rowid` From Playlist")
    fun getAll(): List<Playlist>

    @Update
    fun update(vararg playlist: Playlist)

    @Insert
    fun insertAll(vararg playlist: Playlist)

    @Query("Delete From Playlist Where rowid = :id")
    fun deletePlaylistById(id: Int)

    @Query("Delete From Playlist")
    fun nukeTable()

    @Query("Delete from Playlist where rowid in (:idList)")
    fun deleteById(idList: List<Int>)

    @Query("Select *, `rowid` From Playlist Where name = :playlistName")
    fun getByName(playlistName: String): Playlist
}