package velord.university.application.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.application.broadcast.MiniPlayerBroadcastHub
import velord.university.application.notification.MiniPlayerServiceNotification

abstract class RadioService : Service() {

    abstract val TAG: String

    private lateinit var player: MediaPlayer

    private lateinit var radioStation: Array<String>

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()
        //restore state
        scope.launch {

        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        //store player state
        stopPlayer()
        destroyNotification()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind called")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        return START_STICKY
    }

    protected fun playOrStopService() {
        if (player.isPlaying) pausePlayer()
        else playRadioAfterCreatedPlayer()
    }

    protected fun playRadioAfterCreatedPlayer() {
        if (::player.isInitialized) {
                player.start()
        }
        //send command to change ui
        MiniPlayerBroadcastHub.apply { this@RadioService.playUI() }
        //send command to change notification
        changeNotificationPlayOrStop(true)
        changeNotificationInfo()
    }

    protected fun pausePlayer() = stopOrPausePlayer {
        player.pause()
    }

    protected fun playByUrl(url: String) {

    }

    protected fun likeRadio() {

    }

    protected fun unlikeRadio() {

    }

    protected fun getInfoFromServiceToUI() {

    }

    private fun stopPlayer() {

    }

    private fun changeNotificationInfo() {
        val title = ""
        val artist = ""
        MiniPlayerServiceNotification.updateSongTitleAndArtist(this, title, artist)
    }

    private fun changeNotificationPlayOrStop(isPlaying: Boolean) =
        MiniPlayerServiceNotification.updatePlayOrStop(this, isPlaying)

    private fun stopOrPausePlayer(f: () -> Unit) {
        if (::player.isInitialized) {
            f()
        }
        //send command to change ui
        MiniPlayerBroadcastHub.apply { this@RadioService.stopUI() }
        //send command to change notification
        changeNotificationPlayOrStop(false)
    }

    private fun destroyNotification() {
        MiniPlayerServiceNotification.dismiss()
    }
}