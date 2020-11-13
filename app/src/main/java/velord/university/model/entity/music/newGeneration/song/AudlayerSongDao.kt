package velord.university.model.entity.music.newGeneration.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import velord.university.model.entity.music.newGeneration.song.AudlayerSong

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

    @Query("delete from AudlayerSong where rowid in (:idList)")
    fun deleteById(idList: List<Int>)
}