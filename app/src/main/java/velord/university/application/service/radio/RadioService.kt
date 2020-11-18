package velord.university.application.service.radio

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import velord.university.model.coroutine.getScope
import kotlinx.coroutines.*
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.AppBroadcastHub.iconRadioUI
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.broadcast.restarter.RestarterRadioService
import velord.university.application.service.audioFocus.AudioFocusChangeF
import velord.university.application.service.audioFocus.AudioFocusListenerService
import velord.university.application.settings.miniPlayer.RadioServicePreference
import velord.university.interactor.RadioInteractor
import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.radio.RadioStation
import velord.university.model.entity.isyStreamMeta.IcyStreamMeta
import velord.university.repository.db.transaction.hub.DB
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.repository.hub.RadioRepository
import velord.university.ui.fragment.addToPlaylist.tryAction
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import java.net.URL

abstract class RadioService : AudioFocusListenerService() {

    private val scope: CoroutineScope = getScope()

    private var scopeArtistStream = getScope()

    private lateinit var currentStation: RadioStation

    override val onAudioFocusChange: AudioFocusChangeF =
        AudioFocusChangeF(
            {
                if (player.isPlaying) {
                    pausePlayer()
                    //rearward playing state
                    storeIsPlayingStateTrue()
                }

                unregisterMediaButtonEventReceiver()
            },
            {
                if (player.isPlaying) {
                    pausePlayer()
                    //rearward playing state
                    storeIsPlayingStateTrue()
                }
            },
            {
                if (playerIsInitialized())
                    player.setVolume(0.5f, 0.5f)
            },
            {
                if (RadioServicePreference(this).isPlaying)
                    playRadioIfCan()

                if (playerIsInitialized())
                    player.setVolume(1.0f, 1.0f)
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

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
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
        try {
            val radioStation = RadioInteractor.radioStation
            if (url == radioStation.url) {
                //no need cache if same url radio is playing in this moment
                if (protectSameCache(url)) return
                //assignment
                currentStation = radioStation
                //create
                createPlayer(currentStation.url)?.let {
                    //action
                    stopOrPausePlayer { stopPlayer() }
                    urlIsAvailable(it)
                } ?: urlIsUnavailable(url)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private suspend fun changeLikeInDB(isLike: Boolean) =
        if (isLike) RadioRepository.likeByUrl(currentStation.url)
        else RadioRepository.unlikeByUrl(currentStation.url)

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
        mayInvoke {
            scope.launch {
                sendRadioArtist()
                sendIsPlayed()
                sendRadioName()
                sendIsLiked()
                sendIcon()
            }
        }
    }

    private suspend fun restoreState() = onIO {
        Log.d(TAG, "restoreState")
        val id = RadioServicePreference(this@RadioService).currentRadioId

        tryAction("restoreState") {
            DB.radioTransaction("restoreState") {
                getById(id).let {
                    currentStation = it
                    RadioInteractor.radioStation = currentStation
                    //cache radio
                    mayInvoke {
                        sendAllInfo()
                        playByUrl(currentStation.url)
                        pausePlayer()
                    }
                }
            }
        }
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

    private fun stopMiniPlayerService() =
        AppBroadcastHub.apply {
            doAction(BroadcastActionType.STOP_PLAYER_SERVICE)
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

    private fun saveState() {
        RadioServicePreference(this@RadioService).currentRadioId =
            currentStation.id.toInt()
    }

    private var unavailable: Boolean = false
    private fun sendRadioPlayerUnavailable() {
        if (unavailable.not()) {
            unavailable = true
            AppBroadcastHub.apply {
                doAction(BroadcastActionType.UNAVAILABLE_RADIO_UI)
            }
        }
    }

    private fun sendIcon() {
        currentStation.icon?.let {
            iconRadioUI(it)
        }
    }

    private fun sendRadioArtist() {
        mayInvoke {
            scopeArtistStream.cancel()
            scopeArtistStream = getScope()
            //get info
            scopeArtistStream.launch {
                while (this.isActive) {
                    val meta = IcyStreamMeta()
                    try {
                        meta.urlStream = URL(currentStation.url)
                        val title = meta.getArtistAndTitle()
                        AppBroadcastHub.apply {
                            radioArtistUI(title)
                        }
                    }
                    catch (e: Exception) {
                        Log.d(TAG, e.message.toString())
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
                AppBroadcastHub.run {
                    doAction(BroadcastActionType.PLAY_RADIO_UI)
                }
            }
        }
    }

    private fun sendShowRadioUI() {
        AppBroadcastHub.apply {
            doAction(BroadcastActionType.SHOW_RADIO_UI)
        }
    }

    private fun sendInfoWhenPlay() {
        mayInvoke {
            scope.launch {
                sendAllInfo()
            }
        }
    }

    private suspend fun sendIsLiked() {
        val isLike = DB.radioTransaction("sendIsLiked") {
            getByUrl(currentStation.url).liked
        }
        when (isLike) {
            true -> mayInvoke {
                AppBroadcastHub.run {
                    doAction(BroadcastActionType.LIKE_RADIO_UI)
                }
            }
            false -> mayInvoke {
                AppBroadcastHub.run {
                    doAction(BroadcastActionType.UNLIKE_RADIO_UI)
                }
            }
        }
    }

    private fun sendInfoWhenStop() {
        mayInvoke {
            AppBroadcastHub.run {
                doAction(BroadcastActionType.STOP_RADIO_UI)
            }
        }
    }

    private fun sendUrlIsWrong(url: String) =
        AppBroadcastHub.run { radioUrlIsWrongUI(url) }

    private fun stationIsInitialized(): Boolean =
        ::currentStation.isInitialized

    private fun mayInvoke(f: () -> Unit) =
        MiniPlayerRepository.mayDoAction(
            this, MiniPlayerLayoutState.RADIO, f
        )
}