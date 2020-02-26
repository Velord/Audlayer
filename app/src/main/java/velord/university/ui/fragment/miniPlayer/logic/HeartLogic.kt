package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.MiniPlayerBroadcastLike
import velord.university.application.broadcast.MiniPlayerBroadcastUnlike
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER

object HeartLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastUnlike.apply {
                context.sendBroadcastUnlike(PERM_PRIVATE_MINI_PLAYER)
            }
        }

    override val secondCase: (FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastLike.apply {
                context.sendBroadcastLike(PERM_PRIVATE_MINI_PLAYER)
            }
        }
}