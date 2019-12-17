package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.model.miniPlayer.broadcast.sendBroadcastLoop
import velord.university.model.miniPlayer.broadcast.sendBroadcastLoopAll
import velord.university.model.miniPlayer.broadcast.sendBroadcastNotLoopUI

object RepeatLogic {

    private var repeat = RepeatState.NotRepeat

    fun press(context: FragmentActivity) =
        when(repeat) {
            RepeatState.NotRepeat -> {
                repeat = RepeatState.RepeatAll
                context.sendBroadcastLoopAll(PERM_PRIVATE_MINI_PLAYER)
            }
            RepeatState.RepeatAll -> {
                repeat = RepeatState.Repeat
                context.sendBroadcastLoop(PERM_PRIVATE_MINI_PLAYER)
            }
            RepeatState.Repeat -> {
                repeat = RepeatState.NotRepeat
                context.sendBroadcastNotLoopUI(PERM_PRIVATE_MINI_PLAYER)
            }
        }

    enum class RepeatState {
        NotRepeat,
        Repeat,
        RepeatAll
    }
}