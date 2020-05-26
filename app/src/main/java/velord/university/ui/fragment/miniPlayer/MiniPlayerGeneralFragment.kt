package velord.university.ui.fragment.miniPlayer

import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import velord.university.R
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.MiniPlayerUIReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.model.converter.SongTimeConverter
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import velord.university.ui.fragment.miniPlayer.logic.general.*

open class MiniPlayerGeneralFragment :
    MiniPlayerInitializerFragment(),
    MiniPlayerUIReceiver {

    override val TAG: String = "MiniPlayerGeneralFragment"

    companion object {
        fun newInstance() = MiniPlayerGeneralFragment()
    }

    protected val viewModel by lazy {
        ViewModelProviders.of(this).get(MiniPlayerViewModel::class.java)
    }

    private val receivers = receiver()

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, IntentFilter(it.second), PERM_PRIVATE_MINI_PLAYER
                )
        }
        //get info from service about song cause service was created earlier then this view
        getInfoFromServiceWhenStart()
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    protected fun initMiniPlayerGeneralView(view: View) {
        //init mini player initializer fragment
        super.initMiniPlayerView(view)
        //self
        miniPlayerSongLikedIB.setOnClickListener {
            HeartLogic.press(requireActivity(), viewModel.getState())
        }
        miniPlayerSongRepeatIB.setOnClickListener {
            RepeatLogic.press(requireActivity())
        }
        miniPlayerPlayOrPauseIB.setOnClickListener {
            PlayPauseLogic.press(requireActivity(), viewModel.getState())
        }
        miniPlayerSongShuffleIB.setOnClickListener {
            ShuffleLogic.press(requireActivity(), viewModel.getState())
        }
        miniPlayerSongSkipNextIB.setOnClickListener {
            SkipNextLogic.press(requireActivity())
        }
        miniPlayerSongSkipPrevIB.setOnClickListener {
            SkipPrevLogic.press(requireActivity())
        }
        miniPlayerSongTimeSeekBar.setOnSeekBarChangeListener(
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

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
    }

    override val showF: (Intent?) -> Unit = {
        it?.apply {
            viewModel.setState(MiniPlayerLayoutState.GENERAL)
            showMiniPlayerGeneral()
        }
    }

    override val songArtistF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songArtistUI
            val songArtist = getStringExtra(extra)
            miniPlayerArtistTV.text = songArtist
        }
    }

    override val stopF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.GENERAL) {
            stopButtonInvoke()
        }
    }

    override val playF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.GENERAL) {
            playButtonInvoke()
        }
    }

    override val likeF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.GENERAL) {
            likeButtonInvoke()
        }
    }

    override val unlikeF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.GENERAL) {
            unlikeButtonInvoke()
        }
    }

    override val skipNextF: (Intent?) -> Unit = { }

    override val skipPrevF: (Intent?) -> Unit = { }

    override val rewindF: (Intent?) -> Unit = { intent ->
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

    override val shuffleF: (Intent?) -> Unit = {
        miniPlayerSongShuffleIB.setImageResource(R.drawable.shuffle)
        ShuffleLogic.value = true
    }

    override val unShuffleF: (Intent?) -> Unit = {
        miniPlayerSongShuffleIB.setImageResource(R.drawable.shuffle_gray)
        ShuffleLogic.value = false
    }

    override val loopF: (Intent?) -> Unit = {
        miniPlayerSongRepeatIB.setImageResource(R.drawable.repeat_one)
    }

    override val loopAllF: (Intent?) -> Unit = {
        miniPlayerSongRepeatIB.setImageResource(R.drawable.repeat_all)
    }

    override val notLoopF: (Intent?) -> Unit = {
        miniPlayerSongRepeatIB.setImageResource(R.drawable.repeat_gray)
    }

    override val songNameF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songNameUI
            val value = getStringExtra(extra)
            miniPlayerNameTV.text = value
        }
    }

    override val songHQF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songHQUI
            val value = getBooleanExtra(extra, true)
            if (value) miniPlayerSongQualityTV.visibility = View.VISIBLE
            else miniPlayerSongQualityTV.visibility = View.GONE
        }
    }

    override val songDurationF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = AppBroadcastHub.Extra.songDurationUI
            val seconds = getIntExtra(extra, 0)
            val inMinutes = SongTimeConverter.millisecondsToSeconds(seconds)
            miniPlayerSongTimeEndTV.text =
                SongTimeConverter.secondsToTimeText(inMinutes)
        }
    }

    override val iconF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.iconUI
            val value = getStringExtra(extra)
            loadSongIcon(miniPlayerIV, value)
        }
    }

    protected fun stopButtonInvoke(button: ImageButton = miniPlayerPlayOrPauseIB) {
        button.setImageResource(R.drawable.play)
        PlayPauseLogic.value = false
        stopSongAndArtistNameScrolling()
    }

    protected fun playButtonInvoke(button: ImageButton = miniPlayerPlayOrPauseIB) {
        button.setImageResource(R.drawable.pause)
        PlayPauseLogic.value = true
        startSongAndArtistNameScrolling()
    }

    protected fun likeButtonInvoke(button: ImageButton = miniPlayerSongLikedIB) {
        button.setImageResource(R.drawable.heart_pressed)
        HeartLogic.value = true
    }

    protected fun unlikeButtonInvoke(button: ImageButton = miniPlayerSongLikedIB) {
        button.setImageResource(R.drawable.heart_gray)
        HeartLogic.value = false
    }

    private fun loadSongIcon(view: ImageView, icon: String) {
        Glide.with(requireActivity())
            .load(icon)
            .placeholder(R.drawable.song_item)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(view)
    }

    private fun getInfoFromServiceWhenStart() {
        val f: () -> Unit = {
            AppBroadcastHub.apply {
                showMiniPlayerGeneral()
                requireContext().getInfoService()
            }
        }
        val state = MiniPlayerLayoutState.GENERAL
        viewModel.mayDoAction(state, f)
    }
}

