package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastAction

interface MiniPlayerIconClickReceiver {

    val TAG: String

    fun getIconClickedReceiverList() = arrayOf(
        Pair(iconClicked(), BroadcastAction.clickOnIcon)
    )

    val iconClicked: (Intent?) -> Unit
    fun iconClicked() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            iconClicked(intent)
        }
    }
}