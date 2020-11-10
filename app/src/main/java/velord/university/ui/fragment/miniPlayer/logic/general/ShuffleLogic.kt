package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

object ShuffleLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, _ ->
            AppBroadcastHub.apply {
                context.unShuffleService()
            }
        }

    override val secondCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, _ ->
            AppBroadcastHub.apply {
                context.shuffleService()
            }
        }
}