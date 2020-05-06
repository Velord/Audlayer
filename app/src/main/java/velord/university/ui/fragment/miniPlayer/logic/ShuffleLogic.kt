package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.AppBroadcastHub

object ShuffleLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            AppBroadcastHub.apply {
                context.unShuffleService()
            }
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            AppBroadcastHub.apply {
                context.shuffleService()
            }
        }
}