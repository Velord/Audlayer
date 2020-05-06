package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub

interface RadioServiceReceiver {

    val TAG: String

    fun receiverList() = arrayOf(
        Pair(playByUrl(), AppBroadcastHub.Action.playByUrlRadioService),
        Pair(stop(), AppBroadcastHub.Action.stopRadioService),
        Pair(play(), AppBroadcastHub.Action.playRadioService),
        Pair(like(), AppBroadcastHub.Action.likeRadioService),
        Pair(unlike(), AppBroadcastHub.Action.unlikeRadioService),
        Pair(getInfo(), AppBroadcastHub.Action.getInfoRadioService),
        Pair(playOrStop(), AppBroadcastHub.Action.playOrStopRadioService)
    )

    val playByUrlRadioF: (Intent?) -> Unit
    fun playByUrl() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playByUrlRadioF(intent)
        }
    }

    val stopRadioF: (Intent?) -> Unit
    fun stop() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            stopRadioF(intent)
        }
    }

    val playRadioF: (Intent?) -> Unit
    fun play() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playRadioF(intent)
        }
    }

    val likeRadioF: (Intent?) -> Unit
    fun like() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            likeRadioF(intent)
        }
    }

    val unlikeRadioF: (Intent?) -> Unit
    fun unlike() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            unlikeRadioF(intent)
        }
    }

    val getInfoRadioF: (Intent?) -> Unit
    fun getInfo() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            getInfoRadioF(intent)
        }
    }

    val playOrStopRadioF: (Intent?) -> Unit
    fun playOrStop() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playOrStopRadioF(intent)
        }
    }
}