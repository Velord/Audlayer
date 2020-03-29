package velord.university.repository.transaction.vk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.vk.VkAlbum

object VkAlbumTransaction {

    suspend fun getAlbums(): List<VkAlbum> = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            vkAlbumDao().getAll()
        } ?: listOf()
    }

    suspend fun addAlbum(vararg album: VkAlbum) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            vkAlbumDao().insertAll(*album)
        }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            vkAlbumDao().nukeTable()
        }
    }
}