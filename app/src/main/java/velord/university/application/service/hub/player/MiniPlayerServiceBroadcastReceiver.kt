package velord.university.application.service.hub.player

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.application.broadcast.hub.AppBroadcastHub.songDurationUI
import velord.university.application.broadcast.behaviour.MiniPlayerServiceReceiver
import velord.university.application.broadcast.hub.*
import velord.university.model.converter.SongTimeConverter

class MiniPlayerServiceBroadcastReceiver :
    MiniPlayerService(),
    MiniPlayerServiceReceiver {

    override val TAG: String = "MnPlyrSrvcBrdcstRcvrs"

    private val receivers = receiverList()

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()

        receivers.forEach {
            unregisterBroadcastReceiver(it.first)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        receivers.forEach {
            baseContext.registerBroadcastReceiver(
                it.first, IntentFilter(it.second), PERM_PRIVATE_MINI_PLAYER
            )
        }

        return START_STICKY
    }

    override val playByPathF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra = BroadcastExtra.playByPathService
                val path = it.getStringExtra(extra)!!
                super<MiniPlayerService>.playByPath(path)
            }
        }
    }

    override val stopF: (Intent?) -> Unit = {
        scope.launch {
            super.pausePlayer()
        }
    }

    override val playF: (Intent?) -> Unit = {
        scope.launch {
            super.playSongAfterCreatedPlayer()
        }
    }

    override val likeF: (Intent?) -> Unit = {
        scope.launch {
            super.likeSong()
            AppBroadcastHub.run {
                doAction(BroadcastActionType.LIKE_PLAYER_UI)
            }
        }
    }

    override val unlikeF: (Intent?) -> Unit = {
        scope.launch {
            super.unlikeSong()
            AppBroadcastHub.run {
                doAction(BroadcastActionType.UNLIKE_PLAYER_UI)
            }
        }
    }

    override val skipNextF: (Intent?) -> Unit = {
        scope.launch {
            super.skipSongAndPlayNext()
            AppBroadcastHub.run {
                doAction(BroadcastActionType.SKIP_PLAYER_UI)
            }
        }
    }

    override val skipPrevF: (Intent?) -> Unit = {
        scope.launch {
            super.skipSongAndPlayPrevious()
            AppBroadcastHub.run {
                doAction(BroadcastActionType.SKIP_PREV_PLAYER_UI)
            }
        }
    }
    //get in seconds cause view does not operate at milliseconds
    override val rewindF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra =BroadcastExtra.rewindService
                val value = it.getIntExtra(extra, 0)
                val milliseconds = SongTimeConverter.secondsToMilliseconds(value)
                super.rewindPlayer(milliseconds)
            }
        }
    }

    override val shuffleF: (Intent?) -> Unit = {
        scope.launch {
            super.shuffleOn()
        }
    }

    override val unShuffleF: (Intent?) -> Unit = {
        scope.launch {
            super.shuffleOff()
        }
    }

    override val loopF: (Intent?) -> Unit = {
        scope.launch {
            super.loopState()
        }
    }

    override val loopAllF: (Intent?) -> Unit = {
        scope.launch {
            super.loopAllState()
        }
    }

    override val notLoopF: (Intent?) -> Unit = {
        scope.launch {
            super.notLoopState()
        }
    }

    override val songDurationF: (Intent?) -> Unit = {
        scope.launch {
            songDurationUI(127)
        }
    }

    override val playAllInFolderF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra = BroadcastExtra.folderPathService
                val path = it.getStringExtra(extra)!!
                super<MiniPlayerService>.playAllInFolder(path)
            }
        }
    }

    override val playNextAllInFolderF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra = BroadcastExtra.folderPathService
                val path = it.getStringExtra(extra)!!
                super<MiniPlayerService>.playNextAllInFolder(path)
            }
        }
    }

    override val shuffleAndPlayAllInFolderF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra = BroadcastExtra.folderPathService
                val path = it.getStringExtra(extra)!!
                super<MiniPlayerService>.shuffleAndPlayAllInFolder(path)
            }
        }
    }

    override val addToQueueF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra = BroadcastExtra.folderPathService
                val path = it.getStringExtra(extra)!!
                super.addToQueueOneSong(path)
            }
        }
    }

    override val getInfoF: (Intent?) -> Unit = {
        scope.launch {
            super.getInfoFromServiceToUI()
        }
    }

    override val playOrStopF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                super.playOrStopService()
            }
        }
    }
}