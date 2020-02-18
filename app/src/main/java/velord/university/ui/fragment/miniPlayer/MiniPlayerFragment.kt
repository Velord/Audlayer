package velord.university.ui.fragment.miniPlayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.mini_player_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import velord.university.R
import velord.university.model.miniPlayer.broadcast.*
import velord.university.ui.fragment.miniPlayer.logic.*

class MiniPlayerFragment : MiniPlayerInitializerFragment(), MiniPlayerBroadcastReceiver {

    override val TAG: String
        get() = "MiniPlayerFragment"

    companion object {
        fun newInstance() =
            MiniPlayerFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MiniPlayerViewModel::class.java)
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val receivers = arrayOf(
        Pair(stop(), MiniPlayerBroadcastStop.filterUI),
        Pair(play(), MiniPlayerBroadcastPlay.filterUI),
        Pair(like(), MiniPlayerBroadcastLike.filterUI),
        Pair(unlike(), MiniPlayerBroadcastUnlike.filterUI),
        Pair(shuffle(), MiniPlayerBroadcastShuffle.filterUI),
        Pair(unShuffle(), MiniPlayerBroadcastUnShuffle.filterUI),
        Pair(skipNext(), MiniPlayerBroadcastSkipNext.filterUI),
        Pair(skipPrev(), MiniPlayerBroadcastSkipPrev.filterUI),
        Pair(rewind(), MiniPlayerBroadcastRewind.filterUI),
        Pair(loop(), MiniPlayerBroadcastLoop.filterUI),
        Pair(loopAll(), MiniPlayerBroadcastLoopAll.filterUI),
        Pair(notLoop(), MiniPlayerBroadcastNotLoop.filterUI),
        Pair(songName(), MiniPlayerBroadcastSongName.filterUI),
        Pair(songDuration(), MiniPlayerBroadcastSongDuration.filterUI),
        Pair(songArtist(), MiniPlayerBroadcastSongArtist.filterUI),
        Pair(songHQ(), MiniPlayerBroadcastSongHQ.filterUI))


    private fun initView(view: View) {
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
                        MiniPlayerBroadcastRewind.apply {
                            requireActivity()
                                .sendBroadcastRewind(value)
                        }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) { }

                override fun onStopTrackingTouch(seekBar: SeekBar) { }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mini_player_fragment, container, false).apply {
            super.initMiniPlayerView(this)
            initView(this)
        }
    }

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, it.second, PERM_PRIVATE_MINI_PLAYER)
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    override val songArtistF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = MiniPlayerBroadcastSongArtist.extraValueUI
                val songArtist = getStringExtra(extra)
                mini_player_song_artist.text = songArtist
            }
        }

    override val stopF: (Intent?) -> Unit
        get() = { mini_player_song_play_or_pause.setImageResource(R.drawable.play) }

    override val playF: (Intent?) -> Unit
        get() = { mini_player_song_play_or_pause.setImageResource(R.drawable.pause) }

    override val likeF: (Intent?) -> Unit
        get() = { mini_player_song_liked.setImageResource(R.drawable.heart_pressed) }

    override val unlikeF: (Intent?) -> Unit
        get() = { mini_player_song_liked.setImageResource(R.drawable.heart_gray) }

    override val skipNextF: (Intent?) -> Unit
        get() = {}

    override val skipPrevF: (Intent?) -> Unit
        get() = {}

    override val rewindF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = MiniPlayerBroadcastRewind.extraValueUI
                val progress = intent.getIntExtra(extra, 0)
                miniPlayerSongTimeSeekBar.progress = progress
                miniPlayerSongTimeStartTV.text =
                    SongTimeConverter
                        .percentToSongTimeText(progress, miniPlayerSongTimeEndTV)
            }
        }

    override val shuffleF: (Intent?) -> Unit
        get() = { mini_player_song_shuffle.setImageResource(R.drawable.shuffle) }

    override val unShuffleF: (Intent?) -> Unit
        get() = { mini_player_song_shuffle.setImageResource(R.drawable.shuffle_gray) }

    override val loopF: (Intent?) -> Unit
        get() = { mini_player_song_repeat.setImageResource(R.drawable.repeat_one) }

    override val loopAllF: (Intent?) -> Unit
        get() = { mini_player_song_repeat.setImageResource(R.drawable.repeat_all) }

    override val notLoopF: (Intent?) -> Unit
        get() = { mini_player_song_repeat.setImageResource(R.drawable.repeat_gray) }

    override val songNameF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = MiniPlayerBroadcastSongName.extraValueUI
                val value = getStringExtra(extra)
                mini_player_song_name.text = value
            }
        }

    override val songHQF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = MiniPlayerBroadcastSongHQ.extraValueUI
                val value = getBooleanExtra(extra, true)
                if (value) mini_player_song_quality.visibility = View.VISIBLE
                else mini_player_song_quality.visibility = View.GONE
            }
        }

    override val songDurationF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val extra = MiniPlayerBroadcastSongDuration.extraValueUI
                val value = getIntExtra(extra, 100)
                miniPlayerSongTimeEndTV.text =
                    SongTimeConverter.secondsToTimeText(value)
            }
        }
}
