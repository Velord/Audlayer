package velord.university.ui.fragment.addToPlaylist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.R
import velord.university.application.permission.PermissionChecker
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.ui.BackPressedHandlerFirst
import velord.university.ui.fragment.actionBar.ActionBarFragment
import java.io.File

class AddSongFragment : ActionBarFragment(), BackPressedHandlerFirst {

    //Required interface for hosting activities
    interface Callbacks {
        fun addSongOnBackPressed()
    }

    private var callbacks: Callbacks? =  null

    override val TAG: String = "AddSongFragment"

    companion object {
        fun newInstance() = AddSongFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AddSongViewModel::class.java)
    }

    private lateinit var rv: RecyclerView

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
        return inflater.inflate(R.layout.add_song_fragment, container, false).apply {
            initViews(this)
            //observe changes in search view
            super.observeSearchTerm()
            setupAdapter()
        }
    }

    private fun initViews(view: View) {
        super.initActionBar(view)
        initRV(view)
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.current_folder_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        super.setOnScrollListenerBasedOnRecyclerViewScrolling(rv, 50, -5)
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        callbacks?.addSongOnBackPressed()
        return true
    }

    // action bar ovveriding
    override val observeSearchTerm: (String) -> Unit = { searchTerm ->
        //store search term in shared preferences
        viewModel.currentQuery = searchTerm
        //update files list
        updateAdapterBySearchQuery(searchTerm)
    }
    override val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.folder_sort_by_name -> {
                SortByPreference.setNameArtistDateAddedSongAddFragment(requireContext(), 0)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.folder_sort_by_artist -> {
                SortByPreference.setNameArtistDateAddedSongAddFragment(requireContext(), 1)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.folder_sort_by_date_added -> {
                SortByPreference.setNameArtistDateAddedSongAddFragment(requireContext(), 2)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true                        }
            R.id.folder_sort_by_ascending_order -> {
                SortByPreference.setAscDescSongAddFragment(requireContext(), 0)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.folder_sort_by_descending_order -> {
                SortByPreference.setAscDescSongAddFragment(requireContext(), 1)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            else -> {
                false
            }
        }
    }
    override val initActionMenuLayout: () -> Int = {
        R.menu.sort_by
    }
    override val initActionMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }
    override val initHintTextView: (TextView) -> Unit = {
        it.text = "Choose Song"
    }
    override val initLeftMenu: (ImageButton) -> Unit = {
        it.apply {
            setOnClickListener {
                onBackPressed()
            }
            setImageResource(R.drawable.back_green)
        }
    }
    override val initPopUpMenuOnActionButton: (PopupMenu) -> Unit = {
        //set up checked item
        val menuItem = it.menu

        val nameArtistDateOrder =
            SortByPreference.getNameArtistDateAddedSongAddFragment(requireContext())
        when(nameArtistDateOrder) {
            0 -> { menuItem.getItem(0).isChecked = true }
            1 -> { menuItem.getItem(1).isChecked = true }
            2 -> { menuItem.getItem(2).isChecked = true }
        }

        val ascDescOrder = SortByPreference.getAscDescSongAddFragment(requireContext())
        when(ascDescOrder) {
            0 -> { menuItem.getItem(3).isChecked = true }
            1 -> { menuItem.getItem(4).isChecked = true }
            else -> {}
        }
    }

    private fun updateAdapterBySearchQuery(searchTerm: String) {
        fun _setupAdapter( //default filter
            filter: (File, String) -> Boolean = FileFilter.filterByEmptySearchQuery
        ) {
            //while permission is not granted
            if (checkPermission().not()) _setupAdapter(filter)
            //apply all filters to recycler view
            val filteredAndSortered =
                viewModel.filterAndSortFiles(requireContext(), filter, searchTerm)
            rv.adapter = FileAdapter(filteredAndSortered)
        }

        if (searchTerm.isNotEmpty()) {
            val f = FileFilter.filterBySearchQuery
            _setupAdapter(f)
        }
        else _setupAdapter()
    }

    private fun checkPermission(): Boolean =
        PermissionChecker
            .checkThenRequestReadWriteExternalStoragePermission(
                requireContext(), requireActivity())

    private fun setupAdapter() {
        viewModel.fileList = SongPlaylistInteractor.songs
        updateAdapterBySearchQuery("")
    }

    private inner class FileHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val fileIconImageButton: ImageButton = itemView.findViewById(R.id.add_song_item_icon)
        private val pathTextView: TextView = itemView.findViewById(R.id.add_song_item_path)
        private val fileCheckBox: CheckBox = itemView.findViewById(R.id.add_song_item_checkBox)

        private fun setOnClickAndImageResource(file: File) {
            fileIconImageButton.apply {
                setImageResource(R.drawable.extension_file_song)
                setOnClickListener {
                    fileCheckBox.isChecked = !(fileCheckBox.isChecked)
                }
            }
            pathTextView.setOnClickListener {
                fileCheckBox.isChecked = !(fileCheckBox.isChecked)
            }

            fileCheckBox.setOnClickListener {
                fileCheckBox.isChecked = !(fileCheckBox.isChecked)
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
                R.layout.add_song_fragment_item, parent, false
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