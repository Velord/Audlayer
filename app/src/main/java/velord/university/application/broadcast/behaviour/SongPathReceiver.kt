package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.hub.BroadcastAction

interface SongPathReceiver {

    val TAG: String

    fun songPathReceiverList() = arrayOf(
        Pair(songPath(), BroadcastAction.playByPathUI)
    )

    val songPathF: (Intent?) -> Unit
    fun songPath() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            songPathF(intent)
        }
    }
}