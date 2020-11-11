package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.hub.BroadcastAction


interface MiniPlayerUIReceiver {

    val TAG: String

    fun miniPlayerUIReceiverList() = arrayOf(
        Pair(stop(), BroadcastAction.stopUI),
        Pair(play(), BroadcastAction.playUI),
        Pair(like(), BroadcastAction.likeUI),
        Pair(unlike(), BroadcastAction.unlikeUI),
        Pair(shuffle(), BroadcastAction.shuffleUI),
        Pair(unShuffle(), BroadcastAction.unShuffleUI),
        Pair(skipNext(), BroadcastAction.skipNextUI),
        Pair(skipPrev(), BroadcastAction.skipPrevUI),
        Pair(rewind(), BroadcastAction.rewindUI),
        Pair(loop(), BroadcastAction.loopUI),
        Pair(loopAll(), BroadcastAction.loopAllUI),
        Pair(notLoop(), BroadcastAction.notLoopUI),
        Pair(songName(), BroadcastAction.songNameUI),
        Pair(songDuration(), BroadcastAction.songDurationUI),
        Pair(songArtist(), BroadcastAction.songArtistUI),
        Pair(songHQ(), BroadcastAction.songHQUI),
        Pair(icon(), BroadcastAction.iconUI),
        Pair(playerUnavailable(), BroadcastAction.playerUnavailableUI)
    )

    val playerUnavailableUIF: (Intent?) -> Unit
    fun playerUnavailable() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playerUnavailableUIF(intent)
        }
    }

    val iconF: (Intent?) -> Unit
    fun icon() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            iconF(intent)
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

    val songArtistF: (Intent?) -> Unit
    fun songArtist() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            songArtistF(intent)
        }
    }

    val songNameF: (Intent?) -> Unit
    fun songName() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            songNameF(intent)
        }
    }

    val songHQF: (Intent?) -> Unit
    fun songHQ() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            songHQF(intent)
        }
    }

    val songDurationF: (Intent?) -> Unit
    fun songDuration() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            songDurationF(intent)
        }
    }
}