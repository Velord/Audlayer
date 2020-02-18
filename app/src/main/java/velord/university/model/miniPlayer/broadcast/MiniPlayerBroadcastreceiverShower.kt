package velord.university.model.miniPlayer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

interface MiniPlayerBroadcastreceiverShower {

    val TAG: String

    val showF: (Intent?) -> Unit
    fun show() = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            showF(intent)
        }
    }
}