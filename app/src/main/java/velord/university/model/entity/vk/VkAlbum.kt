package velord.university.model.entity.vk

import androidx.room.*
import kotlinx.serialization.Serializable

@Serializable
@Entity(indices = [Index("title")])
data class VkAlbum(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "owner_id") val owner_id: Int,
    @ColumnInfo(name = "access_key") val access_key: String,

    @Embedded val thumb: VkThumb? = null,

    @PrimaryKey
    @ColumnInfo(name = "vk_album_id")
    val id: Int
)