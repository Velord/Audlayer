package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastAction

interface RadioIconClickReceiver {

    val TAG: String

    fun getRadioIconReceiverList() = arrayOf(
        Pair(iconRadioClicked(), BroadcastAction.clickOnRadioIcon)
    )

    val iconRadioClicked: (Intent?) -> Unit
    fun iconRadioClicked() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            iconRadioClicked(intent)
        }
    }
}