package velord.university.ui.fragment.miniPlayer

import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment

abstract class MiniPlayerInitializerFragment : LoggerSelfLifecycleFragment() {

    protected lateinit var miniPlayerIV: ImageView
    protected lateinit var miniPlayerSongNameTV: TextView
    protected lateinit var miniPlayerSongArtistTV: TextView
    protected lateinit var miniPlayerSongQualityTV: TextView
    protected lateinit var miniPlayerSongShuffleIB: ImageButton
    protected lateinit var miniPlayerSongRepeatIB: ImageButton
    protected lateinit var miniPlayerSongLikedIB: ImageButton
    protected lateinit var miniPlayerSongManagerLL: LinearLayout
    protected lateinit var miniPlayerSongSkipPrevIB: ImageButton
    protected lateinit var miniPlayerSongPlayOrPauseIB: ImageButton
    protected lateinit var miniPlayerSongSkipNextIB: ImageButton
    protected lateinit var miniPlayerSongTimeCS: ConstraintLayout
    protected lateinit var miniPlayerSongTimeStartTV: TextView
    protected lateinit var miniPlayerSongTimeEndTV: TextView
    protected lateinit var miniPlayerSongTimeSeekBar: SeekBar

    protected fun initMiniPlayerView(view: View) {
        miniPlayerIV = view.findViewById(R.id.mini_player_imageView)
        initSongName(view)
        initSongArtist(view)
        miniPlayerSongQualityTV = view.findViewById(R.id.mini_player_song_quality)
        miniPlayerSongShuffleIB = view.findViewById(R.id.mini_player_song_shuffle)
        miniPlayerSongRepeatIB = view.findViewById(R.id.mini_player_song_repeat)
        miniPlayerSongLikedIB = view.findViewById(R.id.mini_player_song_liked)
        miniPlayerSongManagerLL = view.findViewById(R.id.mini_player_song_manager_LinearLayout)
        miniPlayerSongSkipPrevIB = view.findViewById(R.id.mini_player_song_skip_prev)
        miniPlayerSongPlayOrPauseIB = view.findViewById(R.id.mini_player_song_play_or_pause)
        miniPlayerSongSkipNextIB = view.findViewById(R.id.mini_player_song_skip_next)
        miniPlayerSongTimeCS = view.findViewById(R.id.mini_player_song_time_constraintLayout)
        miniPlayerSongTimeStartTV = view.findViewById(R.id.mini_player_song_time_start)
        miniPlayerSongTimeEndTV = view.findViewById(R.id.mini_player_song_time_end)
        miniPlayerSongTimeSeekBar = view.findViewById(R.id.mini_player_song_time_seekBar)
    }

    private fun initSongName(view: View) {
        miniPlayerSongNameTV = view.findViewById(R.id.mini_player_song_name)
        miniPlayerSongNameTV.apply {
            setSingleLine()
            isSelected = false
        }
    }

    private fun initSongArtist(view: View) {
        miniPlayerSongArtistTV = view.findViewById(R.id.mini_player_song_artist)
        miniPlayerSongArtistTV.apply {
            setSingleLine()
            isSelected = false
        }
    }
}