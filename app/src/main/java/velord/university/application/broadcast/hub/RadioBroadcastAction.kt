package velord.university.application.broadcast.hub

import android.content.Context

sealed class RadioBroadcastAction {

    protected val permission: String = PERM_PRIVATE_RADIO

    //what can receive service
    open val actionService: String = ""

    //what can receive ui
    open val actionUI: String = ""

    open val extraValueService: Any = ""

    open val extraValueUI: Any = ""

    open fun toService() {}

    open fun toUI() {}

    //if need transfer some data
    open fun <T> toService(value: T) {}

    open fun <T> toUI(value: T) {}

    internal class Stop(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionService: String = BroadcastAction.stopRadioService

        override val actionUI: String = BroadcastAction.stopRadioUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Play(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionService: String = BroadcastAction.playRadioService

        override val actionUI: String = BroadcastAction.playRadioUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Like(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionService: String = BroadcastAction.likeRadioService

        override val actionUI: String = BroadcastAction.likeRadioUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class UnLike(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionService: String = BroadcastAction.unlikeRadioService

        override val actionUI: String = BroadcastAction.unlikeRadioUI

        override fun toService() = context.sendBroadcast(actionService, permission)

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class GetInfo(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionService: String = BroadcastAction.getInfoRadioService

        override fun toService() = context.sendBroadcast(actionService, permission)
    }

    internal class PlayByUrl(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionService: String = BroadcastAction.playByUrlRadioService

        override val actionUI: String = BroadcastAction.playByUrlRadioUI

        override val extraValueService: String = BroadcastExtra.radioStationUrlService

        override val extraValueUI: String = BroadcastExtra.radioStationUrlService

        override fun <T> toService(value: T) =
            context.sendBroadcast(actionService,
                permission, extraValueService, value as String)

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI,
                permission, extraValueUI,  value as String)
    }

    internal class Artist(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.radioArtistUI

        override val extraValueUI: String = BroadcastExtra.radioArtistUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }

    internal class Name(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.radioNameUI

        override val extraValueUI: String = BroadcastExtra.radioNameUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }

    internal class PlayOrStop(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionService: String = BroadcastAction.playOrStopRadioService

        override fun toService() = context.sendBroadcast(actionService, permission)
    }

    internal class Show(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.showMiniPlayerRadioUI

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Hide(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.hideMiniPlayerRadioUI

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class Icon(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.iconRadioUI

        override val extraValueUI: String = BroadcastExtra.iconRadioUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI,
                permission, extraValueUI,  value as String)
    }

    internal class Unavailable(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.radioPlayerUnavailableUI

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class ClickOnIcon(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.clickOnRadioIcon

        override fun toUI() = context.sendBroadcast(actionUI, permission)
    }

    internal class UrlWrong(
        private val context: Context
    ) : RadioBroadcastAction() {

        override val actionUI: String = BroadcastAction.radioUrlIsWrongUI

        override val extraValueUI: String = BroadcastExtra.radioStationUrlUI

        override fun <T> toUI(value: T) =
            context.sendBroadcast(actionUI, permission, extraValueUI,  value as String)
    }
}