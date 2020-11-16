package velord.university.repository.db.transaction.hub

import velord.university.model.entity.music.newGeneration.playlist.PlaylistDao
import velord.university.model.entity.music.newGeneration.song.AudlayerSongDao
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPosDao
import velord.university.model.entity.music.song.serviceSong.MiniPlayerServiceSongDao
import velord.university.model.entity.music.radio.RadioStationDao
import velord.university.repository.db.transaction.AudlayerSongTransaction
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.RadioTransaction
import velord.university.repository.db.transaction.ServiceTransaction

object HubTransaction {

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

    suspend fun <T> songTransaction(
        tag: String,
        f: AudlayerSongDao.() -> T
    ): T = AudlayerSongTransaction.transaction(tag) {
        songDao().run(f)
    }

    suspend fun <T> playlistTransaction(
        tag: String,
        f: PlaylistDao.() -> T
    ): T = PlaylistTransaction.transaction(tag) {
        playlistDao().run(f)
    }
}