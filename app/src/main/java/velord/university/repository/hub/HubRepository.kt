package velord.university.repository.hub

import android.content.Context

object HubRepository {

    inline fun <T> Context.vkRepository(
        f: VkRepository.(Context) -> T
    ): T = VkRepository.run {
        f(this@vkRepository)
    }
}