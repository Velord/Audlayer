package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.model.miniPlayer.broadcast.sendBroadcastSkipPrev

object SkipPrevLogic: BaseLogic {

    override fun press(context: FragmentActivity) =
        context.sendBroadcastSkipPrev(PERM_PRIVATE_MINI_PLAYER)
}