package velord.university.model.entity.vk

import androidx.room.*

@Entity(
    indices = [Index("artist", "title", "path", "album_id")]
)
data class VkSong(
    var artist: String,
    @ColumnInfo(name = "owner_id") val owner_id: Int,
    var title: String,
    val duration: Int,
    @ColumnInfo(name = "access_key") val access_key: String?,
    @ColumnInfo(name = "is_licensed") val is_licensed: Boolean,
    val date: Int,
    @ColumnInfo(name = "is_hq") val isHQ: Boolean,
    @ColumnInfo(name = "track_genre_id") val track_genre_id: Int,

    @PrimaryKey
    @ColumnInfo(name = "vk_song_id")
    val id: Int,

    @ColumnInfo(name = "album_id")
    var albumId: Int?,

    var url: String = "",
    var path: String = ""
) {
    @Ignore var album: VkAlbum? = null
}

@Dao
interface VkSongDao {

    @Query("Select * From VkSong")
    fun getAll(): List<VkSong>

    @Query("Select * From VkSong Where title = :title")
    fun getByTitle(title: String): VkSong

    @Query("Select * From VkSong Where artist = :artist")
    fun getByArtist(artist: String): VkSong

    @Update
    fun update(vararg song: VkSong)

    @Insert
    fun insertAll(vararg song: VkSong)

    @Query("Delete From VkSong Where title = :title")
    fun deleteByTitle(title: String)

    @Query("Delete From VkSong Where vk_song_id = :songId")
    fun deleteById(songId: Int)

    @Query("Delete From VkSong")
    fun nukeTable()
}

