package velord.university.ui.fragment.main.miniPlayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment
import velord.university.ui.fragment.main.MainFragment

class StopCloseMiniPlayerFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String
        get() = "StopCloseMiniPlayerFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.stop_close_mini_player_fragment, container, false)
    }
}