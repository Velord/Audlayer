package velord.university.model.entity

import androidx.room.*
import java.util.*


@Entity
data class Playlist(
    @ColumnInfo(name = "name") val name: String,

    @ColumnInfo(name = "songs") var songs: List<String>,

    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)


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
    fun deletePlaylistById(playlistId: String)

    @Query("Delete From Playlist")
    fun nukeTable()
}