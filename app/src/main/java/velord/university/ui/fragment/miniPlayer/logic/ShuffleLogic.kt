package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.MiniPlayerBroadcastHub

object ShuffleLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastHub.apply {
                context.unShuffleService()
            }
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastHub.apply {
                context.shuffleService()
            }
        }
}