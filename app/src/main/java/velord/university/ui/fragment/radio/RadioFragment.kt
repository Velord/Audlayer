package velord.university.ui.fragment.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment

class RadioFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String
        get() = "RadioFragment"

    companion object {
        fun newInstance() = RadioFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(RadioViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.radio_fragment, container, false).apply {

        }
    }
}
