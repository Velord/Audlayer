package velord.university.ui.fragment.vk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.*
import velord.university.application.settings.SortByPreference
import velord.university.application.settings.VkPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.model.converter.roundOfDecimalToUp
import velord.university.model.entity.vk.VkSong
import velord.university.repository.fetch.SefonFetch
import velord.university.ui.activity.VkLoginActivity
import velord.university.ui.fragment.actionBar.ActionBarFragment
import velord.university.ui.util.RecyclerViewSelectItemResolver
import velord.university.ui.util.setupPopupMenuOnClick
import java.io.File


class VKFragment : ActionBarFragment(), SongBroadcastReceiver {
    //Required interface for hosting activities
    interface Callbacks {
        fun onAddToPlaylistFromVkFragment()
    }
    private var callbacks: Callbacks? =  null

    override val TAG: String = "VKFragment"

    companion object {
        fun newInstance() = VKFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(VkViewModel::class.java)
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var rv: RecyclerView
    private lateinit var login: Button
    private lateinit var pb: ProgressBar
    private lateinit var webView: WebView

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
                        R.id.song_sort_by_name -> sortBy(0)
                        R.id.song_sort_by_artist -> sortBy(1)
                        R.id.song_sort_by_date_added ->sortBy(2)
                        R.id.song_sort_by_duration ->sortBy(3)
                        R.id.song_sort_by_size -> sortBy(4)

                        R.id.song_sort_by_ascending_order -> {
                            sortByAscDesc(0)
                        }
                        R.id.song_sort_by_descending_order -> {
                            sortByAscDesc(1)
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
                        SortByPreference.getSortByVkFragment(requireContext())
                    when(sortBy) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                        2 -> { menuItem.getItem(2).isChecked = true }
                        3 -> { menuItem.getItem(3).isChecked = true }
                        4 -> { menuItem.getItem(4).isChecked = true }
                    }

                    val ascDescOrder =
                        SortByPreference.getAscDescVkFragment(requireContext())
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
        it.text = getString(R.string.action_bar_hint_vk)
    }
    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.song_fragment_pop_up
    }
    override val actionBarPopUpMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.action_bar_settings_gold)
    }
    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {  }
    override val actionBarPopUp: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.action_bar_pop_up_gold)
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

    override val songPathF: (Intent?) -> Unit = { nullableIntent ->
            nullableIntent?.apply {
                val extra = MiniPlayerBroadcastSongPath.extraValueUI
                val songPath = FileNameParser
                    .removeExtension(File(getStringExtra(extra)))
                scope.launch {
                    changeRVItem(songPath)
                }
            }
        }

    private tailrec suspend fun changeRVItem(songPath: String) {
        if (viewModel.rvResolverIsInitialized()) {
            viewModel.rvResolver.apply {
                userChangeSong(songPath)
                //apply to ui
                val files = viewModel.ordered.map { it.path }
                val containF: (String) -> Boolean = {
                    it == songPath
                }
                applyToRvItem(files, rv, containF)
            }
            return
        } else changeRVItem(songPath)
    }

    private val receivers = arrayOf(
        Pair(songPath(), MiniPlayerBroadcastSongPath.filterUI)
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, it.second, PERM_PRIVATE_MINI_PLAYER
                )
        }

        scope.launch {  checkToken() }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.vk_fragment, container, false).apply {
            scope.launch {
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
                checkToken()
            }
        }
    }

    private suspend fun checkToken() {
        if (::login.isInitialized) {
            val token = VkPreference.getAccessToken(requireContext())
            if (token.isBlank()) {
                withContext(Dispatchers.Main) {
                    login.visibility = View.VISIBLE
                    Toast.makeText(requireContext(),
                        "Login to continue", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    login.visibility = View.GONE
                    pb.visibility = View.VISIBLE
                }
                viewModel.refreshByToken()
                withContext(Dispatchers.Main) {
                    pb.visibility = View.GONE
                }
                updateAdapterBySearchQuery(viewModel.currentQuery)
            }
        }

    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference.setSortByVkFragment(requireContext(), index)
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference.setAscDescVkFragment(requireContext(), index)
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun initViews(view: View) {
        pb = view.findViewById(R.id.vk_pb)
        webView = view.findViewById(R.id.web_view)
        initRV(view)
        initLogin(view)
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.general_RecyclerView)
        rv.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(rv, 50, -5)
    }

    private fun initLogin(view: View) {
        login = view.findViewById(R.id.vk_login)
        login.setOnClickListener {
            val intent = VkLoginActivity
                .newIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun setupAdapter() {
        val query = viewModel.getSearchQuery()
        super.viewModelActionBar.setupSearchQuery(query)
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        if (viewModel.vkPlaylistIsInitialized()) {
            scope.launch {
                val songsFiltered = viewModel.filterByQuery(searchQuery)
                withContext(Dispatchers.Main) {
                    rv.adapter = SongAdapter(songsFiltered.toTypedArray())
                }
            }
        }
    }

    private fun updateAdapterWithShuffled() {
        if (viewModel.vkPlaylistIsInitialized()) {
            val shuffled = viewModel.vkPlaylist
                .toList()
                .shuffled()
                .toTypedArray()
            rv.adapter = SongAdapter(shuffled)
        }
    }

    private fun playAll(vkSong: VkSong) {
        scope.launch {
            if (viewModel.needDownload(vkSong)) {
                val ifDownload: (File) -> Unit = {
                    scope.launch {
                        val index = viewModel.vkPlaylist.indexOf(vkSong)
                        viewModel.applyNewPath(index, it)
                        viewModel.playAudioAndAllSong(vkSong)
                    }
                }
                SefonFetch.download(requireContext(), webView, vkSong, ifDownload)
            }
            else viewModel.playAudioAndAllSong(vkSong)
        }
    }

    private inner class VkHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val text: TextView = itemView.findViewById(R.id.general_item_path)
        private val action: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val frame: FrameLayout = itemView.findViewById(R.id.general_action_frame)
        private val icon: ImageButton = itemView.findViewById(R.id.general_item_icon)

        val selected: (VkSong) -> Array<() -> Unit> = { song ->
            arrayOf(
                {
                    icon.setImageResource(R.drawable.song_item_playing)
                },
                {
                    val size: Double =
                        roundOfDecimalToUp((FileFilter.getSize(File(song.path)).toDouble() / 1024))
                    val album = viewModel.vkAlbums.find {
                        it.id == song.albumId
                    }?.title?.let { "Album: $it" } ?: ""
                    text.text = "${song.artist} - ${song.title}\n$album Mb: $size"
                },
                {
                    itemView.setBackgroundResource(R.color.fragmentBackgroundOpacity)
                }
            )
        }

        val notSelected: (VkSong) -> Array<() -> Unit> = { song ->
            arrayOf(
                {
                    icon.setImageResource(R.drawable.song_item)
                },
                {
                    val album = viewModel.vkAlbums.find {
                        it.id == song.albumId
                    }?.title?.let { "Album: $it" } ?: ""
                    text.text = "${song.artist} - ${song.title}\n$album"
                },
                {
                    itemView.setBackgroundResource(R.color.opacity)
                }
            )
        }

        private val actionPopUpMenu: (VkSong) -> Unit = { song ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.folder_item_is_audio_pop_up }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.folder_recyclerView_item_isAudio_play -> {
                        viewModel.playAudioAndAllSong(song)
                        true
                    }
                    R.id.folder_recyclerView_item_isAudio_play_next -> {
                        viewModel.playAudioNext(song)
                        true
                    }
                    R.id.folder_recyclerView_item_isAudio_add_to_playlist -> {
                        callbacks?.let { callback ->
                            SongPlaylistInteractor.songs = arrayOf(File(song.path))
                            callback.onAddToPlaylistFromVkFragment()
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

        private fun setOnClickAndImageResource(song: VkSong, f: (Int) -> Unit) {
            itemView.setOnClickListener {
                f(0)
                playAll(song)
            }
            text.setOnClickListener {
                f(1)
                playAll(song)
            }
            icon.setOnClickListener {
                f(2)
                playAll(song)
            }
            action.setOnClickListener {
                actionPopUpMenu(song)
            }
            frame.setOnClickListener {
                actionPopUpMenu(song)
            }
        }

        fun bindItem(song: VkSong, position: Int,
                     f: (Array<() -> Unit>) -> (Array<() -> Unit>) -> (Int) -> Unit) {
            val setBackground = f(selected(song))(notSelected(song))
            setOnClickAndImageResource(song, setBackground)
        }
    }

    private inner class SongAdapter(val items: Array<out VkSong>):
        RecyclerView.Adapter<VkHolder>(),  FastScrollRecyclerView.SectionedAdapter{

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VkHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )
            return VkHolder(view)
        }

        override fun onBindViewHolder(holder: VkHolder, position: Int) {
            items[position].apply {
                val f = rvSelectResolver.resolver("${this.artist} - ${this.title}")
                holder.bindItem(this, position, f)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].title[0]}"
    }
}
