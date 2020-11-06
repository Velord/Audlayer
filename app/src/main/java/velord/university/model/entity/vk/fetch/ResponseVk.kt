package velord.university.model.entity.vk.fetch

import kotlinx.serialization.Serializable

@Serializable
data class ResponseVk(
    val response: VkPlaylist
)