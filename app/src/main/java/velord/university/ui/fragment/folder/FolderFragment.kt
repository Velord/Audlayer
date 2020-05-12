package velord.university.ui.fragment.folder

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.R
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.application.broadcast.behaviour.SongReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileExtension
import velord.university.model.FileExtensionModifier
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.model.converter.roundOfDecimalToUp
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.actionBar.ActionBarFragment
import velord.university.ui.util.RecyclerViewSelectItemResolver
import velord.university.ui.util.setupPopupMenuOnClick
import java.io.File

class FolderFragment : ActionBarFragment(),
    BackPressedHandlerZero,
    SongReceiver {
    //Required interface for hosting activities
    interface Callbacks {
        fun onCreatePlaylist()

        fun onAddToPlaylist()

        fun onAddToPlaylistFromFolderFragment()
    }
    private var callbacks: Callbacks? =  null

    override val TAG: String = "FolderFragment"

    companion object {
        fun newInstance() = FolderFragment()
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(FolderViewModel::class.java)
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var rv: RecyclerView
    private lateinit var currentFolderTextView: TextView

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = { it ->
        when (it.itemId) {
            R.id.action_folder_add_to_home_screen -> {
                TODO()
            }
            R.id.action_folder_show_incompatible_files -> {
                TODO()
            }
            R.id.song_fragment_sort_by -> {
                val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                val initActionMenuLayout = { R.menu.general_sort_by }
                val initActionMenuItemClickListener: (MenuItem) -> Boolean = { menuItem ->
                    when (menuItem.itemId) {
                        R.id.sort_by_name -> sortBy(0)
                        R.id.sort_by_artist -> sortBy(1)
                        R.id.sort_by_date_added -> sortBy(2)
                        R.id.sort_by_ascending_order -> sortByAscDesc(0)
                        R.id.sort_by_descending_order -> sortByAscDesc(1)
                        else -> {
                            super.rearwardActionButton()
                            false
                        }
                    }
                }
                val actionBarPopUpMenu: (PopupMenu) -> Unit = { popUpMenu ->
                    //set up checked item
                    val menuItem = popUpMenu.menu

                    val nameArtistDateOrder =
                        SortByPreference.getSortByFolderFragment(requireContext())
                    when(nameArtistDateOrder) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                        2 -> { menuItem.getItem(2).isChecked = true }
                    }

                    when(SortByPreference.getAscDescFolderFragment(requireContext())) {
                        0 -> { menuItem.getItem(3).isChecked = true }
                        1 -> { menuItem.getItem(4).isChecked = true }
                        else -> {}
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
            R.id.action_folder_add_to_playlist -> {
                openAddToPlaylistFragmentByQuery()
                true
            }
            R.id.action_folder_create_playlist -> {
                openCreatePlaylistFragmentByQuery()
                true
            }
            else -> {
                false
            }
        }
    }
    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = getString(R.string.action_bar_hint_folder)
    }
    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.folder_fragment_pop_up
    }
    override val actionBarPopUpMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = {  }
    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {}
    override val actionBarObserveSearchQuery: (String) -> Unit = { searchQuery ->
        //-1 is default value, just ignore it
        val correctQuery =
            if (searchQuery == "-1") ""
            else searchQuery
        //store search term in shared preferences
        viewModel.storeCurrentFolderSearchQuery(correctQuery)
        //update files list
        updateAdapterBySearchQuery(correctQuery)
    }
    override val actionBarPopUp: (ImageButton) -> Unit = { }

    private val receivers = receiverList()

    override val songPathF: (Intent?) -> Unit =
        { nullableIntent ->
            nullableIntent?.apply {
                val extra = AppBroadcastHub.Extra.songPathUI
                val songPath = getStringExtra(extra)
                scope.launch {
                    changeRVItem(songPath)
                }
            }
        }

    private tailrec suspend fun changeRVItem(songPath: String) {
        if (viewModel.rvResolverIsInitialized()) {
            viewModel.rvResolver.apply {
                userChangeItem(songPath)
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
    ): View? {
        return inflater.inflate(R.layout.folder_fragment, container, false).apply {
            //init action bar
            super.initActionBar(this)
            super.changeUIAfterSubmitTextInSearchView(super.searchView)
            //init self view
            initViews(this)
            //observe changes in search view
            super.observeSearchQuery()
            setupAdapter(viewModel.currentFolder)
        }
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")

        val newFile =  viewModel.currentFolder.parentFile!!
        setupAdapter(newFile)
        return true
    }

    private fun openAddToPlaylistFragment(songs: Array<File>) {
        callbacks?.let {
            if (songs.isNotEmpty()) {
                SongPlaylistInteractor.songs = songs
                it.onAddToPlaylist()
            }
            else Toast.makeText(requireContext(),
                    "No one Song", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCreatePlaylistFragment(songs: Array<File>) {
        callbacks?.let {
            if (songs.isNotEmpty()) {
                SongPlaylistInteractor.songs = songs
                it.onCreatePlaylist()
            }
            else Toast.makeText(requireContext(),
                    "No one Song", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAddToPlaylistFragmentByFolder(file: File) {
        val songs = viewModel.onlyAudio(file)
        openAddToPlaylistFragment(songs)
    }

    private fun openCreatePlaylistFragmentByFolder(file: File) {
        val songs = viewModel.onlyAudio(file)
        openCreatePlaylistFragment(songs)
    }

    private fun openAddToPlaylistFragmentByQuery() {
        val files = viewModel.filterAndSortFiles(
            FileFilter.filterFileBySearchQuery, viewModel.currentQuery)
        val audio = FileFilter.filterOnlyAudio(files).toTypedArray()
        openAddToPlaylistFragment(audio)
    }

    private fun openCreatePlaylistFragmentByQuery() {
        val songs = viewModel.filterAndSortFiles(
            FileFilter.filterFileBySearchQuery, viewModel.currentQuery)
        openCreatePlaylistFragment(songs)
    }

    private fun initViews(view: View) {
        initRV(view)
        currentFolderTextView = view.findViewById(R.id.current_folder_textView)
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.general_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(rv, 50, -5)
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference.setSortByFolderFragment(requireContext(), index)
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference.setAscDescFolderFragment(requireContext(), index)
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        fun setupAdapter(file: File = Environment.getExternalStorageDirectory(),
                            //default filter
                          filter: (File, String) -> Boolean = FileFilter.filterByEmptySearchQuery
        ) {
            //now do everything to setup adapter
            changeCurrentTextView(file)
            //apply all filters to recycler view
            viewModel.fileList =
                viewModel.filterAndSortFiles(filter, searchQuery)
            rv.adapter = FileAdapter(viewModel.fileList)
        }

        if (searchQuery.isNotEmpty()) {
            val f = FileFilter.filterFileBySearchQuery
            setupAdapter(viewModel.currentFolder, f)
        }
        else setupAdapter(viewModel.currentFolder)
    }

    private fun setupAdapter(file: File) {
        viewModel.currentFolder = file
        val query = viewModel.getSearchQuery()
        super.viewModelActionBar.setupSearchQuery(query)
    }

    private fun changeCurrentTextView(file: File) {
        val pathToUI = FileNameParser.slashReplaceArrow(file.path)
        currentFolderTextView.text = pathToUI
    }

    fun focusOnMe(): Boolean {
        val path = viewModel.currentFolder.path
        return if (path == Environment.getExternalStorageDirectory().path)
            false
        else {
            //hide searchView
            super.changeUIAfterSubmitTextInSearchView(super.searchView)
            true
        }
    }

    private inner class FileHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val icon: ImageButton = itemView.findViewById(R.id.general_item_icon)
        private val path: TextView = itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout = itemView.findViewById(R.id.general_action_frame)

        private fun setOnClickAndImageResource(file: File, fSelect: (Int) -> Unit) {
            when(FileExtension.getFileExtension(file)) {
                FileExtensionModifier.DIRECTORY -> {
                    val action = { setupAdapter(file) }
                    val popUpAction = {
                        val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                        val initActionMenuLayout = { R.menu.folder_item_is_folder_pop_up }
                        val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                            when (it.itemId) {
                                R.id.folder_recyclerView_item_isFolder_play -> {
                                    viewModel.playAllInFolder(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_play_next -> {
                                    viewModel.playAllInFolderNext(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_add_to_playlist -> {
                                    openAddToPlaylistFragmentByFolder(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_create_playlist -> {
                                    openCreatePlaylistFragmentByFolder(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_shuffle -> {
                                    viewModel.shuffleAndPlayAllInFolder(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_add_to_home_screen -> {
                                    TODO()
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
                    setOnClick(action, popUpAction) {  }
                    icon.setImageResource(R.drawable.extension_file_folder)
                }
                FileExtensionModifier.AUDIO -> {
                    val action = { viewModel.playAudioFile(file) }
                    val popUpAction = {
                        val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                        val initActionMenuLayout = { R.menu.folder_item_is_audio_pop_up }
                        val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                            when (it.itemId) {
                                R.id.folder_recyclerView_item_isAudio_play -> {
                                    viewModel.playAudio(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isAudio_play_next -> {
                                    viewModel.playAudioNext(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isAudio_add_to_playlist -> {
                                    callbacks?.let { callback ->
                                        SongPlaylistInteractor.songs = arrayOf(file)
                                        callback.onAddToPlaylistFromFolderFragment()
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
                            actionImageButton,
                            initActionMenuStyle,
                            initActionMenuLayout,
                            initActionMenuItemClickListener
                        )

                        Unit
                    }
                    setOnClick(action, popUpAction, fSelect)
                }
                FileExtensionModifier.NOT_COMPATIBLE -> {
                    icon.setImageResource(R.drawable.extension_file_not_important)
                }
            }
        }

        private fun setOnClick(f: () -> Unit, popUpF: () -> Unit, fSelect: (Int) -> Unit) {
            itemView.setOnClickListener {
                f()
                fSelect(0)
            }
            icon.setOnClickListener {
                f()
                fSelect(2)
            }
            path.setOnClickListener {
                f()
                fSelect(1)
            }
            actionImageButton.setOnClickListener {
                popUpF()
            }
            actionFrame.setOnClickListener {
                popUpF()
            }
        }

        private val selected:  (File) -> Array<() -> Unit> = { file ->
            arrayOf(
                {
                    val size: Double =
                        roundOfDecimalToUp((FileFilter.getSize(file).toDouble() / 1024))
                    path.text = getString(
                        R.string.folder_fragment_rv_item,
                        FileNameParser.removeExtension(file),
                        size.toString()
                    )
                },
                {
                    itemView.setBackgroundResource(R.color.fragmentBackgroundOpacity)
                },
                {
                    icon.setImageResource(R.drawable.song_item_playing)

                }
            )
        }

        private val notSelected:  (File) -> Array<() -> Unit> = { file ->
            arrayOf(
                {
                    path.text = FileNameParser.removeExtension(file)
                },
                {
                    itemView.setBackgroundResource(R.color.opacity)
                },
                {
                    icon.setImageResource(R.drawable.extension_file_song_purple)

                }
            )
        }

        fun bindItem(file: File, position: Int,
                     f: (Array<() -> Unit>) -> (Array<() -> Unit>) -> (Int) -> Unit) {
            val setBackground = f(selected(file))(notSelected(file))
            setOnClickAndImageResource(file, setBackground)
        }
    }

    private inner class FileAdapter(val items: Array<out File>):
        RecyclerView.Adapter<FileHolder>(),  FastScrollRecyclerView.SectionedAdapter {

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )

            return FileHolder(view)
        }

        override fun onBindViewHolder(holder: FileHolder, position: Int) {
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
