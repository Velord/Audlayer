package velord.university.application.service

import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.MiniPlayerBroadcastHub
import velord.university.application.broadcast.MiniPlayerBroadcastHub.likeUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.skipNextUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.skipPrevUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.songDurationUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.unlikeUI
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.MiniPlayerServiceReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.model.converter.SongTimeConverter

class MiniPlayerServiceBroadcastReceiver :
    MiniPlayerService(),
    MiniPlayerServiceReceiver {

    override val TAG: String = "MnPlyrSrvcBrdcstRcvrs"

    private val receivers = MiniPlayerBroadcastHub
        .miniPlayerReceiver(this as MiniPlayerServiceReceiver)

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
                val extra = MiniPlayerBroadcastHub.Extra.playByPathService
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
            likeUI()
        }

    override val unlikeF: (Intent?) -> Unit
        get() = {
            super.unlikeSong()
           unlikeUI()
        }

    override val skipNextF: (Intent?) -> Unit
        get() = {
            super.skipSongAndPlayNext()
           skipNextUI()
        }

    override val skipPrevF: (Intent?) -> Unit
        get() = {
            super.skipSongAndPlayPrevious()
            skipPrevUI()
        }
    //get in seconds cause view does not operate at milliseconds
    override val rewindF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =MiniPlayerBroadcastHub.Extra.rewindService
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
            songDurationUI(127)

        }

    override val playAllInFolderF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastHub.Extra.folderPathService
                val path = it.getStringExtra(extra)
                super<MiniPlayerService>.playAllInFolder(path)
            }
        }

    override val playNextAllInFolderF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastHub.Extra.folderPathService
                val path = it.getStringExtra(extra)
                super<MiniPlayerService>.playNextAllInFolder(path)
            }
        }

    override val shuffleAndPlayAllInFolderF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastHub.Extra.folderPathService
                val path = it.getStringExtra(extra)
                super<MiniPlayerService>.shuffleAndPlayAllInFolder(path)
            }
        }

    override val addToQueueF: (Intent?) -> Unit
        get() = {
            it?.let {
                val extra =
                    MiniPlayerBroadcastHub.Extra.folderPathService
                val path = it.getStringExtra(extra)
                super.addToQueueOneSong(path)
            }
        }

    override val getInfoF: (Intent?) -> Unit
        get() = {
            super.getInfoFromServiceToUI()
        }
}