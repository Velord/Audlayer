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
import velord.university.application.broadcast.behaviour.SongPathReceiver
import velord.university.application.broadcast.restarter.RestarterNotificationService
import velord.university.application.notification.MiniPlayerNotification
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.file.FileNameParser

class AudlayerNotificationService : Service(),
    MiniPlayerUIReceiver,
    RadioUIReceiver,
    SongPathReceiver {

    override val TAG = "WidgetService"

    private val receivers = miniPlayerUIReceiverList() +
            songPathReceiverList()

    private val receiversRadio = getRadioUIReceiverList()

    override val songPathF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songPathUI
            val songPath = getStringExtra(extra)

            val song = SongPlaylistInteractor.songs.find {
                it.file.path == songPath
            }

            song?.let {
                val songIcon = WidgetService.getSongIconValue(song)
                val artist = FileNameParser.getSongArtist(it.file)
                val title = FileNameParser.getSongTitle(it.file)

                MiniPlayerNotification
                    .updateIcon(this@AudlayerNotificationService, songIcon, true)
                MiniPlayerNotification
                    .updateArtistAndTitle(this@AudlayerNotificationService, artist, title)
            }
        }
    }

    override val nameRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioNameUI
            val value = getStringExtra(extra)!!

            this@AudlayerNotificationService.mayInvokeRadio {
                MiniPlayerNotification
                    .updateSongTitle(this@AudlayerNotificationService, value)
            }
        }
    }

    override val artistRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioArtistUI
            val value = getStringExtra(extra)!!

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
            val value = getStringExtra(extra)!!

           MiniPlayerNotification
               .updateIcon(this@AudlayerNotificationService, value, false)
        }
    }

    override val iconF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.iconUI
            val value = getStringExtra(extra)!!

            MiniPlayerNotification
                .updateIcon(this@AudlayerNotificationService, value, true)
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

    //not need
    override val songArtistF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songArtistUI
            val value = getStringExtra(extra)
        }
    }
    override val songNameF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songNameUI
            val value = getStringExtra(extra)
        }
    }
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