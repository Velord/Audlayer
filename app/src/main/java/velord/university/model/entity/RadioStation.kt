package velord.university.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(
    indices = [Index("name", "url")]
)
@JsonClass(generateAdapter = true)
data class RadioStation(
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "icon")
    val icon: String? = null,

    @ColumnInfo(name = "liked")
    val liked: Boolean = false,

    @PrimaryKey(autoGenerate = true) val id: Long = 0
)