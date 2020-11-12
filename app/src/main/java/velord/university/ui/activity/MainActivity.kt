package velord.university.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import velord.university.ui.util.activity.hideStatusBarAndNoTitle
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.AudlayerApp
import velord.university.application.notification.MiniPlayerNotification
import velord.university.application.permission.PermissionChecker.checkReadWriteExternalStoragePermission
import velord.university.application.settings.AppPreference
import velord.university.model.coroutine.getScope
import velord.university.model.entity.openFragment.general.OpenFragmentEntity
import velord.university.ui.behaviour.supervisor.BackPressSupervisor
import velord.university.ui.behaviour.supervisor.ReturnResultSupervisor
import velord.university.ui.fragment.addToPlaylist.AddToPlaylistFragment
import velord.university.ui.fragment.addToPlaylist.CreateNewPlaylistDialogFragment
import velord.university.ui.fragment.addToPlaylist.select.SelectSongFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.main.MainFragment
import velord.university.ui.fragment.song.AllSongFragment
import velord.university.ui.fragment.vk.VKFragment
import velord.university.ui.fragment.vk.login.dialog.VkLoginDialogFragment
import velord.university.ui.util.*
import velord.university.ui.util.view.VolumeEvent
import velord.university.ui.util.view.hideDefaultChangeVolumeBar


class MainActivity : AppCompatActivity(),
    FolderFragment.Callbacks,
    SelectSongFragment.Callbacks,
    AddToPlaylistFragment.Callbacks,
    CreateNewPlaylistDialogFragment.Callbacks,
    AllSongFragment.Callbacks,
    VKFragment.Callbacks,
    VkLoginDialogFragment.Callbacks {

    private val TAG = "MainActivity"

    private val fm = supportFragmentManager

    private var scopeNotification = getScope()

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "called onCreate")
        super.onCreate(savedInstanceState)

        hideStatusBarAndNoTitle()

        if(baseContext.checkReadWriteExternalStoragePermission(this))
            startApp()
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
            baseContext.checkReadWriteExternalStoragePermission(this)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "called onDestroy")
        super.onDestroy()

        AppPreference(this).appIsDestroyed = false

        scopeNotification.cancel()
    }

    override fun onStart() {
        Log.d(TAG, "called onStart")


        scopeNotification.launch {
            dismissNotification()
        }

        super.onStart()
    }

    override fun onPause() {
        Log.d(TAG, "called onPause")
        super.onPause()
        initNotification()
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

    private fun startApp() {
        try {
            //hide virtual buttons
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

    private fun initNotification() {
        scopeNotification.cancel()
        scopeNotification = getScope()
        MiniPlayerNotification.initNotificationManager(this)
    }

    private tailrec suspend fun dismissNotification() {
        MiniPlayerNotification.dismiss()
        delay(500)
        dismissNotification()
    }

    private inline fun returnResult(
        f: ReturnResultSupervisor.() -> Unit
    ): Unit = ReturnResultSupervisor(this, fm).run { f() }

    override fun toZeroLevel() =
        backPressShell().backPressToZeroLevel()

    override fun openAddToPlaylistFragment() {
        addFragment(
            fm,
            AddToPlaylistFragment(),
            R.id.main_container
        )
    }

    override fun openCreateNewPlaylistFragment() {
        toZeroLevel()
        openCreateNewPlaylistDialogFragment()
    }

    override fun openCreateNewPlaylistDialogFragment() =
        CreateNewPlaylistDialogFragment()
            .show(fm, "CreateNewPlaylistDialogFragment")

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")

        if (backPressShell().backPressedSecondLevel()) return
        if (backPressShell().backPressedFirstLevel()) return

        //not -> cause MainFragment control this
        if (backPressShell().backPressedZeroLevel().not()) {
            //Because single activity architecture
            //When first invoke onBackPressed occurred we returned to MainActivity
            //But we need close app, for this goal we invoke onBackPressed again
            super.onBackPressed()
            super.onBackPressed()
        }
    }

    private fun backPressShell() = BackPressSupervisor(TAG, fm)


    override fun returnResultVkLogin(open: OpenFragmentEntity) =
        returnResult { vkLogin(open) }
}

