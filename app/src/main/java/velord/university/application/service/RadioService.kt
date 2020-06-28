package velord.university.application.service

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.AppBroadcastHub.iconRadioUI
import velord.university.application.broadcast.RestarterRadioService
import velord.university.application.notification.MiniPlayerServiceNotification
import velord.university.application.service.audioFocus.AudioFocusChangeF
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

    override val onAudioFocusChange: AudioFocusChangeF =
        AudioFocusChangeF(
            {
                if (player.isPlaying) {
                    pausePlayer()
                    //rearward playing state
                    storeIsPlayingStateTrue()
                }
            },
            {
                if (player.isPlaying) {
                    pausePlayer()
                    //rearward playing state
                    storeIsPlayingStateTrue()
                }
            },
            {
                player.setVolume(0.5f, 0.5f)
            },
            {
                if (RadioServicePreference(this).isPlaying) {
                    playRadioIfCan()
                    player.setVolume(1.0f, 1.0f)
                }
            }
        )

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return  null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()

        scope.launch {
            restoreState()
            mayInvoke {
                changeNotificationInfo()
                changeNotificationPlayOrStop(false)
            }
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

    protected fun playRadioIfCan() {
        if (playerIsInitialized())
            playRadioAfterCreatedPlayer()
        else playByUrl(currentStation.url)
    }

    protected fun likeRadio() {
        if (stationIsInitialized()) scope.launch {
            changeLikeInDB(true)
        }
    }

    protected fun unlikeRadio() {
        if (stationIsInitialized()) scope.launch {
            changeLikeInDB(false)
        }
    }

    protected fun getInfoFromServiceToUI() {
        if (stationIsInitialized()) sendAllInfo()
        else sendRadioPlayerUnavailable()
    }

    protected fun playByUrl(url: String) {
        //check correct radioStation
        val radioStation = RadioInteractor.radioStation
        if (url == radioStation.url) {
            //no need cache if same url radio is playing ih this moment
            if (protectSameCache(url)) return
            //assignment
            currentStation = radioStation
            //action
            stopOrPausePlayer { stopPlayer() }
            //create
            createPlayer(currentStation.url)?.let {
                urlIsAvailable(it)
            } ?: urlIsUnavailable(url)
        }
    }

    private suspend fun changeLikeInDB(isLike: Boolean) =
        withContext(Dispatchers.IO) {
            if (isLike) RadioRepository.likeByUrl(currentStation.url)
            else RadioRepository.unlikeByUrl(currentStation.url)
        }

    private fun urlIsAvailable(mediaPlayer: MediaPlayer) {
        player = mediaPlayer
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        //focus
        setAudioFocusMusicListener()
        //send info
        sendShowRadioUI()
        //save
        saveState()
        //play
        radioIsCached()
    }

    private fun urlIsUnavailable(url: String) {
        Log.d(TAG, "Url: $url is unavailable")
        sendUrlIsWrong(url)
        scope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@RadioService,
                    "Url: $url is unavailable",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createPlayer(url: String): MediaPlayer? =
        MediaPlayer.create(
            this@RadioService,
            Uri.parse(url)
        )

    private fun protectSameCache(urlIncome: String): Boolean =
        if(playerIsInitialized() &&
            currentStation.url == urlIncome &&
            player.isPlaying) {
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RadioService,
                        "Already is caching", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            sendRadioName()
            true
        }
        else false

    private fun sendAllInfo() {
        scope.launch {
            sendRadioArtist()
            sendIsPlayed()
            sendRadioName()
            sendIsLiked()
            sendIcon()
        }
    }

    private suspend fun restoreState() = withContext(Dispatchers.IO) {
        Log.d(TAG, "restoreState")
        val id = RadioServicePreference(this@RadioService).currentRadioId

        RadioRepository.getById(id)?.let {
            currentStation = it
            RadioInteractor.radioStation = currentStation
            //cache radio
            mayInvoke {
                sendAllInfo()
                playByUrl(currentStation.url)
                pausePlayer()
            }
        } ?: sendRadioPlayerUnavailable()
    }

    private fun restartService() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "RestartRadioService"
        broadcastIntent.setClass(this, RestarterRadioService::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private fun radioIsCached() {
        stopMiniPlayerService()
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
        if (playerIsInitialized()) {
            f()
            storeIsPlayingState()
        }
        //send command to change ui
        sendInfoWhenStop()
    }

    private fun storeIsPlayingState() {
        if (playerIsInitialized()) {
            if (player.isPlaying) storeIsPlayingStateTrue()
            else storeIsPlayingStateFalse()
        }
    }

    private fun storeIsPlayingStateTrue() {
        RadioServicePreference(this@RadioService).isPlaying = true
    }

    private fun storeIsPlayingStateFalse() {
        RadioServicePreference(this@RadioService).isPlaying = false
    }

    private fun appWasDestroyedProtection() {
        Log.d(TAG, "userRotateDeviceProtection")
        //restore isPlaying state
        val isPlaying =
            RadioServicePreference(this@RadioService).isPlaying
        val appWasDestroyed =
            AppPreference(this@RadioService).appIsDestroyed
        //this means ui have been destroyed after destroy main activity
        //but app is still working -> after restoration we should play radio
        if (appWasDestroyed) {
            mayInvoke {
                sendAllInfo()
                playByUrl(currentStation.url)
                pausePlayer()
            }
        }
        else if (isPlaying && appWasDestroyed.not()) {
            if (playerIsInitialized()) player.start()
        }
    }

    private fun saveState() {
        RadioServicePreference(this@RadioService).currentRadioId =
            currentStation.id.toInt()
    }

    private fun sendRadioPlayerUnavailable() =
        AppBroadcastHub.run {
            radioPlayerUnavailableUI()
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

    private fun sendShowRadioUI() {
        MiniPlayerRepository.setState(
            this, MiniPlayerLayoutState.RADIO)
        AppBroadcastHub.apply {
            showRadioUI()
        }
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
        when (RadioRepository.isLike(currentStation.url).getOrElse(null)) {
            true -> mayInvoke {
                AppBroadcastHub.apply { likeRadioUI() }
            }
            false -> mayInvoke {
                AppBroadcastHub.apply { unlikeRadioUI() }
            }
            else -> Log.d(TAG, "sendIsLiked is null value")
        }
    }

    private fun sendInfoWhenStop() {
        mayInvoke {
            AppBroadcastHub.apply { stopRadioUI() }
        }
        //send command to change notification
        changeNotificationPlayOrStop(false)
    }

    private fun sendUrlIsWrong(url: String) =
        AppBroadcastHub.run { radioUrlIsWrongUI(url) }

    private fun changeNotificationInfo() {
        if (stationIsInitialized()) {
            val title = currentStation.name
            val artist = ""
            MiniPlayerServiceNotification
                .updateSongTitleAndArtist(this, title, artist)
        }
    }

    private fun changeNotificationPlayOrStop(isPlaying: Boolean) =
        MiniPlayerServiceNotification.updatePlayOrStop(this, isPlaying)

    private fun stationIsInitialized(): Boolean =
        ::currentStation.isInitialized

    private fun mayInvoke(f: () -> Unit) =
        MiniPlayerRepository.mayDoAction(
            this, MiniPlayerLayoutState.RADIO, f)
}