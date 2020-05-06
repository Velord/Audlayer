package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

abstract class TwoStateLogic {

    abstract var value: Boolean

    abstract val firstCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit

    abstract val secondCase: (context: FragmentActivity, MiniPlayerLayoutState) -> Unit

    fun press(context: FragmentActivity, state: MiniPlayerLayoutState) =
        if (value) {
            value = false
            firstCase(context, state)
        }
        else {
            value = true
            secondCase(context, state)
        }
}