package velord.university.application.service

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.AppBroadcastHub.likeRadioUI
import velord.university.application.broadcast.AppBroadcastHub.unlikeRadioUI
import velord.university.application.broadcast.PERM_PRIVATE_RADIO
import velord.university.application.broadcast.behaviour.RadioServiceReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver

class RadioServiceBroadcastReceiver :
    RadioService(),
    RadioServiceReceiver {

    override val TAG: String = "RadioServiceBrdcstRcvr"

    private val receivers = receiverList()

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()

        receivers.forEach {
            unregisterBroadcastReceiver(it.first)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")

        receivers.forEach {
            baseContext.registerBroadcastReceiver(
                it.first, IntentFilter(it.second), PERM_PRIVATE_RADIO
            )
        }

        return START_STICKY
    }

    override val playByUrlRadioF: (Intent?) -> Unit = {
        it?.let {
            val extra = AppBroadcastHub.Extra.playByRadioStationUrlService
            val path = it.getStringExtra(extra)
            super<RadioService>.playByUrl(path)
        }
    }

    override val stopRadioF: (Intent?) -> Unit = {
        it?.let {
            super.pausePlayer()
        }
    }

    override val playRadioF: (Intent?) -> Unit = {
        it?.let {
            super.playRadioAfterCreatedPlayer()
        }
    }

    override val likeRadioF: (Intent?) -> Unit = {
        it?.let {
            super.likeRadio()
            this.likeRadioUI()
        }
    }

    override val unlikeRadioF: (Intent?) -> Unit = {
        it?.let {
            super.unlikeRadio()
            this.unlikeRadioUI()
        }
    }

    override val getInfoRadioF: (Intent?) -> Unit = {
        it?.let {
            super.getInfoFromServiceToUI()
        }
    }

    override val playOrStopRadioF: (Intent?) -> Unit = {
        it?.let {
            super.playOrStopService()
        }
    }
}