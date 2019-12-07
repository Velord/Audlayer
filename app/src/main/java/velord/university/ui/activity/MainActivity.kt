package velord.university.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import velord.university.R
import velord.university.ui.fragment.MenuFragment
import velord.university.ui.fragment.album.AlbumFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.radio.RadioFragment
import velord.university.ui.fragment.song.SongFragment
import velord.university.ui.fragment.vk.VKFragment
import velord.university.util.initFragment
import velord.university.util.replaceFragment

private const val TAG ="MainActivity"

class MainActivity : AppCompatActivity(), MenuFragment.Callback {

    private val fm = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initFragment(fm, SongFragment(), R.id.container)
    }

    override fun openAlbumFragment() {
        Log.d(TAG, "opening AlbumFragment")
        replaceFragment(fm, AlbumFragment(), R.id.container)
    }

    override fun openRadioFragment() {
        Log.d(TAG, "opening RadioFragment")
        replaceFragment(fm, RadioFragment(), R.id.container)
    }

    override fun openSongFragment() {
        Log.d(TAG, "opening SongFragment")
        replaceFragment(fm, SongFragment(), R.id.container)
    }

    override fun openVKFragment() {
        Log.d(TAG, "opening VKFragment")
        replaceFragment(fm, VKFragment(), R.id.container)
    }

    override fun openFolderFragment() {
        Log.d(TAG, "opening FolderFragment")
        replaceFragment(fm, FolderFragment(), R.id.container)
    }
}
