package velord.university.ui.fragment.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.util.view.makeCheck
import velord.university.ui.util.view.setupAndShowPopupMenuOnClick
import velord.university.ui.util.view.setupPopupMenuOnClick
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.settings.SortByPreference
import velord.university.databinding.ActionBarSearchBinding
import velord.university.databinding.AlbumFragmentBinding
import velord.university.model.entity.Album
import velord.university.model.entity.Playlist
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.actionBar.ActionBarSearchFragment


class AlbumFragment :
    ActionBarSearchFragment(),
    BackPressedHandlerZero {

    override val TAG: String = "AlbumFragment"

    companion object {
        fun newInstance() = AlbumFragment()
    }

    private val viewModel: AlbumViewModel by viewModels()

    private val scope = getScope()
    //view
    private var _binding: AlbumFragmentBinding? = null
    override var _bindingActionBar: ActionBarSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return true
    }

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.album_sort_by_album -> sortBy(0)
            R.id.album_sort_by_artist -> sortBy(1)
            R.id.album_sort_by_year -> sortBy(2)
            R.id.album_sort_by_number_of_tracks -> sortBy(3)
            R.id.album_sort_by_ascending_order -> sortByAscDesc(0)
            R.id.album_sort_by_descending_order -> sortByAscDesc(1)
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
            SortByPreference(requireContext()).sortByAlbumFragment
        when(nameArtistDateOrder) {
            0 -> { menuItem.makeCheck(0) }
            1 -> { menuItem.makeCheck(1) }
            2 -> { menuItem.makeCheck(2) }
            3 -> { menuItem.makeCheck(3) }
        }

        val ascDescOrder = SortByPreference(requireContext()).ascDescAlbumFragment
        when(ascDescOrder) {
            0 -> { menuItem.makeCheck(4) }
            1 -> { menuItem.makeCheck(5) }
            else -> {}
        }
    }
    override val actionBarPopUp: (ImageButton) -> Unit = { }
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
    override val actionSearchView: (SearchView) -> Unit = {  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.album_fragment,
        container, false).run {
        //bind
        _binding = AlbumFragmentBinding.bind(this)
        _bindingActionBar = binding.actionBarInclude
        scope.launch {
            launch {
                viewModel.retrievePlaylistFromDb()
                viewModel.retrieveAlbumFromDb()
            }
            onMain {
                //init action bar
                super.initActionBar()
                super.changeUIAfterSubmitTextInSearchView(
                    super.bindingActionBar.search
                )
                //init self view
                initView()
                //observe changes in search view
                super.observeSearchQuery()
                //setup adapter by invoke change in search view
                setupAdapter()
            }
        }
        binding.root
    }

    private fun initView() {
        initPlaylist()
        initAlbums()
    }

    private fun initAlbums() {
        binding.albumArticle.setOnClickListener {
            binding.albumRV.visibility =
                if (binding.albumRV.visibility == View.GONE) View.VISIBLE
                else View.GONE
        }

        binding.albumRefresh.setOnClickListener {
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

        binding.albumRV.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(
            binding.albumRV, 50, -5
        )
    }

    private fun initPlaylist() {
        binding.playlistArticle.setOnClickListener {
            binding.playlistRV.visibility =
                if (binding.playlistRV.visibility == View.GONE) View.VISIBLE
                else View.GONE
        }
        binding.playlistRefresh.setOnClickListener {
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
        binding.playlistRV.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(
            binding.playlistRV, 50, -5
        )
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference(requireContext()).sortByAlbumFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference(requireContext()).ascDescAlbumFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        scope.launch {
            this.launch {
                if (viewModel.playlistIsInitialized()) {
                    val playlistFiltered =
                        viewModel.filterByQueryPlaylist(searchQuery).toTypedArray()
                    withContext(Dispatchers.Main) {
                        binding.playlistRV.adapter = PlaylistAdapter(playlistFiltered)
                    }
                }
            }
            this.launch {
                if (viewModel.albumsIsInitialized()) {
                    val albums = viewModel.albums.toTypedArray()
                    withContext(Dispatchers.Main) {
                        binding.albumRV.adapter = AlbumAdapter(albums)
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        val query = viewModel.getSearchQuery()
        super.viewModelActionBarSearch.setupSearchQuery(query)
    }

    private inner class AlbumHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val pathTextView: TextView =
            itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton =
            itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout =
            itemView.findViewById(R.id.general_action_frame)
        private val icon: ImageButton =
            itemView.findViewById(R.id.general_item_icon)

        private fun openAlbum(album: Album) {
            viewModel.playSongs(album.songs.toTypedArray())
        }

        private val actionPopUpMenu: (Album) -> Unit = { album ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.add_to_playlist_pop_up }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.playlist_item_play -> {
                        viewModel.playSongs(album.songs.toTypedArray())
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

            actionImageButton.setupPopupMenuOnClick(
                requireContext(),
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
            icon.apply {
                setOnClickListener {
                    actionPopUpMenu(album)
                }
                setImageResource(R.drawable.album_item_icon)
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
                R.layout.album_rv_item, parent, false
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

        private val pathTextView: TextView =
            itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton =
            itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout =
            itemView.findViewById(R.id.general_action_frame)
        private val icon: ImageButton =
            itemView.findViewById(R.id.general_item_icon)

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

            actionImageButton.setupAndShowPopupMenuOnClick(
                requireContext(),
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
            icon.apply {
                setOnClickListener {
                    actionPopUpMenu(playlist)
                }
                setImageResource(R.drawable.playlist)
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
                R.layout.album_rv_item, parent, false
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
