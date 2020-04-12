package velord.university.application.broadcast

import android.content.Context
import android.content.IntentFilter
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.addToQueueService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.getInfoService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.hideUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.likeService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.likeUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.loopAllService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.loopAllUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.loopService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.loopUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.notLoopService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.notLoopUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.playAllInFolderService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.playByPathService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.playNextAllInFolderService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.playService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.playUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.rewindService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.rewindUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.showUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.shuffleAndPlayAllInFolderService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.shuffleService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.shuffleUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.skipNextService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.skipNextUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.skipPrevService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.skipPrevUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.songArtistUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.songDurationUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.songHQUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.songNameUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.songPathUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.unShuffleService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.unShuffleUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.unlikeService
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Action.unlikeUI
import velord.university.application.broadcast.MiniPlayerBroadcastHub.Extra.folderPathService

const val PERM_PRIVATE_MINI_PLAYER = "velord.university.PERM_PRIVATE_MINI_PLAYER"

object MiniPlayerBroadcastHub {

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

    fun Context.showUI() =
        MiniPlayerBroadcastShow.run {
            this@showUI.sendBroadcastShowUI()
        }

    fun Context.hideUI() =
        MiniPlayerBroadcastHide.run {
            this@hideUI.sendBroadcastHide()
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

    object Action {
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
        const val showUI = "velord.university.SHOW"
        const val hideUI = "velord.university.SONG_HIDE"
        const val playAllInFolderService = "velord.university.PLAY_ALL_IN_FOLDER"
        const val playNextAllInFolderService = "velord.university.PLAY_NEXT_ALL_IN_FOLDER"
        const val shuffleAndPlayAllInFolderService = "velord.university.SHUFFLE_AND_PLAY_ALL_IN_FOLDER"
        const val addToQueueService = "velord.university.ADD_TO_QUEUE"
        const val getInfoService = "velord.university.GET_INFO"
        const val songPathIsWrongUI = "velord.university.SONG_PATH_IS_WRONG_UI"
        const val playOrStopService = "velord.university.PLAY_OR_STOP"
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
    }

    private abstract class MiniPlayerBroadcastBase {
        //what can receive service
        open val actionService: String = ""
        //what can receive ui
        open val actionUI: String = ""

        open val filterService: IntentFilter = IntentFilter()

        open val filterUI: IntentFilter = IntentFilter()

        open val extraValueService: Any = ""

        open val extraValueUI: Any = ""
    }

    private object MiniPlayerBroadcastStop : MiniPlayerBroadcastBase() {

        override val actionService: String = Action.stopService

        override val actionUI: String = Action.stopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)


        fun Context.sendBroadcastStop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastStopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)

    }

    private object MiniPlayerBroadcastPlay : MiniPlayerBroadcastBase() {

        override val actionService: String = playService

        override val actionUI: String = playUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastPlay(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastPlayUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLike : MiniPlayerBroadcastBase() {

        override val actionService: String = likeService

        override val actionUI: String = likeUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastUnlike : MiniPlayerBroadcastBase() {

        override val actionService: String = unlikeService

        override val actionUI: String = unlikeUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnlike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnlikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastSkipNext : MiniPlayerBroadcastBase() {

        override val actionService: String = skipNextService

        override val actionUI: String =  skipNextUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastSkipNext(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastSkipNextUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastSkipPrev : MiniPlayerBroadcastBase() {

        override val actionService: String = skipPrevService

        override val actionUI: String = skipPrevUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastSkipPrev(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastSkipPrevUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastRewind : MiniPlayerBroadcastBase() {

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

    private object MiniPlayerBroadcastShuffle : MiniPlayerBroadcastBase() {

        override val actionService: String = shuffleService

        override val actionUI: String = shuffleUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastUnShuffle : MiniPlayerBroadcastBase() {

        override val actionService: String = unShuffleService

        override val actionUI: String = unShuffleUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLoop : MiniPlayerBroadcastBase() {

        override val actionService: String = loopService

        override val actionUI: String = loopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLoopAll : MiniPlayerBroadcastBase() {

        override val actionService: String = loopAllService

        override val actionUI: String = loopAllUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLoopAll(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLoopAllUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastNotLoop : MiniPlayerBroadcastBase() {

        override val actionService: String = notLoopService

        override val actionUI: String = notLoopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastNotLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastNotLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastPlayByPath : MiniPlayerBroadcastBase() {

        override val actionService: String = playByPathService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = Extra.playByPathService

        fun Context.sendBroadcastPlayByPath(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, path)
    }

    private object MiniPlayerBroadcastSongPath : MiniPlayerBroadcastBase() {

        override val actionUI: String = songPathUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songPathUI

        fun Context.sendBroadcastSongPathUI(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, path)
    }

    private object MiniPlayerBroadcastSongArtist : MiniPlayerBroadcastBase() {

        override val actionUI: String = songArtistUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songArtistUI

        fun Context.sendBroadcastSongArtistUI(
            artist: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionUI, permission, extraValueUI, artist)
    }

    private object MiniPlayerBroadcastSongName : MiniPlayerBroadcastBase() {

        override val actionUI: String = songNameUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songNameUI

        fun Context.sendBroadcastSongNameUI(
            name: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission, extraValueUI, name)
    }

    private object MiniPlayerBroadcastSongHQ : MiniPlayerBroadcastBase() {

        override val actionUI: String = songHQUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songHQUI

        fun Context.sendBroadcastSongHQUI(
            isHQ: Boolean,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission, extraValueUI, isHQ)
    }

    private object MiniPlayerBroadcastSongDuration : MiniPlayerBroadcastBase() {

        override val actionUI: String = songDurationUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songDurationUI

        fun Context.sendBroadcastSongDurationUI(
            duration: Int,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission, extraValueUI, duration)
    }

    private object MiniPlayerBroadcastShow : MiniPlayerBroadcastBase() {
        override val actionUI: String = showUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShowUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastHide : MiniPlayerBroadcastBase() {
        override val actionUI: String = hideUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastHide(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastPlayAllInFolder : MiniPlayerBroadcastBase() {

        override val actionService: String = playAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastPlayAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastPlayNextAllInFolder : MiniPlayerBroadcastBase() {

        override val actionService: String = playNextAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastPlayNextAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastShuffleAndPlayAllInFolder : MiniPlayerBroadcastBase() {

        override val actionService: String = shuffleAndPlayAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastShuffleAndPlayAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastAddToQueue : MiniPlayerBroadcastBase() {

        override val actionService: String = addToQueueService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = folderPathService

        fun Context.sendBroadcastAddToQueue(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission, extraValueService, path)
    }

    private object MiniPlayerBroadcastGetInfo : MiniPlayerBroadcastBase() {
        override val actionService: String = getInfoService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastGetInfo(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)
    }

    private object MiniPlayerBroadcastPathWrong : MiniPlayerBroadcastBase() {
        override val actionUI: String = Action.songPathIsWrongUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = Extra.songPathUI

        fun Context.sendBroadcastPathIsWrongUI(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, path)
    }

    private object MiniPlayerBroadcastPlayOrStop : MiniPlayerBroadcastBase() {
        override val actionService: String = Action.playOrStopService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastPlayOrStop(
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionService, permission)
    }
}

