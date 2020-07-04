package velord.university.application.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import velord.university.application.broadcast.*
import velord.university.application.broadcast.behaviour.MiniPlayerUIReceiver
import velord.university.application.broadcast.behaviour.RadioUIReceiver
import velord.university.ui.widget.AudlayerWidget

class WidgetService : Service(),
    MiniPlayerUIReceiver,
    RadioUIReceiver {

    override val TAG = "WidgetService"

    private val receivers = miniPlayerUIReceiverList()

    private val receiversRadio = getRadioUIReceiverList()

    override val nameRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioNameUI
            val value = getStringExtra(extra)
            AudlayerWidget.widgetTitle = value

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val artistRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioArtistUI
            val value = getStringExtra(extra)
            AudlayerWidget.widgetArtist = value

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val stopRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            AudlayerWidget.widgetIsPlaying = false

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val iconRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.iconRadioUI
            val value = getStringExtra(extra)

            AudlayerWidget.iconIsSong = false
            AudlayerWidget.widgetIcon = value

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val playRadioUIF: (Intent?) -> Unit = {
        AudlayerWidget.widgetIsPlaying = true
    }

    override val iconF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.iconUI
            val value = getStringExtra(extra)

            AudlayerWidget.iconIsSong = true
            AudlayerWidget.widgetIcon = value

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val stopF: (Intent?) -> Unit = {
        it?.apply {
            AudlayerWidget.widgetIsPlaying = false

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val playF: (Intent?) -> Unit = {
        it?.apply {
            AudlayerWidget.widgetIsPlaying = true

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val songArtistF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songArtistUI
            val songArtist = getStringExtra(extra)
            AudlayerWidget.widgetArtist = songArtist

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    override val songNameF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songNameUI
            val value = getStringExtra(extra)
            AudlayerWidget.widgetTitle = value

            AudlayerWidget.invokeUpdate(this@WidgetService)
        }
    }

    //not need
    override val showRadioUIF: (Intent?) -> Unit = {
        it?.apply {
        }
    }
    override val likeRadioUIF: (Intent?) -> Unit = {

    }
    override val unlikeRadioUIF: (Intent?) -> Unit = {

    }
    override val radioPlayerUnavailableUIF: (Intent?) -> Unit = {
        it?.apply {
        }
    }
    override val radioUrlIsWrongUIF: (Intent?) -> Unit = {}

    override val playerUnavailableUIF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val showF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val likeF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val unlikeF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val skipNextF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val skipPrevF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val rewindF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val shuffleF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val unShuffleF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val loopF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val loopAllF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val notLoopF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val songHQF: (Intent?) -> Unit = {
        it?.apply {

        }
    }
    override val songDurationF: (Intent?) -> Unit = {
        it?.apply {

        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()

        AppBroadcastHub.apply {
            this@WidgetService.getInfoService()
            this@WidgetService.getInfoRadioService()
        }


        AudlayerWidget.invokeUpdate(this)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_SHORT).show()

        restartService()

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()

        receivers.forEach {
            this.applicationContext.unregisterBroadcastReceiver(it.first)
        }
        receiversRadio.forEach {
            this.applicationContext.unregisterBroadcastReceiver(it.first)
        }

        restartService()
    }

    override fun onStartCommand(intent: Intent?,
                                flags: Int,
                                startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        receivers.forEach {
            this.applicationContext.registerBroadcastReceiver(
                it.first,
                IntentFilter(it.second),
                PERM_PRIVATE_MINI_PLAYER
            )
        }
        receiversRadio.forEach {
            this.applicationContext.registerBroadcastReceiver(
                it.first,
                IntentFilter(it.second),
                PERM_PRIVATE_RADIO
            )
        }

        return START_STICKY
    }

    private fun restartService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "RestartWidgetService"
        broadcastIntent.setClass(this, RestarterWidgetService::class.java)
        this.sendBroadcast(broadcastIntent)
    }
}