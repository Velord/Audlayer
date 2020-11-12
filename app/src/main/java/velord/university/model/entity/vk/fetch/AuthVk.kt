package velord.university.model.entity.vk.fetch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthVk(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("user_id")
    val userId: Int,
    val secret: String? = null,
    @SerialName("https_required")
    val httpsRequired: Int? = null
)