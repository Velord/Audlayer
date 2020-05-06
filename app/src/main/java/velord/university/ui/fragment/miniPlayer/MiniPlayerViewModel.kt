package velord.university.ui.fragment.miniPlayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import velord.university.application.settings.miniPlayer.MiniPlayerUIPreference
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

class MiniPlayerViewModel(private val app: Application) : AndroidViewModel(app) {

    fun getState(): MiniPlayerLayoutState =
        when(MiniPlayerUIPreference.getState(app)) {
            0 -> MiniPlayerLayoutState.GENERAL
            1 -> MiniPlayerLayoutState.RADIO
            else -> MiniPlayerLayoutState.GENERAL
        }

    fun setState(state: MiniPlayerLayoutState) =
        when(state) {
            MiniPlayerLayoutState.GENERAL ->
                MiniPlayerUIPreference.setState(app, 0)
            MiniPlayerLayoutState.RADIO ->
                MiniPlayerUIPreference.setState(app, 1)
        }

    fun mayDoAction(state: MiniPlayerLayoutState) =
        state == getState()
}
