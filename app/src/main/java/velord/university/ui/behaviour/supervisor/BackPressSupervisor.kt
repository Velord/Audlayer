package velord.university.ui.behaviour.supervisor

import android.util.Log
import androidx.fragment.app.FragmentManager
import velord.university.ui.behaviour.backPressed.*

class BackPressSupervisor(
    private val TAG: String,
    private val fm: FragmentManager
) {

    fun backPressToZeroLevel() {
        backPressedFourthLevel()
        backPressedThirdLevel()
        backPressedSecondLevel()
        backPressedFirstLevel()
    }

    fun backPressToFirstLevel() {
        backPressedFourthLevel()
        backPressedThirdLevel()
        backPressedSecondLevel()
    }

    fun backPressedZeroLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerZero> { frag, response -> true }

    fun backPressedFirstLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerFirst> { frag, response ->
            if (response) {
                frag.popBackStackImmediate()
                true
            } else true
        }

    fun backPressedSecondLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerSecond> { frag, response ->
            if (response) {
                frag.popBackStackImmediate()
                true
            } else true
        }

    fun backPressedThirdLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerThird> { frag, response ->
            if (response) {
                frag.popBackStackImmediate()
                true
            } else true
        }

    fun backPressedFourthLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerFourth> { frag, response ->
            //if response true fragment answer he don't care about press back
            //if false -> care just return true
            if (response) {
                frag.popBackStackImmediate()
                true
            } else true
        }

    private inline fun <reified T : BackPressedHandler>
            checkFragmentBackStack(f: (FragmentManager, Boolean) -> Boolean
    ): Boolean {
        var handled = false
        fm.apply {
            fragments.forEach {
                if (it is T) {
                    try {
                        handled = it.onBackPressed()
                        Log.d(TAG, "$handled, $it")
                        return f(this, handled)
                    } catch (e: Exception) {
                        Log.d(TAG, e.message.toString())
                        //this@MainActivity.toastError(e.message.toString())
                    }
                }
            }
        }
        return handled
    }

    fun clearAllFragments() {
        backPressToZeroLevel()
        if (fm.backStackEntryCount > 0)
            fm.popBackStackImmediate()
    }

}