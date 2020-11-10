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
                        context.doAction(BroadcastActionType.STOP_MINI_PLAYER)
                    }
                MiniPlayerLayoutState.RADIO ->
                    AppBroadcastHub.apply {
                        context.stopRadioService()
                    }
            }
        }

    override val secondCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, state ->
            when(state) {
                MiniPlayerLayoutState.GENERAL ->
                    AppBroadcastHub.apply {
                        context.doAction(BroadcastActionType.PLAY_MINI_PLAYER)
                    }
                MiniPlayerLayoutState.RADIO ->
                    AppBroadcastHub.apply {
                        context.playRadioService()
                    }
            }
        }
}