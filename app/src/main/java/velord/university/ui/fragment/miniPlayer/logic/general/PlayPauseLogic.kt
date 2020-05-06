package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.AppBroadcastHub

object PlayPauseLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            AppBroadcastHub.apply {
                context.stopService()
            }
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            AppBroadcastHub.apply {
                context.playService()
            }
        }
}