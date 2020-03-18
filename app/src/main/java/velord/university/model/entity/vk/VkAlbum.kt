package velord.university.model.entity.vk

import androidx.room.Entity

@Entity
data class VkAlbum(
    val id: Int,
    val title: String?,
    val owner_id: Int,
    val access_key: String,
    val thumb: VkThumb
)