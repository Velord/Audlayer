package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

object PlayPauseLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, state ->
            when(state) {
                MiniPlayerLayoutState.GENERAL ->
                    AppBroadcastHub.apply {
                        context.doAction(BroadcastActionType.STOP_PLAYER_SERVICE)
                    }
                MiniPlayerLayoutState.RADIO ->
                    AppBroadcastHub.run {
                        context.doAction(BroadcastActionType.STOP_RADIO_SERVICE)
                    }
            }
        }

    override val secondCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, state ->
            when(state) {
                MiniPlayerLayoutState.GENERAL ->
                    AppBroadcastHub.apply {
                        context.doAction(BroadcastActionType.PLAY_PLAYER_SERVICE)
                    }
                MiniPlayerLayoutState.RADIO ->
                    AppBroadcastHub.run {
                        context.doAction(BroadcastActionType.PLAY_RADIO_SERVICE)
                    }
            }
        }
}