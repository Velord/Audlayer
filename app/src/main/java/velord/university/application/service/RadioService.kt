package velord.university.application.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.notification.MiniPlayerServiceNotification
import velord.university.application.settings.miniPlayer.RadioServicePreference
import velord.university.interactor.RadioInteractor
import velord.university.model.entity.RadioStation
import velord.university.repository.MiniPlayerRepository
import velord.university.repository.RadioRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

abstract class RadioService : Service() {

    abstract val TAG: String

    private lateinit var player: MediaPlayer

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var currentStation: RadioStation

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
        saveState()
        //destroyNotification()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind called")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?,
                                flags: Int,
                                startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        return START_STICKY
    }

    protected fun playOrStopService() {
        if (player.isPlaying) pausePlayer()
        else playRadioAfterCreatedPlayer()
    }

    protected fun pausePlayer() = stopOrPausePlayer {
        player.pause()
    }

    protected fun playByUrl(url: String) {
        scope.launch {
            //check correct radioStation
            val radioStation = RadioInteractor.radioStation
            if (url == radioStation.url) {
                //assignment
                currentStation = radioStation
                //action
                stopOrPausePlayer { stopPlayer() }
                //create
                player = MediaPlayer.create(
                    this@RadioService,
                    Uri.parse(url)
                )
                player.setAudioStreamType(AudioManager.STREAM_MUSIC)
                //save
                saveState()
                //play
                radioIsCached()
            }
        }
    }

    protected fun playRadioIfCan() {
        if (::player.isInitialized)
            playRadioAfterCreatedPlayer()
        else playByUrl(currentStation.url)
    }

    private fun playRadioAfterCreatedPlayer() {
        if (::player.isInitialized) {
            player.start()
            //send command to change ui
            sendInfoWhenPlay()
        }
    }

    private fun stopMiniPlayerService() {
        AppBroadcastHub.apply { stopService() }
    }

    private fun stopPlayer() = stopOrPausePlayer {
        player.stop()
    }

    private fun stopOrPausePlayer(f: () -> Unit) {
        if (::player.isInitialized) {
            f()
        }
        //send command to change ui
        sendInfoWhenStop()
    }
    //ignore cause radioFragment handle this
    protected fun likeRadio() {}
    //ignore cause radioFragment handle this
    protected fun unlikeRadio() {}

    protected fun getInfoFromServiceToUI() {
        scope.launch {
            if (stationIsInitialized().not())
                restoreState()
            if (stationIsInitialized()) {
                sendIsPlayed()
                sendRadioName()
                sendIsLiked()
            }
        }
    }

    private fun radioIsCached() {
        stopMiniPlayerService()
        sendShowRadioUI()
        playRadioAfterCreatedPlayer()
    }

    private suspend fun restoreState() = withContext(Dispatchers.IO) {
        val id = RadioServicePreference.getCurrentRadioId(this@RadioService)
        currentStation = RadioRepository.getById(id)
        RadioInteractor.radioStation = currentStation
    }

    private fun saveState() {
        RadioServicePreference
            .setCurrentRadioId(this, currentStation.id.toInt())
    }

    private fun sendRadioName() {
        mayInvoke {
            AppBroadcastHub.apply {
                radioNameUI(currentStation.name)
            }
        }
    }

    private fun sendIsPlayed() {
        mayInvoke {
            if (::player.isInitialized && player.isPlaying) {
                AppBroadcastHub.apply {
                    playRadioUI()
                }
            }
        }
    }

    private fun sendShowRadioUI() =
        AppBroadcastHub.apply {
            showRadioUI()
        }

    private fun sendInfoWhenPlay() {
        mayInvoke {
            getInfoFromServiceToUI()
        }
        //send command to change notification
        changeNotificationPlayOrStop(true)
        changeNotificationInfo()
    }

    private suspend fun sendIsLiked() = withContext(Dispatchers.IO) {
        when (RadioRepository.isLike(currentStation.url)) {
            true -> mayInvoke {
                AppBroadcastHub.apply { likeRadioUI() }
            }
            false -> mayInvoke {
                AppBroadcastHub.apply { unlikeRadioUI() }
            }
        }
    }

    private fun sendInfoWhenStop() {
        mayInvoke {
            AppBroadcastHub.apply { stopRadioUI() }
        }
        //send command to change notification
        changeNotificationPlayOrStop(false)
    }

    private fun changeNotificationInfo() {
        val title = currentStation.name
        val artist = ""
        MiniPlayerServiceNotification
            .updateSongTitleAndArtist(this, title, artist)
    }

    private fun changeNotificationPlayOrStop(isPlaying: Boolean) =
        MiniPlayerServiceNotification.updatePlayOrStop(this, isPlaying)

    private fun stationIsInitialized(): Boolean =
        ::currentStation.isInitialized

    private fun mayInvoke(f: () -> Unit) =
        MiniPlayerRepository.mayDoAction(
            this, MiniPlayerLayoutState.RADIO, f)
}