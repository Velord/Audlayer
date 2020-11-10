package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastAction

interface RadioUnavailableUIReceiver {

    abstract val TAG: String

    fun getRadioUnavailableUIReceiverList() = arrayOf(
        Pair(radioPlayerUnavailable(), BroadcastAction.radioPlayerUnavailableUI),
        Pair(radioUrlIsWrongUI(), BroadcastAction.radioUrlIsWrongUI)
    )

    val radioUrlIsWrongUIF: (Intent?) -> Unit
    fun radioUrlIsWrongUI() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            radioUrlIsWrongUIF(intent)
        }
    }

    val radioPlayerUnavailableUIF: (Intent?) -> Unit
    fun radioPlayerUnavailable() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            radioPlayerUnavailableUIF(intent)
        }
    }
}