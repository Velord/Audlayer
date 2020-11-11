package velord.university.application.broadcast.hub

import android.content.Context
import android.content.IntentFilter

const val PERM_PRIVATE_MINI_PLAYER = "velord.university.PERM_PRIVATE_MINI_PLAYER"
const val PERM_PRIVATE_RADIO = "velord.university.PERM_PRIVATE_RADIO"

enum class BroadcastActionType {
    STOP_PLAYER_SERVICE,
    STOP_PLAYER_UI,
    PLAY_PLAYER_SERVICE,
    PLAY_PLAYER_UI,
    LIKE_PLAYER_SERVICE,
    LIKE_PLAYER_UI,
    UNLIKE_PLAYER_SERVICE,
    UNLIKE_PLAYER_UI,
    SKIP_PLAYER_SERVICE,
    SKIP_PLAYER_UI,
    SKIP_PREV_PLAYER_SERVICE,
    SKIP_PREV_PLAYER_UI,
    SHUFFLE_PLAYER_SERVICE,
    SHUFFLE_PLAYER_UI,
    UN_SHUFFLE_PLAYER_SERVICE,
    UN_SHUFFLE_PLAYER_UI,
    LOOP_PLAYER_SERVICE,
    LOOP_PLAYER_UI,
    LOOP_ALL_PLAYER_SERVICE,
    LOOP_ALL_PLAYER_UI,
    LOOP_NOT_PLAYER_SERVICE,
    LOOP_NOT_PLAYER_UI,
    STOP_RADIO_SERVICE,
    STOP_RADIO_UI,
    PLAY_RADIO_SERVICE,
    PLAY_RADIO_UI,
    LIKE_RADIO_SERVICE,
    LIKE_RADIO_UI,
    UNLIKE_RADIO_SERVICE,
    UNLIKE_RADIO_UI,
}

object AppBroadcastHub {

    fun Context.doAction(
        type: BroadcastActionType
    ) = when(type) {
        BroadcastActionType.STOP_PLAYER_SERVICE -> MiniPlayerBroadcastStop.run {
            sendBroadcastStop()
        }
        BroadcastActionType.STOP_PLAYER_UI -> MiniPlayerBroadcastStop.run {
            sendBroadcastStopUI()
        }
        BroadcastActionType.PLAY_PLAYER_SERVICE -> MiniPlayerBroadcastPlay.run {
            sendBroadcastPlay()
        }
        BroadcastActionType.PLAY_PLAYER_UI -> MiniPlayerBroadcastPlay.run {
            sendBroadcastPlayUI()
        }
        BroadcastActionType.LIKE_PLAYER_SERVICE -> MiniPlayerBroadcastLike.run {
            sendBroadcastLike()
        }
        BroadcastActionType.LIKE_PLAYER_UI -> MiniPlayerBroadcastLike.run {
            sendBroadcastLikeUI()
        }
        BroadcastActionType.UNLIKE_PLAYER_SERVICE -> MiniPlayerBroadcastUnlike.run {
            sendBroadcastUnlike()
        }
        BroadcastActionType.UNLIKE_PLAYER_UI -> MiniPlayerBroadcastUnlike.run {
            sendBroadcastUnlikeUI()
        }
        BroadcastActionType.SKIP_PLAYER_SERVICE -> MiniPlayerBroadcastSkipNext.run {
            sendBroadcastSkipNext()
        }
        BroadcastActionType.SKIP_PLAYER_UI -> MiniPlayerBroadcastSkipNext.run {
            sendBroadcastSkipNextUI()
        }
        BroadcastActionType.SKIP_PREV_PLAYER_SERVICE -> MiniPlayerBroadcastSkipPrev.run {
            sendBroadcastSkipPrev()
        }
        BroadcastActionType.SKIP_PREV_PLAYER_UI -> MiniPlayerBroadcastSkipPrev.run {
            sendBroadcastSkipPrevUI()
        }
        BroadcastActionType.SHUFFLE_PLAYER_SERVICE -> MiniPlayerBroadcastShuffle.run {
            sendBroadcastShuffle()
        }
        BroadcastActionType.SHUFFLE_PLAYER_UI -> MiniPlayerBroadcastShuffle.run {
            sendBroadcastShuffleUI()
        }
        BroadcastActionType.UN_SHUFFLE_PLAYER_SERVICE -> MiniPlayerBroadcastUnShuffle.run {
            sendBroadcastUnShuffle()
        }
        BroadcastActionType.UN_SHUFFLE_PLAYER_UI -> MiniPlayerBroadcastUnShuffle.run {
            sendBroadcastUnShuffleUI()
        }
        BroadcastActionType.LOOP_PLAYER_SERVICE -> MiniPlayerBroadcastLoop.run {
            sendBroadcastLoop()
        }
        BroadcastActionType.LOOP_PLAYER_UI -> MiniPlayerBroadcastLoop.run {
            sendBroadcastLoopUI()
        }
        BroadcastActionType.LOOP_ALL_PLAYER_SERVICE -> MiniPlayerBroadcastLoopAll.run {
            sendBroadcastLoopAll()
        }
        BroadcastActionType.LOOP_ALL_PLAYER_UI -> MiniPlayerBroadcastLoopAll.run {
            sendBroadcastLoopAllUI()
        }
        BroadcastActionType.LOOP_NOT_PLAYER_SERVICE -> MiniPlayerBroadcastNotLoop.run {
            sendBroadcastNotLoop()
        }
        BroadcastActionType.LOOP_NOT_PLAYER_UI -> MiniPlayerBroadcastNotLoop.run {
            sendBroadcastNotLoopUI()
        }
        BroadcastActionType.STOP_RADIO_SERVICE -> RadioBroadcastStop.run {
            sendBroadcastStop()
        }
        BroadcastActionType.STOP_RADIO_UI -> RadioBroadcastStop.run {
            sendBroadcastStopUI()
        }
        BroadcastActionType.PLAY_RADIO_SERVICE -> RadioBroadcastPlay.run {
            sendBroadcastPlay()
        }
        BroadcastActionType.PLAY_RADIO_UI -> RadioBroadcastPlay.run {
            sendBroadcastPlayUI()
        }
        BroadcastActionType.LIKE_RADIO_SERVICE -> RadioBroadcastLike.run {
            sendBroadcastLike()
        }
        BroadcastActionType.LIKE_RADIO_UI -> RadioBroadcastLike.run {
            sendBroadcastLikeUI()
        }
        BroadcastActionType.UNLIKE_RADIO_SERVICE -> RadioBroadcastUnlike.run {
            sendBroadcastUnlike()
        }
        BroadcastActionType.UNLIKE_RADIO_UI -> RadioBroadcastUnlike.run {
            sendBroadcastUnlikeUI()
        }
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

    fun Context.getInfoService() =
        MiniPlayerBroadcastGetInfo.run {
            this@getInfoService.sendBroadcastGetInfo()
        }

    fun Context.playOrStopService() =
        MiniPlayerBroadcastPlayOrStop.run {
            this@playOrStopService.sendBroadcastPlayOrStop()
        }

    fun Context.playerUnavailableUI() =
        MiniPlayerBroadcastUnavailable.run {
            this@playerUnavailableUI.sendBroadcastUnavailableUI()
        }

    fun Context.clickOnIcon() =
        MiniPlayerBroadcastClickOnIcon.run {
            this@clickOnIcon.sendBroadcastClickOnIcon()
        }

    fun Context.pathIsWrongUI(path: String) =
        MiniPlayerBroadcastPathWrong.run {
            this@pathIsWrongUI.sendBroadcastPathIsWrongUI(path)
        }

    fun Context.iconUI(icon: String) =
        MiniPlayerBroadcastIcon.run {
            this@iconUI.sendBroadcastIconUI(icon)
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

    fun Context.rewindService(duration: Int) =
        MiniPlayerBroadcastRewind.run {
            this@rewindService.sendBroadcastRewind(duration)
        }

    fun Context.rewindUI(duration: Int) =
        MiniPlayerBroadcastRewind.run {
            this@rewindUI.sendBroadcastRewindUI(duration)
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

    //radio
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

    fun Context.radioPlayerUnavailableUI() =
        RadioBroadcastUnavailable.run {
            this@radioPlayerUnavailableUI.sendBroadcastUnavailableUI()
        }

    fun Context.clickOnRadioIcon() =
        RadioBroadcastClickOnIcon.run {
            this@clickOnRadioIcon.sendBroadcastClickOnIcon()
        }

    fun Context.radioUrlIsWrongUI(url: String) =
        RadioBroadcastUrlWrong.run {
            this@radioUrlIsWrongUI.sendBroadcastUrlIsWrongUI(url)
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

        override val actionService: String = BroadcastAction.stopService

        override val actionUI: String = BroadcastAction.stopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)


        fun Context.sendBroadcastStop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastStopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)

    }

    private object MiniPlayerBroadcastPlay : BroadcastBase() {

        override val actionService: String = BroadcastAction.playService

        override val actionUI: String = BroadcastAction.playUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastPlay(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastPlayUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLike : BroadcastBase() {

        override val actionService: String = BroadcastAction.likeService

        override val actionUI: String = BroadcastAction.likeUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastUnlike : BroadcastBase() {

        override val actionService: String = BroadcastAction.unlikeService

        override val actionUI: String = BroadcastAction.unlikeUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnlike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnlikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastSkipNext : BroadcastBase() {

        override val actionService: String = BroadcastAction.skipNextService

        override val actionUI: String = BroadcastAction.skipNextUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastSkipNext(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastSkipNextUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastSkipPrev : BroadcastBase() {

        override val actionService: String = BroadcastAction.skipPrevService

        override val actionUI: String = BroadcastAction.skipPrevUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastSkipPrev(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastSkipPrevUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastRewind : BroadcastBase() {

        override val actionService: String = BroadcastAction.rewindService

        override val actionUI: String = BroadcastAction.rewindUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueService: String = BroadcastExtra.rewindService

        override val extraValueUI: String = BroadcastExtra.rewindUI

        fun Context.sendBroadcastRewind(
            duration: Int,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionService, permission, extraValueService, duration)

        fun Context.sendBroadcastRewindUI(
            duration: Int,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionUI, permission, extraValueUI, duration)
    }

    private object MiniPlayerBroadcastShuffle : BroadcastBase() {

        override val actionService: String = BroadcastAction.shuffleService

        override val actionUI: String = BroadcastAction.shuffleUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastUnShuffle : BroadcastBase() {

        override val actionService: String = BroadcastAction.unShuffleService

        override val actionUI: String = BroadcastAction.unShuffleUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLoop : BroadcastBase() {

        override val actionService: String = BroadcastAction.loopService

        override val actionUI: String = BroadcastAction.loopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastLoopAll : BroadcastBase() {

        override val actionService: String = BroadcastAction.loopAllService

        override val actionUI: String = BroadcastAction.loopAllUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLoopAll(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLoopAllUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastNotLoop : BroadcastBase() {

        override val actionService: String = BroadcastAction.notLoopService

        override val actionUI: String = BroadcastAction.notLoopUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastNotLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastNotLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastPlayByPath : BroadcastBase() {

        override val actionService: String = BroadcastAction.playByPathService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = BroadcastExtra.playByPathService

        fun Context.sendBroadcastPlayByPath(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionService, permission, extraValueService, path)
    }

    private object MiniPlayerBroadcastSongPath : BroadcastBase() {

        override val actionUI: String = BroadcastAction.songPathUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.songPathUI

        fun Context.sendBroadcastSongPathUI(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, path)
    }

    private object MiniPlayerBroadcastSongArtist : BroadcastBase() {

        override val actionUI: String = BroadcastAction.songArtistUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.songArtistUI

        fun Context.sendBroadcastSongArtistUI(
            artist: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, artist)
    }

    private object MiniPlayerBroadcastSongName : BroadcastBase() {

        override val actionUI: String = BroadcastAction.songNameUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.songNameUI

        fun Context.sendBroadcastSongNameUI(
            name: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, name)
    }

    private object MiniPlayerBroadcastSongHQ : BroadcastBase() {

        override val actionUI: String = BroadcastAction.songHQUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.songHQUI

        fun Context.sendBroadcastSongHQUI(
            isHQ: Boolean,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionUI, permission, extraValueUI, isHQ)
    }

    private object MiniPlayerBroadcastSongDuration : BroadcastBase() {

        override val actionUI: String = BroadcastAction.songDurationUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.songDurationUI

        fun Context.sendBroadcastSongDurationUI(
            duration: Int,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionUI, permission, extraValueUI, duration)
    }

    private object MiniPlayerBroadcastShowGeneral : BroadcastBase() {
        override val actionUI: String = BroadcastAction.showMiniPlayerGeneralUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShowUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastHide : BroadcastBase() {
        override val actionUI: String = BroadcastAction.hideUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastHide(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastShow : BroadcastBase() {
        override val actionUI: String = BroadcastAction.showUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShow(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastPlayAllInFolder : BroadcastBase() {

        override val actionService: String = BroadcastAction.playAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = BroadcastExtra.folderPathService

        fun Context.sendBroadcastPlayAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastPlayNextAllInFolder : BroadcastBase() {

        override val actionService: String = BroadcastAction.playNextAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = BroadcastExtra.folderPathService

        fun Context.sendBroadcastPlayNextAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastShuffleAndPlayAllInFolder : BroadcastBase() {

        override val actionService: String = BroadcastAction.shuffleAndPlayAllInFolderService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = BroadcastExtra.folderPathService

        fun Context.sendBroadcastShuffleAndPlayAllInFolder(
            folderPath: String, permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionService, permission, extraValueService, folderPath)
    }

    private object MiniPlayerBroadcastAddToQueue : BroadcastBase() {

        override val actionService: String = BroadcastAction.addToQueueService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = BroadcastExtra.folderPathService

        fun Context.sendBroadcastAddToQueue(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) =
            sendBroadcast(actionService, permission, extraValueService, path)
    }

    private object MiniPlayerBroadcastGetInfo : BroadcastBase() {
        override val actionService: String = BroadcastAction.getInfoService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastGetInfo(permission: String = PERM_PRIVATE_MINI_PLAYER) =
            sendBroadcast(actionService, permission)
    }

    private object MiniPlayerBroadcastPathWrong : BroadcastBase() {
        override val actionUI: String = BroadcastAction.songPathIsWrongUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.songPathUI

        fun Context.sendBroadcastPathIsWrongUI(
            path: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, path)
    }

    private object MiniPlayerBroadcastPlayOrStop : BroadcastBase() {
        override val actionService: String = BroadcastAction.playOrStopService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastPlayOrStop(
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionService, permission)
    }

    private object MiniPlayerBroadcastIcon : BroadcastBase() {

        override val actionUI: String = BroadcastAction.iconUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.iconUI

        fun Context.sendBroadcastIconUI(
            icon: String,
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission, extraValueUI, icon)
    }

    private object MiniPlayerBroadcastUnavailable : BroadcastBase() {

        override val actionUI: String = BroadcastAction.playerUnavailableUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnavailableUI(
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission)
    }

    private object MiniPlayerBroadcastClickOnIcon : BroadcastBase() {

        override val actionUI: String = BroadcastAction.clickOnIcon

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastClickOnIcon(
            permission: String = PERM_PRIVATE_MINI_PLAYER
        ) = sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastStop : BroadcastBase() {

        override val actionService: String = BroadcastAction.stopRadioService

        override val actionUI: String = BroadcastAction.stopRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)


        fun Context.sendBroadcastStop(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastStopUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)

    }

    private object RadioBroadcastPlay : BroadcastBase() {

        override val actionService: String = BroadcastAction.playRadioService

        override val actionUI: String = BroadcastAction.playRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastPlay(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastPlayUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastLike : BroadcastBase() {

        override val actionService: String = BroadcastAction.likeRadioService

        override val actionUI: String = BroadcastAction.likeRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastLike(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastLikeUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastUnlike : BroadcastBase() {

        override val actionService: String = BroadcastAction.unlikeRadioService

        override val actionUI: String = BroadcastAction.unlikeRadioUI

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnlike(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)

        fun Context.sendBroadcastUnlikeUI(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastGetInfo : BroadcastBase() {

        override val actionService: String = BroadcastAction.getInfoRadioService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastGetInfo(permission: String = PERM_PRIVATE_RADIO) =
            sendBroadcast(actionService, permission)
    }

    private object RadioBroadcastPlayByUrl : BroadcastBase() {

        override val actionService: String = BroadcastAction.playByUrlRadioService

        override val filterService: IntentFilter = IntentFilter(actionService)

        override val extraValueService: String = BroadcastExtra.radioStationUrlService

        fun Context.sendBroadcastPlayByUrl(
            url: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionService, permission, extraValueService, url)
    }

    private object RadioBroadcastArtist : BroadcastBase() {

        override val actionUI: String = BroadcastAction.radioArtistUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.radioArtistUI

        fun Context.sendBroadcastRadioArtistUI(
            artist: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission, extraValueUI, artist)
    }

    private object RadioBroadcastName : BroadcastBase() {

        override val actionUI: String = BroadcastAction.radioNameUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.radioNameUI

        fun Context.sendBroadcastRadioNameUI(
            name: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission, extraValueUI, name)
    }

    private object RadioBroadcastPlayOrStop: BroadcastBase() {
        override val actionService: String = BroadcastAction.playOrStopRadioService

        override val filterService: IntentFilter = IntentFilter(actionService)

        fun Context.sendBroadcastPlayOrStop(
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionService, permission)
    }

    private object RadioBroadcastShow : BroadcastBase() {
        override val actionUI: String = BroadcastAction.showMiniPlayerRadioUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastShowUI(
            permission: String = PERM_PRIVATE_RADIO
        ) =
            sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastIcon : BroadcastBase() {

        override val actionUI: String = BroadcastAction.iconRadioUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.iconRadioUI

        fun Context.sendBroadcastRadioIconUI(
            icon: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission, extraValueUI, icon)
    }

    private object RadioBroadcastUnavailable : BroadcastBase() {

        override val actionUI: String = BroadcastAction.radioPlayerUnavailableUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastUnavailableUI(
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastClickOnIcon : BroadcastBase() {

        override val actionUI: String = BroadcastAction.clickOnRadioIcon

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        fun Context.sendBroadcastClickOnIcon(
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission)
    }

    private object RadioBroadcastUrlWrong : BroadcastBase() {
        override val actionUI: String = BroadcastAction.radioUrlIsWrongUI

        override val filterUI: IntentFilter = IntentFilter(actionUI)

        override val extraValueUI: String = BroadcastExtra.radioStationUrlUI

        fun Context.sendBroadcastUrlIsWrongUI(
            url: String,
            permission: String = PERM_PRIVATE_RADIO
        ) = sendBroadcast(actionUI, permission, extraValueUI, url)
    }
}
