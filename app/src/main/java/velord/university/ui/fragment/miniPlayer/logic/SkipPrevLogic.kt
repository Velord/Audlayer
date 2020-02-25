package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSkipPrev

object SkipPrevLogic: BaseLogic {

    override fun press(context: FragmentActivity)  {
        MiniPlayerBroadcastSkipPrev.apply {
            context.sendBroadcastSkipPrev()
        }
    }
}

