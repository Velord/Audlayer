package velord.university.repository.hub

import android.content.Context
import velord.university.application.settings.miniPlayer.MiniPlayerUIPreference
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

object MiniPlayerRepository {

    fun getState(context: Context): MiniPlayerLayoutState =
        when(MiniPlayerUIPreference(context).state) {
            0 -> MiniPlayerLayoutState.GENERAL
            1 -> MiniPlayerLayoutState.RADIO
            else -> MiniPlayerLayoutState.GENERAL
        }

    fun setState(context: Context,
                 state: MiniPlayerLayoutState) =
        when(state) {
            MiniPlayerLayoutState.GENERAL ->
                MiniPlayerUIPreference(context).state = 0
            MiniPlayerLayoutState.RADIO ->
                MiniPlayerUIPreference(context).state = 1
        }

    fun mayDoAction(context: Context,
                    state: MiniPlayerLayoutState,
                    f: () ->  Unit) {
        if (state == getState(context)) f()
    }
}