package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

object HeartLogic: TwoStateLogic() {

    override var value: Boolean = false

    override val firstCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, state ->
            when(state) {
                MiniPlayerLayoutState.GENERAL ->
                    AppBroadcastHub.apply {
                        context.unlikeService()
                    }
                MiniPlayerLayoutState.RADIO ->
                    AppBroadcastHub.apply {
                        context.unlikeRadioService()
                    }
            }
        }

    override val secondCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit
        get() = { context, state ->
            when(state) {
                MiniPlayerLayoutState.GENERAL ->
                    AppBroadcastHub.apply {
                        context.likeService()
                    }
                MiniPlayerLayoutState.RADIO ->
                    AppBroadcastHub.apply {
                        context.likeRadioService()
                    }
            }
        }
}