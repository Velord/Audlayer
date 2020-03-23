package velord.university.repository.dao.vk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import velord.university.model.entity.vk.VkAlbum

@Dao
interface VkAlbumDao {

    @Query("Select * From VkAlbum")
    fun getAll(): List<VkAlbum>

    @Query("Select * From VkAlbum Where title = :title")
    fun getByTitle(title: String): VkAlbum
    //don't work
    @Query("UPDATE VkAlbum SET owner_id =:ownerId, access_key =:accessKey WHERE title =:title")
    fun updateByTitle(title: String, ownerId: Int, accessKey: String)

    @Update
    fun update(vararg album: VkAlbum)

    @Insert
    fun insertAll(vararg album: VkAlbum)

    @Query("Delete From VkAlbum Where title = :title")
    fun deleteAlbumByTitle(title: String)

    @Query("Delete From VkAlbum Where vk_album_id = :albumId")
    fun deleteAlbumById(albumId: Int)

    @Query("Delete From VkAlbum")
    fun nukeTable()
}