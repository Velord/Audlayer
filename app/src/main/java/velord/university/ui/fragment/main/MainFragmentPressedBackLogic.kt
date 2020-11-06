package velord.university.ui.fragment.main

import android.content.Context
import android.util.SparseArray
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import velord.university.R
import velord.university.databinding.MainFragmentBinding
import velord.university.ui.fragment.folder.FolderFragment

object MainFragmentPressedBackLogic {

    private var backPressedCount = 0

    //if folder fragment is current -> on focus
    //if current fragment is not in center -> set it
    //if current fragment is center -> make toast
    //if current fragment is center and toast was made less 5 seconds ago -> close app
    fun pressOccur(activity: Context,
                   binding: MainFragmentBinding,
                   fragmentHashMap: SparseArray<Fragment>): Boolean {
        if (binding.menuMemberViewPager.currentItem == 0) {
            val folderFragment = fragmentHashMap[0] as FolderFragment
            if (folderFragment.focusOnMe()) {
                folderFragment.onBackPressed()
                backPressedCount = 0
                return true
            }
        }
        if (binding.menuMemberViewPager.currentItem != 2) {
            binding.bottomNavigation.selectedItemId = R.id.action_all
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