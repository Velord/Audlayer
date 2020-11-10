package velord.university.repository.db.transaction.hub

import velord.university.repository.db.dao.AlbumDao
import velord.university.repository.db.dao.MiniPlayerServiceSongDao
import velord.university.repository.db.dao.PlaylistDao
import velord.university.repository.db.dao.RadioStationDao
import velord.university.repository.db.dao.vk.VkAlbumDao
import velord.university.repository.db.dao.vk.VkSongDao
import velord.university.repository.db.transaction.AlbumTransaction
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.RadioTransaction
import velord.university.repository.db.transaction.ServiceTransaction
import velord.university.repository.db.transaction.vk.VkAlbumTransaction
import velord.university.repository.db.transaction.vk.VkSongTransaction

object HubTransaction {

    suspend fun <T> vkAlbumTransaction(
        tag: String,
        f: VkAlbumDao.() -> T
    ): T = VkAlbumTransaction.transaction(tag) {
        vkAlbumDao().run(f)
    }

    suspend fun <T> vkSongTransaction(
        tag: String,
        f: VkSongDao.() -> T
    ): T = VkSongTransaction.transaction(tag) {
        vkSongDao().run(f)
    }

    suspend fun <T> albumTransaction(
        tag: String,
        f: AlbumDao.() -> T
    ): T = AlbumTransaction.transaction(tag) {
        albumDao().run(f)
    }

    suspend fun <T> playlistTransaction(
        tag: String,
        f: PlaylistDao.() -> T
    ): T = PlaylistTransaction.transaction(tag) {
        playlistDao().run(f)
    }

    suspend fun <T> radioTransaction(
        tag: String,
        f: RadioStationDao.() -> T
    ): T = RadioTransaction.transaction(tag) {
        radioDao().run(f)
    }

    suspend fun <T> serviceTransaction(
        tag: String,
        f: MiniPlayerServiceSongDao.() -> T
    ): T = ServiceTransaction.transaction(tag) {
        serviceDao().run(f)
    }
}