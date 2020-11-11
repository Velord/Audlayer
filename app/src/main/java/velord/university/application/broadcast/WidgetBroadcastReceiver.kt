package velord.university.application.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.notification.MiniPlayerNotification.NOTIFICATION_ACTION_NEXT
import velord.university.application.notification.MiniPlayerNotification.NOTIFICATION_ACTION_PLAY_OR_STOP
import velord.university.application.notification.MiniPlayerNotification.NOTIFICATION_ACTION_PREVIUOS
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

class WidgetBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            NOTIFICATION_ACTION_PLAY_OR_STOP ->
                when(MiniPlayerRepository.getState(context!!)) {
                    MiniPlayerLayoutState.DEFAULT -> AppBroadcastHub.run {
                        context.doAction(BroadcastActionType.PLAY_OR_STOP_PLAYER_SERVICE)
                    }
                    MiniPlayerLayoutState.RADIO -> AppBroadcastHub.run {
                        context.doAction(BroadcastActionType.PLAY_OR_STOP_RADIO_SERVICE)
                    }
                }
            NOTIFICATION_ACTION_NEXT ->
                when(MiniPlayerRepository.getState(context!!)) {
                    MiniPlayerLayoutState.DEFAULT ->
                        AppBroadcastHub.run {
                            context.doAction(BroadcastActionType.SKIP_PLAYER_SERVICE)
                        }
                }

            NOTIFICATION_ACTION_PREVIUOS ->
                when(MiniPlayerRepository.getState(context!!)) {
                    MiniPlayerLayoutState.DEFAULT ->
                        AppBroadcastHub.run {
                            context.doAction(BroadcastActionType.SKIP_PREV_PLAYER_SERVICE)
                        }
                }
        }
    }
}