package velord.university.ui.fragment.folder

import android.content.Context
import android.os.Bundle
import android.os.Environment
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
import velord.university.R
import velord.university.application.permission.PermissionChecker
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileExtension
import velord.university.model.FileExtensionModifier
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.ui.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.actionBar.ActionBarFragment
import velord.university.ui.util.setupPopupMenuOnClick
import java.io.File


class FolderFragment : ActionBarFragment(), BackPressedHandlerZero {
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
        ViewModelProviders.of(this).get(FolderViewModel::class.java)
    }

    private lateinit var rv: RecyclerView
    private lateinit var currentFolderTextView: TextView

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.action_folder_add_to_home_screen -> {
                TODO()
            }
            R.id.action_folder_show_incompatible_files -> {
                TODO()
            }
            R.id.action_folder_sort_by -> {
                val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                val initActionMenuLayout = { R.menu.sort_by }
                val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                    when (it.itemId) {
                        R.id.folder_sort_by_name -> {
                            SortByPreference.setNameArtistDateAddedFolderFragment(requireContext(), 0)

                            updateAdapterBySearchQuery(viewModel.currentQuery)

                            super.rearwardActionButton()
                            true
                        }
                        R.id.folder_sort_by_artist -> {
                            SortByPreference.setNameArtistDateAddedFolderFragment(requireContext(), 1)

                            updateAdapterBySearchQuery(viewModel.currentQuery)

                            super.rearwardActionButton()
                            true
                        }
                        R.id.folder_sort_by_date_added -> {
                            SortByPreference.setNameArtistDateAddedFolderFragment(requireContext(), 2)

                            updateAdapterBySearchQuery(viewModel.currentQuery)

                            super.rearwardActionButton()
                            true                        }
                        R.id.folder_sort_by_ascending_order -> {
                            SortByPreference.setAscDescFolderFragment(requireContext(), 0)

                            updateAdapterBySearchQuery(viewModel.currentQuery)

                            super.rearwardActionButton()
                            true
                        }
                        R.id.folder_sort_by_descending_order -> {
                            SortByPreference.setAscDescFolderFragment(requireContext(), 1)

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

                setupPopupMenuOnClick(
                    requireContext(),
                    super.actionButton,
                    initActionMenuStyle,
                    initActionMenuLayout,
                    initActionMenuItemClickListener
                ).also {
                    //set up checked item
                    val menuItem = it.menu

                    val nameArtistDateOrder =
                        SortByPreference.getNameArtistDateAddedFolderFragment(requireContext())
                    when(nameArtistDateOrder) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                        2 -> { menuItem.getItem(2).isChecked = true }
                    }

                    val ascDescOrder = SortByPreference.getAscDescFolderFragment(requireContext())
                    when(ascDescOrder) {
                        0 -> { menuItem.getItem(3).isChecked = true }
                        1 -> { menuItem.getItem(4).isChecked = true }
                        else -> {}
                    }
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
        it.text = "Find Audio"
    }

    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.action_bar_folder_pop_up
    }

    override val actionBarPopUpMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }

    override val actionBarLeftMenu: (ImageButton) -> Unit = {  }

    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {}

    override val actionBarObserveSearchQuery: (String) -> Unit = { searchQuery ->
        //-1 is default value, just ignore it
        if (searchQuery != "-1") {
            //store search term in shared preferences
            viewModel.storeCurrentFolderSearchQuery(searchQuery)
            //update files list
            updateAdapterBySearchQuery(searchQuery)
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
            else
                Toast.makeText(requireContext(), "No one Song", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun openCreatePlaylistFragment(songs: Array<File>) {
        callbacks?.let {
            if (songs.isNotEmpty()) {
                SongPlaylistInteractor.songs = songs
                it.onCreatePlaylist()
            }
            else
                Toast.makeText(requireContext(), "No one Song", Toast.LENGTH_SHORT)
                    .show()
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
        val songs = viewModel.filterAndSortFiles(
            FileFilter.filterBySearchQuery, viewModel.currentQuery)
        openAddToPlaylistFragment(songs)
    }

    private fun openCreatePlaylistFragmentByQuery() {
        val songs = viewModel.filterAndSortFiles(
            FileFilter.filterBySearchQuery, viewModel.currentQuery)
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
        super.setOnScrollListenerBasedOnRecyclerViewScrolling(rv, 50, -5)
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        fun _setupAdapter(file: File = Environment.getExternalStorageDirectory(),
                            //default filter
                          filter: (File, String) -> Boolean = FileFilter.filterByEmptySearchQuery
        ) {
            //while permission is not granted
            if (checkPermission().not()) _setupAdapter(file, filter)
            //now do everything to setup adapter
            changeCurrentTextView(file)
            //apply all filters to recycler view
            viewModel.fileList =
                viewModel.filterAndSortFiles(filter, searchQuery)
            rv.adapter = FileAdapter(viewModel.fileList)
        }

        if (searchQuery.isNotEmpty()) {
            val f = FileFilter.filterBySearchQuery
            _setupAdapter(viewModel.currentFolder, f)
        }
        else _setupAdapter(viewModel.currentFolder)
    }

    private fun setupAdapter(file: File) {
        viewModel.currentFolder = file
        super.viewModelActionBar.setupSearchQueryByFilePath(file)
    }

    private fun checkPermission(): Boolean =
        PermissionChecker
                .checkThenRequestReadWriteExternalStoragePermission(
                    requireContext(), requireActivity())

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

        private val fileIconImageButton: ImageButton = itemView.findViewById(R.id.folder_item_icon)
        private val pathTextView: TextView = itemView.findViewById(R.id.folder_item_path)
        private val fileActionImageButton: ImageButton = itemView.findViewById(R.id.item_action)
        private val fileActionFrame: FrameLayout = itemView.findViewById(R.id.item_action_frame)

        private fun setOnClickAndImageResource(file: File) {
            when(FileExtension.checkCompatibleFileExtension(file)) {
                FileExtensionModifier.DIRECTORY -> {
                    val action = { setupAdapter(file) }
                    val popUpAction = {
                        val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                        val initActionMenuLayout = { R.menu.folder_recycler_item_is_folder_pop_up }
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
                            fileActionImageButton,
                            initActionMenuStyle,
                            initActionMenuLayout,
                            initActionMenuItemClickListener
                        )

                        Unit
                    }

                    setOnClick(action, popUpAction)
                    fileIconImageButton.setImageResource(R.drawable.extension_file_folder)
                }
                FileExtensionModifier.AUDIO -> {
                    val action = { viewModel.playAudioFile(file) }
                    val popUpAction = {
                        val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                        val initActionMenuLayout = { R.menu.folder_recycler_item_is_audio_pop_up }
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
                                    callbacks?.let {
                                        SongPlaylistInteractor.songs = arrayOf(file)
                                        it.onAddToPlaylistFromFolderFragment()
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
                            fileActionImageButton,
                            initActionMenuStyle,
                            initActionMenuLayout,
                            initActionMenuItemClickListener
                        )

                        Unit
                    }
                    setOnClick(action, popUpAction)
                    fileIconImageButton.setImageResource(R.drawable.extension_file_song)
                }
                FileExtensionModifier.NOTCOMPATIBLE -> {
                    fileIconImageButton.setImageResource(R.drawable.extension_file_not_important)
                }
            }
        }

        private fun setOnClick(f: () -> Unit, popUpF: () -> Unit) {
            itemView.setOnClickListener {
                f()
            }
            fileIconImageButton.setOnClickListener {
                f()
            }
            pathTextView.setOnClickListener {
                f()
            }
            fileActionImageButton.setOnClickListener {
                popUpF()
            }
            fileActionFrame.setOnClickListener {
                popUpF()
            }
        }

        fun bindItem(file: File, position: Int) {
            setOnClickAndImageResource(file)
            pathTextView.text = FileNameParser.removeExtension(file)
        }
    }

    private inner class FileAdapter(val items: Array<out File>):
        RecyclerView.Adapter<FileHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.folder_fragment_item, parent, false
            )

            return FileHolder(view)
        }

        override fun onBindViewHolder(holder: FileHolder, position: Int) {
            items[position].apply {
                holder.bindItem(this, position)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }
}
