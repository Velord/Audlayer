package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.AppBroadcastHub

object SkipNextLogic:
    BaseLogic {

    override fun press(context: FragmentActivity) {
        AppBroadcastHub.apply {
            context.skipNextService()
        }
    }
}

