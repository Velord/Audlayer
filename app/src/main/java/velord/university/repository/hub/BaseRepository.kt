package velord.university.repository.hub

import android.content.Context
import android.util.Log
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onIO
import kotlinx.coroutines.launch
import velord.university.application.AudlayerApp

abstract class BaseRepository {

    protected open val TAG = "BaseRepository"

    private val scope = getScope()

    protected val db = AudlayerApp.db!!

    protected suspend fun <T> Context.fetchByToken(
        tag: String,
        f: suspend String.() -> T
    ): T = onIO {
        try {
            val token = "UserPreferences(this).userToken"
            if (token.isNotEmpty()) token.f()
            else error("Token is empty")
        }
        catch (e: Exception) {
            val message = e.message.toString()
            checkTokenSessionIsExpired(
                this@fetchByToken,
                message
            )
            Log.d("$TAG-$tag", message)
            throw e
        }
    }

    protected suspend fun <T> fetch(
        tag: String,
        f: suspend () -> T
    ): T = onIO {
        try { f() }
        catch (e: Exception) {
            val message = e.message.toString()
            Log.d("$TAG-$tag", message)
            throw e
        }
    }

    fun checkTokenSessionIsExpired(context: Context,
                                   error: String) {
        scope.launch {
            //todo
//            if (error == "TOKEN IS EXPIRED")
//                BroadcastHub.apply {
//                    context.sendTokenSessionIsExpired()
//                }
        }
    }
}