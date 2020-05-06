package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.AppBroadcastHub

object SkipNextLogic: BaseLogic {

    override fun press(context: FragmentActivity) {
        AppBroadcastHub.apply {
            context.skipNextService()
        }
    }
}

