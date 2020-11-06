package velord.university.ui.fragment.main.initializer

import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.set
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import kotlinx.coroutines.*
import velord.university.R
import velord.university.databinding.MainFragmentBinding
import velord.university.ui.fragment.album.AlbumFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.radio.RadioFragment
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import velord.university.ui.fragment.song.AllSongFragment
import velord.university.ui.fragment.vk.VKFragment


abstract class BottomMenuFragment :
    LoggerSelfLifecycleFragment() {

    override val TAG: String = "BottomMenuFragment"

    private var buttonPressed: Int = 2

    private val fm by lazy {
        requireActivity().supportFragmentManager
    }

    private val scope = getScope()

    override fun onDetach() {
        super.onDetach()

        scope.cancel()
    }

    //view
    abstract val binding: MainFragmentBinding
    protected val fragmentHashMap = SparseArray<Fragment>()

    private fun initBottomMenu() {
        binding.bottomNavigation.apply {
            //for work custom selectors on icon
            itemIconTintList = null
            //onclick
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.action_folder -> {
                        openFolderFragment()
                    }
                    R.id.action_album -> {
                        openAlbumFragment()
                    }
                    R.id.action_all -> {
                        openSongFragment()
                    }
                    R.id.action_radio -> {
                        openRadioFragment()
                    }
                    R.id.action_vk -> {
                        openVKFragment()
                    }
                }

                true
            }
            //init
            selectedItemId = R.id.action_all
        }
    }

    protected fun initViewPagerAndBottomMenu() {
        initViewPager()
        initBottomMenu()
    }

    private fun initViewPager() {
        binding.menuMemberViewPager.adapter = MenuMemberPagerAdapter(fm)
        binding.menuMemberViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                when(position) {
                    0 -> {  Log.d(TAG, "onPageScrolled FolderFragment") }
                    1 -> {  Log.d(TAG, "onPageScrolled AlbumFragment") }
                    2 -> {  Log.d(TAG, "onPageScrolled SongFragment") }
                    3 -> {  Log.d(TAG, "onPageScrolled RadioFragment") }
                    4 -> {  Log.d(TAG, "onPageScrolled VKFragment") }
                }
            }

            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        Log.d(TAG, "onPageSelected FolderFragment")
                        changeUI(R.drawable.star_sky_night, position)
                    }
                    1 -> {
                        Log.d(TAG, "onPageSelected AlbumFragment")
                        changeUI(R.drawable.star_sky_horizon, position)
                    }
                    2 -> {
                        Log.d(TAG, "onPageSelected SongFragment")
                        changeUI(R.drawable.star_sky_mount, position)
                    }
                    3 -> {
                        Log.d(TAG, "onPageSelected RadioFragment")
                        changeUI(R.drawable.mountains_sea_ocean, position)
                    }
                    4 -> {
                        Log.d(TAG, "onPageSelected VKFragment")
                        changeUI(R.drawable.sea_lake_island, position)
                    }
                }
            }
        })
    }

    private fun openFolderFragment() {
        Log.d(TAG, "opening FolderFragment")
        binding.menuMemberViewPager.currentItem = 0
        //when user press on folder and current folder is not default pressed back should occur
//        val folderFragment = fragmentHashMap[0] as FolderFragment
//        if(folderFragment.focusOnMe())
//                folderFragment.onBackPressed()
    }

    private fun openAlbumFragment() {
        Log.d(TAG, "opening AlbumFragment")
        binding.menuMemberViewPager.currentItem = 1
    }

    private fun openSongFragment() {
        Log.d(TAG, "opening SongFragment")
        binding.menuMemberViewPager.currentItem = 2
    }

    private fun openRadioFragment() {
        Log.d(TAG, "opening RadioFragment")
        binding.menuMemberViewPager.currentItem = 3
    }

    private fun openVKFragment() {
        Log.d(TAG, "opening VKFragment")
        binding.menuMemberViewPager.currentItem = 4
    }

    private fun changeUI(background: Int, position: Int) {
        buttonPressed = position
        // main fragment background
        scope.launch {
            onMain {
                binding.mainFragmentContainer.setBackgroundResource(background)
            }
        }
        //main fragment bottom_menu button
//        changeButtonToStandardBackground(buttonPressed)
//        //main fragment bottom_menu button
//        changeButtonToNewBackground(position)
    }

    private inner class MenuMemberPagerAdapter(
        fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            when(position) {
                0 -> {  Log.d(TAG, "Destroy FolderFragment") }
                1 -> {  Log.d(TAG, "Destroy AlbumFragment") }
                2 -> {  Log.d(TAG, "Destroy SongFragment") }
                3 -> {  Log.d(TAG, "Destroy RadioFragment") }
                4 -> {  Log.d(TAG, "Destroy VKFragment") }
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            when(position) {
                0 -> {  Log.d(TAG, "Instantiate FolderFragment") }
                1 -> {  Log.d(TAG, "Instantiate AlbumFragment") }
                2 -> {  Log.d(TAG, "Instantiate SongFragment") }
                3 -> {  Log.d(TAG, "Instantiate RadioFragment") }
                4 -> {  Log.d(TAG, "Instantiate VKFragment") }
            }
            return super.instantiateItem(container, position)
        }

        override fun getItem(pos: Int): Fragment {
            if (fragmentHashMap[pos] != null) {
                return fragmentHashMap[pos]!!
            }
            val fragment = whichFragment(pos)
            fragmentHashMap[pos] = fragment
            return fragment
        }

        override fun getCount(): Int = 5
    }

    private fun whichFragment(position: Int): Fragment =
        when (position) {
            // Fragment # 0 - This will show FirstFragment
            0 -> {
                Log.d(TAG, "create FolderFragment")
                FolderFragment()
            }
            1 -> {
                Log.d(TAG, "create AlbumFragment")
                AlbumFragment()
            }
            2 -> {
                Log.d(TAG, "create SongFragment")
                AllSongFragment()
            }
            3 -> {
                Log.d(TAG, "create RadioFragment")
                RadioFragment()
            }
            4 ->  {
                Log.d(TAG, "create VKFragment")
                VKFragment()
            }
            else -> {
                Log.d(TAG, "create SongFragment")
                AllSongFragment()
            }
        }
}
