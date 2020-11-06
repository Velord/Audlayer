package velord.university.model.entity.vk.fetch

import kotlinx.serialization.Serializable
import velord.university.model.entity.vk.fetch.VkSongFetch

@Serializable
data class VkPlaylist(
    val count: Int,
    val items: Array<VkSongFetch>
)