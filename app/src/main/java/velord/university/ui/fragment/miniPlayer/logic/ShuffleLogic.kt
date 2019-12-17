package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.model.miniPlayer.broadcast.sendBroadcastShuffle
import velord.university.model.miniPlayer.broadcast.sendBroadcastUnShuffle

object ShuffleLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            context.sendBroadcastUnShuffle(PERM_PRIVATE_MINI_PLAYER)
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            context.sendBroadcastShuffle(PERM_PRIVATE_MINI_PLAYER)
        }
}