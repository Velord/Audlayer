package velord.university.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import velord.university.R
import velord.university.ui.fragment.MenuFragment
import velord.university.ui.fragment.album.AlbumFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.radio.RadioFragment
import velord.university.ui.fragment.song.SongFragment
import velord.university.ui.fragment.vk.VKFragment

private const val TAG ="MainActivity"

class MainActivity : AppCompatActivity(), MenuFragment.Callback {

    private val fm = supportFragmentManager

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initView()
    }

    override fun openFolderFragment() {
        Log.d(TAG, "opening FolderFragment")
        viewPager.currentItem = 0
    }

    override fun openAlbumFragment() {
        Log.d(TAG, "opening AlbumFragment")
        viewPager.currentItem = 1
    }

    override fun openSongFragment() {
        Log.d(TAG, "opening SongFragment")
        viewPager.currentItem = 2
    }

    override fun openRadioFragment() {
        Log.d(TAG, "opening RadioFragment")
        viewPager.currentItem = 3
    }

    override fun openVKFragment() {
        Log.d(TAG, "opening VKFragment")
        viewPager.currentItem = 4
    }

    private fun initView() {
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = MyPagerAdapter(fm)
        viewPager.currentItem = 2
    }

    private inner class MyPagerAdapter(
        fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(pos: Int): Fragment =
            when (pos) {
                // Fragment # 0 - This will show FirstFragment
                0 -> FolderFragment()
                // Fragment # 0 - This will show FirstFragment different title
                1 -> AlbumFragment()
                // Fragment # 1 - This will show SecondFragment
                2 -> SongFragment()
                3 -> RadioFragment()
                4 -> VKFragment()
                else -> SongFragment()
            }

        override fun getCount(): Int = 5
    }
}

