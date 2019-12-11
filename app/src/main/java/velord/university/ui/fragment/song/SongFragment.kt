package velord.university.ui.fragment.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment

class SongFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String
        get() = "SongFragment"

    companion object {
        fun newInstance() = SongFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(SongViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.song_fragment, container, false).apply {

        }
    }
}
