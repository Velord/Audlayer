package velord.university.repository.db.transaction.hub

import android.util.Log
import velord.university.application.AudlayerApp
import velord.university.model.coroutine.onIO
import velord.university.repository.db.dao.RadioStationDao
import velord.university.repository.db.factory.AppDatabase

abstract class BaseTransaction {

    protected open val TAG = "BaseTransaction"

    internal suspend fun <T> transaction(
        tag: String,
        f: suspend AppDatabase.() -> T
    ): T = onIO {
        try { AudlayerApp.getDatabase()!!.f() }
        catch (e: Exception) {
            val message = e.message.toString()
            Log.d("$TAG-$tag", message)
            throw e
        }
    }

    internal suspend fun <T> transactionSafe(
        tag: String,
        f: suspend AppDatabase.() -> T
    ): T? = onIO {
        try { AudlayerApp.getDatabase()?.f() }
        catch (e: Exception) {
            val message = e.message.toString()
            Log.d("$TAG-$tag", message)
            throw e
        }
    }

}