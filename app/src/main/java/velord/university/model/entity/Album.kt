package velord.university.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Album(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "genre") val genre: String?,
    @ColumnInfo(name = "songs") var songs: List<String>,

    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)