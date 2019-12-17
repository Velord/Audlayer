package velord.university.model.miniPlayer.service

import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*
import velord.university.model.miniPlayer.broadcast.*

class MiniPlayerServiceBroadcastReceiver : MiniPlayerService(),
    MiniPlayerBroadcastReceiver {

    override val TAG: String
        get() = "MnPlyrSrvcBrdcstRcvrs"

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val receivers = arrayOf(
        Pair(stop(), filterStopService),  Pair(play(), filterPlayService),
        Pair(like(), filterLikeService), Pair(unlike(), filterUnlikeService),
        Pair(shuffle(), filterShuffleService), Pair(unShuffle(), filterUnShuffleService),
        Pair(skipNext(), filterSkipNextService), Pair(skipPrev(), filterSkipPrevService),
        Pair(rewind(), filterRewindService), Pair(loop(), filterLoopService),
        Pair(loopAll(), filterLoopAllService))

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()

        receivers.forEach {
            unregisterBroadcastReceiver(it.first)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")

        receivers.forEach {
            baseContext.registerBroadcastReceiver(
                it.first, it.second, PERM_PRIVATE_MINI_PLAYER)
        }

        scope.launch {
            delay(6000)
            Log.d(TAG, "sending broadcast to ui")
            sendBroadcastStopUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastLikeUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastLoopUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastPlayUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastRewindUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastSkipPrevUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastSkipNextUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastUnShuffleUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastShuffleUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastUnlikeUI(PERM_PRIVATE_MINI_PLAYER)
            sendBroadcastLoopAllUI(PERM_PRIVATE_MINI_PLAYER)
        }

        return START_STICKY
    }
}