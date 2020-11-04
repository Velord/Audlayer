package velord.university.repository.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import velord.university.model.entity.MiniPlayerServiceSong

@Dao
interface MiniPlayerServiceSongDao {

    @Transaction
    fun updateData(values: Array<MiniPlayerServiceSong>) {
        nukeTable()
        insertAll(*values)
    }

    @Query("Select * From MiniPlayerServiceSong")
    fun getAll(): List<MiniPlayerServiceSong>

    @Insert
    fun insertAll(vararg paths: MiniPlayerServiceSong)

    @Query("Delete From MiniPlayerServiceSong")
    fun nukeTable()
}