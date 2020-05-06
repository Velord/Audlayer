package velord.university.ui.fragment.miniPlayer

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.MiniPlayerUIReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.model.converter.SongTimeConverter
import velord.university.ui.fragment.miniPlayer.logic.general.*

open class MiniPlayerGeneralFragment :
    MiniPlayerInitializerFragment(),
    MiniPlayerUIReceiver {

    override val TAG: String = "MiniPlayerGeneralFragment"

    companion object {
        fun newInstance() = MiniPlayerGeneralFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MiniPlayerViewModel::class.java)
    }

    private val receivers = receiver()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mini_player_fragment, container, false).apply {
            super.initMiniPlayerView(this)
            initView()
        }
    }

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, IntentFilter(it.second), PERM_PRIVATE_MINI_PLAYER)
        }
        //get info from service about song cause service was created earlier then this view
        AppBroadcastHub.apply {
            requireContext().getInfoService()
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    private fun initView() {
        miniPlayerSongLikedIB.setOnClickListener {
            HeartLogic.press(requireActivity())
        }
        miniPlayerSongRepeatIB.setOnClickListener {
            RepeatLogic.press(requireActivity())
        }
        miniPlayerSongPlayOrPauseIB.setOnClickListener {
            PlayPauseLogic.press(requireActivity())
        }
        miniPlayerSongShuffleIB.setOnClickListener {
            ShuffleLogic.press(requireActivity())
        }
        miniPlayerSongSkipNextIB.setOnClickListener {
            SkipNextLogic.press(requireActivity())
        }
        miniPlayerSongSkipPrevIB.setOnClickListener {
            SkipPrevLogic.press(requireActivity())
        }
        miniPlayerSongTimeSeekBar.setOnSeekBarChangeListener (
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    value: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser)
                        AppBroadcastHub.apply {
                            val allSeconds =
                                SongTimeConverter.textToSeconds(miniPlayerSongTimeEndTV)
                            val seconds =
                                SongTimeConverter.percentToSeconds(value, allSeconds)
                            requireActivity().rewindService(seconds)
                        }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) { }

                override fun onStopTrackingTouch(seekBar: SeekBar) { }
            })
    }

    override val songArtistF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = AppBroadcastHub.Extra.songArtistUI
                val songArtist = getStringExtra(extra)
                miniPlayerSongArtistTV.text = songArtist
            }
        }

    override val stopF: (Intent?) -> Unit
        get() = {
            miniPlayerSongPlayOrPauseIB.setImageResource(R.drawable.play)
            PlayPauseLogic.value = false
            stopSongAndArtistNameScrolling()
        }

    override val playF: (Intent?) -> Unit
        get() = {
            miniPlayerSongPlayOrPauseIB.setImageResource(R.drawable.pause)
            PlayPauseLogic.value = true
            startSongAndArtistNameScrolling()
        }

    override val likeF: (Intent?) -> Unit
        get() = {
            miniPlayerSongLikedIB.setImageResource(R.drawable.heart_pressed)
            HeartLogic.value = true
        }

    override val unlikeF: (Intent?) -> Unit
        get() = {
            miniPlayerSongLikedIB.setImageResource(R.drawable.heart_gray)
            HeartLogic.value = false
        }

    override val skipNextF: (Intent?) -> Unit
        get() = { }

    override val skipPrevF: (Intent?) -> Unit
        get() = { }

    override val rewindF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = AppBroadcastHub.Extra.rewindUI
                val second = intent.getIntExtra(extra, 0)

                val allSeconds =
                    SongTimeConverter.textToSeconds(miniPlayerSongTimeEndTV)
                val progress =
                    SongTimeConverter.secondsToPercent(second, allSeconds)

                //change UI
                miniPlayerSongTimeSeekBar.progress = progress
                miniPlayerSongTimeStartTV.text =
                    SongTimeConverter.secondsToTimeText(second)
            }
        }

    override val shuffleF: (Intent?) -> Unit
        get() = {
            miniPlayerSongShuffleIB.setImageResource(R.drawable.shuffle)
            ShuffleLogic.value = true
        }

    override val unShuffleF: (Intent?) -> Unit
        get() = {
            miniPlayerSongShuffleIB.setImageResource(R.drawable.shuffle_gray)
            ShuffleLogic.value = false
        }

    override val loopF: (Intent?) -> Unit
        get() = {
            miniPlayerSongRepeatIB.setImageResource(R.drawable.repeat_one)
        }

    override val loopAllF: (Intent?) -> Unit
        get() = {
            miniPlayerSongRepeatIB.setImageResource(R.drawable.repeat_all)
        }

    override val notLoopF: (Intent?) -> Unit
        get() = {
            miniPlayerSongRepeatIB.setImageResource(R.drawable.repeat_gray)
        }


    override val songNameF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = AppBroadcastHub.Extra.songNameUI
                val value = getStringExtra(extra)
                miniPlayerSongNameTV.text = value
            }
        }

    override val songHQF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = AppBroadcastHub.Extra.songHQUI
                val value = getBooleanExtra(extra, true)
                if (value) miniPlayerSongQualityTV.visibility = View.VISIBLE
                else miniPlayerSongQualityTV.visibility = View.GONE
            }
        }

    override val songDurationF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = AppBroadcastHub.Extra.songDurationUI
                val seconds = getIntExtra(extra, 0)
                val inMinutes = SongTimeConverter.millisecondsToSeconds(seconds)
                miniPlayerSongTimeEndTV.text =
                    SongTimeConverter.secondsToTimeText(inMinutes)
            }
        }

    private fun startSongAndArtistNameScrolling() {
        miniPlayerSongArtistTV.isSelected = true
        miniPlayerSongNameTV.isSelected = true
    }

    private fun stopSongAndArtistNameScrolling()  {
        miniPlayerSongArtistTV.isSelected = false
        miniPlayerSongNameTV.isSelected = false
    }
}

