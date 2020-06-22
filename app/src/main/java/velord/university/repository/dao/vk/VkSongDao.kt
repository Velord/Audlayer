package velord.university.repository.dao.vk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import velord.university.model.entity.vk.VkSong

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

    @Query("Select * From VkSong Join VkAlbum On VkSong.album_id = VkAlbum.vk_album_id")
    fun getVkPlaylist(): List<VkSong>

    @Query("Delete From VkSong")
    fun nukeTable()
}