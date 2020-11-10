package velord.university.model.entity.vk.entity

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class VkThumb(
    val width: Int?,
    val height: Int?,
    @ColumnInfo(name = "photo_34") val photo_34: String?,
    @ColumnInfo(name = "photo_68") val photo_68: String?,
    @ColumnInfo(name = "photo_135") val photo_135: String?,
    @ColumnInfo(name = "photo_270") val photo_270: String?,
    @ColumnInfo(name = "photo_300") val photo_300: String?,
    @ColumnInfo(name = "photo_600") val photo_600: String?,
    @ColumnInfo(name = "photo_1200") val photo_1200: String?
)