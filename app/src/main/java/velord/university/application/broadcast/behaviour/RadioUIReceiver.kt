package velord.university.application.broadcast.behaviour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastAction

interface RadioUIReceiver:
    RadioNameArtistUIReceiver,
    RadioUnavailableUIReceiver{

    fun getRadioUIReceiverList() =
        getRadioNameArtistUIReceiverList() +
                getRadioUnavailableUIReceiverList() +
                arrayOf(
                    Pair(stopRadio(), BroadcastAction.stopRadioUI),
                    Pair(playRadio(), BroadcastAction.playRadioUI),
                    Pair(likeRadio(), BroadcastAction.likeRadioUI),
                    Pair(unlikeRadio(), BroadcastAction.unlikeRadioUI),
                    Pair(iconRadio(), BroadcastAction.iconRadioUI),
                    Pair(radioPlayerUnavailable(), BroadcastAction.radioPlayerUnavailableUI),
                    Pair(radioUrlIsWrongUI(), BroadcastAction.radioUrlIsWrongUI)
                )

    val iconRadioUIF: (Intent?) -> Unit
    fun iconRadio() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG, "received broadcast: ${intent?.action}")
            iconRadioUIF(intent)
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