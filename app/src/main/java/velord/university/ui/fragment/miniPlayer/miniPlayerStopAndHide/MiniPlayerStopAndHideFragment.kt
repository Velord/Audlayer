package velord.university.ui.fragment.miniPlayer.miniPlayerStopAndHide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import velord.university.R
import velord.university.ui.fragment.main.MainFragment
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment

class MiniPlayerStopAndHideFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String = "StopCloseMiniPlayerFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mini_player_stop_and_hide_fragment, container, false)
    }
}