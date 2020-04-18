package velord.university.ui.fragment.radio

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment

class RadioFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String = "RadioFragment"

    companion object {
        fun newInstance() = RadioFragment()
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(RadioViewModel::class.java)
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var radioTest: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.radio_fragment, container, false).apply {
            initView(this)
        }
    }

    private fun initView(view: View) {
        radioTest = view.findViewById(R.id.radio_start)
        radioTest.setOnClickListener {
            playRadio()
        }
    }

    private fun playRadio() {
        scope.launch {
            val url = "http://air.radiorecord.ru:805/rr_320"
            val mediaPlayer = MediaPlayer.create(
                requireContext(),
                Uri.parse(url)
            )
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.start()
        }
    }
}
