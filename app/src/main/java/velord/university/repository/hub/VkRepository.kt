package velord.university.repository.hub

import android.content.Context
import velord.university.model.coroutine.onIO
import velord.university.model.entity.vk.fetch.VkPlaylist
import velord.university.model.entity.vk.entity.VkSong
import velord.university.repository.db.transaction.vk.VkSongTransaction
import velord.university.repository.fetch.VkFetch

object VkRepository : BaseRepository() {

    suspend fun getPlaylistViaCredential(context: Context): VkPlaylist = onIO {
        VkFetch.fetchPlaylist(context)
    }

    suspend fun deleteAllTables() = onIO {
        VkSongTransaction.deleteAll()
    }

    suspend fun insertSong(song: Array<VkSong>) = onIO {
        VkSongTransaction.addSong(*song)
    }
}