package velord.university.repository.db.transaction.vk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.vk.entity.VkSong
import velord.university.model.entity.vk.fetch.VkPlaylist
import velord.university.repository.db.transaction.BaseTransaction

object VkSongTransaction : BaseTransaction() {

    suspend fun getSongs(): Array<VkSong> =
       makeTransaction { vkSongDao().getAll().toTypedArray() }

    suspend fun addSong(vararg song: VkSong) =
        makeTransaction { vkSongDao().insertAll(*song) }

    suspend fun update(vararg song: VkSong) =
        makeTransaction { vkSongDao().update(*song) }

    suspend fun delete(vararg song: VkSong) = makeTransaction {
        song.forEach {
            vkSongDao().deleteById(it.id)
        }
    }

    suspend fun deleteAll() =
        makeTransaction { vkSongDao().nukeTable() }

    suspend fun getPlaylist(): Array<VkSong> =
        makeTransaction { vkSongDao().getVkPlaylist().toTypedArray() }
}