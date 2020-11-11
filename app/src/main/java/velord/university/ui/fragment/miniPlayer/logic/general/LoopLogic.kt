package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType

object LoopLogic {

    private var repeat =
        RepeatState.NotRepeat

    fun press(context: FragmentActivity) =
        when(repeat) {
            RepeatState.NotRepeat -> {
                repeat =
                    RepeatState.RepeatAll
                AppBroadcastHub.apply {
                    context.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
                }
            }
            RepeatState.RepeatAll -> {
                repeat =
                    RepeatState.Repeat
                AppBroadcastHub.apply {
                    context.doAction(BroadcastActionType.LOOP_PLAYER_SERVICE)
                }
            }
            RepeatState.Repeat -> {
                repeat =
                    RepeatState.NotRepeat
                AppBroadcastHub.apply {
                    context.doAction(BroadcastActionType.LOOP_NOT_PLAYER_SERVICE)
                }
            }
        }

    enum class RepeatState {
        NotRepeat,
        Repeat,
        RepeatAll
    }
}