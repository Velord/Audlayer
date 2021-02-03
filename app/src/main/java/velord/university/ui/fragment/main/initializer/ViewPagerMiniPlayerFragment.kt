package velord.university.ui.fragment.main.initializer

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import velord.university.application.broadcast.behaviour.MiniPlayerShowAndHiderBroadcastReceiver
import velord.university.ui.util.view.gone
import velord.university.ui.util.view.visible
import velord.university.application.broadcast.hub.*
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.ui.fragment.miniPlayer.MiniPlayerRadioDefaultFragment
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import velord.university.ui.fragment.miniPlayer.miniPlayerHide.MiniPlayerHideFragment
import velord.university.ui.fragment.miniPlayer.miniPlayerStopAndHide.MiniPlayerStopAndHideFragment


abstract class ViewPagerMiniPlayerFragment :
    ViewPagerBottomMenuFragment(),
    //control viewpager visibility
    MiniPlayerShowAndHiderBroadcastReceiver {

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
    }

    private fun hideMiniPlayer() {
        AppBroadcastHub.apply {
            requireContext().doAction(BroadcastActionType.HIDE_PLAYER_UI)
        }
    }

    private fun stopAndHideMiniPLayer() {
        when(MiniPlayerRepository.getState(requireContext())) {
            MiniPlayerLayoutState.DEFAULT -> AppBroadcastHub.run {
                requireContext().doAction(BroadcastActionType.STOP_PLAYER_SERVICE)
            }
            MiniPlayerLayoutState.RADIO -> AppBroadcastHub.run {
                requireContext().doAction(BroadcastActionType.STOP_RADIO_SERVICE)
            }
        }
        AppBroadcastHub.apply {
            requireContext().doAction(BroadcastActionType.HIDE_PLAYER_UI)
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
                    MiniPlayerRadioDefaultFragment()
                }
                2 -> {
                    Log.d(TAG, "create MiniPlayerStopAndHideFragment")
                    MiniPlayerStopAndHideFragment()
                }
                else -> {
                    Log.d(TAG, "create MiniPlayerFragment")
                    MiniPlayerRadioDefaultFragment()
                }
            }

        override fun getItemCount(): Int = 3
    }

    override val showF: (Intent?) -> Unit = {
        showViewPager()
    }

    override val hideF: (Intent?) -> Unit = {
        binding.miniPlayerFrame.gone()
    }

    override val showRadioF: (Intent?) -> Unit = {
        showViewPager()
    }

    override val hideRadioF: (Intent?) -> Unit = {
        binding.miniPlayerFrame.gone()
    }

    private fun showViewPager() {
        binding.miniPlayerViewPager.currentItem = 1
        binding.miniPlayerFrame.visible()
    }
}