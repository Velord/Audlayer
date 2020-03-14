package velord.university.model.entity

import androidx.room.*
import java.util.*

@Entity
data class MiniPlayerServiceSong(
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "position") val pos: Int,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)

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