package velord.university.application.service.radio

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.application.broadcast.hub.AppBroadcastHub.likeRadioUI
import velord.university.application.broadcast.hub.AppBroadcastHub.unlikeRadioUI
import velord.university.application.broadcast.behaviour.RadioServiceReceiver
import velord.university.application.broadcast.hub.*

class RadioServiceBroadcastReceiver :
    RadioService(),
    RadioServiceReceiver {

    override val TAG: String = "RadioServiceBrdcstRcvr"

    private val receivers = receiverServiceList()

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()

        receivers.forEach {
            unregisterBroadcastReceiver(it.first)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        receivers.forEach {
            baseContext.registerBroadcastReceiver(
                it.first, IntentFilter(it.second), PERM_PRIVATE_RADIO
            )
        }

        return START_STICKY
    }

    override val playByUrlRadioF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                val extra = BroadcastExtra.radioStationUrlService
                val path = it.getStringExtra(extra)!!
                super<RadioService>.playByUrl(path)
            }
        }
    }

    override val stopRadioF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                super.pausePlayer()
            }
        }
    }

    override val playRadioF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                super.playRadioIfCan()
            }
        }
    }

    override val likeRadioF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                super.likeRadio()
                this@RadioServiceBroadcastReceiver.likeRadioUI()
            }
        }
    }

    override val unlikeRadioF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                super.unlikeRadio()
                this@RadioServiceBroadcastReceiver.unlikeRadioUI()
            }
        }
    }

    override val getInfoRadioF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                super.getInfoFromServiceToUI()
            }
        }
    }

    override val playOrStopRadioF: (Intent?) -> Unit = {
        scope.launch {
            it?.let {
                super.playOrStopService()
            }
        }
    }
}