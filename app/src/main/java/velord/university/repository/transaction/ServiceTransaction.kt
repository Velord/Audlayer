package velord.university.repository.transaction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.MiniPlayerServiceSong

object ServiceDb {
    suspend fun update(songs: Array<MiniPlayerServiceSong>) {
        AudlayerApp.db?.apply {
            serviceDao().updateData(songs)
        }
    }

    suspend fun getPlaylist(): List<MiniPlayerServiceSong> = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            serviceDao().getAll()
        }
    } ?: listOf()
}