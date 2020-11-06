package velord.university.application.service.hub.player

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
                it.first, IntentFilter(it.second), PERM_PRIVATE_MINI_PLAYER)
        }

        return START_STICKY
    }

    override val playByPathF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra = AppBroadcastHub.Extra.playByPathService
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
            likeUI()
        }
    }

    override val unlikeF: (Intent?) -> Unit = {
        scope.launch {
            super.unlikeSong()
            unlikeUI()
        }
    }

    override val skipNextF: (Intent?) -> Unit = {
        scope.launch {
            super.skipSongAndPlayNext()
            skipNextUI()
        }
    }

    override val skipPrevF: (Intent?) -> Unit = {
        scope.launch {
            super.skipSongAndPlayPrevious()
            skipPrevUI()
        }
    }
    //get in seconds cause view does not operate at milliseconds
    override val rewindF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra =AppBroadcastHub.Extra.rewindService
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
                val extra =
                    AppBroadcastHub.Extra.folderPathService
                val path = it.getStringExtra(extra)!!
                super<MiniPlayerService>.playAllInFolder(path)
            }
        }
    }

    override val playNextAllInFolderF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra =
                    AppBroadcastHub.Extra.folderPathService
                val path = it.getStringExtra(extra)!!
                super<MiniPlayerService>.playNextAllInFolder(path)
            }
        }
    }

    override val shuffleAndPlayAllInFolderF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra =
                    AppBroadcastHub.Extra.folderPathService
                val path = it.getStringExtra(extra)!!
                super<MiniPlayerService>.shuffleAndPlayAllInFolder(path)
            }
        }
    }

    override val addToQueueF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra =
                    AppBroadcastHub.Extra.folderPathService
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