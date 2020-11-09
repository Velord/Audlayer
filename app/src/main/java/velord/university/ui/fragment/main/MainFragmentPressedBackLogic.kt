package velord.university.ui.fragment.main

import android.content.Context
import android.util.SparseArray
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import velord.university.R
import velord.university.databinding.MainFragmentBinding
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.util.findBy

object MainFragmentPressedBackLogic {

    private var backPressedCount = 0

    //if folder fragment is current -> onBackPress
    //if current fragment is not in center -> set it
    //if current fragment is center -> make toast
    //if current fragment is center and toast was made less 5 seconds ago -> close app
    fun pressOccur(activity: Context,
                   binding: MainFragmentBinding,
                   fm: FragmentManager): Boolean {
        if (binding.menuMemberViewPager.currentItem == 0) {
            val folderFragment = fm.findBy<FolderFragment>()
            //if folder fragment still have is not empty stack directories
            if (folderFragment.onBackPressed()) {
                backPressedCount = 0
                //change to central fragment
                binding.bottomNavigation.selectedItemId = R.id.action_all
            }
            return true
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