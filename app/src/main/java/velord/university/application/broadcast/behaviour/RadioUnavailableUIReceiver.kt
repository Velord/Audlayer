package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub

interface RadioUnavailableUIReceiver {

    abstract val TAG: String

    fun getRadioUnavailableUIReceiverList() = arrayOf(
        Pair(radioPlayerUnavailable(), AppBroadcastHub.Action.radioPlayerUnavailableUI),
        Pair(radioUrlIsWrongUI(), AppBroadcastHub.Action.radioUrlIsWrongUI)
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