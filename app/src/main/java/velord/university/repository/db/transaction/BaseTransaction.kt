package velord.university.repository.db.transaction

import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.playlist.Playlist
import velord.university.repository.db.factory.AppDatabase

abstract class BaseTransaction {

    protected fun getDb(): AppDatabase? =
        AudlayerApp.db

    internal suspend fun <T> makeTransaction(
        f: suspend AppDatabase.() -> T
    ): T = onIO { getDb()!!.f() }

    internal suspend fun <T> makeSafeTransaction(
        f: suspend AppDatabase.() -> T
    ): T? = onIO { getDb()?.f() }

}