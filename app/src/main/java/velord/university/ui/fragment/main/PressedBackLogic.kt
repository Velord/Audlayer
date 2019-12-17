package velord.university.ui.fragment.main

import android.content.Context
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import velord.university.R

object PressedBackLogic {

    private var backPressedCount = 0

    //if not center fragment set it
    //if center fragment -> make toast
    //if center fragment and toast was made less 5 seconds ago -> close app
    fun pressOccur(activity: Context, menuMemberFragment: ViewPager): Boolean {
        if (menuMemberFragment.currentItem != 2) {
            menuMemberFragment.currentItem = 2
            backPressedCount = 0
            return true
        }
        if (++backPressedCount == 1) {
            Toast.makeText(activity, R.string.backPressed, Toast.LENGTH_SHORT).show()
            GlobalScope.launch {
                delay(5000)
                --backPressedCount
            }
            return true
        }
        backPressedCount = 0
        return false
    }
}