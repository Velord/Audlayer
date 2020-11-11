package velord.university.ui.fragment.miniPlayer

import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.mini_player_default.view.*
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.behaviour.MiniPlayerUIReceiver
import velord.university.application.broadcast.hub.*
import velord.university.databinding.MiniPlayerDefaultBinding
import velord.university.databinding.MiniPlayerRadioBinding
import velord.university.model.converter.SongTimeConverter
import velord.university.model.coroutine.getScope
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import velord.university.ui.fragment.miniPlayer.logic.general.*
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.view.gone
import velord.university.ui.util.view.setAutoScrollable
import velord.university.ui.util.view.visible

abstract class MiniPlayerDefaultFragment :
    LoggerSelfLifecycleFragment(),
    MiniPlayerUIReceiver {

    override val TAG: String = "MiniPlayerDefaultFragment"

    protected val viewModel: MiniPlayerViewModel by viewModels()

    private val receivers = this.miniPlayerUIReceiverList()

    private val scope = getScope()

    abstract var _bindingRadio: MiniPlayerRadioBinding?
    abstract var _bindingDefault: MiniPlayerDefaultBinding?

    protected val bindingRadio get() = _bindingRadio ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")
    protected val bindingDefault get() = _bindingDefault ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

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

    protected fun initMiniPlayerGeneralView() {
        //self
        bindingDefault.icon.setOnClickListener {
            AppBroadcastHub.run {
                requireContext().doAction(BroadcastActionType.CLICK_ON_ICON_PLAYER_UI)
            }
        }
        bindingDefault.songLiked.setOnClickListener {
            HeartLogic.press(requireActivity(), viewModel.getState())
        }
        bindingDefault.songRepeat.setOnClickListener {
            LoopLogic.press(requireActivity())
        }
        bindingDefault.songPlayOrPause.setOnClickListener {
            PlayPauseLogic.press(requireActivity(), viewModel.getState())
        }
        bindingDefault.songShuffle.setOnClickListener {
            ShuffleLogic.press(requireActivity(), viewModel.getState())
        }
        bindingDefault.songSkipNext.setOnClickListener {
            SkipNextLogic.press(requireActivity())
        }
        bindingDefault.songSkipPrev.setOnClickListener {
            SkipPrevLogic.press(requireActivity())
        }
        bindingDefault.songTimeSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    value: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) AppBroadcastHub.apply {
                        val allSeconds =
                            SongTimeConverter.textToSeconds(bindingDefault.songTimeEnd)
                        val seconds =
                            SongTimeConverter.percentToSeconds(value, allSeconds)
                        requireActivity().rewindService(seconds)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

        initSongName()
        initSongArtist()
    }

    private fun initSongName() {
        bindingDefault.songName.setAutoScrollable()
    }

    private fun initSongArtist() {
        bindingDefault.songArtist.setAutoScrollable()
    }

    protected fun startSongAndArtistNameScrolling() {
        bindingDefault.songArtist.isSelected = true
        bindingDefault.songName.isSelected = true
    }

    protected fun stopSongAndArtistNameScrolling()  {
        bindingDefault.songArtist.isSelected = false
        bindingDefault.songName.isSelected = false
    }

    protected fun showMiniPlayerDefault() {
        bindingDefault.miniPlayerGeneralContainer.visible()
        bindingRadio.miniPlayerRadioContainer.gone()
    }

    protected fun showMiniPlayerRadio() {
        bindingDefault.miniPlayerGeneralContainer.gone()
        bindingRadio.miniPlayerRadioContainer.visible()
    }

    override val songArtistF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.songArtistUI
            val songArtist = getStringExtra(extra)
            bindingDefault.songArtist.text = songArtist
        }
    }

    override val stopF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.DEFAULT) {
            stopButtonInvoke()
        }
    }

    override val playF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.DEFAULT) {
            playButtonInvoke()
        }
    }

    override val likeF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.DEFAULT) {
            likeButtonInvoke()
        }
    }

    override val unlikeF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.DEFAULT) {
            unlikeButtonInvoke()
        }
    }

    override val skipNextF: (Intent?) -> Unit = { }

    override val skipPrevF: (Intent?) -> Unit = { }

    override val rewindF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.rewindUI
            val second = intent.getIntExtra(extra, 0)

            val allSeconds =
                SongTimeConverter.textToSeconds(bindingDefault.songTimeEnd)
            val progress =
                SongTimeConverter.secondsToPercent(second, allSeconds)

            //change UI
            bindingDefault.songTimeSeekBar.progress = progress
            bindingDefault.songTimeStart.text =
                SongTimeConverter.secondsToTimeText(second)
        }
    }

    override val shuffleF: (Intent?) -> Unit = {
        bindingDefault.songShuffle
            .setImageResource(R.drawable.round_shuffle_teal_700_48dp)
        ShuffleLogic.value = true
    }

    override val unShuffleF: (Intent?) -> Unit = {
        bindingDefault.songShuffle.setImageResource(R.drawable.round_shuffle_grey_600_48dp)
        ShuffleLogic.value = false
    }

    override val loopF: (Intent?) -> Unit = {
        bindingDefault.songRepeat.setImageResource(R.drawable.one_teal_700)
    }

    override val loopAllF: (Intent?) -> Unit = {
        bindingDefault.songRepeat.setImageResource(R.drawable.round_loop_teal_700_48dp)
    }

    override val notLoopF: (Intent?) -> Unit = {
        bindingDefault.songRepeat.setImageResource(R.drawable.round_loop_grey_600_48dp)
    }

    override val songNameF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.songNameUI
            val value = getStringExtra(extra)
            bindingDefault.songName.text = value
        }
    }

    override val songHQF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.songHQUI
            val value = getBooleanExtra(extra, true)
            if (value) bindingDefault.songQuality.visible()
            else bindingDefault.songQuality.gone()
        }
    }

    override val songDurationF: (Intent?) -> Unit = { intent ->
        intent?.apply {
            val extra = BroadcastExtra.songDurationUI
            val seconds = getIntExtra(extra, 0)
            val inMinutes = SongTimeConverter.millisecondsToSeconds(seconds)
            bindingDefault.songTimeEnd.text =
                SongTimeConverter.secondsToTimeText(inMinutes)
        }
    }

    override val iconF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.iconUI
            val value = getStringExtra(extra)!!

            loadIcon(value)
        }
    }

    override val playerUnavailableUIF: (Intent?) -> Unit = {
        it?.apply {
            //wait and request info
            scope.launch {
                delay(4000)
                getInfoFromServiceWhenStart()
            }
        }
    }

    protected fun stopButtonInvoke(
        button: ImageButton = bindingDefault.songPlayOrPause) {
        button.setImageResource(R.drawable.play)
        PlayPauseLogic.value = false
        stopSongAndArtistNameScrolling()
    }

    protected fun playButtonInvoke(
        button: ImageButton = bindingDefault.songPlayOrPause) {
        button.setImageResource(R.drawable.pause)
        PlayPauseLogic.value = true
        startSongAndArtistNameScrolling()
    }

    protected fun likeButtonInvoke(
        button: ImageButton = bindingDefault.songLiked) {
        button.setImageResource(R.drawable.heart_pressed)
        HeartLogic.value = true
    }

    protected fun unlikeButtonInvoke(
        button: ImageButton = bindingDefault.songLiked) {
        button.setImageResource(R.drawable.heart_gray)
        HeartLogic.value = false
    }

    private fun loadIcon(value: String) {
        //check if url -> load via Glide else via drawable
        if (URLUtil.isHttpUrl(value) ||
            URLUtil.isHttpsUrl(value))
            Glide.with(requireContext())
                .load(value)
                .placeholder(R.drawable.repair_tools)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(bindingDefault.icon)
        else DrawableIcon.loadSongIconDrawable(
            requireContext(), bindingDefault.icon, value.toInt())
    }

    private fun getInfoFromServiceWhenStart() {
        val f: () -> Unit = {
            AppBroadcastHub.apply {
                showMiniPlayerDefault()
                requireContext().doAction(BroadcastActionType.GET_INFO_PLAYER_SERVICE)
            }
        }
        val state = MiniPlayerLayoutState.DEFAULT
        viewModel.mayDoAction(state, f)
    }
}

