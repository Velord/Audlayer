package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

object ShuffleLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, _ ->
            AppBroadcastHub.run {
                context.doAction(BroadcastActionType.UN_SHUFFLE_PLAYER_SERVICE)
            }
        }

    override val secondCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, _ ->
            AppBroadcastHub.run {
                context.doAction(BroadcastActionType.SHUFFLE_PLAYER_SERVICE)
            }
        }
}