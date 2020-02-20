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
import velord.university.model.FileExtension
import velord.university.model.FileExtensionModifier
import velord.university.model.FileNameParser
import velord.university.model.miniPlayer.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.model.miniPlayer.broadcast.PERM_PRIVATE_MINI_PLAYER
import velord.university.ui.fragment.ActionBarFragment
import velord.university.ui.fragment.BackPressedHandler
import velord.university.util.PermissionChecker
import java.io.File


class FolderFragment : ActionBarFragment(), BackPressedHandler {

    override val TAG: String
        get() = "FolderFragment"

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
            setupAdapter()
            //observe changes in search view
            observeSearchTerm()
        }
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")

        val path = currentFolder.path
        setupAdapter(File(path).parentFile!!.path)

        return true
    }

    override fun observeSearchTerm() {
        super.viewModelActionBar.mutableSearchTerm.observe(
            viewLifecycleOwner,
            Observer { searchTerm ->
                if (searchTerm.isNotEmpty()) {
                    val f: (File) -> Boolean = {
                        it.path.toUpperCase().contains(searchTerm.toUpperCase()) &&
                                FileExtension.checkCompatibleFileExtension(it) !=
                                FileExtensionModifier.NOTCOMPATIBLE
                    }
                    setupAdapter(currentFolder.path, f)
                }
                else {
                    setupAdapter(currentFolder.path)
                }
            }
        )
    }

    private fun getFilesInCurrentFolder(): Array<File> {
        val path = currentFolder.path
        val file = File(path)
        val filesInFolder = file.listFiles()
        return filesInFolder ?: arrayOf()
    }

    fun focusOnMe(): Boolean {
        val path = currentFolder.path
        return if (path == Environment.getExternalStorageDirectory().path)
            false
        else true
    }

    private fun initViews(view: View) {
        initActionBar(view)

        rv = view.findViewById(R.id.current_folder_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        setOnScrollListenerBasedOnRecyclerViewScrolling(rv, 50, -5)

        currentFolderTextView = view.findViewById(R.id.current_folder_textView)
    }

    private fun checkPermisiiion(): Boolean =
        PermissionChecker
                .checkThenRequestReadWriteExternalStoragePermission(
                    this.requireContext(), this.requireActivity())

    private fun changeCurrentTextView(file: File) {
        val pathToUI = FileNameParser.slashReplaceArrow(file.path)
        currentFolderTextView.text = pathToUI
    }

    private fun setupAdapter(path: String? = null,
                             //default filter
                             filter: (File) -> Boolean = {
                                 FileExtension.checkCompatibleFileExtension(it) !=
                                         FileExtensionModifier.NOTCOMPATIBLE
                             }
    ) {
        fun setupAdapter_(file: File) {
            changeCurrentTextView(file)
            val filesInFolder = getFilesInCurrentFolder()
            //if you would see not compatible format
            //just remove or comment 2 lines bottom
            val compatibleFileFormat =
                filesInFolder.filter{ filter(it) }

            rv.adapter = FileAdapter(compatibleFileFormat.toTypedArray())

        }

        //while permission is not granted
        if (checkPermisiiion().not()) setupAdapter()
        //when we need handle arrived path
        path?.also {
            currentFolder = File(path)
            val startupFolder = File(it)
            setupAdapter_(startupFolder)
            return
        }
        //default folder
        val startupFolder = Environment.getExternalStorageDirectory()
        currentFolder = startupFolder
        //init adapter
        setupAdapter_(startupFolder)
    }

    private fun playAudioFile(file: File) {
        MiniPlayerBroadcastPlayByPath.apply {
            requireActivity()
                .sendBroadcastPlayByPath(PERM_PRIVATE_MINI_PLAYER, file.absolutePath)
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
                        setupAdapter(file.absolutePath)
                    }
                    fileIconImageButton.setOnClickListener {
                        setupAdapter(file.absolutePath)
                    }
                    pathTextView.setOnClickListener {
                        setupAdapter(file.absolutePath)
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
            pathTextView.text = file.name


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
