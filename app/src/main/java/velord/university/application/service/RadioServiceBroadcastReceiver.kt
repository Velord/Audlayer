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

    override val playByUrlF: (Intent?) -> Unit = {
        it?.let {
            val extra = AppBroadcastHub.Extra.playByRadioStationUrlService
            val path = it.getStringExtra(extra)
            super<RadioService>.playByUrl(path)
        }
    }

    override val stopF: (Intent?) -> Unit = {
        it?.let {
            super.pausePlayer()
        }
    }

    override val playF: (Intent?) -> Unit = {
        it?.let {
            super.playRadioAfterCreatedPlayer()
        }
    }

    override val likeF: (Intent?) -> Unit = {
        it?.let {
            super.likeRadio()
            this.likeRadioUI()
        }
    }

    override val unlikeF: (Intent?) -> Unit = {
        it?.let {
            super.unlikeRadio()
            this.unlikeRadioUI()
        }
    }

    override val getInfoF: (Intent?) -> Unit = {
        it?.let {
            super.getInfoFromServiceToUI()
        }
    }

    override val playOrStopF: (Intent?) -> Unit = {
        it?.let {
            super.playOrStopService()
        }
    }
}