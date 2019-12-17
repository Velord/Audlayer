package velord.university.model.miniPlayer.service

import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*
import velord.university.model.miniPlayer.broadcast.*

class MiniPlayerServiceBroadcastReceiver : MiniPlayerService(), MiniPlayerBroadcastReceiver {

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

        //only test
        scope.launch {
            delay(4000)
            sendBroadcastSongArtistUI(PERM_PRIVATE_MINI_PLAYER, "David Guetta")
            sendBroadcastSongNameUI(PERM_PRIVATE_MINI_PLAYER, "Clap Your Hands")
            sendBroadcastSongHQUI(PERM_PRIVATE_MINI_PLAYER, true)
            sendBroadcastSongDurationUI(PERM_PRIVATE_MINI_PLAYER, 127)
        }

        return START_STICKY
    }

    override val stopF: (Intent?) -> Unit
        get() = {
            sendBroadcastStopUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val playF: (Intent?) -> Unit
        get() = {
            sendBroadcastPlayUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val likeF: (Intent?) -> Unit
        get() = {
            sendBroadcastLikeUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val unlikeF: (Intent?) -> Unit
        get() = {
            sendBroadcastUnlikeUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val skipNextF: (Intent?) -> Unit
        get() = {
            sendBroadcastSkipNextUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val skipPrevF: (Intent?) -> Unit
        get() = {
            sendBroadcastSkipPrevUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val rewindF: (Intent?) -> Unit
        get() = {
            it?.let {
                val value = it.getIntExtra(PROGRESS, 0)
                sendBroadcastRewindUI(PERM_PRIVATE_MINI_PLAYER, value)
            }
        }

    override val shuffleF: (Intent?) -> Unit
        get() = {
            sendBroadcastShuffleUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val unShuffleF: (Intent?) -> Unit
        get() = {
            sendBroadcastUnShuffleUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val loopF: (Intent?) -> Unit
        get() = {
            sendBroadcastLoopUI(PERM_PRIVATE_MINI_PLAYER)

        }
    override val loopAllF: (Intent?) -> Unit
        get() = {
            sendBroadcastLoopAllUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val notLoopF: (Intent?) -> Unit
        get() = {
            sendBroadcastNotLoopUI(PERM_PRIVATE_MINI_PLAYER)
        }

    override val songArtistF: (Intent?) -> Unit
        get() = {
            sendBroadcastSongArtistUI(PERM_PRIVATE_MINI_PLAYER, "David Guetta")
        }

    override val songNameF: (Intent?) -> Unit
        get() = {
            sendBroadcastSongNameUI(PERM_PRIVATE_MINI_PLAYER, "Clap Your Hands")
        }

    override val songHQF: (Intent?) -> Unit
        get() = {
            sendBroadcastSongHQUI(PERM_PRIVATE_MINI_PLAYER, true)
        }

    override val songDurationF: (Intent?) -> Unit
        get() = {
            sendBroadcastSongDurationUI(PERM_PRIVATE_MINI_PLAYER, 127)
        }
}