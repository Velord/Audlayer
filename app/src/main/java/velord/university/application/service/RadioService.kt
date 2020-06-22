package velord.university.application.service

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.AppBroadcastHub.iconRadioUI
import velord.university.application.broadcast.RestarterRadioService
import velord.university.application.notification.MiniPlayerServiceNotification
import velord.university.application.service.audioFocus.AudioFocusListenerService
import velord.university.application.settings.AppPreference
import velord.university.application.settings.miniPlayer.RadioServicePreference
import velord.university.interactor.RadioInteractor
import velord.university.model.entity.RadioStation
import velord.university.model.isyStreamMeta.IcyStreamMeta
import velord.university.repository.MiniPlayerRepository
import velord.university.repository.RadioRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import java.net.URL

abstract class RadioService : AudioFocusListenerService() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    private var scopeArtistStream = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var currentStation: RadioStation

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()

        scope.launch {
            restoreState()
            changeNotificationInfo()
            changeNotificationPlayOrStop(player.isPlaying)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        //store player state
        storeIsPlayingState()
        stopPlayer()
        saveState()
        restartService()
    }

    override fun onStartCommand(intent: Intent?,
                                flags: Int,
                                startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY
    }

    protected fun playOrStopService() {
        if (playerIsInitialized() && player.isPlaying) pausePlayer()
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
                //send info
                AppBroadcastHub.run { showRadioUI() }
                //focus
                setAudioFocusMusicListener()
                //save
                saveState()
                //play
                radioIsCached()
            }
        }
    }

    protected fun playRadioIfCan() {
        if (playerIsInitialized())
            playRadioAfterCreatedPlayer()
        else playByUrl(currentStation.url)
    }
    //ignore cause radioFragment handle this
    protected fun likeRadio() {}
    //ignore cause radioFragment handle this
    protected fun unlikeRadio() {}

    protected fun getInfoFromServiceToUI() {
        if (playerIsInitialized()) {
            scope.launch {
                if (stationIsInitialized().not())
                    restoreState()
                if (stationIsInitialized()) {
                    //audio focus loss protection
                    //if this will be invoke before focus loss
                    //player will pause
                    launch {
                        delay(500)
                        userRotateDeviceProtection()
                        delay(500)
                    }
                    //send info
                    AppBroadcastHub.run { showRadioUI() }
                    sendAllInfo()
                }
            }
        }
        else AppBroadcastHub.run {
            radioPlayerUnavailableUI()
        }
    }

    private suspend fun sendAllInfo() {
        sendRadioArtist()
        sendIsPlayed()
        sendRadioName()
        sendIsLiked()
        sendIcon()
    }

    private suspend fun restoreState() = withContext(Dispatchers.IO) {
        Log.d(TAG, "restoreState")
        val id = RadioServicePreference.getCurrentRadioId(this@RadioService)
        currentStation = RadioRepository.getById(id)
        RadioInteractor.radioStation = currentStation

        //userRotateDeviceProtection()
    }

    private fun restartService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "RestartRadioService"
        broadcastIntent.setClass(this, RestarterRadioService::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private fun radioIsCached() {
        MiniPlayerRepository.setState(
            this, MiniPlayerLayoutState.RADIO)
        stopMiniPlayerService()
        sendShowRadioUI()
        playRadioAfterCreatedPlayer()
    }

    private fun playRadioAfterCreatedPlayer() {
        if (playerIsInitialized()) {
            player.start()
            //store player state
            storeIsPlayingState()
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
        if (playerIsInitialized()) f()
        //send command to change ui
        sendInfoWhenStop()
    }

    private fun storeIsPlayingState() {
        if (playerIsInitialized()) {
            if (player.isPlaying) RadioServicePreference
                .setIsPlaying(this, true)
            else RadioServicePreference
                .setIsPlaying(this, false)
        }
    }

    private fun userRotateDeviceProtection() {
        Log.d(TAG, "userRotateDeviceProtection")
        //restore isPlaying state
        val isPlaying = RadioServicePreference
            .getIsPlaying(this@RadioService)
        val appWasDestroyed = AppPreference
            .getAppIsDestroyed(this@RadioService)
        //this means ui have been destroyed after destroy main activity
        //but app is still working -> after restoration we should play radio
        if (appWasDestroyed) {
            mayInvoke {
                scope.launch {
                    sendAllInfo()
                    playByUrl(currentStation.url)
                    pausePlayer()
                }
            }
        }
        else if (isPlaying && appWasDestroyed.not()) {
            if (playerIsInitialized()) player.start()
        }
    }

    private fun saveState() {
        RadioServicePreference
            .setCurrentRadioId(this, currentStation.id.toInt())
    }

    private fun sendIcon() {
        currentStation.icon?.let {
            iconRadioUI(it)
        }
    }

    private fun sendRadioArtist() {
        mayInvoke {
            scopeArtistStream.cancel()
            scopeArtistStream = CoroutineScope(Job() + Dispatchers.Default)
            //get info
            scopeArtistStream.launch {
                while (this.isActive) {
                    val meta = IcyStreamMeta()
                    meta.urlStream = URL(currentStation.url)
                    val title = meta.getArtistAndTitle()
                    AppBroadcastHub.apply {
                        radioArtistUI(title)
                    }
                    delay(10000)
                }
            }
        }
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
            if (playerIsInitialized() && player.isPlaying) {
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
            scope.launch {
                sendAllInfo()
            }
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