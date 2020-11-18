package velord.university.model.entity.music.song.withPos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongWithPosDao {

    @Query("Select *, `rowid` From SongWithPos")
    fun getAll(): List<SongWithPos>

    @Update
    fun update(vararg playlist: SongWithPos)

    @Insert
    fun insertAll(vararg playlist: SongWithPos)

    @Query("Delete From SongWithPos Where rowid = :id")
    fun deleteSongById(id: Long)

    @Query("Delete From SongWithPos")
    fun nukeTable()

    @Query("Delete from SongWithPos Where rowid in (:idList)")
    fun deleteById(idList: List<Int>)

    @Query("Select *, `rowid` From SongWithPos Where rowid = :id")
    fun getById(id: Int): SongWithPos

    @Query("SELECT rowid FROM SongWithPos ORDER BY rowid DESC LIMIT 1")
    fun getLastId(): Int
}