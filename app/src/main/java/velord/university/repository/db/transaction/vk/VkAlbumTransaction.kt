package velord.university.repository.db.transaction.vk

import velord.university.model.entity.vk.entity.VkAlbum
import velord.university.repository.db.dao.vk.VkAlbumDao
import velord.university.repository.db.dao.vk.VkSongDao
import velord.university.repository.db.transaction.hub.BaseTransaction
import velord.university.repository.db.transaction.hub.HubTransaction.vkAlbumTransaction

object VkAlbumTransaction : BaseTransaction() {

    override val TAG: String = "VkAlbumTransaction"

    suspend fun getAlbums(): Array<VkAlbum> =
        vkAlbumTransaction("getAlbums") { getAll().toTypedArray() }

    suspend fun addAlbum(vararg album: VkAlbum) =
        vkAlbumTransaction("addAlbum") { insertAll(*album) }

    suspend fun deleteAll() =
        vkAlbumTransaction("deleteAll") { nukeTable() }
}