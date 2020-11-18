package velord.university.repository.db.transaction.hub

import velord.university.model.entity.music.playlist.PlaylistDao
import velord.university.model.entity.music.song.main.AudlayerSongDao
import velord.university.model.entity.music.song.withPos.SongWithPosDao
import velord.university.model.entity.music.radio.RadioStationDao
import velord.university.repository.db.transaction.AudlayerSongTransaction
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.RadioTransaction

object DB {

    suspend fun <T> radioTransaction(
        tag: String,
        f: RadioStationDao.() -> T
    ): T = RadioTransaction.transaction(tag) {
        radioDao().run(f)
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

    suspend fun <T> songWithPos(
        tag: String,
        f: SongWithPosDao.() -> T
    ): T = PlaylistTransaction.transaction(tag) {
        songWithPosDao().run(f)
    }
}