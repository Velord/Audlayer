package velord.university.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.application.service.MiniPlayerServiceBroadcastReceiver
import velord.university.application.settings.AppPreference
import velord.university.ui.backPressed.BackPressedHandlerFirst
import velord.university.ui.backPressed.BackPressedHandlerSecond
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.addToPlaylist.AddToPlaylist
import velord.university.ui.fragment.addToPlaylist.CreateNewPlaylistDialogFragment
import velord.university.ui.fragment.addToPlaylist.SelectSongFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.main.MainFragment
import velord.university.ui.fragment.song.SongFragment
import velord.university.ui.util.addFragment
import velord.university.ui.util.initFragment


private const val TAG ="MainActivity"

class MainActivity : AppCompatActivity(),
    FolderFragment.Callbacks,
    SelectSongFragment.Callbacks,
    AddToPlaylist.Callbacks,
    CreateNewPlaylistDialogFragment.Callbacks,
    SongFragment.Callbacks{

    private val fm = supportFragmentManager

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        //service
        startService(Intent(this, MiniPlayerServiceBroadcastReceiver().javaClass))
        //self view
        setContentView(R.layout.main_activity)
        //fragment
        initFragment(
            fm,
            MainFragment(),
            R.id.main_container
        )
        viewModel
    }

    override fun onDestroy() {
        Log.d(TAG, "called onDestroy")
        super.onDestroy()
        stopService(Intent(this, MiniPlayerServiceBroadcastReceiver().javaClass))

        AppPreference.setAppIsDestroyed(this, false)
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")

        if (backPressedSecondLevel())
            return
        if (backPressedFirstLevel())
            return

        val handled = backPressedZeroLevel()

        if (!handled) {
            //Because single activity architecture
            //When first invoke onBackPressed occurred we returned to MainActivity
            //But we need close app, for this goal we invoke onBackPressed again
            super.onBackPressed()
            super.onBackPressed()
        }
    }

    override fun onAddToPlaylist() {
        addFragment(
            fm,
            SelectSongFragment(),
            R.id.main_container
        )
    }

    override fun onCreatePlaylist() {
        toZeroLevel()
        openCreateNewPlaylistDialogFragment()
    }

    override fun onAddToPlaylistFromAddSongFragment() {
        toZeroLevel()
        openAddToPlaylistFragment()
    }

    override fun openCreateNewPlaylistDialogFragment() {
        CreateNewPlaylistDialogFragment()
            .show(fm, "CreateNewPlaylistDialogFragment")
    }

    override fun success() {
        toZeroLevel()
    }

    override fun closeAddToPlaylistFragment() {
        toZeroLevel()
    }

    override fun onAddToPlaylistFromFolderFragment() {
        toZeroAndOpenAddToPlaylist()
    }

    override fun onAddToPlaylistFromSongFragment() {
        toZeroAndOpenAddToPlaylist()
    }

    override fun toZeroLevelFromSelectSongFragment() {
        toZeroLevel()
    }

    private fun toZeroAndOpenAddToPlaylist() {
        toZeroLevel()
        openAddToPlaylistFragment()
    }

    private fun openAddToPlaylistFragment() {
        addFragment(
            fm,
            AddToPlaylist(),
            R.id.main_container
        )
    }

    private fun toZeroLevel() {
        backPressedSecondLevel()
        backPressedFirstLevel()
    }

    private fun backPressedSecondLevel(): Boolean {
        val fragments = supportFragmentManager.fragments
        var handled = false

        for (fragment in fragments) {
            if (fragment is BackPressedHandlerSecond) {
                handled = fragment.onBackPressed()
                if (handled) {
                    fm.popBackStackImmediate()
                    return true
                }
            }
        }

        return handled
    }

    private fun backPressedFirstLevel(): Boolean {
        val fragments = supportFragmentManager.fragments
        var handled = false

        for (fragment in fragments) {
            if (fragment is BackPressedHandlerFirst) {
                handled = fragment.onBackPressed()
                if (handled) {
                    fm.popBackStackImmediate()
                    return true
                }
            }
        }

        return handled
    }

    private fun backPressedZeroLevel(): Boolean {
        val fragments = supportFragmentManager.fragments
        var handled = false

        for (fragment in fragments) {
            if (fragment is BackPressedHandlerZero) {
                handled = fragment.onBackPressed()
                if (handled) {
                    return true
                }
            }
        }

        return handled
    }
}

