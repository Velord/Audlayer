package velord.university.application.service

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.AppBroadcastHub.likeUI
import velord.university.application.broadcast.AppBroadcastHub.skipNextUI
import velord.university.application.broadcast.AppBroadcastHub.skipPrevUI
import velord.university.application.broadcast.AppBroadcastHub.songDurationUI
import velord.university.application.broadcast.AppBroadcastHub.unlikeUI
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.MiniPlayerServiceReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.model.converter.SongTimeConverter

class MiniPlayerServiceBroadcastReceiver :
    MiniPlayerService(),
    MiniPlayerServiceReceiver {

    override val TAG: String = "MnPlyrSrvcBrdcstRcvrs"

    private val receivers = receiverList()

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
                it.first, IntentFilter(it.second), PERM_PRIVATE_MINI_PLAYER)
        }

        return START_STICKY
    }

    override val playByPathF: (Intent?) -> Unit = {
        it?.let {
            val extra = AppBroadcastHub.Extra.playByPathService
            val path = it.getStringExtra(extra)
            super<MiniPlayerService>.playByPath(path)
        }
    }

    override val stopF: (Intent?) -> Unit = {
        super.pausePlayer()
    }

    override val playF: (Intent?) -> Unit = {
        super.playSongAfterCreatedPlayer()
    }

    override val likeF: (Intent?) -> Unit = {
        super.likeSong()
        likeUI()
    }

    override val unlikeF: (Intent?) -> Unit = {
        super.unlikeSong()
        unlikeUI()
    }

    override val skipNextF: (Intent?) -> Unit = {
        super.skipSongAndPlayNext()
        skipNextUI()
    }

    override val skipPrevF: (Intent?) -> Unit = {
        super.skipSongAndPlayPrevious()
        skipPrevUI()
    }
    //get in seconds cause view does not operate at milliseconds
    override val rewindF: (Intent?) -> Unit = {
        it?.let {
            val extra =AppBroadcastHub.Extra.rewindService
            val value = it.getIntExtra(extra, 0)
            val milliseconds = SongTimeConverter.secondsToMilliseconds(value)
            super.rewindPlayer(milliseconds)
        }
    }

    override val shuffleF: (Intent?) -> Unit = {
        super.shuffleOn()
    }

    override val unShuffleF: (Intent?) -> Unit = {
        super.shuffleOff()
    }

    override val loopF: (Intent?) -> Unit = {
        super.loopState()
    }
    override val loopAllF: (Intent?) -> Unit = {
        super.loopAllState()
    }

    override val notLoopF: (Intent?) -> Unit = {
        super.notLoopState()
    }

    override val songDurationF: (Intent?) -> Unit = {
        songDurationUI(127)

    }

    override val playAllInFolderF: (Intent?) -> Unit = {
        it?.let {
            val extra =
                AppBroadcastHub.Extra.folderPathService
            val path = it.getStringExtra(extra)
            super<MiniPlayerService>.playAllInFolder(path)
        }
    }

    override val playNextAllInFolderF: (Intent?) -> Unit = {
        it?.let {
            val extra =
                AppBroadcastHub.Extra.folderPathService
            val path = it.getStringExtra(extra)
            super<MiniPlayerService>.playNextAllInFolder(path)
        }
    }

    override val shuffleAndPlayAllInFolderF: (Intent?) -> Unit = {
        it?.let {
            val extra =
                AppBroadcastHub.Extra.folderPathService
            val path = it.getStringExtra(extra)
            super<MiniPlayerService>.shuffleAndPlayAllInFolder(path)
        }
    }

    override val addToQueueF: (Intent?) -> Unit = {
        it?.let {
            val extra =
                AppBroadcastHub.Extra.folderPathService
            val path = it.getStringExtra(extra)
            super.addToQueueOneSong(path)
        }
    }

    override val getInfoF: (Intent?) -> Unit = {
        super.getInfoFromServiceToUI()
    }

    override val playOrStopF: (Intent?) -> Unit = {
        it?.let {
            super.playOrStopService()
        }
    }
}