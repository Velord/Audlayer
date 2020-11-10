package velord.university.repository.db.transaction.vk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.vk.entity.VkAlbum
import velord.university.repository.db.transaction.BaseTransaction

object VkAlbumTransaction : BaseTransaction() {

    suspend fun getAlbums(): Array<VkAlbum> =
        makeTransaction { vkAlbumDao().getAll().toTypedArray() }

    suspend fun addAlbum(vararg album: VkAlbum) =
        makeTransaction { vkAlbumDao().insertAll(*album) }

    suspend fun deleteAll() =
        makeTransaction { vkAlbumDao().nukeTable() }
}