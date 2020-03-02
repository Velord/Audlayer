package velord.university.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.application.service.MiniPlayerServiceBroadcastReceiver
import velord.university.application.settings.AppPreference
import velord.university.ui.BackPressedHandlerFirst
import velord.university.ui.BackPressedHandlerSecond
import velord.university.ui.addFragment
import velord.university.ui.fragment.BackPressedHandler
import velord.university.ui.fragment.addToPlaylist.AddToPlaylist
import velord.university.ui.fragment.addToPlaylist.CreateNewPlaylistDialogFragment
import velord.university.ui.fragment.addToPlaylist.SelectSongFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.main.MainFragment
import velord.university.ui.initFragment


private const val TAG ="MainActivity"

class MainActivity : AppCompatActivity(),
    FolderFragment.Callbacks,
    SelectSongFragment.Callbacks,
    AddToPlaylist.Callbacks,
    CreateNewPlaylistDialogFragment.Callbacks {

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

        val fragments = supportFragmentManager.fragments

        var handled = false

        for (fragment in fragments) {
            if (fragment is BackPressedHandlerSecond) {
                handled = fragment.onBackPressed()
                if (handled) {
                    fm.popBackStackImmediate()
                    return
                }
            }
        }

        for (fragment in fragments) {
            if (fragment is BackPressedHandlerFirst) {
                handled = fragment.onBackPressed()
                if (handled) {
                    fm.popBackStackImmediate()
                    return
                }
            }
        }

        for (fragment in fragments) {
            if (fragment is BackPressedHandler) {
                handled = fragment.onBackPressed()
                if (handled) {
                    return
                }
            }
        }

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
        openCreateNewPlaylistDialogFragment()
    }

    override fun onAddToPlaylistFromAddSongFragment() {
        fm.popBackStackImmediate()
        openAddToPlaylistFragment()
    }

    override fun openCreateNewPlaylistDialogFragment() {
        CreateNewPlaylistDialogFragment()
            .show(fm, "CreateNewPlaylistDialogFragment")
    }

    override fun succes() {
        onBackPressed()
    }

    override fun closeAddToPlaylistFragment() {
        onBackPressed()
    }

    override fun onAddToPlaylistFromFolderFragment() {
        openAddToPlaylistFragment()
    }

    private fun openAddToPlaylistFragment() {
        addFragment(
            fm,
            AddToPlaylist(),
            R.id.main_container
        )
    }
}

