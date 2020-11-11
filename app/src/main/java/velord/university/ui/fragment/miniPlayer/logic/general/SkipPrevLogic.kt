package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType

object SkipPrevLogic:
    BaseLogic {

    override fun press(context: FragmentActivity)  {
        AppBroadcastHub.run {
            context.doAction(BroadcastActionType.SKIP_PREV_PLAYER_SERVICE)
        }
    }
}

