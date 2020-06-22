package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub

interface MiniPlayerIconReceiver {

    val TAG: String

    fun getIconReceiverList() = arrayOf(
        Pair(iconClicked(), AppBroadcastHub.Action.clickOnIcon)
    )

    val iconClicked: (Intent?) -> Unit
    fun iconClicked() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            iconClicked(intent)
        }
    }
}