package velord.university.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.ui.fragment.main.construct.MenuMiniPlayerFragment


class MainFragment : MenuMiniPlayerFragment() {

    private var backPressedCount = 0

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

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        if (++backPressedCount == 1) {
            Toast.makeText(activity, R.string.backPressed, Toast.LENGTH_LONG).show()
            return true
        }
        else
           return false
    }
}
