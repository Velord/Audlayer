package velord.university.model.entity.vk

import androidx.room.*

@Entity(indices = [Index("title")])
data class VkAlbum(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "owner_id") val owner_id: Int,
    @ColumnInfo(name = "access_key") val access_key: String,

    @Embedded val thumb: VkThumb?,

    @PrimaryKey
    @ColumnInfo(name = "vk_album_id")
    val id: Int
)

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