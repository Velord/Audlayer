package velord.university.repository.hub

import android.content.Context

object HubRepository {

    inline fun <T> Context.vkRepository(
        f: VkRepository.(Context) -> T
    ): T = VkRepository.run {
        f(this@vkRepository)
    }

    inline fun <T> Context.radioRepository(
        f: RadioRepository.(Context) -> T
    ): T = RadioRepository.run {
        f(this@radioRepository)
    }

    inline fun <T> Context.miniPlayerRepository(
        f: MiniPlayerRepository.(Context) -> T
    ): T = MiniPlayerRepository.run {
        f(this@miniPlayerRepository)
    }

    inline fun <T> Context.folderRepository(
        f: FolderRepository.(Context) -> T
    ): T = FolderRepository.run {
        f(this@folderRepository)
    }

    inline fun <T> Context.downloadRepository(
        f: DownloadRepository.(Context) -> T
    ): T = DownloadRepository.run {
        f(this@downloadRepository)
    }
}