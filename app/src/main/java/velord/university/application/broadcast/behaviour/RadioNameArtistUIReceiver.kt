package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastAction

interface RadioNameArtistUIReceiver {

    val TAG: String

    fun getRadioNameArtistUIReceiverList() = arrayOf(
        Pair(nameRadio(), BroadcastAction.radioNameUI),
        Pair(artistRadio(), BroadcastAction.radioArtistUI)
    )

    val nameRadioUIF: (Intent?) -> Unit
    fun nameRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            nameRadioUIF(intent)
        }
    }

    val artistRadioUIF: (Intent?) -> Unit
    fun artistRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            artistRadioUIF(intent)
        }
    }
}