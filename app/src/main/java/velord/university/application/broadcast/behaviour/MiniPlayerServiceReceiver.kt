package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub

interface MiniPlayerServiceReceiver {

    val TAG: String

    fun receiverList() = arrayOf(
        Pair(playByPath(), AppBroadcastHub.Action.playByPathService),
        Pair(stop(), AppBroadcastHub.Action.stopService),
        Pair(play(), AppBroadcastHub.Action.playService),
        Pair(like(), AppBroadcastHub.Action.likeService),
        Pair(unlike(), AppBroadcastHub.Action.unlikeService),
        Pair(shuffle(), AppBroadcastHub.Action.shuffleService),
        Pair(unShuffle(), AppBroadcastHub.Action.unShuffleService),
        Pair(skipNext(), AppBroadcastHub.Action.skipNextService),
        Pair(skipPrev(), AppBroadcastHub.Action.skipPrevService),
        Pair(rewind(), AppBroadcastHub.Action.rewindService),
        Pair(loop(), AppBroadcastHub.Action.loopService),
        Pair(loopAll(), AppBroadcastHub.Action.loopAllService),
        Pair(notLoop(), AppBroadcastHub.Action.notLoopService),
        Pair(playAllInFolder(), AppBroadcastHub.Action.playAllInFolderService),
        Pair(playNextAllInFolder(), AppBroadcastHub.Action.playNextAllInFolderService),
        Pair(shuffleAndPlayAllInFolder(), AppBroadcastHub.Action.shuffleAndPlayAllInFolderService),
        Pair(addToQueue(), AppBroadcastHub.Action.addToQueueService),
        Pair(getInfo(), AppBroadcastHub.Action.getInfoService),
        Pair(playOrStop(), AppBroadcastHub.Action.playOrStopService)
    )

    val playByPathF: (Intent?) -> Unit
    fun playByPath() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playByPathF(intent)
        }
    }

    val stopF: (Intent?) -> Unit
    fun stop() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            stopF(intent)
        }
    }

    val playF: (Intent?) -> Unit
    fun play() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playF(intent)
        }
    }

    val likeF: (Intent?) -> Unit
    fun like() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            likeF(intent)
        }
    }

    val unlikeF: (Intent?) -> Unit
    fun unlike() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            unlikeF(intent)
        }
    }

    val skipNextF: (Intent?) -> Unit
    fun skipNext() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            skipNextF(intent)
        }
    }

    val skipPrevF: (Intent?) -> Unit
    fun skipPrev() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            skipPrevF(intent)
        }
    }

    val rewindF: (Intent?) -> Unit
    fun rewind() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            rewindF(intent)
        }
    }

    val shuffleF: (Intent?) -> Unit
    fun shuffle() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            shuffleF(intent)
        }
    }

    val unShuffleF: (Intent?) -> Unit
    fun unShuffle() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            unShuffleF(intent)
        }
    }

    val loopF: (Intent?) -> Unit
    fun loop() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            loopF(intent)
        }
    }

    val loopAllF: (Intent?) -> Unit
    fun loopAll() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            loopAllF(intent)
        }
    }

    val notLoopF: (Intent?) -> Unit
    fun notLoop() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            notLoopF(intent)
        }
    }

    val songDurationF: (Intent?) -> Unit
    fun songDuration() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            songDurationF(intent)
        }
    }

    val playAllInFolderF: (Intent?) -> Unit
    fun playAllInFolder() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playAllInFolderF(intent)
        }
    }

    val playNextAllInFolderF: (Intent?) -> Unit
    fun playNextAllInFolder() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playNextAllInFolderF(intent)
        }
    }

    val shuffleAndPlayAllInFolderF: (Intent?) -> Unit
    fun shuffleAndPlayAllInFolder() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            shuffleAndPlayAllInFolderF(intent)
        }
    }

    val addToQueueF: (Intent?) -> Unit
    fun addToQueue() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            addToQueueF(intent)
        }
    }

    val getInfoF: (Intent?) -> Unit
    fun getInfo() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            getInfoF(intent)
        }
    }

    val playOrStopF: (Intent?) -> Unit
    fun playOrStop() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playOrStopF(intent)
        }
    }
}