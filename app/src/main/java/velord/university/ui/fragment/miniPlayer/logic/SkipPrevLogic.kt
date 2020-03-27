package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.MiniPlayerBroadcastHub

object SkipPrevLogic: BaseLogic {

    override fun press(context: FragmentActivity)  {
        MiniPlayerBroadcastHub.apply {
            context.skipPrevService()
        }
    }
}

