package velord.university.ui.fragment.miniPlayer.logic

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.MiniPlayerBroadcastPlay
import velord.university.application.broadcast.MiniPlayerBroadcastStop

object PlayPauseLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastStop.apply {
                context.sendBroadcastStop()
            }
        }

    override val secondCase: (context: FragmentActivity) -> Unit
        get() = { context ->
            MiniPlayerBroadcastPlay.apply {
                context.sendBroadcastPlay()
            }
        }
}