package velord.university.model.entity

import androidx.room.*
import java.util.*

@Entity
data class Album(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "genre") val genre: String?,
    @ColumnInfo(name = "songs") var songs: List<String>,

    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)

@Dao
interface AlbumDao {

    @Query("Select * From Album")
    fun getAll(): List<Album>

    @Query("Select songs From Album Where name = :name")
    fun getSongsByName(name: String): List<String>

    @Query("Select * From Album Where name = :name")
    fun getByName(name: String): Album
    //don't work
    @Query("UPDATE Album SET songs =:songsValue WHERE name =:nameValue")
    fun updateByName(nameValue: String, songsValue: List<String>)

    @Update
    fun update(vararg album: Album)

    @Insert
    fun insertAll(vararg album: Album)

    @Query("Delete From Album Where name = :albumName")
    fun deleteAlbumByName(albumName: String)

    @Query("Delete From Album Where id = :albumId")
    fun deleteAlbumById(albumId: String)

    @Query("Delete From Album")
    fun nukeTable()
}