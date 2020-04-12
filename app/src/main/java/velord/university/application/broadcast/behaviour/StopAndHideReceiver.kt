package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.MiniPlayerBroadcastHub

interface MiniPlayerBroadcastReceiverShowAndHider {

    val TAG: String

    fun receiverList() = arrayOf(
        Pair(show(), MiniPlayerBroadcastHub.Action.showUI),
        Pair(hide(), MiniPlayerBroadcastHub.Action.hideUI)
    )

    val showF: (Intent?) -> Unit
    fun show() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            showF(intent)
        }
    }

    val hideF: (Intent?) -> Unit
    fun hide() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            hideF(intent)
        }
    }
}