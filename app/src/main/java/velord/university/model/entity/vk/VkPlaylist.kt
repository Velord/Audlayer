package velord.university.model.entity.vk

import kotlinx.serialization.Serializable

@Serializable
data class VkPlaylist(
    val count: Int,
    val items: Array<VkSongFetch>
)