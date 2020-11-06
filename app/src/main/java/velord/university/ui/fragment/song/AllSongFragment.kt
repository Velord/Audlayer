package velord.university.ui.fragment.song

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.statuscasellc.statuscase.model.coroutine.getScope
import com.statuscasellc.statuscase.model.coroutine.onMain
import com.statuscasellc.statuscase.model.exception.ViewDestroyed
import com.statuscasellc.statuscase.ui.util.view.setupAndShowPopupMenuOnClick
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.MiniPlayerIconClickReceiver
import velord.university.application.broadcast.behaviour.SongPathReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.application.settings.SortByPreference
import velord.university.databinding.ActionBarSearchBinding
import velord.university.databinding.AllSongFragmentBinding
import velord.university.databinding.GeneralRvBinding
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.converter.SongBitrate
import velord.university.model.converter.roundOfDecimalToUp
import velord.university.model.entity.Playlist
import velord.university.model.entity.music.Song
import velord.university.model.file.FileFilter
import velord.university.model.file.FileNameParser
import velord.university.ui.fragment.actionBar.ActionBarSearch
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.RVSelection

class AllSongFragment :
    ActionBarSearch(),
    SongPathReceiver,
    MiniPlayerIconClickReceiver {

    override val TAG: String = "AllSongFragment"

    //Required interface for hosting activities
    interface Callbacks {
        fun onAddToPlaylistFromSongFragment()
    }

    private var callbacks: Callbacks? =  null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null

        scope.cancel()
    }

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, IntentFilter(it.second), PERM_PRIVATE_MINI_PLAYER
                )
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    companion object {
        fun newInstance() = AllSongFragment()
    }

    private val scope = getScope()

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AllSongViewModel::class.java)
    }

    private val receivers = songPathReceiverList() +
            getIconClickedReceiverList()

    override val songPathF: (Intent?) -> Unit = { nullableIntent ->
            nullableIntent?.apply {
                val extra = AppBroadcastHub.Extra.songPathUI
                val songPath = getStringExtra(extra)!!
                scope.launch {
                    changeRVItem(songPath)
                }
            }
    }

    override val iconClicked: (Intent?) -> Unit = {
        it?.apply {
            scope.launch {
                viewModel.rvResolver.scroll(bindingRv.fastScrollRv)
            }
        }
    }

    private tailrec suspend fun changeRVItem(songPath: String) {
        if (viewModel.rvResolverIsInitialized()) {
            viewModel.rvResolver.apply {
                val song = viewModel.songs.find {
                    it.file.absolutePath == songPath
                } ?: return

                clearAndChangeSelectedItem(song)
                //apply to ui
                val songList = viewModel.ordered
                val containF: (Song) -> Boolean = {
                    it == song
                }
                refreshAndScroll(songList, bindingRv.fastScrollRv, containF)
                //send new icon
                //this covers case when app is launch
                viewModel.sendIconToMiniPlayer(song)
            }
            return
        } else {
            delay(500)
            changeRVItem(songPath)
        }
    }

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = { it ->
        when (it.itemId) {
            R.id.song_fragment_add_to_home_screen -> {
                TODO()
            }
            R.id.song_fragment_shuffle -> {
                updateAdapterWithShuffled()
                super.rearwardActionButton()
                true
            }
            R.id.song_fragment_sort_by -> {
                val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                val initActionMenuLayout = { R.menu.song_fragment_sort_by }
                val initActionMenuItemClickListener: (MenuItem) -> Boolean = { menuItem ->
                    when (menuItem.itemId) {
                        R.id.song_sort_by_name -> sortBy(0)
                        R.id.song_sort_by_artist -> sortBy(1)
                        R.id.song_sort_by_date_added -> sortBy(2)
                        R.id.song_sort_by_duration -> sortBy(3)
                        R.id.song_sort_by_size -> sortBy(4)
                        R.id.song_sort_by_ascending_order -> sortByAscDesc(0)
                        R.id.song_sort_by_descending_order -> sortByAscDesc(1)
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
                        SortByPreference(requireContext()).sortByAllSongFragment
                    when(sortBy) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                        2 -> { menuItem.getItem(2).isChecked = true }
                        3 -> { menuItem.getItem(3).isChecked = true }
                        4 -> { menuItem.getItem(4).isChecked = true }
                    }

                    val ascDescOrder =
                        SortByPreference(requireContext()).ascDescAllSongFragment
                    when(ascDescOrder) {
                        0 -> { menuItem.getItem(5).isChecked = true }
                        1 -> { menuItem.getItem(6).isChecked = true }
                    }
                }

                super.bindingActionBar.action.setupAndShowPopupMenuOnClick(
                    requireContext(),
                    initActionMenuStyle,
                    initActionMenuLayout,
                    initActionMenuItemClickListener
                ).also {
                    actionBarPopUpMenu(it)
                }
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
        if (searchQuery != "-1") {
            //store search term in shared preferences
            viewModel.storeSearchQuery(searchQuery)
            //update files list
            updateAdapterBySearchQuery(searchQuery)
        }
    }
    override val actionBarPopUp: (ImageButton) -> Unit = { }
    override val actionSearchView: (SearchView) -> Unit = {  }

    //view
    private var _binding: AllSongFragmentBinding? = null
    override var _bindingActionBar: ActionBarSearchBinding? = null
    private var _bindingRv: GeneralRvBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")
    private val bindingRv get() = _bindingRv ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.all_song_fragment,
        container, false).run {
        //bind
        _binding = AllSongFragmentBinding.bind(this)
        _bindingActionBar = binding.actionBarInclude
        _bindingRv = binding.rvInclude
        scope.launch {
            viewModel
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
        bindingRv.fastScrollRv.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(
            bindingRv.fastScrollRv, 50, -5
        )
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference(requireContext()).sortByAllSongFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference(requireContext()).ascDescAllSongFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun setupAdapter() {
        val query = viewModel.getSearchQuery()
        super.viewModelActionBarSearch.setupSearchQuery(query)
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        if (viewModel.songsIsInitialized()) {
            scope.launch {
                val songsFiltered =
                    viewModel.filterByQuery(searchQuery).toTypedArray()
                withContext(Dispatchers.Main) {
                    bindingRv.fastScrollRv.adapter = SongAdapter(songsFiltered)
                }
            }
        }
    }

    private fun updateAdapterWithShuffled() {
        if (viewModel.songsIsInitialized()) {
            val shuffled = viewModel.shuffle()
            bindingRv.fastScrollRv.adapter = SongAdapter(shuffled)
        }
    }

    private fun getRecyclerViewResolver(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ): RVSelection<Song> {
        return if (viewModel.rvResolverIsInitialized()) {
            viewModel.rvResolver.adapter = adapter
            viewModel.rvResolver
        }
        //new
        else {
            viewModel.rvResolver = RVSelection(adapter, 0)
            viewModel.rvResolver
        }
    }

    private inner class SongHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val text: TextView = itemView.findViewById(R.id.general_item_path)
        private val action: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val frame: FrameLayout = itemView.findViewById(R.id.general_action_frame)
        private val icon: ImageButton = itemView.findViewById(R.id.general_item_icon)

        val selected:  (Song) -> Array<() -> Unit> = { song ->
            arrayOf(
                {
                    icon.setImageResource(R.drawable.song_item_playing)
                },
                {
                    val album = Playlist.whichPlaylist(
                        viewModel.allPlaylist, song.file.path)

                    val size: Double = roundOfDecimalToUp(
                            (FileFilter.getSize(song.file).toDouble() / 1024)
                    )

                    val bitrate = SongBitrate.getKbpsString(song.file)

                    text.text = getString(
                        R.string.song_rv_item,
                        FileNameParser.removeExtension(song.file),
                        size.toString(),
                        album,
                        bitrate
                    )
                },
                {
                    itemView.setBackgroundResource(R.color.fragmentBackgroundOpacity)
                }
            )
        }

        val notSelected: (Song) -> Array<() -> Unit> = { song ->
            arrayOf(
                {
                    icon.setImageResource(R.drawable.song_item_black)
                },
                {
                    text.text = FileNameParser.removeExtension(song.file)
                },
                {
                    itemView.setBackgroundResource(R.color.opacity)
                }
            )
        }

        private fun playSong(song: Song) =
            viewModel.playAudioAndAllSong(song)

        private val actionPopUpMenu: (Song) -> Unit = { song ->
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

            action.setupAndShowPopupMenuOnClick(
                requireContext(),
                initActionMenuStyle,
                initActionMenuLayout,
                initActionMenuItemClickListener
            )
            Unit
        }

        private fun setOnClickAndImageResource(song: Song,
                                               rvSelectResolver: RVSelection<Song>) {
            itemView.setOnClickListener {
                scope.launch {
                    withContext(Dispatchers.Main) {
                        rvSelectResolver.singleSelectionPrinciple(song)
                        playSong(song)
                    }
                }
            }
            text.setOnClickListener {
                scope.launch {
                    withContext(Dispatchers.Main) {
                        rvSelectResolver.singleSelectionPrinciple(song)
                        playSong(song)

                    }
                }
            }
            setIconView(song, rvSelectResolver)
            action.setOnClickListener {
                actionPopUpMenu(song)
            }
            frame.setOnClickListener {
                actionPopUpMenu(song)
            }

        }

        private fun setIconView(song: Song,
                                rvSelectResolver: RVSelection<Song>) {
            icon.setOnClickListener {
                scope.launch {
                    withContext(Dispatchers.Main) {
                        rvSelectResolver.singleSelectionPrinciple(song)
                        playSong(song)
                    }
                }
            }

            DrawableIcon.loadSongIconDrawable(
                requireContext(), icon, song.icon)
        }

        private fun applyState(value: Song,
                               rvSelectResolver: RVSelection<Song>) {
            when(rvSelectResolver.state) {
                0 -> rvSelectResolver.isContains(
                    value,
                    selected,
                    notSelected
                )
            }
        }

        fun bindItem(song: Song, position: Int,
                     rvSelectResolver: RVSelection<Song>) {
            applyState(song, rvSelectResolver)
            setOnClickAndImageResource(song, rvSelectResolver)
        }
    }

    private inner class SongAdapter(val items: Array<out Song>):
        RecyclerView.Adapter<SongHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        private val rvSelectResolver = getRecyclerViewResolver(
            this as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): SongHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )
            return SongHolder(view)
        }

        override fun onBindViewHolder(holder: SongHolder,
                                      position: Int) {
            items[position].apply {
                holder.bindItem(this, position, rvSelectResolver)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].file.name[0]}"
    }
}
