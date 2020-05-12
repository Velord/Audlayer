package velord.university.application.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.repository.RadioRepository

abstract class RadioService : Service() {

    abstract val TAG: String

    private lateinit var player: MediaPlayer

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var currentStationUrl: String

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()
        //restore state
        scope.launch {
            restoreState()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        //store player state
        stopPlayer()
        //destroyNotification()
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
            //send command to change ui
            sendInfoWhenPlay()
        }
    }

    protected fun pausePlayer() = stopOrPausePlayer {
        player.pause()
    }

    protected fun playByUrl(url: String) {
        scope.launch {
            //assignment
            currentStationUrl = url
            //action
            stopOrPausePlayer { stopPlayer() }
            //create
            player = MediaPlayer.create(
                this@RadioService,
                Uri.parse(url)
            )
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
            //play
            playRadioAfterCreatedPlayer()
        }
    }

    //ignore cause radioFragment handle this
    protected fun likeRadio() {}
    //ignore cause radioFragment handle this
    protected fun unlikeRadio() {}

    protected fun getInfoFromServiceToUI() {

    }

    private fun stopMiniPlayerService() {
        //if (MiniPlayerUIPreference.getState(this) == 0)
            AppBroadcastHub.apply {
                this@RadioService.stopService()
            }
    }

    private fun stopPlayer() = stopOrPausePlayer {
        player.stop()
    }

    private fun restoreState() {

    }

    private fun sendInfoWhenPlay() {
        stopMiniPlayerService()
        AppBroadcastHub.apply {
            this@RadioService.showRadioUI()
            this@RadioService.playRadioUI()
            sendIsLiked()
        }
        //send command to change notification
        //changeNotificationPlayOrStop(true)
        //changeNotificationInfo()
    }

    private fun sendIsLiked() =
        scope.launch {
            when (RadioRepository.isLike(currentStationUrl)) {
                true -> AppBroadcastHub.apply {
                    this@RadioService.likeRadioUI()
                }
                false -> AppBroadcastHub.apply {
                    this@RadioService.unlikeRadioUI()
                }
            }
        }

    private fun sendInfoWhenStop() {
        AppBroadcastHub.apply {
            this@RadioService.stopRadioUI()
        }
        //send command to change notification
        //changeNotificationPlayOrStop(false)
    }

    private fun stopOrPausePlayer(f: () -> Unit) {
        if (::player.isInitialized) {
            f()
        }
        //send command to change ui
        sendInfoWhenStop()
    }
}