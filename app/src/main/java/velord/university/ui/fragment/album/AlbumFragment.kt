package velord.university.ui.fragment.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import velord.university.R
import velord.university.application.settings.SortByPreference
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.actionBar.ActionBarFragment


class AlbumFragment : ActionBarFragment(), BackPressedHandlerZero {

    override val TAG: String = "AlbumFragment"

    companion object {
        fun newInstance() = AlbumFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AlbumViewModel::class.java)
    }

    private lateinit var albumArticle: TextView
    private lateinit var playlistArticle: TextView
    private lateinit var albumFrame: FrameLayout
    private lateinit var playlistFrame: FrameLayout
    private lateinit var albumRV: RecyclerView
    private lateinit var playlistRV: RecyclerView

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return true
    }

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.album_sort_by_album -> {
                SortByPreference.setAlbumArtistYearNumberAlbumFragment(requireContext(), 0)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.album_sort_by_artist -> {
                SortByPreference.setAlbumArtistYearNumberAlbumFragment(requireContext(), 1)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.album_sort_by_year -> {
                SortByPreference.setAlbumArtistYearNumberAlbumFragment(requireContext(), 2)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true                        }
            R.id.album_sort_by_number_of_tracks -> {
                SortByPreference.setAlbumArtistYearNumberAlbumFragment(requireContext(), 3)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.album_sort_by_ascending_order -> {
                SortByPreference.setAscDescAlbumFragment(requireContext(), 0)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.album_sort_by_descending_order -> {
                SortByPreference.setAscDescAlbumFragment(requireContext(), 1)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            else -> {
                false
            }
        }
    }
    override val actionBarPopUpMenuLayout: () -> Int = { R.menu.album_fragment_pop_up }
    override val actionBarPopUpMenuStyle: () -> Int = { R.style.PopupMenuOverlapAnchorFolder }
    override val actionBarHintArticle: (TextView) -> Unit = { it.text = "Playlist | Album" }
    override val actionBarLeftMenu: (ImageButton) -> Unit = { }
    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {
        //set up checked item
        val menuItem = it.menu

        val nameArtistDateOrder =
            SortByPreference.getAlbumArtistYearNumberAlbumFragment(requireContext())
        when(nameArtistDateOrder) {
            0 -> { menuItem.getItem(0).isChecked = true }
            1 -> { menuItem.getItem(1).isChecked = true }
            2 -> { menuItem.getItem(2).isChecked = true }
            3 -> { menuItem.getItem(3).isChecked = true }
        }

        val ascDescOrder = SortByPreference.getAscDescAlbumFragment(requireContext())
        when(ascDescOrder) {
            0 -> { menuItem.getItem(4).isChecked = true }
            1 -> { menuItem.getItem(5).isChecked = true }
            else -> {}
        }
    }
    override val actionBarObserveSearchQuery: (String) -> Unit = { searchQuery ->
        //store search term in shared preferences
        viewModel.storeSearchQuery(searchQuery)
        //update files list
        updateAdapterBySearchQuery(searchQuery)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_fragment, container, false).apply {
            //init action bar
            super.initActionBar(this)
            //init self view
            initViews(this)
            //observe changes in search view
            super.observeSearchTerm()
            //setup adapter by invoke change in search view
            setupAdapter()
        }
    }

    private fun initViews(view: View) {
        initPlaylists(view)
        initAlbums(view)
    }

    private fun initAlbums(view: View) {
        albumArticle = view.findViewById(R.id.album_fragment_album_article)
        albumFrame = view.findViewById(R.id.album_fragment_album_rv_frame)
        albumRV = view.findViewById(R.id.general_RecyclerView)

        albumArticle.setOnClickListener {
            albumFrame.visibility = if (albumFrame.visibility == View.GONE)
                View.VISIBLE
            else View.GONE
        }

        albumRV.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        super.setOnScrollListenerBasedOnRecyclerViewScrolling(albumRV, 50, -5)
    }

    private fun initPlaylists(view: View) {
        playlistArticle =  view.findViewById(R.id.album_fragment_playlist_article)
        playlistFrame = view.findViewById(R.id.album_fragment_playlist_rv_frame)
        playlistRV = view.findViewById(R.id.general_RecyclerView)

        playlistArticle.setOnClickListener {
            playlistFrame.visibility = if (playlistFrame.visibility == View.GONE)
                View.VISIBLE
            else View.GONE
        }

        playlistRV.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        super.setOnScrollListenerBasedOnRecyclerViewScrolling(playlistRV, 50, -5)
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {

    }

    private fun setupAdapter() =
        super.viewModelActionBar.setupSearchQueryByAlbumPreference()
}
