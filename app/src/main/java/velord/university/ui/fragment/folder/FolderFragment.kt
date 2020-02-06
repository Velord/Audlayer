package velord.university.ui.fragment.folder

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import velord.university.R
import velord.university.model.FileExtension
import velord.university.model.FileExtension.checkCompatibleFileExtension
import velord.university.model.FileExtensionModifier
import velord.university.ui.fragment.BackPressedHandler
import velord.university.ui.fragment.LoggerSelfLifecycleFragment
import velord.university.util.PermissionChecker
import java.io.File


class FolderFragment : LoggerSelfLifecycleFragment(), BackPressedHandler {

    override val TAG: String
        get() = "FolderFragment"

    companion object {
        fun newInstance() = FolderFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(FolderViewModel::class.java)
    }

    private lateinit var rv: RecyclerView
    private lateinit var currentFolder: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.folder_fragment, container, false).apply {
            initViews(this)
            setupAdapter()
        }
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")

        setupAdapter(File(currentFolder.text.toString()).parentFile!!.path)

        return true
    }

    fun focusOnMe(): Boolean =
        if (currentFolder.text == Environment.getExternalStorageDirectory().path)
            false
        else true

    private fun initViews(view: View) {
        rv = view.findViewById(R.id.current_folder_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)

        currentFolder = view.findViewById(R.id.current_folder_textView)
    }
    
    private fun setupAdapter(path: String? = null) {
        fun setupAdapter_(startupFolder: File) {
            currentFolder.text = "${startupFolder.path}"

            val filesInFolder = startupFolder.listFiles()

            if (filesInFolder ==  null)
                rv.adapter = FileAdapter(arrayOf())
            else {
                //if you would see not compatible format just remove or comment 2 lines bottom
                val compatibleFileFormat = filesInFolder.filterNot{
                    checkCompatibleFileExtension(it) == FileExtensionModifier.NOTCOMPATIBLE
                }

                rv.adapter = FileAdapter(compatibleFileFormat.toTypedArray())
            }
        }

        val permissionGranted =
            PermissionChecker
                .checkThenRequestReadWriteExternalStoragePermission(
                    this.requireContext(), this.requireActivity())
        if (permissionGranted.not()) setupAdapter()

        path?.also {
            val startupFolder = File(it)
            setupAdapter_(startupFolder)
            return
        }
        //default folder
        val startupFolder = Environment.getExternalStorageDirectory()
        setupAdapter_(startupFolder)
    }

    private fun playAudioFile(file: File) {

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
        RecyclerView.Adapter<FileHolder>() {

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
    }
}
