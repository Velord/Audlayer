package velord.university.model.entity.music.song.main

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AudlayerSongDao {

    @Query("Select *, `rowid` From AudlayerSong")
    fun getAll(): List<AudlayerSong>

    @Update
    fun update(vararg playlist: AudlayerSong)

    @Insert
    fun insertAll(vararg playlist: AudlayerSong)

    @Query("Delete From AudlayerSong Where title = :name")
    fun deleteSongByTitle(name: String)

    @Query("Delete From AudlayerSong Where rowid = :id")
    fun deleteSongById(id: Long)

    @Query("Delete From AudlayerSong")
    fun nukeTable()

    @Query("delete from AudlayerSong Where rowid in (:idList)")
    fun deleteById(idList: List<Int>)

    @Query("Select *, `rowid` From AudlayerSong Where rowid = :id")
    fun getById(id: Int): AudlayerSong

    @Query("Select *, `rowid` From AudlayerSong Where :artist Not In (artist) And :title Not In (title)")
    fun getByNameArtistNot(artist: String, title: String): Boolean

    @Query("Select *, `rowid` From AudlayerSong Where artist In (:artist) And title In (:title)")
    fun getByNameArtist(artist: List<String>, title: List<String>): List<AudlayerSong>

    @Query("Select `rowid` From AudlayerSong ")
    fun getAllId(): List<Int>
}