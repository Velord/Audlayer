package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.MiniPlayerBroadcastHub

object RepeatLogic {

    private var repeat = RepeatState.NotRepeat

    fun press(context: FragmentActivity) =
        when(repeat) {
            RepeatState.NotRepeat -> {
                repeat = RepeatState.RepeatAll
                MiniPlayerBroadcastHub.apply {
                    context.loopAllService()
                }
            }
            RepeatState.RepeatAll -> {
                repeat = RepeatState.Repeat
                MiniPlayerBroadcastHub.apply {
                    context.loopService()
                }
            }
            RepeatState.Repeat -> {
                repeat = RepeatState.NotRepeat
                MiniPlayerBroadcastHub.apply {
                    context.notLoopService()
                }
            }
        }

    enum class RepeatState {
        NotRepeat,
        Repeat,
        RepeatAll
    }
}