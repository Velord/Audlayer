package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.miniPlayer.broadcast.MiniPlayerBroadcastSkipNext
object SkipNextLogic: BaseLogic {

    override fun press(context: FragmentActivity) {
        MiniPlayerBroadcastSkipNext.apply {
            context.sendBroadcastSkipNext()
        }
    }
}

