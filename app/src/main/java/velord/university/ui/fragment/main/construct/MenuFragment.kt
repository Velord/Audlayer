package velord.university.ui.fragment.main.construct

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.*
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment
import velord.university.ui.fragment.album.AlbumFragment
import velord.university.ui.fragment.folder.FolderFragment
import velord.university.ui.fragment.radio.RadioFragment
import velord.university.ui.fragment.song.SongFragment
import velord.university.ui.fragment.vk.VKFragment

abstract class MenuFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String
        get() = "MenuNowPlayingFragment"

    lateinit var menuMemberViewPager: ViewPager
    private var buttonPressed: Int = 2

    private val fm by lazy {
        activity!!.supportFragmentManager
    }

    private lateinit var selfView: View

    protected lateinit var folderImageBt: ImageButton
    protected lateinit var albumImageBt: ImageButton
    protected lateinit var songImageBt: ImageButton
    protected lateinit var radioImageBt: ImageButton
    protected lateinit var vkImageBt: ImageButton

    protected lateinit var folderTextBt: TextView
    protected lateinit var albumTextBt: TextView
    protected lateinit var songTextBt: TextView
    protected lateinit var radioTextBt: TextView
    protected lateinit var vkTextBt: TextView

    protected fun initMenuFragmentView(view: View) {
        selfView = view
        initMenuButtons(view)
    }

    private fun initMenuButtons(view: View) {
        initMenuImageButtons(view)
        initMenuTextButtons(view)
        initViewPager(view)
    }

    private fun initViewPager(view: View) {
        menuMemberViewPager = view.findViewById(R.id.menu_member_viewPager)
        menuMemberViewPager.adapter = MenuMemberPagerAdapter(fm)
        menuMemberViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                when(position) {
                    0 -> {  Log.d(TAG, "onPageScrolled FolderFragment") }
                    1 -> {  Log.d(TAG, "onPageScrolled AlbumFragment") }
                    2 -> {  Log.d(TAG, "onPageScrolled SongFragment") }
                    3 -> {  Log.d(TAG, "onPageScrolled RadioFragment") }
                    4 -> {  Log.d(TAG, "onPageScrolled VKFragment") }
                }
            }

            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        Log.d(TAG, "onPageSelected FolderFragment")
                        changeUI(R.drawable.star_sky_night, position)
                    }
                    1 -> {
                        Log.d(TAG, "onPageSelected AlbumFragment")
                        changeUI(R.drawable.star_sky_horizon, position)
                    }
                    2 -> {
                        Log.d(TAG, "onPageSelected SongFragment")
                        changeUI(R.drawable.star_sky_mount, position)
                    }
                    3 -> {
                        Log.d(TAG, "onPageSelected RadioFragment")
                        changeUI(R.drawable.mountains_sea_ocean, position)
                    }
                    4 -> {
                        Log.d(TAG, "onPageSelected VKFragment")
                        changeUI(R.drawable.sea_lake_island, position)
                    }
                }
            }
        })
        menuMemberViewPager.currentItem = 2
    }

    private fun initMenuTextButtons(view: View) {
        folderTextBt = view.findViewById(R.id.folder_textView)
        albumTextBt = view.findViewById(R.id.album_textView)
        songTextBt = view.findViewById(R.id.song_textView)
        radioTextBt = view.findViewById(R.id.radio_textView)
        vkTextBt = view.findViewById(R.id.vk_textView)

        folderTextBt.setOnClickListener { openFolderFragment() }
        albumTextBt.setOnClickListener { openAlbumFragment() }
        songTextBt.setOnClickListener { openSongFragment() }
        radioTextBt.setOnClickListener { openRadioFragment() }
        vkTextBt.setOnClickListener { openVKFragment() }
    }

    private fun initMenuImageButtons(view: View) {
        folderImageBt = view.findViewById(R.id.folder)
        albumImageBt = view.findViewById(R.id.album)
        songImageBt = view.findViewById(R.id.song)
        radioImageBt = view.findViewById(R.id.radio)
        vkImageBt = view.findViewById(R.id.vk)

        folderImageBt.setOnClickListener { openFolderFragment() }
        albumImageBt.setOnClickListener { openAlbumFragment() }
        songImageBt.setOnClickListener { openSongFragment() }
        radioImageBt.setOnClickListener { openRadioFragment() }
        vkImageBt.setOnClickListener { openVKFragment() }
    }

    private fun openFolderFragment() {
        Log.d(TAG, "opening FolderFragment")
        menuMemberViewPager.currentItem = 0
    }

    private fun openAlbumFragment() {
        Log.d(TAG, "opening AlbumFragment")
        menuMemberViewPager.currentItem = 1
    }

    private fun openSongFragment() {
        Log.d(TAG, "opening SongFragment")
        menuMemberViewPager.currentItem = 2
    }

    private fun openRadioFragment() {
        Log.d(TAG, "opening RadioFragment")
        menuMemberViewPager.currentItem = 3
    }

    private fun openVKFragment() {
        Log.d(TAG, "opening VKFragment")
        menuMemberViewPager.currentItem = 4
    }

    private fun changeUI(background: Int, position: Int) {
        // main fragment background
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                changeMainFragmentBackground(background)
            }
        }
        //main fragment menu button
        changeButtonToStandardBackground(buttonPressed)
        //main fragment menu button
        buttonPressed = position
        changeButtonToNewBackground(position)
    }

    private fun changeButtonToStandardBackground(position: Int) =
        when (position) {
            0 -> {
                folderImageBt.setImageResource(R.drawable.folder_gray)
            }
            1 -> {
                albumImageBt.setImageResource(R.drawable.album_gray)
            }
            2 -> {
                songImageBt.setImageResource(R.drawable.song_gray)
            }
            3 -> {
                radioImageBt.setImageResource(R.drawable.radio_gray)
            }
            4 -> {
                vkImageBt.setImageResource(R.drawable.vk_gray)
            }
            else -> {
                songImageBt.setImageResource(R.drawable.song_gray)
            }
    }

    private fun changeButtonToNewBackground(position: Int) =
        when (position) {
            0 -> {
                folderImageBt.setImageResource(R.drawable.folder_pressed)
            }
            1 -> {
                albumImageBt.setImageResource(R.drawable.album_pressed)
            }
            2 -> {
                songImageBt.setImageResource(R.drawable.song_pressed)
            }
            3 -> {
                radioImageBt.setImageResource(R.drawable.radio_pressed)
            }
            4 -> {
                vkImageBt.setImageResource(R.drawable.vk_pressed)
            }
            else -> {
                songImageBt.setImageResource(R.drawable.song_pressed)
            }
        }

    private  fun CoroutineScope.changeMainFragmentBackground(background: Int) {
       this.launch {  selfView.setBackgroundResource(background) }
    }

    private inner class MenuMemberPagerAdapter(
        fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            when(position) {
                0 -> {  Log.d(TAG, "Destroy FolderFragment") }
                1 -> {  Log.d(TAG, "Destroy AlbumFragment") }
                2 -> {  Log.d(TAG, "Destroy SongFragment") }
                3 -> {  Log.d(TAG, "Destroy RadioFragment") }
                4 -> {  Log.d(TAG, "Destroy VKFragment") }
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            when(position) {
                0 -> {  Log.d(TAG, "Instantiate FolderFragment") }
                1 -> {  Log.d(TAG, "Instantiate AlbumFragment") }
                2 -> {  Log.d(TAG, "Instantiate SongFragment") }
                3 -> {  Log.d(TAG, "Instantiate RadioFragment") }
                4 -> {  Log.d(TAG, "Instantiate VKFragment") }
            }
            return super.instantiateItem(container, position)
        }

        override fun getItem(pos: Int): Fragment =
            when (pos) {
                // Fragment # 0 - This will show FirstFragment
                0 -> {
                    Log.d(TAG, "create FolderFragment")
                    FolderFragment()
                }
                1 -> {
                    Log.d(TAG, "create AlbumFragment")
                    AlbumFragment()
                }
                2 -> {
                    Log.d(TAG, "create SongFragment")
                    SongFragment()
                }
                3 -> {
                    Log.d(TAG, "create RadioFragment")
                    RadioFragment()
                }
                4 ->  {
                    Log.d(TAG, "create VKFragment")
                    VKFragment()
                }
                else -> {
                    Log.d(TAG, "create SongFragment")
                    SongFragment()
                }
            }

        override fun getCount(): Int = 5
    }
}
