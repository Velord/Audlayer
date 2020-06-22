package velord.university.ui.fragment.miniPlayer

import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.MiniPlayerUIReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.model.converter.SongTimeConverter
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import velord.university.ui.fragment.miniPlayer.logic.general.*
import velord.university.ui.util.DrawableIcon

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

    private val scope =
        CoroutineScope(Job() + Dispatchers.Default)

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
            LoopLogic.press(requireActivity())
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
        miniPlayerSongShuffleIB.setImageResource(R.drawable.round_shuffle_teal_700_48dp)
        ShuffleLogic.value = true
    }

    override val unShuffleF: (Intent?) -> Unit = {
        miniPlayerSongShuffleIB.setImageResource(R.drawable.round_shuffle_grey_600_48dp)
        ShuffleLogic.value = false
    }

    override val loopF: (Intent?) -> Unit = {
        miniPlayerSongRepeatIB.setImageResource(R.drawable.one_teal_700)
    }

    override val loopAllF: (Intent?) -> Unit = {
        miniPlayerSongRepeatIB.setImageResource(R.drawable.round_loop_teal_700_48dp)
    }

    override val notLoopF: (Intent?) -> Unit = {
        miniPlayerSongRepeatIB.setImageResource(R.drawable.round_loop_grey_600_48dp)
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

            loadIcon(value)
        }
    }

    override val playerUnavailableUIF: (Intent?) -> Unit = {
        it?.apply {
            //wait and request info
            scope.launch {
                delay(500)
                getInfoFromServiceWhenStart()
            }
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

    private fun loadIcon(value: String) {
        //check if url -> load via Glide else via drawable
        if (URLUtil.isHttpUrl(value) ||
            URLUtil.isHttpsUrl(value))
            Glide.with(requireContext())
                .load(value)
                .placeholder(R.drawable.repair_tools)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(miniPlayerIV)
        else DrawableIcon.loadSongIconByName(
            requireContext(), miniPlayerIV, value)
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

