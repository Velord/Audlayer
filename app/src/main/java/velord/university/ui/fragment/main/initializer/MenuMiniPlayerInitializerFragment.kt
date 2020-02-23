package velord.university.ui.fragment.main.initializer

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import velord.university.R
import velord.university.model.miniPlayer.broadcast.*
import velord.university.ui.fragment.miniPlayer.MiniPlayerFragment
import velord.university.ui.fragment.miniPlayer.miniPlayerHide.MiniPlayerHideFragment
import velord.university.ui.fragment.miniPlayer.miniPlayerStopAndHide.MiniPlayerStopAndHideFragment

abstract class MenuMiniPlayerInitializerFragment : MenuInitializerFragment(), MiniPlayerBroadcastReceiverShowAndHider {

    override val TAG: String
        get() = "MenuNowPlayingFragment"

    private lateinit var viewFrame: View
    lateinit var miniPlayerViewPager: ViewPager

    private val receivers = arrayOf(
        Pair(show(), MiniPlayerBroadcastShow.filterUI),
        Pair(hide(), MiniPlayerBroadcastHide.filterUI))

    private val fm by lazy {
        activity!!.supportFragmentManager
    }

    override fun onStart() {
        super.onStart()
        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, it.second, PERM_PRIVATE_MINI_PLAYER)
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
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
                        hideMiniPlayer()
                    }
                    1 -> {
                        Log.d(TAG, "onPageSelected MiniPlayerFragment")
                    }
                    2 -> {
                        Log.d(TAG, "onPageSelected StopAndCloseFragment2")
                        stopAndHideMiniPLayer()
                    }
                }
            }
        })
        miniPlayerViewPager.currentItem = 1
    }

    private fun hideMiniPlayer() {
        MiniPlayerBroadcastHide.apply {
            requireContext().sendBroadcastHide()
        }
    }

    private fun stopAndHideMiniPLayer() {
        MiniPlayerBroadcastStop.apply {
            requireContext().sendBroadcastStop()
        }
        hide()
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
                    Log.d(TAG, "create MiniPlayerHideFragment")
                    MiniPlayerHideFragment()
                }
                1 -> {
                    Log.d(TAG, "create MiniPlayerFragment")
                    MiniPlayerFragment()
                }
                2 -> {
                    Log.d(TAG, "create MiniPlayerStopAndHideFragment")
                    MiniPlayerStopAndHideFragment()
                }
                else -> {
                    Log.d(TAG, "create MiniPlayerFragment")
                    MiniPlayerFragment()
                }
            }

        override fun getCount(): Int = 3
    }

    override val showF: (Intent?) -> Unit
        get() = {
            miniPlayerViewPager.currentItem = 1
            viewFrame.visibility = View.VISIBLE
        }

    override val hideF: (Intent?) -> Unit
        get() = {
            viewFrame.visibility = View.GONE
        }
}