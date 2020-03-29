package velord.university.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import velord.university.R
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.main.initializer.MenuMiniPlayerInitializerFragment

class MainFragment : MenuMiniPlayerInitializerFragment(),
    BackPressedHandlerZero {

    override val TAG: String = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
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

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return PressedBackLogic
            .pressOccur(requireActivity(), menuMemberViewPager, fragmentHashMap)
    }
}
