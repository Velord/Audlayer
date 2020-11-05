package velord.university.ui.fragment.main

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.statuscasellc.statuscase.ui.util.activity.hideVirtualButtons
import com.statuscasellc.statuscase.ui.util.view.gone
import com.statuscasellc.statuscase.ui.util.view.visible
import kotlinx.coroutines.*
import velord.university.R
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.main.initializer.MenuMiniPlayerInitializerFragment


class MainFragment :
    MenuMiniPlayerInitializerFragment(),
    BackPressedHandlerZero {

    override val TAG: String = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    private lateinit var changeVolumeSB: SeekBar

    private lateinit var volumeContentObserver: VolumeContentObserver

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return PressedBackLogic.pressOccur(
            requireActivity(),
            menuMemberViewPager, fragmentHashMap
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container,
            false).apply {
            super.initMenuFragmentView(this)
            super.initMiniPlayerFragmentView(this)
            initView(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        volumeContentObserver =
            VolumeContentObserver(requireContext(), Handler())

        requireContext().contentResolver
            .registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                volumeContentObserver
            )
    }

    override fun onDestroy() {
        super.onDestroy()

        requireContext().contentResolver
            .unregisterContentObserver(
                volumeContentObserver
            )
    }

    private fun initView(view: View) {
        changeVolumeSB = view.findViewById(R.id.change_volume_seekBar)
    }

    inner class VolumeContentObserver(private val c: Context,
                                      handler: Handler?) : ContentObserver(handler) {
        private var previousVolume: Int

        private var scopeVolume = CoroutineScope(Job() + Dispatchers.Default)

        init {
            val audio = c.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
        //based on 15th scale
        //min is zero(0), max is fifteenth(15)
        //TODO() must check headset plug in or off
        //cause when headset on must return 10 scale
        private fun getVolumeForSeekBar(volume: Int,
                                        headsetOn: Boolean): Int =
            when(headsetOn) {
                true -> tenthScale(volume)
                false -> fifteenthScale(volume)
            }

        private fun tenthScale(volume: Int): Int =
            when(volume) {
                0 -> 10
                1 -> 20
                2 -> 30
                3 -> 40
                4 -> 50
                5 -> 60
                6 -> 70
                7 -> 80
                8 -> 90
                9 -> 100
                else -> 100
            }

        private fun fifteenthScale(volume: Int): Int =
            when(volume) {
                0 -> 0
                1 -> 7
                2 -> 13
                3 -> 20
                4 -> 27
                5 -> 33
                6 -> 40
                7 -> 47
                8 -> 53
                9 -> 60
                10 -> 67
                11 -> 73
                12 -> 80
                14 -> 87
                13 -> 93
                15 -> 100
                else -> 100
            }

        private fun changeVolume(volume: Int,
                                 headsetOn: Boolean) {
            //reassignment and cancel scope
            scopeVolume.cancel()
            scopeVolume =
                CoroutineScope(Job() + Dispatchers.Default)
            //show seekbar
            changeVolumeSB.visible()
            //change volume
            changeVolumeSB.progress =
                getVolumeForSeekBar(volume, headsetOn)
            //after change hide seekbar
            scopeVolume.launch {
                repeat(3) {
                    delay(400)
                }
                withContext(Dispatchers.Main) {
                    changeVolumeSB.gone()
                }
            }
        }

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            val audio = c.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            val delta = previousVolume - currentVolume
            if (delta > 0) {
                Log.d(TAG, "Decreased to: $currentVolume")
                previousVolume = currentVolume
            }
            else if (delta < 0) {
                Log.d(TAG, "Increased to: $currentVolume")
            }

            val headsetPlug = audio.isWiredHeadsetOn
            changeVolume(currentVolume, headsetPlug)
        }
    }
}
