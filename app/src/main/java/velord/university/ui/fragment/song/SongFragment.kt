package velord.university.ui.fragment.song

import android.content.Context
import android.os.Bundle
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
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileNameParser
import velord.university.ui.fragment.actionBar.ActionBarFragment
import velord.university.ui.util.RecyclerViewSelectItemResolver
import velord.university.ui.util.setupPopupMenuOnClick
import java.io.File


class SongFragment : ActionBarFragment() {
    //Required interface for hosting activities
    interface Callbacks {
        fun onAddToPlaylistFromSongFragment()
    }
    private var callbacks: Callbacks? =  null

    override val TAG: String = "SongFragment"

    companion object {
        fun newInstance() = SongFragment()
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(SongViewModel::class.java)
    }

    private lateinit var rv: RecyclerView

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.song_fragment_add_to_home_screen -> {
                TODO()
            }
            R.id.song_fragment_shuffle -> {
                updateAdapterWithShuffled()
                super.rearwardActionButton()
                true
            }
            R.id.action_folder_sort_by -> {
                val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                val initActionMenuLayout = { R.menu.song_fragment_sort_by }
                val initActionMenuItemClickListener: (MenuItem) -> Boolean = { menuItem ->
                    when (menuItem.itemId) {
                        R.id.song_sort_by_name -> {
                            SortByPreference.setSortBySongFragment(requireContext(), 0)
                            updateAdapterBySearchQuery(viewModel.currentQuery)
                            super.rearwardActionButton()
                            true
                        }
                        R.id.song_sort_by_artist -> {
                            SortByPreference.setSortBySongFragment(requireContext(), 1)
                            updateAdapterBySearchQuery(viewModel.currentQuery)
                            super.rearwardActionButton()
                            true
                        }
                        R.id.song_sort_by_date_added -> {
                            SortByPreference.setSortBySongFragment(requireContext(), 2)
                            updateAdapterBySearchQuery(viewModel.currentQuery)
                            super.rearwardActionButton()
                            true                        }
                        R.id.song_sort_by_duration -> {
                            SortByPreference.setSortBySongFragment(requireContext(), 3)
                            updateAdapterBySearchQuery(viewModel.currentQuery)
                            super.rearwardActionButton()
                            true
                        }
                        R.id.song_sort_by_size -> {
                            SortByPreference.setSortBySongFragment(requireContext(), 4)
                            updateAdapterBySearchQuery(viewModel.currentQuery)
                            super.rearwardActionButton()
                            true
                        }
                        R.id.song_sort_by_ascending_order -> {
                            SortByPreference.setAscDescSongFragment(requireContext(), 0)
                            updateAdapterBySearchQuery(viewModel.currentQuery)
                            super.rearwardActionButton()
                            true
                        }
                        R.id.song_sort_by_descending_order -> {
                            SortByPreference.setAscDescSongFragment(requireContext(), 1)
                            updateAdapterBySearchQuery(viewModel.currentQuery)
                            super.rearwardActionButton()
                            true
                        }
                        else -> {
                            super.rearwardActionButton()
                            false
                        }
                    }
                }
                val actionBarPopUpMenu: (PopupMenu) -> Unit = { menu ->
                    //set up checked item
                    val menuItem = menu.menu

                    val sortBy =
                        SortByPreference.getSortBySongFragment(requireContext())
                    when(sortBy) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                        2 -> { menuItem.getItem(2).isChecked = true }
                        3 -> { menuItem.getItem(3).isChecked = true }
                        4 -> { menuItem.getItem(4).isChecked = true }
                    }

                    val ascDescOrder =
                        SortByPreference.getAscDescSongFragment(requireContext())
                    when(ascDescOrder) {
                        0 -> { menuItem.getItem(5).isChecked = true }
                        1 -> { menuItem.getItem(6).isChecked = true }
                    }
                }

                setupPopupMenuOnClick(
                    requireContext(),
                    super.actionButton,
                    initActionMenuStyle,
                    initActionMenuLayout,
                    initActionMenuItemClickListener
                ).also {
                    actionBarPopUpMenu(it)
                }
                //invoke immediately popup menu
                super.actionButton.callOnClick()
                true
            }
            else -> false
        }
    }

    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = getString(R.string.action_bar_hint_song)
    }

    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.song_fragment_pop_up
    }

    override val actionBarPopUpMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }

    override val actionBarLeftMenu: (ImageButton) -> Unit = {  }

    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {  }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.song_fragment, container, false).apply {
            scope.launch {
                viewModel.retrieveSongsFromDb()
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
        rv = view.findViewById(R.id.general_RecyclerView)

        rv.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(rv, 50, -5)
    }

    private fun setupAdapter() {
        val query = viewModel.getSearchQuery()
        super.viewModelActionBar.setupSearchQuery(query)
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        if (viewModel.songsIsInitialized()) {
            scope.launch {
                val songsFiltered =
                    viewModel.filterByQuery(searchQuery).toTypedArray()
                withContext(Dispatchers.Main) {
                    rv.adapter = SongAdapter(songsFiltered)
                }
            }
        }
    }

    private fun updateAdapterWithShuffled() {
        if (viewModel.songsIsInitialized()) {
            val shuffled = viewModel.shuffle().toTypedArray()
            rv.adapter = SongAdapter(shuffled)
        }
    }

    private inner class SongHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        val text: TextView = itemView.findViewById(R.id.general_item_path)
        private val action: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val frame: FrameLayout = itemView.findViewById(R.id.general_action_frame)
        val icon: ImageButton = itemView.findViewById(R.id.general_item_icon)

        private fun playSong(song: File) {
            viewModel.playAudioAndAllSong(song)
        }

        private val actionPopUpMenu: (File) -> Unit = { song ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.folder_item_is_audio_pop_up }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.folder_recyclerView_item_isAudio_play -> {
                        viewModel.playAudio(song)
                        true
                    }
                    R.id.folder_recyclerView_item_isAudio_play_next -> {
                        viewModel.playAudioNext(song)
                        true
                    }
                    R.id.folder_recyclerView_item_isAudio_add_to_playlist -> {
                        callbacks?.let { callback ->
                            SongPlaylistInteractor.songs = arrayOf(song)
                            callback.onAddToPlaylistFromSongFragment()
                        }
                        true
                    }
                    R.id.folder_recyclerView_item_isAudio_set_as_ringtone -> {
                        TODO()
                    }
                    R.id.folder_recyclerView_item_isAudio_add_to_home_screen -> {
                        TODO()
                    }
                    else -> {
                        false
                    }
                }
            }

            setupPopupMenuOnClick(
                requireContext(),
                action,
                initActionMenuStyle,
                initActionMenuLayout,
                initActionMenuItemClickListener
            )
            Unit
        }

        private fun setOnClickAndImageResource(song: File, f: (Int) -> Unit) {
            itemView.setOnClickListener {
                f(0)
                playSong(song)
            }
            text.setOnClickListener {
                f(1)
                playSong(song)
            }
            icon.setOnClickListener {
                f(2)
            }
            action.setOnClickListener {
                actionPopUpMenu(song)
            }
            frame.setOnClickListener {
                actionPopUpMenu(song)
            }
        }

        fun bindItem(song: File, position: Int,
                     f: (View, Int, Int) -> (Int) -> Unit) {
            val setBackground = f(itemView, R.color.fragmentBackgroundDarkerOpacity, R.color.opacity)
            setOnClickAndImageResource(song, setBackground)
            text.text = FileNameParser.removeExtension(song)
        }
    }

    private inner class SongAdapter(val items: Array<out File>):
        RecyclerView.Adapter<SongHolder>(),  FastScrollRecyclerView.SectionedAdapter{

        private val rvSelectResolver =
            //just change old adapter to new
            if (viewModel.rvResolverIsInitialized()) {
                viewModel.rvResolver.adapter = this as RecyclerView.Adapter<RecyclerView.ViewHolder>
                viewModel.rvResolver
            }
            //new
            else {
                viewModel.rvResolver = RecyclerViewSelectItemResolver(
                    this as RecyclerView.Adapter<RecyclerView.ViewHolder>, 3, ""
                )
                viewModel.rvResolver
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )
            return SongHolder(view)
        }

        override fun onBindViewHolder(holder: SongHolder, position: Int) {
            items[position].apply {
                val f = rvSelectResolver.resolver(this.path)
                holder.bindItem(this, position, f)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }
}
