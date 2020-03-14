package velord.university.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import velord.university.model.entity.Album

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