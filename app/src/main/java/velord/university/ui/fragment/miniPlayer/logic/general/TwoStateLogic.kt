package velord.university.ui.fragment.miniPlayer.logic.general

import androidx.fragment.app.FragmentActivity

abstract class TwoStateLogic {

    abstract var value: Boolean

    abstract val firstCase: (context: FragmentActivity) -> Unit

    abstract val secondCase: (context: FragmentActivity) -> Unit

    fun press(context: FragmentActivity) =
        if (value) {
            value = false
            firstCase(context)
        }
        else {
            value = true
            secondCase(context)
        }
}