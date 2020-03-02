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

    @Query("Update Playlist SET songs =:songsValue Where name =:nameValue")
    fun updateByName(nameValue: String, songsValue: List<String>)

    @Update
    fun update(vararg playlist: Playlist)

    @Insert
    fun insertAll(vararg playlist: Playlist)

    @Query("Delete From Playlist")
    fun nukeTable()
}