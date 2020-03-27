package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.MiniPlayerBroadcastHub

object PlayPauseLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastHub.apply {
                context.stopService()
            }
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastHub.apply {
                context.playService()
            }
        }
}