package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.AppBroadcastHub

object HeartLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (FragmentActivity) -> Unit
        get() = { context ->
            AppBroadcastHub.apply {
                context.unlikeService()
            }
        }

    override val secondCase: (FragmentActivity) -> Unit
        get() = { context ->
            AppBroadcastHub.apply {
                context.likeService()
            }
        }
}