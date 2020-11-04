package velord.university.ui.fragment.miniPlayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
class MiniPlayerViewModel(private val app: Application) : AndroidViewModel(app) {

    fun getState(): MiniPlayerLayoutState =
       MiniPlayerRepository.getState(app)

    fun setState(state: MiniPlayerLayoutState) =
        MiniPlayerRepository.setState(app, state)

    fun mayDoAction(state: MiniPlayerLayoutState,
                    f: () ->  Unit) =
        MiniPlayerRepository.mayDoAction(app, state, f)
}

