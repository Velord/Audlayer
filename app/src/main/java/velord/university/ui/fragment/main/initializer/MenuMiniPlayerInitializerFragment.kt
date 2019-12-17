package velord.university.ui.fragment.main.initializer

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import velord.university.R
import velord.university.ui.fragment.miniPlayer.MiniPlayerFragment
import velord.university.ui.fragment.miniPlayer.StopCloseMiniPlayerFragment

abstract class MenuMiniPlayerInitializerFragment : MenuInitializerFragment() {

    override val TAG: String
        get() = "MenuNowPlayingFragment"

    private lateinit var viewFrame: View
    lateinit var miniPlayerViewPager: ViewPager

    private val fm by lazy {
        activity!!.supportFragmentManager
    }

    protected fun initMiniPlayerFragmentView(view: View) {
        initViewPager(view)
    }

    private fun initViewPager(view: View) {
        viewFrame = view.findViewById(R.id.mini_player_frame)
        //init viewPager
        miniPlayerViewPager = view.findViewById(R.id.mini_player_viewPager)
        miniPlayerViewPager.adapter = MiniPlayerPagerAdapter(fm)
        miniPlayerViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                when(position) {
                    0 -> {  Log.d(TAG, "onPageScrolled StopAndCloseFragment1") }
                    1 -> {  Log.d(TAG, "onPageScrolled MiniPlayerFragment") }
                    2 -> {  Log.d(TAG, "onPageScrolled StopAndCloseFragment2") }
                }
            }

            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        Log.d(TAG, "onPageSelected StopAndCloseFragment1")
                        changeUI()
                    }
                    1 -> {
                        Log.d(TAG, "onPageSelected MiniPlayerFragment")
                    }
                    2 -> {
                        Log.d(TAG, "onPageSelected StopAndCloseFragment2")
                        changeUI()
                    }
                }
            }
        })
        miniPlayerViewPager.currentItem = 1
    }

    private fun changeUI() {
        viewFrame.visibility = View.GONE
    }

    private inner class MiniPlayerPagerAdapter(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm) {

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            when(position) {
                0 -> {  Log.d(TAG, "Destroy StopAndCloseFragment1") }
                1 -> {  Log.d(TAG, "Destroy MiniPlayerFragment") }
                2 -> {  Log.d(TAG, "Destroy StopAndCloseFragment2") }
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            when(position) {
                0 -> {  Log.d(TAG, "Instantiate StopAndCloseFragment1") }
                1 -> {  Log.d(TAG, "Instantiate MiniPlayerFragment") }
                2 -> {  Log.d(TAG, "Instantiate StopAndCloseFragment2") }
            }
            return super.instantiateItem(container, position)
        }

        override fun getItem(pos: Int): Fragment =
            when (pos) {
                // Fragment # 0 - This will show FirstFragment
                0 -> {
                    Log.d(TAG, "create StopAndCloseFragment1")
                    StopCloseMiniPlayerFragment()
                }
                1 -> {
                    Log.d(TAG, "create MiniPlayerFragment")
                    MiniPlayerFragment()
                }
                2 -> {
                    Log.d(TAG, "create StopAndCloseFragment2")
                    StopCloseMiniPlayerFragment()
                }
                else -> {
                    Log.d(TAG, "create MiniPlayerFragment")
                    MiniPlayerFragment()
                }
            }

        override fun getCount(): Int = 3
    }
}