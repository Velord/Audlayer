package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastLoop
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastLoopAll
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastNotLoop

object RepeatLogic {

    private var repeat = RepeatState.NotRepeat

    fun press(context: FragmentActivity) =
        when(repeat) {
            RepeatState.NotRepeat -> {
                repeat = RepeatState.RepeatAll
                MiniPlayerBroadcastLoopAll.apply {
                    context.sendBroadcastLoopAll()
                }
            }
            RepeatState.RepeatAll -> {
                repeat = RepeatState.Repeat
                MiniPlayerBroadcastLoop.apply {
                    context.sendBroadcastLoop()
                }
            }
            RepeatState.Repeat -> {
                repeat = RepeatState.NotRepeat
                MiniPlayerBroadcastNotLoop.apply {
                    context.sendBroadcastNotLoopUI()
                }
            }
        }

    enum class RepeatState {
        NotRepeat,
        Repeat,
        RepeatAll
    }
}