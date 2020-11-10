package velord.university.repository.db.transaction.vk

import velord.university.model.entity.vk.entity.VkSong
import velord.university.repository.db.dao.AlbumDao
import velord.university.repository.db.dao.vk.VkSongDao
import velord.university.repository.db.transaction.hub.BaseTransaction
import velord.university.repository.db.transaction.hub.HubTransaction.vkSongTransaction

object VkSongTransaction : BaseTransaction() {

    override val TAG: String = "VkSongTransaction"

    suspend fun getAllSong(): Array<VkSong> =
        vkSongTransaction("getSongs") { getAll().toTypedArray() }

    suspend fun addSong(vararg song: VkSong) =
        vkSongTransaction("addSong") { insertAll(*song) }

    suspend fun update(vararg song: VkSong) =
        vkSongTransaction("update") { update(*song) }

    suspend fun delete(vararg song: VkSong) =
        vkSongTransaction("delete") {
            song.forEach { deleteById(it.id) }
        }

    suspend fun deleteAll() =
        vkSongTransaction("deleteAll") { nukeTable() }

    suspend fun getPlaylist(): Array<VkSong> =
        vkSongTransaction("getPlaylist") { getVkPlaylist().toTypedArray() }
}