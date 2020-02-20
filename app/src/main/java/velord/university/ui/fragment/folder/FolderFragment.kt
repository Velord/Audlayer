package velord.university.ui.fragment.folder

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.R
import velord.university.application.PermissionChecker
import velord.university.application.QueryPreferences
import velord.university.model.FileExtension
import velord.university.model.FileExtensionModifier
import velord.university.model.FileNameParser
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.ui.fragment.BackPressedHandler
import velord.university.ui.fragment.actionBar.ActionBarFragment
import java.io.File
import java.util.*


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
    private lateinit var currentFolder: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.folder_fragment, container, false).apply {
            initViews(this)
            setupAdapterBySearchQuery(currentFolder)
            //observe changes in search view
            observeSearchTerm()
        }
    }

    private fun initViews(view: View) {
        initActionBar(view)
        initRV(view)
        initCurrentFolder(view)
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.current_folder_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        setOnScrollListenerBasedOnRecyclerViewScrolling(rv, 50, -5)
    }

    private fun initCurrentFolder(view: View) {
        //setup current folder
        currentFolder = Environment.getExternalStorageDirectory()
        currentFolderTextView = view.findViewById(R.id.current_folder_textView)
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")

        val path = currentFolder.path
        currentFolder = File(path).parentFile!!
        setupAdapterBySearchQuery(currentFolder)
        return true
    }

    override fun observeSearchTerm() {
        super.viewModelActionBar.mutableSearchTerm.observe(
            viewLifecycleOwner,
            Observer { searchTerm ->
                //update files list
                updateAdapterBySearchQuery(searchTerm)
                //store search term in shared preferences
                val folderPath = currentFolder.path
                QueryPreferences.setStoredQueryFolder(requireContext(), folderPath, searchTerm)
            }
        )
    }

    private fun updateAdapterBySearchQuery(searchTerm: String) {
        fun setupAdapter(file: File = Environment.getExternalStorageDirectory(),
                        //default filter
                         filter: (File) -> Boolean = {
                             FileExtension.checkCompatibleFileExtension(it) !=
                                     FileExtensionModifier.NOTCOMPATIBLE
                         }
        ) {
            //while permission is not granted
            if (checkPermission().not()) setupAdapter()
            //now do everything to setup adapter
            changeCurrentTextView(file)
            val filesInFolder = getFilesInCurrentFolder()
            //if you would see not compatible format
            //just remove or comment 2 lines bottom
            val compatibleFileFormat =
                filesInFolder.filter { filter(it) }

            rv.adapter = FileAdapter(compatibleFileFormat.toTypedArray())
        }

        if (searchTerm.isNotEmpty()) {
            val f: (File) -> Boolean = {
                val extension =
                    FileExtension.checkCompatibleFileExtension(it) !=
                        FileExtensionModifier.NOTCOMPATIBLE
                val contQuery =
                    FileNameParser.removeExtension(it)
                    .substringAfterLast('/')
                    .toUpperCase(Locale.ROOT)
                    .contains(searchTerm.toUpperCase(Locale.ROOT))

                extension && contQuery
            }
            setupAdapter(currentFolder, f)
        }
        else setupAdapter(currentFolder)
    }

    private fun setupAdapterBySearchQuery(file: File) {
        currentFolder = file
        val searchTerm =
            QueryPreferences.getStoredQueryFolder(requireContext(), currentFolder.path)
        //invoke search view
        if (searchTerm.isNotBlank()) {
            super.searchView.setQuery(searchTerm, true)
        }

    }

    private fun getFilesInCurrentFolder(): Array<File> {
        val path = currentFolder.path
        val file = File(path)
        val filesInFolder = file.listFiles()
        return filesInFolder ?: arrayOf()
    }

    private fun checkPermission(): Boolean =
        PermissionChecker
                .checkThenRequestReadWriteExternalStoragePermission(
                    this.requireContext(), this.requireActivity())

    private fun changeCurrentTextView(file: File) {
        val pathToUI = FileNameParser.slashReplaceArrow(file.path)
        currentFolderTextView.text = pathToUI
    }

    private fun playAudioFile(file: File) {
        MiniPlayerBroadcastPlayByPath.apply {
            requireActivity()
                .sendBroadcastPlayByPath(PERM_PRIVATE_MINI_PLAYER, file.absolutePath)
        }
    }

    fun focusOnMe(): Boolean {
        val path = currentFolder.path
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

        private fun setOnClick(file: File) {
            when(FileExtension.checkCompatibleFileExtension(file)) {
                FileExtensionModifier.DIRECTORY -> {
                    itemView.setOnClickListener {
                        setupAdapterBySearchQuery(file)
                    }
                    fileIconImageButton.setOnClickListener {
                        setupAdapterBySearchQuery(file)
                    }
                    pathTextView.setOnClickListener {
                        setupAdapterBySearchQuery(file)
                    }
                    fileActionImageButton.setOnClickListener {}
                }
                FileExtensionModifier.AUDIO -> {
                    itemView.setOnClickListener {
                        playAudioFile(file)
                    }
                    fileIconImageButton.setOnClickListener {
                        playAudioFile(file)
                    }
                    pathTextView.setOnClickListener {
                        playAudioFile(file)
                    }
                    fileActionImageButton.setOnClickListener {}
                }
                FileExtensionModifier.NOTCOMPATIBLE -> {}
            }
        }

        fun bindItem(file: File, position: Int) {
            setOnClick(file)
            pathTextView.text = FileNameParser.removeExtension(file)


            when(FileExtension.checkCompatibleFileExtension(file)) {
                FileExtensionModifier.DIRECTORY ->
                    fileIconImageButton.setImageResource(R.drawable.extension_file_folder)
                FileExtensionModifier.AUDIO ->
                    fileIconImageButton.setImageResource(R.drawable.extension_file_song)
                FileExtensionModifier.NOTCOMPATIBLE ->
                    fileIconImageButton.setImageResource(R.drawable.extension_file_not_important)
            }
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
