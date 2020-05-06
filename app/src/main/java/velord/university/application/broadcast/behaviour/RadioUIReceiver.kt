package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub

interface RadioUIReceiver {

    val TAG: String

    fun getRadioReceiverList() = arrayOf(
        Pair(stopRadio(), AppBroadcastHub.Action.stopRadioService),
        Pair(playRadio(), AppBroadcastHub.Action.playRadioService),
        Pair(likeRadio(), AppBroadcastHub.Action.likeRadioService),
        Pair(unlikeRadio(), AppBroadcastHub.Action.unlikeRadioService)
    )

    val stopRadioF: (Intent?) -> Unit
    fun stopRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            stopRadioF(intent)
        }
    }

    val playRadioF: (Intent?) -> Unit
    fun playRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playRadioF(intent)
        }
    }

    val likeRadioF: (Intent?) -> Unit
    fun likeRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            likeRadioF(intent)
        }
    }

    val unlikeRadioF: (Intent?) -> Unit
    fun unlikeRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            unlikeRadioF(intent)
        }
    }
}