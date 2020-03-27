package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.MiniPlayerBroadcastHub

object HeartLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastHub.apply {
                context.unlikeService()
            }
        }

    override val secondCase: (FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastHub.apply {
                context.likeService()
            }
        }
}