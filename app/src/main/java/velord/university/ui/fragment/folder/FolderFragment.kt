package velord.university.ui.fragment.folder

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.R
import velord.university.application.broadcast.*
import velord.university.application.permission.PermissionChecker
import velord.university.interactor.SongQueryInteractor
import velord.university.model.FileExtension
import velord.university.model.FileExtensionModifier
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.ui.fragment.BackPressedHandler
import velord.university.ui.fragment.actionBar.ActionBarFragment
import java.io.File


class FolderFragment : ActionBarFragment(), BackPressedHandler {

    override val TAG: String = "FolderFragment"

    companion object {
        fun newInstance() = FolderFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(FolderViewModel::class.java)
    }

    private lateinit var rv: RecyclerView
    private lateinit var currentFolderTextView: TextView

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

    override val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.action_folder_add_to_home_screen -> {
                TODO()
            }
            R.id.action_folder_show_incompatible_files -> {
                TODO()
            }
            else -> {
                false
            }
        }
    }

    override val initActionMenuLayout: () -> Int = {
        R.menu.action_bar_folder_pop_up
    }

    override val initActionMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")

        val newFile =  viewModel.currentFolder.parentFile!!
        setupAdapter(newFile)
        return true
    }

    override val observeSearchTerm: (String) -> Unit = { searchTerm ->
        //store search term in shared preferences
        viewModel.storeCurrentFolderSearchQuery(searchTerm)
        //update files list
        updateAdapterBySearchQuery(searchTerm)
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
            val filesInFolder = viewModel.getFilesInCurrentFolder()
            //if you would see not compatible format
            //just remove or comment 2 lines bottom
            val compatibleFileFormat =
                filesInFolder.filter { filter(it, searchTerm) }

            viewModel.fileList = compatibleFileFormat.toTypedArray()
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
                    this.requireContext(), this.requireActivity())

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
                                    TODO()
                                }
                                R.id.folder_recyclerView_item_isFolder_create_playlist -> {
                                    TODO()
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

                        velord.university.ui.initActionButton(
                            requireContext(),
                            fileActionImageButton,
                            initActionMenuStyle,
                            initActionMenuLayout,
                            initActionMenuItemClickListener
                        )
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
                                    SongQueryInteractor.songs = arrayOf(file)
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
                                    SongQueryInteractor.songs = arrayOf(file)
                                    //add to queue one song
                                    MiniPlayerBroadcastAddToQueue.apply {
                                        requireContext().sendBroadcastAddToQueue(file.path)
                                    }
                                    true
                                }
                                R.id.folder_recyclerView_item_isAudio_add_to_playlist -> {
                                    TODO()
                                }
                                R.id.folder_recyclerView_item_isAudio_create_playlist -> {
                                    TODO()
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

                        velord.university.ui.initActionButton(
                            requireContext(),
                            fileActionImageButton,
                            initActionMenuStyle,
                            initActionMenuLayout,
                            initActionMenuItemClickListener
                        )
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
