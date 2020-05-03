package velord.university.application.service

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import velord.university.application.broadcast.MiniPlayerBroadcastHub
import velord.university.application.broadcast.MiniPlayerBroadcastHub.likeRadioUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.unlikeRadioUI
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
            val extra = MiniPlayerBroadcastHub.Extra.playByRadioStationUrlService
            val path = it.getStringExtra(extra)
            super<RadioService>.playByUrl(path)
        }
    }

    override val stopF: (Intent?) -> Unit = {
        super.pausePlayer()
    }

    override val playF: (Intent?) -> Unit = {
        super.playRadioAfterCreatedPlayer()
    }

    override val likeF: (Intent?) -> Unit = {
        super.likeRadio()
        this.likeRadioUI()
    }

    override val unlikeF: (Intent?) -> Unit = {
        super.unlikeRadio()
        this.unlikeRadioUI()
    }

    override val getInfoF: (Intent?) -> Unit = {
        super.getInfoFromServiceToUI()
    }

    override val playOrStopF: (Intent?) -> Unit = {
        it?.let {
            super.playOrStopService()
        }
    }
}