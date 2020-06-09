package velord.university.application.broadcast

import android.content.Context
import android.content.IntentFilter
import velord.university.application.broadcast.AppBroadcastHub.Action.addToQueueService
import velord.university.application.broadcast.AppBroadcastHub.Action.getInfoService
import velord.university.application.broadcast.AppBroadcastHub.Action.likeService
import velord.university.application.broadcast.AppBroadcastHub.Action.likeUI
import velord.university.application.broadcast.AppBroadcastHub.Action.loopAllService
import velord.university.application.broadcast.AppBroadcastHub.Action.loopAllUI
import velord.university.application.broadcast.AppBroadcastHub.Action.loopService
import velord.university.application.broadcast.AppBroadcastHub.Action.loopUI
import velord.university.application.broadcast.AppBroadcastHub.Action.notLoopService
import velord.university.application.broadcast.AppBroadcastHub.Action.notLoopUI
import velord.university.application.broadcast.AppBroadcastHub.Action.playAllInFolderService
import velord.university.application.broadcast.AppBroadcastHub.Action.playByPathService
import velord.university.application.broadcast.AppBroadcastHub.Action.playNextAllInFolderService
import velord.university.application.broadcast.AppBroadcastHub.Action.playService
import velord.university.application.broadcast.AppBroadcastHub.Action.playUI
import velord.university.application.broadcast.AppBroadcastHub.Action.rewindService
import velord.university.application.broadcast.AppBroadcastHub.Action.rewindUI
import velord.university.application.broadcast.AppBroadcastHub.Action.showMiniPlayerGeneralUI
import velord.university.application.broadcast.AppBroadcastHub.Action.shuffleAndPlayAllInFolderService
import velord.university.application.broadcast.AppBroadcastHub.Action.shuffleService
import velord.university.application.broadcast.AppBroadcastHub.Action.shuffleUI
import velord.university.application.broadcast.AppBroadcastHub.Action.skipNextService
import velord.university.application.broadcast.AppBroadcastHub.Action.skipNextUI
import velord.university.application.broadcast.AppBroadcastHub.Action.skipPrevService
import velord.university.application.broadcast.AppBroadcastHub.Action.skipPrevUI
import velord.university.application.broadcast.AppBroadcastHub.Action.songArtistUI
import velord.university.application.broadcast.AppBroadcastHub.Action.songDurationUI
import velord.university.application.broadcast.AppBroadcastHub.Action.songHQUI
import velord.university.application.broadcast.AppBroadcastHub.Action.songNameUI
import velord.university.application.broadcast.AppBroadcastHub.Action.songPathUI
import velord.university.application.broadcast.AppBroadcastHub.Action.unShuffleService
import velord.university.application.broadcast.AppBroadcastHub.Action.unShuffleUI
import velord.university.application.broadcast.AppBroadcastHub.Action.unlikeService
import velord.university.application.broadcast.AppBroadcastHub.Action.unlikeUI
import velord.university.application.broadcast.AppBroadcastHub.Extra.folderPathService

const val PERM_PRIVATE_MINI_PLAYER = "velord.university.PERM_PRIVATE_MINI_PLAYER"
const val PERM_PRIVATE_RADIO = "velord.university.PERM_PRIVATE_RADIO"

object AppBroadcastHub {

    fun Context.stopService() =
        MiniPlayerBroadcastStop.run {
            this@stopService.sendBroadcastStop()
        }

    fun Context.stopUI() =
        MiniPlayerBroadcastStop.run {
            this@stopUI.sendBroadcastStopUI()
        }

    fun Context.playService() =
        MiniPlayerBroadcastPlay.run {
            this@playService.sendBroadcastPlay()
        }

    fun Context.playUI() =
        MiniPlayerBroadcastPlay.run {
            this@playUI.sendBroadcastPlayUI()
        }

    fun Context.likeService() =
        MiniPlayerBroadcastLike.run {
            this@likeService.sendBroadcastLike()
        }

    fun Context.likeUI() =
        MiniPlayerBroadcastLike.run {
            this@likeUI.sendBroadcastLikeUI()
        }

    fun Context.unlikeService() =
        MiniPlayerBroadcastUnlike.run {
            this@unlikeService.sendBroadcastUnlike()
        }

    fun Context.unlikeUI() =
        MiniPlayerBroadcastUnlike.run {
            this@unlikeUI.sendBroadcastUnlikeUI()
        }

    fun Context.skipNextService() =
        MiniPlayerBroadcastSkipNext.run {
            this@skipNextService.sendBroadcastSkipNext()
        }

    fun Context.skipNextUI() =
        MiniPlayerBroadcastSkipNext.run {
            this@skipNextUI.sendBroadcastSkipNextUI()
        }

    fun Context.skipPrevService() =
        MiniPlayerBroadcastSkipPrev.run {
            this@skipPrevService.sendBroadcastSkipPrev()
        }

    fun Context.skipPrevUI() =
        MiniPlayerBroadcastSkipPrev.run {
            this@skipPrevUI.sendBroadcastSkipPrevUI()
        }

    fun Context.rewindService(duration: Int) =
        MiniPlayerBroadcastRewind.run {
            this@rewindService.sendBroadcastRewind(duration)
        }

    fun Context.rewindUI(duration: Int) =
        MiniPlayerBroadcastRewind.run {
            this@rewindUI.sendBroadcastRewindUI(duration)
        }

    fun Context.shuffleService() =
        MiniPlayerBroadcastShuffle.run {
            this@shuffleService.sendBroadcastShuffle()
        }

    fun Context.shuffleUI() =
        MiniPlayerBroadcastShuffle.run {
            this@shuffleUI.sendBroadcastShuffleUI()
        }

    fun Context.unShuffleService() =
        MiniPlayerBroadcastUnShuffle.run {
            this@unShuffleService.sendBroadcastUnShuffle()
        }

    fun Context.unShuffleUI() =
        MiniPlayerBroadcastUnShuffle.run {
            this@unShuffleUI.sendBroadcastUnShuffleUI()
        }

    fun Context.loopService() =
        MiniPlayerBroadcastLoop.run {
            this@loopService.sendBroadcastLoop()
        }

    fun Context.loopUI() =
        MiniPlayerBroadcastLoop.run {
            this@loopUI.sendBroadcastLoopUI()
        }

    fun Context.loopAllService() =
        MiniPlayerBroadcastLoopAll.run {
            this@loopAllService.sendBroadcastLoopAll()
        }

    fun Context.loopAllUI() =
        MiniPlayerBroadcastLoopAll.run {
            this@loopAllUI.sendBroadcastLoopAllUI()
        }

    fun Context.notLoopService() =
        MiniPlayerBroadcastNotLoop.run {
            this@notLoopService.sendBroadcastNotLoop()
        }

    fun Context.notLoopUI() =
        MiniPlayerBroadcastNotLoop.run {
            this@notLoopUI.sendBroadcastNotLoopUI()
        }

    fun Context.playByPathService(path: String) =
        MiniPlayerBroadcastPlayByPath.run {
            this@playByPathService.sendBroadcastPlayByPath(path)
        }

    fun Context.songPathUI(path: String) =
        MiniPlayerBroadcastSongPath.run {
            this@songPathUI.sendBroadcastSongPathUI(path)
        }

    fun Context.songNameUI(name: String) =
        MiniPlayerBroadcastSongName.run {
            this@songNameUI.sendBroadcastSongNameUI(name)
        }

    fun Context.songArtistUI(artist: String) =
        MiniPlayerBroadcastSongArtist.run {
            this@songArtistUI.sendBroadcastSongArtistUI(artist)
        }

    fun Context.songHQUI(isHQ: Boolean) =
        MiniPlayerBroadcastSongHQ.run {
            this@songHQUI.sendBroadcastSongHQUI(isHQ)
        }

    fun Context.songDurationUI(duration: Int) =
        MiniPlayerBroadcastSongDuration.run {
            this@songDurationUI.sendBroadcastSongDurationUI(duration)
        }

    fun Context.showGeneralUI() =
        MiniPlayerBroadcastShowGeneral.run {
            this@showGeneralUI.sendBroadcastShowUI()
        }

    fun Context.hideUI() =
        MiniPlayerBroadcastHide.run {
            this@hideUI.sendBroadcastHide()
        }

    fun Context.showUI() =
        MiniPlayerBroadcastShow.run {
            this@showUI.sendBroadcastShow()
        }

    fun Context.playAllInFolderService(path: String) =
        MiniPlayerBroadcastPlayAllInFolder.run {
            this@playAllInFolderService.sendBroadcastPlayAllInFolder(path)
        }

    fun Context.playNextAllInFolderService(path: String) =
        MiniPlayerBroadcastPlayNextAllInFolder.run {
            this@playNextAllInFolderService.sendBroadcastPlayNextAllInFolder(path)
        }

    fun Context.shuffleAndPlayAllInFolderService(path: String) =
        MiniPlayerBroadcastShuffleAndPlayAllInFolder.run {
            this@shuffleAndPlayAllInFolderService.sendBroadcastShuffleAndPlayAllInFolder(path)
        }

    fun Context.addToQueueService(path: String) =
        MiniPlayerBroadcastAddToQueue.run {
            this@addToQueueService.sendBroadcastAddToQueue(path)
        }

    fun Context.getInfoService() =
        MiniPlayerBroadcastGetInfo.run {
            this@getInfoService.sendBroadcastGetInfo()
        }

    fun Context.pathIsWrongUI(path: String) =
        MiniPlayerBroadcastPathWrong.run {
            this@pathIsWrongUI.sendBroadcastPathIsWrongUI(path)
        }

    fun Context.playOrStopService() =
        MiniPlayerBroadcastPlayOrStop.run {
            this@playOrStopService.sendBroadcastPlayOrStop()
        }

    fun Context.iconUI(icon: String) =
        MiniPlayerBroadcastIcon.run {
            this@iconUI.sendBroadcastIconUI(icon)
        }

    //radio
    fun Context.stopRadioService() =
        RadioBroadcastStop.run {
            this@stopRadioService.sendBroadcastStop()
        }

    fun Context.stopRadioUI() =
        RadioBroadcastStop.run {
            this@stopRadioUI.sendBroadcastStopUI()
        }

    fun Context.playRadioService() =
        RadioBroadcastPlay.run {
            this@playRadioService.sendBroadcastPlay()
        }

    fun Context.playRadioUI() =
        RadioBroadcastPlay.run {
            this@playRadioUI.sendBroadcastPlayUI()
        }

    fun Context.likeRadioService() =
        RadioBroadcastLike.run {
            this@likeRadioService.sendBroadcastLike()
        }

    fun Context.likeRadioUI() =
        RadioBroadcastLike.run {
            this@likeRadioUI.sendBroadcastLikeUI()
        }

    fun Context.unlikeRadioService() =
        RadioBroadcastUnlike.run {
            this@unlikeRadioService.sendBroadcastUnlike()
        }

    fun Context.unlikeRadioUI() =
        RadioBroadcastUnlike.run {
            this@unlikeRadioUI.sendBroadcastUnlikeUI()
        }

    fun Context.radioNameUI(name: String) =
        RadioBroadcastName.run {
            this@radioNameUI.sendBroadcastRadioNameUI(name)
        }

    fun Context.radioArtistUI(artist: String) =
        RadioBroadcastArtist.run {
            this@radioArtistUI.sendBroadcastRadioArtistUI(artist)
        }

    fun Context.playOrStopRadioService() =
        RadioBroadcastPlayOrStop.run {
            this@playOrStopRadioService.sendBroadcastPlayOrStop()
        }

    fun Context.playByUrlRadioService(url: String) =
        RadioBroadcastPlayByUrl.run {
            this@playByUrlRadioService.sendBroadcastPlayByUrl(url)
        }

    fun Context.getInfoRadioService() =
        RadioBroadcastGetInfo.run {
            this@getInfoRadioService.sendBroadcastGetInfo()
        }

    fun Context.showRadioUI() =
        RadioBroadcastShow.run {
            this@showRadioUI.sendBroadcastShowUI()
        }

    fun Context.iconRadioUI(icon: String) =
        RadioBroadcastIcon.run {
            this@iconRadioUI.sendBroadcastRadioIconUI(icon)
        }

    object Action {
        //general
        const val hideUI = "velord.university.GENERAL_HIDE"
        const val showUI = "velord.university.GENERAL_SHOW"
        //miniPlayer
        const val stopService = "velord.university.STOP"
        const val stopUI = "velord.university.STOP_UI"
        const val playService = "velord.university.PlAY"
        const val playUI ="velord.university.PlAY_UI"
        const val likeService = "velord.university.LIKE"
        const val likeUI = "velord.university.LIKE_UI"
        const val unlikeService = "velord.university.UNLIKE"
        const val unlikeUI = "velord.university.UNLIKE_UI"
        const val skipNextService = "velord.university.SKIP_NEXT"
        const val skipNextUI = "velord.university.SKIP_NEXT_UI"
        const val skipPrevService = "velord.university.SKIP_PREV"
        const val skipPrevUI = "velord.university.SKIP_PREV_UI"
        const val rewindService = "velord.university.REWIND"
        const val rewindUI = "velord.university.REWIND_UI"
        const val shuffleService = "velord.university.SHUFFLE"
        const val shuffleUI = "velord.university.SHUFFLE_UI"
        const val unShuffleService = "velord.university.UN_SHUFFLE"
        const val unShuffleUI = "velord.university.UN_SHUFFLE_UI"
        const val loopService = "velord.university.LOOP"
        const val loopUI = "velord.university.LOOP_UI"
        const val loopAllService = "velord.university.LOOP_ALL"
        const val loopAllUI = "velord.university.LOOP_ALL_UI"
        const val notLoopService = "velord.university.NOT_LOOP"
        const val notLoopUI = "velord.university.NOT_LOOP_UI"
        const val playByPathService = "velord.university.PLAY_BY_PATH"
        const val songPathUI = "velord.university.SONG_PATH_UI"
        const val songArtistUI = "velord.university.SONG_ARTIST_UI"
        const val songNameUI = "velord.university.SONG_NAME_UI"
        const val songHQUI = "velord.university.SONG_HQ_UI"
        const val songDurationUI = "velord.university.SONG_DURATION_UI"
        const val showMiniPlayerGeneralUI = "velord.university.SHOW_MINI_PLAYER_GENERAL_UI"
        const val playAllInFolderService = "velord.university.PLAY_ALL_IN_FOLDER"
        const val playNextAllInFolderService = "velord.university.PLAY_NEXT_ALL_IN_FOLDER"
        const val shuffleAndPlayAllInFolderService = "velord.university.SHUFFLE_AND_PLAY_ALL_IN_FOLDER"
        const val addToQueueService = "velord.university.ADD_TO_QUEUE"
        const val getInfoService = "velord.university.GET_INFO"
        const val songPathIsWrongUI = "velord.university.SONG_PATH_IS_WRONG_UI"
        const val playOrStopService = "velord.university.PLAY_OR_STOP"
        const val iconUI = "velord.university.ICON_UI"
        //radio
        const val stopRadioService = "velord.university.STOP_RADIO"
        const val stopRadioUI = "velord.university.STOP_RADIO_UI"
        const val playRadioService = "velord.university.PlAY_RADIO"
        const val playRadioUI ="velord.university.PlAY_RADIO_UI"
        const val likeRadioService = "velord.university.LIKE_RADIO"
        const val likeRadioUI = "velord.university.LIKE_RADIO_UI"
        const val unlikeRadioService = "velord.university.UNLIKE_RADIO"
        const val unlikeRadioUI = "velord.university.UNLIKE_RADIO_UI"
        const val playOrStopRadioService = "velord.university.PLAY_OR_STOP_RADIO"
        const val playByUrlRadioService = "velord.university.PLAY_BY_URL_RADIO"
        const val getInfoRadioService = "velord.university.GET_INFO_RADIO"
        const val radioArtistUI = "velord.university.RADIO_ARTIST_UI"
        const val radioNameUI = "velord.university.RADIO_NAME_UI"
        const val showMiniPlayerRadioUI = "velord.university.RADIO_SHOW_UI"
        const val iconRadioUI = "velord.university.ICON_RADIO_UI"
    }

    object Extra {
        const val rewindService = "PROGRESS"
        const val rewindUI = "PROGRESS_UI"
        const val playByPathService = "AUDIO_FILE_PATH"
        const val songPathUI = "SONG_PATH_UI"
        const val songArtistUI = "SONG_ARTIST_UI"
        const val songNameUI = "SONG_NAME_UI"
        const val songHQUI = "SONG_HQ_UI"
        const val songDurationUI = "SONG_DURATION_UI"
        const val folderPathService = "AUDIO_FOLDER_PATH"
        const val iconUI = "SONG_ICON"
        //radio
        const val playByRadioStationUrlService= "RADIO_STATION_URL"
        const val radioNameUI = "RADIO_STATION_NAME"
        const val radioArtistUI = "RADIO_STATION_ARTIST"
        const val iconRadioUI = "RADIO_ICON_ASSET"
    }

    private abstract class BroadcastBase {
        //what can receive service
        open val actionService: String = ""
        //what can receive ui
        open val actionUI: String = ""

        open val filterService: IntentFilter = IntentFilter()

        open val filterUI: IntentFilter = IntentFilter()

        open val extraValueService: Any = ""

        open val extraValueUI: Any = ""
    }

    private object MiniPlayerBroadcastStop : BroadcastBase() {

        override val actionService: String = Action.stopService

        override val actionUI: String = Action.stopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)


        fun Context.sendBroadcastStop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastStopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)

    }

    private object MiniPlayerBroadcastPlay : BroadcastBase() {

        override val actionService: String = playService

        override val actionUI: String = playUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastPlay(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastPlayUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLike : BroadcastBase() {

        override val actionService: String = likeService

        override val actionUI: String = likeUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastUnlike : BroadcastBase() {

        override val actionService: String = unlikeService

        override val actionUI: String = unlikeUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnlike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnlikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastSkipNext : BroadcastBase() {

        override val actionService: String = skipNextService

        override val actionUI: String =  skipNextUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastSkipNext(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastSkipNextUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastSkipPrev : BroadcastBase() {

        override val actionService: String = skipPrevService

        override val actionUI: String = skipPrevUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastSkipPrev(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastSkipPrevUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastRewind : BroadcastBase() {

        override val actionService: String = rewindService

        override val actionUI: String =  rewindUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueService: String = Extra.rewindService

        override val extraValueUI: String = Extra.rewindUI

        fun Context.sendBroadcastRewind(
            duration: Int,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, duration)

        fun Context.sendBroadcastRewindUI(
            duration: Int,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission, extraValueUI, duration)
    }

    private object MiniPlayerBroadcastShuffle : BroadcastBase() {

        override val actionService: String = shuffleService

        override val actionUI: String = shuffleUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastUnShuffle : BroadcastBase() {

        override val actionService: String = unShuffleService

        override val actionUI: String = unShuffleUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLoop : BroadcastBase() {

        override val actionService: String = loopService

        override val actionUI: String = loopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLoopAll : BroadcastBase() {

        override val actionService: String = loopAllService

        override val actionUI: String = loopAllUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLoopAll(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLoopAllUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastNotLoop : BroadcastBase() {

        override val actionService: String = notLoopService

        override val actionUI: String = notLoopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastNotLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastNotLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastPlayByPath : BroadcastBase() {

        override val actionService: String = playByPathService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = Extra.playByPathService

        fun Context.sendBroadcastPlayByPath(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, path)
    }

    private object MiniPlayerBroadcastSongPath : BroadcastBase() {

        override val actionUI: String = songPathUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songPathUI

        fun Context.sendBroadcastSongPathUI(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, path)
    }

    private object MiniPlayerBroadcastSongArtist : BroadcastBase() {

        override val actionUI: String = songArtistUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songArtistUI

        fun Context.sendBroadcastSongArtistUI(
            artist: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, artist)
    }

    private object MiniPlayerBroadcastSongName : BroadcastBase() {

        override val actionUI: String = songNameUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songNameUI

        fun Context.sendBroadcastSongNameUI(
            name: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, name)
    }

    private object MiniPlayerBroadcastSongHQ : BroadcastBase() {

        override val actionUI: String = songHQUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songHQUI

        fun Context.sendBroadcastSongHQUI(
            isHQ: Boolean,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission, extraValueUI, isHQ)
    }

    private object MiniPlayerBroadcastSongDuration : BroadcastBase() {

        override val actionUI: String = songDurationUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songDurationUI

        fun Context.sendBroadcastSongDurationUI(
            duration: Int,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission, extraValueUI, duration)
    }

    private object MiniPlayerBroadcastShowGeneral : BroadcastBase() {
        override val actionUI: String = showMiniPlayerGeneralUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShowUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastHide : BroadcastBase() {
        override val actionUI: String = Action.hideUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastHide(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastShow : BroadcastBase() {
        override val actionUI: String = Action.showUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShow(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastPlayAllInFolder : BroadcastBase() {

        override val actionService: String = playAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastPlayAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastPlayNextAllInFolder : BroadcastBase() {

        override val actionService: String = playNextAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastPlayNextAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastShuffleAndPlayAllInFolder : BroadcastBase() {

        override val actionService: String = shuffleAndPlayAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastShuffleAndPlayAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastAddToQueue : BroadcastBase() {

        override val actionService: String = addToQueueService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastAddToQueue(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, path)
    }

    private object MiniPlayerBroadcastGetInfo : BroadcastBase() {
        override val actionService: String = getInfoService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastGetInfo(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)
    }

    private object MiniPlayerBroadcastPathWrong : BroadcastBase() {
        override val actionUI: String = Action.songPathIsWrongUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songPathUI

        fun Context.sendBroadcastPathIsWrongUI(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, path)
    }

    private object MiniPlayerBroadcastPlayOrStop : BroadcastBase() {
        override val actionService: String = Action.playOrStopService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastPlayOrStop(
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionService, permission)
    }

    private object MiniPlayerBroadcastIcon : BroadcastBase() {

        override val actionUI: String = Action.iconUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.iconUI

        fun Context.sendBroadcastIconUI(
            icon: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, icon)
    }

    private object RadioBroadcastStop : BroadcastBase() {

        override val actionService: String = Action.stopRadioService

        override val actionUI: String = Action.stopRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)


        fun Context.sendBroadcastStop(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastStopUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)

    }

    private object RadioBroadcastPlay : BroadcastBase() {

        override val actionService: String = Action.playRadioService

        override val actionUI: String = Action.playRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastPlay(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastPlayUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastLike : BroadcastBase() {

        override val actionService: String = Action.likeRadioService

        override val actionUI: String = Action.likeRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLike(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLikeUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastUnlike : BroadcastBase() {

        override val actionService: String = Action.unlikeRadioService

        override val actionUI: String = Action.unlikeRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnlike(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnlikeUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastGetInfo : BroadcastBase() {

        override val actionService: String = Action.getInfoRadioService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastGetInfo(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)
    }

    private object RadioBroadcastPlayByUrl : BroadcastBase() {

        override val actionService: String = Action.playByUrlRadioService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = Extra.playByRadioStationUrlService

        fun Context.sendBroadcastPlayByUrl(
            url: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionService, permission, extraValueService, url)
    }

    private object RadioBroadcastArtist : BroadcastBase() {

        override val actionUI: String = Action.radioArtistUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.radioArtistUI

        fun Context.sendBroadcastRadioArtistUI(
            artist: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission, extraValueUI, artist)
    }

    private object RadioBroadcastName : BroadcastBase() {

        override val actionUI: String = Action.radioNameUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.radioNameUI

        fun Context.sendBroadcastRadioNameUI(
            name: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission, extraValueUI, name)
    }

    private object RadioBroadcastPlayOrStop: BroadcastBase() {
        override val actionService: String = Action.playOrStopRadioService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastPlayOrStop(
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionService, permission)
    }

    private object RadioBroadcastShow : BroadcastBase() {
        override val actionUI: String = Action.showMiniPlayerRadioUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShowUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastIcon : BroadcastBase() {

        override val actionUI: String = Action.iconRadioUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.iconRadioUI

        fun Context.sendBroadcastRadioIconUI(
            icon: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission, extraValueUI, icon)
    }
}

