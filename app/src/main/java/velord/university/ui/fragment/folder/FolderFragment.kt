package velord.university.ui.fragment.folder

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.R
import velord.university.application.broadcast.*
import velord.university.application.permission.PermissionChecker
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileExtension
import velord.university.model.FileExtensionModifier
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.ui.fragment.BackPressedHandler
import velord.university.ui.fragment.actionBar.ActionBarFragment
import java.io.File


class FolderFragment : ActionBarFragment(), BackPressedHandler {
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
            initViews(this)
            //observe changes in search view
            super.observeSearchTerm()
            setupAdapter(viewModel.currentFolder)
        }
    }

    override val initActionPopUpMenuItemClickListener: (MenuItem) -> Boolean = {
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

                velord.university.ui.setupPopupMenuOnClick(
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
                openAddToPlaylistFragment(viewModel.currentFolder)
                true
            }
            R.id.action_folder_create_playlist -> {
                openCreatePlaylistFragment(viewModel.currentFolder)
                true
            }
            else -> {
                false
            }
        }
    }

    override val initHintTextView: (TextView) -> Unit = {
        it.text = "Find Audio"
    }

    override val initActionPopUpMenuLayout: () -> Int = {
        R.menu.action_bar_folder_pop_up
    }

    override val initActionPopUpMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }

    override val initLeftMenu: (ImageButton) -> Unit = {  }

    override val initPopUpMenuOnActionButton: (PopupMenu) -> Unit = { }

    override val observeSearchTerm: (String) -> Unit = { searchTerm ->
        //store search term in shared preferences
        viewModel.storeCurrentFolderSearchQuery(searchTerm)
        //update files list
        updateAdapterBySearchQuery(searchTerm)
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")

        val newFile =  viewModel.currentFolder.parentFile!!
        setupAdapter(newFile)
        return true
    }

    private fun openAddToPlaylistFragment(file: File) {
        callbacks?.let {
            val songs = FileFilter
                .filterOnlyAudio(file)
                .toTypedArray()

            if (songs.isNotEmpty()) {
                SongPlaylistInteractor.songs = songs
                it.onAddToPlaylist()
            }
            else
                Toast.makeText(requireContext(), "No one Song", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun openCreatePlaylistFragment(file: File) {
        callbacks?.let {
            val songs = FileFilter
                .filterOnlyAudio(file)
                .map { it.path }
                .toTypedArray()

            if (songs.isNotEmpty()) {
                SongPlaylistInteractor.songsPath = songs
                it.onCreatePlaylist()
            }
            else
                Toast.makeText(requireContext(), "No one Song", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun initViews(view: View) {
        super.initActionBar(view)
        initRV(view)
        currentFolderTextView = view.findViewById(R.id.current_folder_textView)
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.current_folder_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        super.setOnScrollListenerBasedOnRecyclerViewScrolling(rv, 50, -5)
    }

    private fun updateAdapterBySearchQuery(searchTerm: String) {
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
                viewModel.filterAndSortFiles(requireContext(), filter, searchTerm)
            rv.adapter = FileAdapter(viewModel.fileList)
        }

        if (searchTerm.isNotEmpty()) {
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
        private val fileActionImageButton: ImageButton = itemView.findViewById(R.id.folder_item_action)

        init {
            fileActionImageButton.setImageResource(R.drawable.action_folder_item)
        }

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
                                    //don't remember for SongQueryInteractor
                                    MiniPlayerBroadcastPlayAllInFolder.apply {
                                        requireContext().sendBroadcastPlayAllInFolder(file.path)
                                    }
                                    MiniPlayerBroadcastLoopAll.apply {
                                        requireContext().sendBroadcastLoopAll()
                                    }
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_play_next -> {
                                    //add to queue
                                    MiniPlayerBroadcastPlayNextAllInFolder.apply {
                                        requireContext().sendBroadcastPlayNextAllInFolder(file.path)
                                    }
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_add_to_playlist -> {
                                    openAddToPlaylistFragment(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_create_playlist -> {
                                    openCreatePlaylistFragment(file)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_shuffle -> {
                                    MiniPlayerBroadcastShuffleAndPlayAllInFolder.apply {
                                        requireContext().sendBroadcastShuffleAndPlayAllInFolder(file.path)
                                    }
                                    MiniPlayerBroadcastLoopAll.apply {
                                        requireContext().sendBroadcastLoopAll()
                                    }
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

                        velord.university.ui.setupPopupMenuOnClick(
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
                                    //don't remember for SongQueryInteractor it will be used between this and service
                                    SongPlaylistInteractor.songs = arrayOf(file)
                                    MiniPlayerBroadcastPlayByPath.apply {
                                        requireContext().sendBroadcastPlayByPath(file.path)
                                    }
                                    MiniPlayerBroadcastLoop.apply {
                                        requireContext().sendBroadcastLoop()
                                    }
                                    true
                                }
                                R.id.folder_recyclerView_item_isAudio_play_next -> {
                                    //don't remember for SongQueryInteractor it will be used between this and service
                                    SongPlaylistInteractor.songs = arrayOf(file)
                                    //add to queue one song
                                    MiniPlayerBroadcastAddToQueue.apply {
                                        requireContext().sendBroadcastAddToQueue(file.path)
                                    }
                                    true
                                }
                                R.id.folder_recyclerView_item_isAudio_add_to_playlist -> {
                                    callbacks?.let {
                                        SongPlaylistInteractor.songsPath = arrayOf(file.path)
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

                        velord.university.ui.setupPopupMenuOnClick(
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
