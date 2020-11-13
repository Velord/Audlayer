package velord.university.model.entity.music.playlist.base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaylistDao {

    @Query("Select * From Playlist")
    fun getAll(): List<Playlist>

    @Query("Select songs From Playlist Where name = :name")
    fun getSongsByName(name: String): List<String>

    @Query("Select * From Playlist Where name = :name")
    fun getByName(name: String): Playlist
    //don't work
    @Query("UPDATE Playlist SET songs =:songsValue WHERE name =:nameValue")
    fun updateByName(nameValue: String, songsValue: List<String>)

    @Update
    fun update(vararg playlist: Playlist)

    @Insert
    fun insertAll(vararg playlist: Playlist)

    @Query("Delete From Playlist Where name = :playlistName")
    fun deletePlaylistByName(playlistName: String)

    @Query("Delete From Playlist Where id = :playlistId")
    fun deletePlaylistById(playlistId: Long)

    @Query("Delete From Playlist")
    fun nukeTable()
}