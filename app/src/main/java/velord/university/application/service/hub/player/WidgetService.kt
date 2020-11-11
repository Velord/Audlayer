package velord.university.application.service.hub.player

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import velord.university.application.broadcast.behaviour.MiniPlayerUIReceiver
import velord.university.application.broadcast.behaviour.RadioUIReceiver
import velord.university.application.broadcast.behaviour.SongPathReceiver
import velord.university.application.broadcast.hub.*
import velord.university.application.broadcast.restarter.RestarterWidgetService
import velord.university.application.service.mayInvokeDefault
import velord.university.application.service.mayInvokeRadio
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.music.song.Song
import velord.university.model.entity.fileType.file.FileNameParser
import velord.university.ui.widget.AudlayerWidget

class WidgetService : Service(),
    MiniPlayerUIReceiver,
    RadioUIReceiver,
    SongPathReceiver {

    override val TAG = "WidgetService"

    private val receivers = miniPlayerUIReceiverList() +
            songPathReceiverList()

    private val receiversRadio = getRadioUIReceiverList()

    override val songPathF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.playByPathUI
            val songPath = getStringExtra(extra)

            val song = SongPlaylistInteractor.songs.find {
                it.file.path == songPath
            }

            song?.let {
                val songIcon = getSongIconValue(song)

                AudlayerWidget.widgetArtist = FileNameParser.getSongArtist(it.file)
                AudlayerWidget.widgetTitle =  FileNameParser.getSongTitle(it.file)

                this@WidgetService.mayInvokeDefault {
                    AudlayerWidget.invokeUpdate(this@WidgetService)
                }
                changeIcon(songIcon)
            }
        }
    }

    override val nameRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.radioNameUI
            val value = getStringExtra(extra)!!
            AudlayerWidget.widgetTitle = value

            this@WidgetService.mayInvokeRadio {
                AudlayerWidget.invokeUpdate(this@WidgetService)
            }
        }
    }

    override val artistRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.radioArtistUI
            val value = getStringExtra(extra)!!
            AudlayerWidget.widgetArtist = value

            this@WidgetService.mayInvokeRadio {
                AudlayerWidget.invokeUpdate(this@WidgetService)
            }
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
            val extra = BroadcastExtra.iconRadioUI
            val value = getStringExtra(extra)!!

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
            val extra = BroadcastExtra.iconUI
            val value = getStringExtra(extra)!!

            changeIcon(value)
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

    //not need
    override val songArtistF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.songArtistUI
            val songArtist = getStringExtra(extra)
        }
    }
    override val songNameF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.songNameUI
            val value = getStringExtra(extra)
        }
    }

    override val likeRadioUIF: (Intent?) -> Unit = {  }

    override val unlikeRadioUIF: (Intent?) -> Unit = {  }

    override val radioPlayerUnavailableUIF: (Intent?) -> Unit = {
        it?.apply {
        }
    }

    override val radioUrlIsWrongUIF: (Intent?) -> Unit = {}

    override val playerUnavailableUIF: (Intent?) -> Unit = {
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

        AppBroadcastHub.apply {
            doAction(BroadcastActionType.GET_INFO_PLAYER_SERVICE)
            doAction(BroadcastActionType.GET_INFO_RADIO_SERVICE)
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

        return START_STICKY
    }

    companion object {
        fun getSongIconValue(song: Song): String =
            if (song.iconUrl.isNullOrBlank().not())
                song.iconUrl!!
            else song.icon.toString()
    }

    private fun changeIcon(value: String) {
        AudlayerWidget.iconIsSong = true
        AudlayerWidget.widgetIcon = value

        AudlayerWidget.invokeUpdate(this@WidgetService)
    }

    private fun restartService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "RestartWidgetService"
        broadcastIntent.setClass(this, RestarterWidgetService::class.java)
        this.sendBroadcast(broadcastIntent)
    }
}