package velord.university.repository.transaction.vk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong

object VkSongTransaction {

    suspend fun getSongs(): List<VkSong> = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            vkSongDao().getAll()
        } ?: listOf()
    }

    suspend fun getVkPlaylist(): VkPlaylist = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            val songs = vkSongDao().getAll().toTypedArray()
            VkPlaylist(songs.size, songs)
        } ?: VkPlaylist(0, arrayOf())
    }

    suspend fun addSong(vararg song: VkSong) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            vkSongDao().insertAll(*song)
        }
    }

    suspend fun update(vararg song: VkSong) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            vkSongDao().update(*song)
        }
    }

    suspend fun delete(vararg song: VkSong) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            song.forEach {
                vkSongDao().deleteById(it.id)
            }
        }
    }
}