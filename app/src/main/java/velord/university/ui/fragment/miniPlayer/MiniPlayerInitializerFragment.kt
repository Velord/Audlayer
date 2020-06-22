package velord.university.ui.fragment.miniPlayer

import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import velord.university.R
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import velord.university.ui.util.setAutoScrollable

abstract class MiniPlayerInitializerFragment : LoggerSelfLifecycleFragment() {

    protected lateinit var miniPlayerIconIV: ImageView
    protected lateinit var miniPlayerNameTV: TextView
    protected lateinit var miniPlayerArtistTV: TextView
    protected lateinit var miniPlayerSongQualityTV: TextView
    protected lateinit var miniPlayerSongShuffleIB: ImageButton
    protected lateinit var miniPlayerSongRepeatIB: ImageButton
    protected lateinit var miniPlayerSongLikedIB: ImageButton
    protected lateinit var miniPlayerSongManagerLL: LinearLayout
    protected lateinit var miniPlayerSongSkipPrevIB: ImageButton
    protected lateinit var miniPlayerPlayOrPauseIB: ImageButton
    protected lateinit var miniPlayerSongSkipNextIB: ImageButton
    protected lateinit var miniPlayerSongTimeCS: ConstraintLayout
    protected lateinit var miniPlayerSongTimeStartTV: TextView
    protected lateinit var miniPlayerSongTimeEndTV: TextView
    protected lateinit var miniPlayerSongTimeSeekBar: SeekBar
    protected lateinit var miniPlayerGeneral: View
    protected lateinit var miniPlayerRadio: View
    protected lateinit var miniPlayerRadioNameTV: TextView
    protected lateinit var miniPlayerRadioArtistTV: TextView
    protected lateinit var miniPlayerRadioLikedIB: ImageButton
    protected lateinit var miniPlayerRadioPlayOrPauseIB: ImageButton
    protected lateinit var miniPlayerRadioIcon: ImageView

    protected fun initMiniPlayerView(view: View) {
        miniPlayerIconIV = view.findViewById(R.id.mini_player_icon_imageView)
        initSongName(view)
        initSongArtist(view)
        miniPlayerSongQualityTV = view.findViewById(R.id.mini_player_song_quality)
        miniPlayerSongShuffleIB = view.findViewById(R.id.mini_player_song_shuffle)
        miniPlayerSongRepeatIB = view.findViewById(R.id.mini_player_song_repeat)
        miniPlayerSongLikedIB = view.findViewById(R.id.mini_player_song_liked)
        miniPlayerSongManagerLL = view.findViewById(R.id.mini_player_song_manager_LinearLayout)
        miniPlayerSongSkipPrevIB = view.findViewById(R.id.mini_player_song_skip_prev)
        miniPlayerPlayOrPauseIB = view.findViewById(R.id.mini_player_song_play_or_pause)
        miniPlayerSongSkipNextIB = view.findViewById(R.id.mini_player_song_skip_next)
        miniPlayerSongTimeCS = view.findViewById(R.id.mini_player_song_time_constraintLayout)
        miniPlayerSongTimeStartTV = view.findViewById(R.id.mini_player_song_time_start)
        miniPlayerSongTimeEndTV = view.findViewById(R.id.mini_player_song_time_end)
        miniPlayerSongTimeSeekBar = view.findViewById(R.id.mini_player_song_time_seekBar)
        miniPlayerGeneral = view.findViewById(R.id.mini_player_general)
        miniPlayerRadio = view.findViewById(R.id.mini_player_radio)
        miniPlayerRadioNameTV = view.findViewById(R.id.mini_player_radio_name)
        miniPlayerRadioArtistTV = view.findViewById(R.id.mini_player_radio_artist)
        miniPlayerRadioLikedIB = view.findViewById(R.id.mini_player_radio_liked)
        miniPlayerRadioPlayOrPauseIB = view.findViewById(R.id.mini_player_radio_play_or_pause)
        miniPlayerRadioIcon = view.findViewById(R.id.mini_player_radio_imageView)
    }

    protected fun showMiniPlayerGeneral() {
        miniPlayerGeneral.visibility = View.VISIBLE
        miniPlayerRadio.visibility = View.GONE
    }

    protected fun showMiniPlayerRadio() {
        miniPlayerGeneral.visibility = View.GONE
        miniPlayerRadio.visibility = View.VISIBLE
    }

    protected fun startSongAndArtistNameScrolling() {
        miniPlayerArtistTV.isSelected = true
        miniPlayerNameTV.isSelected = true
    }

    protected fun stopSongAndArtistNameScrolling()  {
        miniPlayerArtistTV.isSelected = false
        miniPlayerNameTV.isSelected = false
    }

    private fun initSongName(view: View) {
        miniPlayerNameTV = view.findViewById(R.id.mini_player_song_name)
        miniPlayerNameTV.setAutoScrollable()
    }

    private fun initSongArtist(view: View) {
        miniPlayerArtistTV = view.findViewById(R.id.mini_player_song_artist)
        miniPlayerArtistTV.setAutoScrollable()
    }
}