package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.model.miniPlayer.broadcast.sendBroadcastLike
import velord.university.model.miniPlayer.broadcast.sendBroadcastUnlike

object HeartLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (FragmentActivity) -> Unit
        get() = { context ->
            context.sendBroadcastUnlike(PERM_PRIVATE_MINI_PLAYER)
        }

    override val secondCase: (FragmentActivity) -> Unit
        get() = { context ->
            context.sendBroadcastLike(PERM_PRIVATE_MINI_PLAYER)
        }
}