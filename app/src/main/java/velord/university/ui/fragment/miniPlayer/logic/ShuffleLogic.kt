package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastShuffle
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastUnShuffle
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER

object ShuffleLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastUnShuffle.apply {
                context.sendBroadcastUnShuffle(PERM_PRIVATE_MINI_PLAYER)
            }
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastShuffle.apply {
                context.sendBroadcastShuffle(PERM_PRIVATE_MINI_PLAYER)
            }
        }
}