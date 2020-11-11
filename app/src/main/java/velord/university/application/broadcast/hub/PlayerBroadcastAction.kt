package velord.university.application.broadcast.hub

import android.content.Context
import android.content.IntentFilter

sealed class PlayerBroadcastAction {

    protected val permission: String = PERM_PRIVATE_MINI_PLAYER

    //what can receive service
    open val actionService: String = ""
    //what can receive ui
    open val actionUI: String = ""

    open val extraValueService: Any = ""

    open val extraValueUI: Any = ""

    open fun toService() {  }

    open fun toUI() {  }
    //if need transfer some data
    open fun <T> toService(value: T) {  }

    open fun <T> toUI(value: T) {  }

    internal class Stop(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.stopService

        override val actionUI: String = BroadcastAction.stopUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Play(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.playService

        override val actionUI: String = BroadcastAction.playUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Like(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.likeService

        override val actionUI: String = BroadcastAction.likeUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class UnLike(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.unlikeService

        override val actionUI: String = BroadcastAction.unlikeUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class SkipNext(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.skipNextService

        override val actionUI: String = BroadcastAction.skipNextUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class SkipPrev(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.skipPrevService

        override val actionUI: String = BroadcastAction.skipPrevUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Shuffle(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.shuffleService

        override val actionUI: String = BroadcastAction.shuffleUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class UnShuffle(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.unShuffleService

        override val actionUI: String = BroadcastAction.unShuffleUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Loop(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.loopService

        override val actionUI: String = BroadcastAction.loopUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class LoopAll(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.loopAllService

        override val actionUI: String = BroadcastAction.loopAllUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class LoopNot(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.notLoopService

        override val actionUI: String = BroadcastAction.notLoopUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Rewind(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.rewindService

        override val actionUI: String = BroadcastAction.rewindUI

        override val extraValueService: String = BroadcastExtra.rewindService

        override val extraValueUI: String = BroadcastExtra.rewindUI

        override fun <T> toService(value: T) =
            context.sendBroadcast(actionService, permission, extraValueService, value as Int)

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI, value as Int)
    }

    internal class PlayByPath(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.playByPathService

        override val actionUI: String = BroadcastAction.playByPathUI

        override val extraValueService: String = BroadcastExtra.playByPathService

        override val extraValueUI: String = BroadcastExtra.playByPathUI

        override fun <T> toService(value: T) =
            context.sendBroadcast(actionService, permission, extraValueService, value as String)

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }

    internal class SongArtist(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.songArtistUI

        override val extraValueUI: String = BroadcastExtra.songArtistUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }

    internal class SongName(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.songNameUI

        override val extraValueUI: String = BroadcastExtra.songNameUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }

    internal class SongHq(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.songHQUI

        override val extraValueUI: String = BroadcastExtra.songHQUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI, value as Boolean)
    }

    internal class SongDuration(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.songDurationUI

        override val extraValueUI: String = BroadcastExtra.songDurationUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI, value as Int)
    }

    internal class Hide(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.hideMiniPlayerUI

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Show(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.showMiniPlayerUI

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class PlayAllInFolder(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.playAllInFolderService

        override val extraValueService: String = BroadcastExtra.folderPathService

        override fun <T> toService(value: T) =
            context.sendBroadcast(actionService,
                permission, extraValueService, value as String)
    }

    internal class PlayNextAllInFolder(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.playNextAllInFolderService

        override val extraValueService: String = BroadcastExtra.folderPathService

        override fun <T> toService(value: T) =
            context.sendBroadcast(actionService,
                permission, extraValueService, value as String)
    }

    internal class ShuffleAndPlayAllInFolder(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.shuffleAndPlayAllInFolderService

        override val extraValueService: String = BroadcastExtra.folderPathService

        override fun <T> toService(value: T) =
            context.sendBroadcast(actionService,
                permission, extraValueService, value as String)
    }

    internal class AddToQueue(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.addToQueueService

        override val extraValueService: String = BroadcastExtra.folderPathService

        override fun <T> toService(value: T) =
            context.sendBroadcast(actionService,
                permission, extraValueService, value as String)
    }

    internal class GetInfo(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.getInfoService

        override fun toService() = context.sendBroadcast(actionService, permission)
    }

    internal class PathWrong(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.songPathIsWrongUI

        override val extraValueUI: String = BroadcastExtra.playByPathUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }

    internal class PlayOrStop(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionService: String = BroadcastAction.playOrStopService

        override fun toService() = context.sendBroadcast(actionService, permission)
    }

    internal class Icon(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.iconUI

        override val extraValueUI: String = BroadcastExtra.iconUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }

    internal class Unavailable(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.playerUnavailableUI

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class ClickOnIcon(
        private val context: Context
    ) : PlayerBroadcastAction() {

        override val actionUI: String = BroadcastAction.clickOnIcon

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }
}