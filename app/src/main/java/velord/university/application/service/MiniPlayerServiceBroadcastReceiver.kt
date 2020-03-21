package velord.university.application.service

import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.*
import velord.university.application.broadcast.MiniPlayerBroadcastLike.sendBroadcastLikeUI
import velord.university.application.broadcast.MiniPlayerBroadcastSkipNext.sendBroadcastSkipNextUI
import velord.university.application.broadcast.MiniPlayerBroadcastSkipPrev.sendBroadcastSkipPrevUI
import velord.university.application.broadcast.MiniPlayerBroadcastSongDuration.sendBroadcastSongDurationUI
import velord.university.application.broadcast.MiniPlayerBroadcastUnlike.sendBroadcastUnlikeUI
import velord.university.model.converter.SongTimeConverter

class MiniPlayerServiceBroadcastReceiver :
    MiniPlayerService(),
    MiniPlayerBroadcastReceiverService {

    override val TAG: String = "MnPlyrSrvcBrdcstRcvrs"

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
        Pair(addToQueue(), MiniPlayerBroadcastAddToQueue.filterService),
        Pair(getInfo(), MiniPlayerBroadcastGetInfo.filterService)
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
            super.pausePlayer()
        }

    override val playF: (Intent?) -> Unit
        get() = {
            super.playSongAfterCreatedPlayer()
        }

    override val likeF: (Intent?) -> Unit
        get() = {
            super.likeSong()
            sendBroadcastLikeUI()
        }

    override val unlikeF: (Intent?) -> Unit
        get() = {
            super.unlikeSong()
            sendBroadcastUnlikeUI()
        }

    override val skipNextF: (Intent?) -> Unit
        get() = {
            super.skipSongAndPlayNext()
            sendBroadcastSkipNextUI()
        }

    override val skipPrevF: (Intent?) -> Unit
        get() = {
            super.skipSongAndPlayPrevious()
            sendBroadcastSkipPrevUI()
        }
    //get in seconds cause view does not operate at milliseconds
    override val rewindF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra = MiniPlayerBroadcastRewind.extraValueService
                val value = it.getIntExtra(extra, 0)
                val milliseconds = SongTimeConverter.secondsToMilliseconds(value)
                super.rewindPlayer(milliseconds)
            }
        }

    override val shuffleF: (Intent?) -> Unit
        get() = {
            super.shuffleOn()
        }

    override val unShuffleF: (Intent?) -> Unit
        get() = {
            super.shuffleOff()
        }

    override val loopF: (Intent?) -> Unit
        get() = {
            super.loopState()
        }
    override val loopAllF: (Intent?) -> Unit
        get() = {
            super.loopAllState()
        }

    override val notLoopF: (Intent?) -> Unit
        get() = {
            super.notLoopState()
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
                super<MiniPlayerService>.addToQueueOneSong(path)
            }
        }

    override val getInfoF: (Intent?) -> Unit
        get() = {
            super.getInfoFromServiceToUI()
        }
}