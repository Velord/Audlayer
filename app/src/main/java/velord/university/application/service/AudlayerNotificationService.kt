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
import velord.university.application.notification.MiniPlayerNotification

class AudlayerNotificationService : Service(),
    MiniPlayerUIReceiver,
    RadioUIReceiver {

    override val TAG = "WidgetService"

    private val receivers = miniPlayerUIReceiverList()

    private val receiversRadio = getRadioUIReceiverList()

    override val nameRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioNameUI
            val value = getStringExtra(extra)

            this@AudlayerNotificationService.mayInvokeRadio {
                MiniPlayerNotification
                    .updateSongTitle(this@AudlayerNotificationService, value)
            }
        }
    }

    override val artistRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioArtistUI
            val value = getStringExtra(extra)

            this@AudlayerNotificationService.mayInvokeRadio {
                MiniPlayerNotification
                    .updateSongArtist(this@AudlayerNotificationService, value)
            }
        }
    }

    override val stopRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            changeNotificationPlayOrStop(false)
        }
    }

    override val playRadioUIF: (Intent?) -> Unit = {
        it?.let {
            changeNotificationPlayOrStop(true)
        }
    }

    override val iconRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.iconRadioUI
            val value = getStringExtra(extra)

           MiniPlayerNotification
               .updateIcon(this@AudlayerNotificationService, value)
        }
    }

    override val iconF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.iconUI
            val value = getStringExtra(extra)

            MiniPlayerNotification
                .updateIcon(this@AudlayerNotificationService, value)
        }
    }

    override val stopF: (Intent?) -> Unit = {
        it?.apply {
            changeNotificationPlayOrStop(false)
        }
    }

    override val playF: (Intent?) -> Unit = {
        it?.apply {
            changeNotificationPlayOrStop(true)
        }
    }

    override val songArtistF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songArtistUI
            val value = getStringExtra(extra)

            this@AudlayerNotificationService.mayInvokeGeneral {
                MiniPlayerNotification
                    .updateSongArtist(this@AudlayerNotificationService, value)
            }
        }
    }

    override val songNameF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songNameUI
            val value = getStringExtra(extra)

            this@AudlayerNotificationService.mayInvokeGeneral {
                MiniPlayerNotification
                    .updateSongTitle(this@AudlayerNotificationService, value)
            }
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

        return START_STICKY
    }

    private fun changeNotificationPlayOrStop(isPlaying: Boolean) =
        MiniPlayerNotification.updatePlayOrStop(this, isPlaying)

    private fun restartService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "RestartNotificationService"
        broadcastIntent.setClass(this, RestarterNotificationService::class.java)
        this.sendBroadcast(broadcastIntent)
    }
}