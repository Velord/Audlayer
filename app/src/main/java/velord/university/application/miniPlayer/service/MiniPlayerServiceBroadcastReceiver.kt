package velord.university.application.miniPlayer.service

import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import velord.university.application.miniPlayer.broadcast.*
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastLike.sendBroadcastLikeUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastLoop.sendBroadcastLoopUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastLoopAll.sendBroadcastLoopAllUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastNotLoop.sendBroadcastNotLoopUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastShuffle.sendBroadcastShuffleUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSkipNext.sendBroadcastSkipNextUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSkipPrev.sendBroadcastSkipPrevUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSongDuration.sendBroadcastSongDurationUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastUnShuffle.sendBroadcastUnShuffleUI
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastUnlike.sendBroadcastUnlikeUI
import velord.university.model.QueueResolver

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
        Pair(notLoop(), MiniPlayerBroadcastNotLoop.filterService),
        Pair(playAllInFolder(), MiniPlayerBroadcastPlayAllInFolder.filterService),
        Pair(playNextAllInFolder(), MiniPlayerBroadcastPlayNextAllInFolder.filterService),
        Pair(shuffleAndPlayAllInFolder(), MiniPlayerBroadcastShuffleAndPlayAllInFolder.filterService),
        Pair(addToQueue(), MiniPlayerBroadcastAddToQueue.filterService)
    )

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
                super<MiniPlayerService>.playByPath(path)
            }
        }

    override val stopF: (Intent?) -> Unit
        get() = {
            super<MiniPlayerService>.pausePlayer()
        }

    override val playF: (Intent?) -> Unit
        get() = {
            super<MiniPlayerService>.playSongAfterCreatedPlayer()
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
            super<MiniPlayerService>.skipSongAndPlayNext()
            sendBroadcastSkipNextUI()
        }

    override val skipPrevF: (Intent?) -> Unit
        get() = {
            super<MiniPlayerService>.skipSongAndPlayPrevious()
            sendBroadcastSkipPrevUI()
        }

    override val rewindF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra = MiniPlayerBroadcastRewind.extraValueService
                val value = it.getIntExtra(extra, 0)
                super<MiniPlayerService>.rewindPlayer(value)
            }
        }

    override val shuffleF: (Intent?) -> Unit
        get() = {
            super.shuffleOn()
            sendBroadcastShuffleUI()
        }

    override val unShuffleF: (Intent?) -> Unit
        get() = {
            super.shuffleOff()
            sendBroadcastUnShuffleUI()
        }

    override val loopF: (Intent?) -> Unit
        get() = {
            QueueResolver.loopState()
            sendBroadcastLoopUI()
        }
    override val loopAllF: (Intent?) -> Unit
        get() = {
            QueueResolver.loopAllState()
            sendBroadcastLoopAllUI()
        }

    override val notLoopF: (Intent?) -> Unit
        get() = {
            QueueResolver.notLoopState()
            sendBroadcastNotLoopUI()
        }

    override val songDurationF: (Intent?) -> Unit
        get() = {
            sendBroadcastSongDurationUI(127)
        }

    override val playAllInFolderF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastPlayAllInFolder.extraValueService
                val path = it.getStringExtra(extra)
                super<MiniPlayerService>.playAllInFolder(path)
            }
        }

    override val playNextAllInFolderF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastPlayNextAllInFolder.extraValueService
                val path = it.getStringExtra(extra)
                super<MiniPlayerService>.playNextAllInFolder(path)
            }
        }

    override val shuffleAndPlayAllInFolderF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastShuffleAndPlayAllInFolder.extraValueService
                val path = it.getStringExtra(extra)
                super<MiniPlayerService>.shuffleAndPlayAllInFolder(path)
            }
        }

    override val addToQueueF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastAddToQueue.extraValueService
                val path = it.getStringExtra(extra)
                super<MiniPlayerService>.addToQueue(path)
            }
        }

}