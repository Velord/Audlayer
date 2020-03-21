package velord.university.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "genre") val genre: String?,
    @ColumnInfo(name = "songs") var songs: List<String>,

    @PrimaryKey(autoGenerate = true) val id: Long = 0
)