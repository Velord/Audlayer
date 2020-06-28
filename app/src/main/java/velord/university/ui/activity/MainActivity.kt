package velord.university.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.AudlayerApp
import velord.university.application.notification.MiniPlayerServiceNotification
import velord.university.application.permission.PermissionChecker
import velord.university.application.settings.AppPreference
import velord.university.ui.backPressed.BackPressedHandler
import velord.university.ui.backPressed.BackPressedHandlerFirst
import velord.university.ui.backPressed.BackPressedHandlerSecond
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.addToPlaylist.AddToPlaylist
import velord.university.ui.fragment.addToPlaylist.CreateNewPlaylistDialogFragment
import velord.university.ui.fragment.addToPlaylist.SelectSongFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.main.MainFragment
import velord.university.ui.fragment.song.AllSongFragment
import velord.university.ui.fragment.vk.VKFragment
import velord.university.ui.util.*


class MainActivity : AppCompatActivity(),
    FolderFragment.Callbacks,
    SelectSongFragment.Callbacks,
    AddToPlaylist.Callbacks,
    CreateNewPlaylistDialogFragment.Callbacks,
    AllSongFragment.Callbacks,
    VKFragment.Callbacks {

    private val TAG = "MainActivity"

    private val fm = supportFragmentManager

    private var scopeNotification = CoroutineScope(Job() + Dispatchers.Default)

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "called onCreate")
        super.onCreate(savedInstanceState)

        if(PermissionChecker.checkReadWriteExternalStoragePermission(baseContext, this))
            startApp()
    }

    private fun startApp() {
        try {
            //hide virtual buttons
            hideVirtualButtons()
            //start app
            AudlayerApp.initApp(baseContext)
            //self view
            setContentView(R.layout.main_activity)
            //fragment
            initFragment(
                fm,
                MainFragment(),
                R.id.main_container
            )
            //
            viewModel
        }
        catch(e: Exception) {
            startApp()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(baseContext, "Permission granted", Toast.LENGTH_SHORT).show()
            //start activity
            startApp()
        } else {
            PermissionChecker.checkReadWriteExternalStoragePermission(baseContext, this)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "called onDestroy")
        super.onDestroy()
        AppPreference(this).appIsDestroyed = false
    }

    override fun onStart() {
        scopeNotification.launch {
            dismissNotification()
        }
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        initNotification()
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")

        if (backPressedSecondLevel())
            return
        if (backPressedFirstLevel())
            return

        //not -> cause MainFragment control this
        if (backPressedZeroLevel().not()) {
            //Because single activity architecture
            //When first invoke onBackPressed occurred we returned to MainActivity
            //But we need close app, for this goal we invoke onBackPressed again
            super.onBackPressed()
            super.onBackPressed()
        }
    }
    //main fragment handle this
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                hideDefaultChangeVolumeBar(this, VolumeEvent.INCREASE)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                hideDefaultChangeVolumeBar(this, VolumeEvent.DECREASE)
                true
            }
            else -> super.onKeyDown(keyCode, event)
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

    override fun onAddToPlaylistFromVkFragment() {
        toZeroAndOpenAddToPlaylist()
    }

    override fun toZeroLevelFromSelectSongFragment() {
        toZeroLevel()
    }

    override fun onAddToPlaylistFromSongFragment() {
        toZeroAndOpenAddToPlaylist()
    }

    private fun initNotification() {
        scopeNotification.cancel()
        scopeNotification = CoroutineScope(Job() + Dispatchers.Default)
        MiniPlayerServiceNotification.initNotificationManager(this)
    }

    private tailrec suspend fun dismissNotification() {
        MiniPlayerServiceNotification.dismiss()
        delay(1000)
        dismissNotification()
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

    private fun backPressedSecondLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerSecond> {
            it.popBackStackImmediate()
            true
        }

    private fun backPressedFirstLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerFirst> {
            it.popBackStackImmediate()
            true
        }

    private fun backPressedZeroLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerZero> { true }

    private inline fun <reified T: BackPressedHandler>
            checkFragmentBackStack(f: (FragmentManager) -> Boolean): Boolean {
        var handled = false
        fm.apply {
            fragments.forEach {
                if (it is T) {
                    handled = it.onBackPressed()
                    return f(this)
                }
            }
        }
        return handled
    }
}

