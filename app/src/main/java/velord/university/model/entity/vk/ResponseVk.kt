package velord.university.model.entity.vk

import kotlinx.serialization.Serializable

@Serializable
data class ResponseVk(
    val response: VkPlaylist
)