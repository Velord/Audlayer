package velord.university.ui.fragment.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.settings.SortByPreference
import velord.university.model.entity.Album
import velord.university.model.entity.Playlist
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.actionBar.ActionBarFragment
import velord.university.ui.util.setupPopupMenuOnClick


class AlbumFragment : ActionBarFragment(), BackPressedHandlerZero {

    override val TAG: String = "AlbumFragment"

    companion object {
        fun newInstance() = AlbumFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AlbumViewModel::class.java)
    }

    val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var albumArticle: TextView
    private lateinit var playlistArticle: TextView
    private lateinit var albumFrame: LinearLayout
    private lateinit var playlistFrame: LinearLayout
    private lateinit var albumRV: RecyclerView
    private lateinit var playlistRV: RecyclerView
    private lateinit var playlistRefresh: TextView
    private lateinit var albumRefresh: TextView

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return true
    }

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.album_sort_by_album -> {
                SortByPreference.setSortByAlbumFragment(requireContext(), 0)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.album_sort_by_artist -> {
                SortByPreference.setSortByAlbumFragment(requireContext(), 1)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.album_sort_by_year -> {
                SortByPreference.setSortByAlbumFragment(requireContext(), 2)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true                        }
            R.id.album_sort_by_number_of_tracks -> {
                SortByPreference.setSortByAlbumFragment(requireContext(), 3)
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
    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = getString(R.string.action_bar_hint_album)
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = { }
    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {
        //set up checked item
        val menuItem = it.menu

        val nameArtistDateOrder =
            SortByPreference.getSortByAlbumFragment(requireContext())
        when(nameArtistDateOrder) {
            0 -> { menuItem.getItem(0).isChecked = true }
            1 -> { menuItem.getItem(1).isChecked = true }
            2 -> { menuItem.getItem(2).isChecked = true }
            3 -> { menuItem.getItem(3).isChecked = true }
        }

        when(SortByPreference.getAscDescAlbumFragment(requireContext())) {
            0 -> { menuItem.getItem(4).isChecked = true }
            1 -> { menuItem.getItem(5).isChecked = true }
            else -> {}
        }
    }
    override val actionBarObserveSearchQuery: (String) -> Unit = { searchQuery ->
        //-1 is default value, just ignore it
        val correctQuery =
            if (searchQuery == "-1") ""
            else searchQuery
            //store search term in shared preferences
            viewModel.storeSearchQuery(correctQuery)
            //update files list
            updateAdapterBySearchQuery(correctQuery)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_fragment, container, false).apply {
            scope.launch {
                launch {
                    viewModel.retrievePlaylistFromDb()
                    viewModel.retrieveAlbumFromDb()
                }
                withContext(Dispatchers.Main) {
                    //init action bar
                    super.initActionBar(this@apply)
                    super.changeUIAfterSubmitTextInSearchView(super.searchView)
                    //init self view
                    initViews(this@apply)
                    //observe changes in search view
                    super.observeSearchQuery()
                    //setup adapter by invoke change in search view
                    setupAdapter()
                }
            }
        }
    }

    private fun initViews(view: View) {
        initPlaylist(view)
        initAlbums(view)
    }

    private fun initAlbums(view: View) {
        albumArticle = view.findViewById(R.id.album_fragment_album_article)
        albumFrame = view.findViewById(R.id.album_fragment_album_rv_frame)
        albumRV = view.findViewById(R.id.album_RV)
        albumRefresh = view.findViewById(R.id.album_refresh)

        albumArticle.setOnClickListener {
            albumRV.visibility =
                if (albumRV.visibility == View.GONE) View.VISIBLE
                else View.GONE
        }

        albumRefresh.setOnClickListener {
            scope.launch {
                withContext(Dispatchers.Main) {
                    val refresh = it as TextView
                    refresh.text = getString(R.string.album_refreshing)
                }
                viewModel.refreshAllAlbum()
                updateAdapterBySearchQuery(viewModel.currentQuery)
                withContext(Dispatchers.Main) {
                    val refresh = it as TextView
                    refresh.text = getString(R.string.album_refresh)
                }
            }
        }

        albumRV.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(albumRV, 50, -5)
    }

    private fun initPlaylist(view: View) {
        playlistArticle =  view.findViewById(R.id.album_fragment_playlist_article)
        playlistFrame = view.findViewById(R.id.album_fragment_playlist_rv_frame)
        playlistRV = view.findViewById(R.id.playlist_RV)
        playlistRefresh = view.findViewById(R.id.playlist_refresh)

        playlistArticle.setOnClickListener {
            playlistRV.visibility =
                if (playlistRV.visibility == View.GONE) View.VISIBLE
                else View.GONE
        }

        playlistRefresh.setOnClickListener {
            scope.launch {
                withContext(Dispatchers.Main) {
                    val refresh = it as TextView
                    refresh.text = getString(R.string.album_refreshing)
                }
                viewModel.retrievePlaylistFromDb()
                updateAdapterBySearchQuery(viewModel.currentQuery)
                withContext(Dispatchers.Main) {
                    val refresh = it as TextView
                    refresh.text = getString(R.string.album_refresh)
                }
            }
        }

        playlistRV.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(playlistRV, 50, -5)
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        scope.launch {
            this.launch {
                if (viewModel.playlistIsInitialized()) {
                    val playlistFiltered =
                        viewModel.filterByQueryPlaylist(searchQuery).toTypedArray()
                    withContext(Dispatchers.Main) {
                        playlistRV.adapter = PlaylistAdapter(playlistFiltered)
                    }
                }
            }
            this.launch {
                if (viewModel.albumsIsInitialized()) {
                    val albums = viewModel.albums.toTypedArray()
                    withContext(Dispatchers.Main) {
                        albumRV.adapter = AlbumAdapter(albums)
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        val query = viewModel.getSearchQuery()
        super.viewModelActionBar.setupSearchQuery(query)
    }

    private inner class AlbumHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val pathTextView: TextView = itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout = itemView.findViewById(R.id.general_action_frame)

        private fun openAlbum(album: Album) {
            viewModel.playSongs(album.songs.toTypedArray())
        }

        private val actionPopUpMenu: (Album) -> Unit = { album ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.add_to_playlist_pop_up }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.playlist_item_play -> {
                        TODO()
                        true
                    }
                    R.id.playlist_item_add_to_home_screen -> {
                        TODO()
                        true
                    }
                    R.id.playlist_item_delete -> {
                        TODO()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            setupPopupMenuOnClick(
                requireContext(),
                actionImageButton,
                initActionMenuStyle,
                initActionMenuLayout,
                initActionMenuItemClickListener
            )

            Unit
        }

        private fun setOnClickAndImageResource(album: Album) {
            itemView.setOnClickListener {
                openAlbum(album)
            }
            pathTextView.setOnClickListener {
                openAlbum(album)
            }
            actionImageButton.setOnClickListener {
                actionPopUpMenu(album)
            }
            actionFrame.setOnClickListener {
                actionPopUpMenu(album)
            }
        }

        fun bindItem(album: Album, position: Int) {
            setOnClickAndImageResource(album)
            pathTextView.text =
                getString(R.string.album_item, album.name, album.songs.size)
        }
    }

    private inner class AlbumAdapter(val items:  Array<out Album>):
        RecyclerView.Adapter<AlbumHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )
            return AlbumHolder(view)
        }

        override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
            items[position].apply {
                holder.bindItem(this, position)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }

    private inner class PlaylistHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val pathTextView: TextView = itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout = itemView.findViewById(R.id.general_action_frame)

        private fun openPlaylist(playlist: Playlist) {
            viewModel.playSongs(playlist.songs.toTypedArray())
        }

        private val actionPopUpMenu: (Playlist) -> Unit = { playlist ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.add_to_playlist_pop_up }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.playlist_item_play -> {
                        viewModel.playSongs(playlist.songs.toTypedArray())
                        true
                    }
                    R.id.playlist_item_add_to_home_screen -> {
                        TODO()
                        true
                    }
                    R.id.playlist_item_delete -> {
                        scope.launch {
                            viewModel.deletePlaylist(playlist)
                            withContext(Dispatchers.Main) {
                                setupAdapter()
                            }
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            setupPopupMenuOnClick(
                requireContext(),
                actionImageButton,
                initActionMenuStyle,
                initActionMenuLayout,
                initActionMenuItemClickListener
            )

            Unit
        }

        private fun setOnClickAndImageResource(playlist: Playlist) {
            itemView.setOnClickListener {
                openPlaylist(playlist)
            }
            pathTextView.setOnClickListener {
                openPlaylist(playlist)
            }
            actionImageButton.setOnClickListener {
                actionPopUpMenu(playlist)
            }
            actionFrame.setOnClickListener {
                actionPopUpMenu(playlist)
            }
        }

        fun bindItem(playlist: Playlist, position: Int) {
            setOnClickAndImageResource(playlist)
            pathTextView.text =
                getString(R.string.album_item, playlist.name, playlist.songs.size)
        }
    }

    private inner class PlaylistAdapter(val items:  Array<out Playlist>):
        RecyclerView.Adapter<PlaylistHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )
            return PlaylistHolder(view)
        }

        override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
            items[position].apply {
                holder.bindItem(this, position)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }
}
