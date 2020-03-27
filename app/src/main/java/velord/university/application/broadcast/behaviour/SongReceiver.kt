package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

interface SongReceiver {

    val TAG: String

    val songPathF: (Intent?) -> Unit
    fun songPath() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            songPathF(intent)
        }
    }
}