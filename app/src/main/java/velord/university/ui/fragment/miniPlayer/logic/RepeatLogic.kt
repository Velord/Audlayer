package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.AppBroadcastHub

object RepeatLogic {

    private var repeat = RepeatState.NotRepeat

    fun press(context: FragmentActivity) =
        when(repeat) {
            RepeatState.NotRepeat -> {
                repeat = RepeatState.RepeatAll
                AppBroadcastHub.apply {
                    context.loopAllService()
                }
            }
            RepeatState.RepeatAll -> {
                repeat = RepeatState.Repeat
                AppBroadcastHub.apply {
                    context.loopService()
                }
            }
            RepeatState.Repeat -> {
                repeat = RepeatState.NotRepeat
                AppBroadcastHub.apply {
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