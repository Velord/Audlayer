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
        Pair(stop(), filterStopUI),  Pair(play(), filterPlayUI),
        Pair(like(), filterLikeUI), Pair(unlike(), filterUnlikeUI),
        Pair(shuffle(), filterShuffleUI), Pair(unShuffle(), filterUnShuffleUI),
        Pair(skipNext(), filterSkipNextUI), Pair(skipPrev(), filterSkipPrevUI),
        Pair(rewind(), filterRewindUI), Pair(loop(), filterLoopUI),
        Pair(loopAll(), filterLoopAllUI), Pair(notLoop(), filterNotLoopedUI),
        Pair(songName(), filterSongNameUI), Pair(songDuration(), filterSongDurationUI),
        Pair(songArtist(), filterSongArtistUI), Pair(songHQ(), filterSongHQUI))


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
        miniPlayerSongTimeSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser)
                        requireActivity()
                            .sendBroadcastRewind(PERM_PRIVATE_MINI_PLAYER, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) { }

                override fun onStopTrackingTouch(seekBar: SeekBar?) { }
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
                val songArtist = getStringExtra(SONG_ARTIST_UI)
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
                val progress = intent.getIntExtra(PROGRESS_UI, 0)
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
    ////!!!!!!
    override val loopF: (Intent?) -> Unit
        get() = { mini_player_song_repeat.setImageResource(R.drawable.repeat_one) }

    override val loopAllF: (Intent?) -> Unit
        get() = { mini_player_song_repeat.setImageResource(R.drawable.repeat_all) }

    override val notLoopF: (Intent?) -> Unit
        get() = { mini_player_song_repeat.setImageResource(R.drawable.repeat_gray) }

    override val songNameF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val value = getStringExtra(SONG_NAME_UI)
                mini_player_song_name.text = value
            }
        }

    override val songHQF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val value = getBooleanExtra(SONG_DURATION_UI, true)
                if (value) mini_player_song_quality.visibility = View.VISIBLE
                else mini_player_song_quality.visibility = View.GONE
            }
        }

    override val songDurationF: (Intent?) -> Unit
        get() = { intent ->
            intent?.apply {
                val value = getIntExtra(SONG_DURATION_UI, 100)
                miniPlayerSongTimeEndTV.text =
                    SongTimeConverter.secondsToTimeText(value)
            }
        }
}
