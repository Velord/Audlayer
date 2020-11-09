package velord.university.ui.fragment.main.initializer

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import velord.university.ui.util.view.gone
import velord.university.ui.util.view.visible
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.MiniPlayerBroadcastReceiverShowAndHider
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.ui.fragment.miniPlayer.MiniPlayerRadioGeneralFragment
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import velord.university.ui.fragment.miniPlayer.miniPlayerHide.MiniPlayerHideFragment
import velord.university.ui.fragment.miniPlayer.miniPlayerStopAndHide.MiniPlayerStopAndHideFragment


abstract class MiniPlayerFragment :
    BottomMenuFragment(),
    MiniPlayerBroadcastReceiverShowAndHider {

    override val TAG: String = "MenuNowPlayingFragment"

    private val receivers = this.miniPlayerShowAndHiderReceiverList()

    private val fm by lazy {
        requireActivity().supportFragmentManager
    }

    override fun onStart() {
        super.onStart()
        receivers.forEach {
            requireActivity().registerBroadcastReceiver(
                it.first,
                IntentFilter(it.second),
                PERM_PRIVATE_MINI_PLAYER
            )
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    protected fun initMiniPlayer() {
        //init viewPager
        binding.miniPlayerViewPager.adapter = MiniPlayerPagerAdapter(requireActivity())
        binding.miniPlayerViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

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
        binding.miniPlayerViewPager.currentItem = 1
    }

    private fun hideMiniPlayer() {
        AppBroadcastHub.apply {
            requireContext().hideUI()
        }
    }

    private fun stopAndHideMiniPLayer() {
        when(MiniPlayerRepository.getState(requireContext())) {
            MiniPlayerLayoutState.GENERAL -> AppBroadcastHub.apply {
                requireContext().stopService()
            }
            MiniPlayerLayoutState.RADIO -> AppBroadcastHub.apply {
                requireContext().stopRadioService()
            }
        }
        AppBroadcastHub.apply {
            requireContext().hideUI()
        }
    }

    private inner class MiniPlayerPagerAdapter(
        fa: FragmentActivity
    ) : FragmentStateAdapter(fa) {

        override fun createFragment(pos: Int): Fragment =
            when (pos) {
                // Fragment # 0 - This will show FirstFragment
                0 -> {
                    Log.d(TAG, "create MiniPlayerHideFragment")
                    MiniPlayerHideFragment()
                }
                1 -> {
                    Log.d(TAG, "create MiniPlayerFragment")
                    MiniPlayerRadioGeneralFragment()
                }
                2 -> {
                    Log.d(TAG, "create MiniPlayerStopAndHideFragment")
                    MiniPlayerStopAndHideFragment()
                }
                else -> {
                    Log.d(TAG, "create MiniPlayerFragment")
                    MiniPlayerRadioGeneralFragment()
                }
            }

        override fun getItemCount(): Int = 3
    }

    override val showF: (Intent?) -> Unit = {
        binding.miniPlayerViewPager.currentItem = 1
        binding.miniPlayerFrame.visible()
    }

    override val hideF: (Intent?) -> Unit = {
        binding.miniPlayerFrame.gone()
    }
}