package velord.university.ui.fragment.miniPlayer.miniPlayerHide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment
import velord.university.ui.fragment.main.MainFragment

class MiniPlayerHideFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String = "StopCloseMiniPlayerFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mini_player_hide_fragment, container, false)
    }
}