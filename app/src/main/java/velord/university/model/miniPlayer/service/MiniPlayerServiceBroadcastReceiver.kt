package velord.university.model.miniPlayer.service

import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import velord.university.model.miniPlayer.broadcast.*
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastLike.sendBroadcastLikeUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastLoop.sendBroadcastLoopUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastLoopAll.sendBroadcastLoopAllUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastNotLoop.sendBroadcastNotLoopUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastPlay.sendBroadcastPlayUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastRewind.sendBroadcastRewindUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastShuffle.sendBroadcastShuffleUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSkipNext.sendBroadcastSkipNextUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSkipPrev.sendBroadcastSkipPrevUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastSongDuration.sendBroadcastSongDurationUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastStop.sendBroadcastStopUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastUnShuffle.sendBroadcastUnShuffleUI
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastUnlike.sendBroadcastUnlikeUI

class MiniPlayerServiceBroadcastReceiver : MiniPlayerService(), MiniPlayerBroadcastReceiverService {

    override val TAG: String
        get() = "MnPlyrSrvcBrdcstRcvrs"

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val receivers = arrayOf(
        Pair(playByPath(), MiniPlayerBroadcastPlayByPath.filterService),
        Pair(stop(), MiniPlayerBroadcastStop.filterService),
        Pair(play(), MiniPlayerBroadcastPlay.filterService),
        Pair(like(), MiniPlayerBroadcastLike.filterService),
        Pair(unlike(), MiniPlayerBroadcastUnlike.filterService),
        Pair(shuffle(), MiniPlayerBroadcastShuffle.filterService),
        Pair(unShuffle(), MiniPlayerBroadcastUnShuffle.filterService),
        Pair(skipNext(), MiniPlayerBroadcastSkipNext.filterService),
        Pair(skipPrev(), MiniPlayerBroadcastSkipPrev.filterService),
        Pair(rewind(), MiniPlayerBroadcastRewind.filterService),
        Pair(loop(), MiniPlayerBroadcastLoop.filterService),
        Pair(loopAll(), MiniPlayerBroadcastLoopAll.filterService),
        Pair(notLoop(), MiniPlayerBroadcastNotLoop.filterService))

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

        return START_STICKY
    }

    override val playByPathF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra = MiniPlayerBroadcastPlayByPath.extraValueService
                val path = it.getStringExtra(extra)
                playByPath(path)
            }
        }

    override val stopF: (Intent?) -> Unit
        get() = {
            sendBroadcastStopUI()
        }

    override val playF: (Intent?) -> Unit
        get() = {
            sendBroadcastPlayUI()
        }

    override val likeF: (Intent?) -> Unit
        get() = {
            sendBroadcastLikeUI()
        }

    override val unlikeF: (Intent?) -> Unit
        get() = {
            sendBroadcastUnlikeUI()
        }

    override val skipNextF: (Intent?) -> Unit
        get() = {

            sendBroadcastSkipNextUI()
        }

    override val skipPrevF: (Intent?) -> Unit
        get() = {
            sendBroadcastSkipPrevUI()
        }

    override val rewindF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra = MiniPlayerBroadcastRewind.extraValueService
                val value = it.getIntExtra(extra, 0)
                sendBroadcastRewindUI(value)
            }
        }

    override val shuffleF: (Intent?) -> Unit
        get() = {
            sendBroadcastShuffleUI()
        }

    override val unShuffleF: (Intent?) -> Unit
        get() = {
            sendBroadcastUnShuffleUI()
        }

    override val loopF: (Intent?) -> Unit
        get() = {
            sendBroadcastLoopUI()

        }
    override val loopAllF: (Intent?) -> Unit
        get() = {
            sendBroadcastLoopAllUI()
        }

    override val notLoopF: (Intent?) -> Unit
        get() = {
            sendBroadcastNotLoopUI()
        }

    override val songDurationF: (Intent?) -> Unit
        get() = {
            sendBroadcastSongDurationUI(127)
        }
}