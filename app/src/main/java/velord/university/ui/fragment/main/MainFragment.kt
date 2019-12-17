package velord.university.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.*
import velord.university.R
import velord.university.model.miniPlayer.broadcast.*
import velord.university.ui.fragment.BackPressedHandler
import velord.university.ui.fragment.main.construct.MenuMiniPlayerFragment

class MainFragment : MenuMiniPlayerFragment(), BackPressedHandler,
    MiniPlayerBroadcastReceiver {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val receivers = arrayOf(
        Pair(stop(), filterStopUI),  Pair(play(), filterPlayUI),
        Pair(like(), filterLikeUI), Pair(unlike(), filterUnlikeUI),
        Pair(shuffle(), filterShuffleUI), Pair(unShuffle(), filterUnShuffleUI),
        Pair(skipNext(), filterSkipNextUI), Pair(skipPrev(), filterSkipPrevUI),
        Pair(rewind(), filterRewindUI), Pair(loop(), filterLoopUI),
        Pair(loopAll(), filterLoopAllUI))

    override val TAG: String
        get() = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false).apply {
            super.initMenuFragmentView(this)
            super.initMiniPlayerFragmentView(this)
        }
    }

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, it.second, PERM_PRIVATE_MINI_PLAYER)
        }


        activity?.apply {
            scope.launch {
                delay(6000)
                Log.d(TAG, "sending broadcast to service")
                sendBroadcastStop(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastLike(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastLoop(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastPlay(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastRewind(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastSkipPrev(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastSkipNext(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastUnShuffle(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastShuffle(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastUnlike(PERM_PRIVATE_MINI_PLAYER)
                sendBroadcastLoopAll(PERM_PRIVATE_MINI_PLAYER)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return PressedBackLogic
            .pressOccur(requireActivity(), menuMemberViewPager)
    }
}
