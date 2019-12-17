package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.model.miniPlayer.broadcast.sendBroadcastPlay
import velord.university.model.miniPlayer.broadcast.sendBroadcastStop

object PlayPauseLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            context.sendBroadcastStop(PERM_PRIVATE_MINI_PLAYER)
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            context.sendBroadcastPlay(PERM_PRIVATE_MINI_PLAYER)
        }
}