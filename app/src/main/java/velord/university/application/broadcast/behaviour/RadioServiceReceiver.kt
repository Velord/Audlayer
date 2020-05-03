package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.MiniPlayerBroadcastHub

interface RadioServiceReceiver {

    val TAG: String

    fun receiverList() = arrayOf(
        Pair(playByUrl(), MiniPlayerBroadcastHub.Action.playByUrlRadioService),
        Pair(stop(), MiniPlayerBroadcastHub.Action.stopRadioService),
        Pair(play(), MiniPlayerBroadcastHub.Action.playRadioService),
        Pair(like(), MiniPlayerBroadcastHub.Action.likeRadioService),
        Pair(unlike(), MiniPlayerBroadcastHub.Action.unlikeRadioService),
        Pair(getInfo(), MiniPlayerBroadcastHub.Action.getInfoRadioService),
        Pair(playOrStop(), MiniPlayerBroadcastHub.Action.playOrStopRadioService)
    )

    val playByUrlF: (Intent?) -> Unit
    fun playByUrl() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playByUrlF(intent)
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