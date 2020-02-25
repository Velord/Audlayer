package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastLoop
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastLoopAll
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastNotLoop

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
                    context.sendBroadcastNotLoop()
                }
            }
        }

    enum class RepeatState {
        NotRepeat,
        Repeat,
        RepeatAll
    }
}