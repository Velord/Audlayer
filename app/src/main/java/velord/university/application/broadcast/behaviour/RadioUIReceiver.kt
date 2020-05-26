package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub

interface RadioUIReceiver {

    val TAG: String

    fun getRadioUIReceiverList() = arrayOf(
        Pair(stopRadio(), AppBroadcastHub.Action.stopRadioUI),
        Pair(playRadio(), AppBroadcastHub.Action.playRadioUI),
        Pair(likeRadio(), AppBroadcastHub.Action.likeRadioUI),
        Pair(unlikeRadio(), AppBroadcastHub.Action.unlikeRadioUI),
        Pair(nameRadio(), AppBroadcastHub.Action.radioNameUI),
        Pair(artistRadio(), AppBroadcastHub.Action.radioArtistUI),
        Pair(showRadio(), AppBroadcastHub.Action.showMiniPlayerRadioUI),
        Pair(iconRadio(), AppBroadcastHub.Action.iconRadioUI)
    )

    val iconRadioUIF: (Intent?) -> Unit
    fun iconRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            iconRadioUIF(intent)
        }
    }

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

    val showRadioUIF: (Intent?) -> Unit
    fun showRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            showRadioUIF(intent)
        }
    }

    val stopRadioUIF: (Intent?) -> Unit
    fun stopRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            stopRadioUIF(intent)
        }
    }

    val playRadioUIF: (Intent?) -> Unit
    fun playRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            playRadioUIF(intent)
        }
    }

    val likeRadioUIF: (Intent?) -> Unit
    fun likeRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            likeRadioUIF(intent)
        }
    }

    val unlikeRadioUIF: (Intent?) -> Unit
    fun unlikeRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            unlikeRadioUIF(intent)
        }
    }
}