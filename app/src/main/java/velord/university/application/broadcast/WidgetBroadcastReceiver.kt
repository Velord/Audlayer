package velord.university.application.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import velord.university.application.notification.MiniPlayerNotification.NOTIFICATION_ACTION_NEXT
import velord.university.application.notification.MiniPlayerNotification.NOTIFICATION_ACTION_PLAY_OR_STOP
import velord.university.application.notification.MiniPlayerNotification.NOTIFICATION_ACTION_PREVIUOS
import velord.university.repository.MiniPlayerRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

class WidgetBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            NOTIFICATION_ACTION_PLAY_OR_STOP ->
                when(MiniPlayerRepository.getState(context!!)) {
                    MiniPlayerLayoutState.GENERAL ->
                        AppBroadcastHub.run { context.playOrStopService() }
                    MiniPlayerLayoutState.RADIO ->
                        AppBroadcastHub.run { context.playOrStopRadioService() }
                }
            NOTIFICATION_ACTION_NEXT ->
                when(MiniPlayerRepository.getState(context!!)) {
                    MiniPlayerLayoutState.GENERAL ->
                        AppBroadcastHub.run { context.skipNextService() }
                }

            NOTIFICATION_ACTION_PREVIUOS ->
                when(MiniPlayerRepository.getState(context!!)) {
                    MiniPlayerLayoutState.GENERAL ->
                        AppBroadcastHub.run { context.skipPrevService() }
                }
        }
    }
}