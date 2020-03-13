package velord.university.ui.fragment.addToPlaylist

import android.content.Context
import android.os.Bundle
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
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.permission.PermissionChecker
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.ui.backPressed.BackPressedHandlerFirst
import velord.university.ui.fragment.actionBar.ActionBarFragment
import java.io.File

class SelectSongFragment : ActionBarFragment(),
    BackPressedHandlerFirst {
    //Required interface for hosting activities
    interface Callbacks {
        fun onAddToPlaylistFromAddSongFragment()
    }
    private var callbacks: Callbacks? =  null

    override val TAG: String = "AddSongFragment"

    companion object {
        fun newInstance() = SelectSongFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(SelectSongViewModel::class.java)
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var rv: RecyclerView
    private lateinit var selectAllButton: Button
    private lateinit var continueButton: Button

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
            super.observeSearchQuery()
            setupAdapter()
        }
    }

    private fun initViews(view: View) {
        super.initActionBar(view)
        initSelectAll(view)
        initContinue(view)
        initRV(view)
    }

    private fun initSelectAll(view: View) {
        selectAllButton = view.findViewById(R.id.add_song_select_all)
        selectAllButton.setOnClickListener {
            scope.launch {
                val checkedAll = viewModel.checked.size == viewModel.fileList.size
                viewModel.checked.clear()
                if (checkedAll.not())
                    viewModel.fileList.forEach {
                        viewModel.checked += it.path
                    }

                withContext(Dispatchers.Main) {
                    updateAdapterBySearchQuery(viewModel.currentQuery)
                }
            }
        }
    }

    private fun initContinue(view: View) {
        continueButton = view.findViewById(R.id.add_song_continue)
        continueButton.setOnClickListener {
            callbacks?.let {
                if (viewModel.checked.isNotEmpty()) {
                    SongPlaylistInteractor.songs =
                        viewModel.checked.map { File(it) }.toTypedArray()
                    it.onAddToPlaylistFromAddSongFragment()
                }
                else Toast.makeText(requireContext(),
                        "Choose anyone song", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.general_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(rv, 50, -5)
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return true
    }
    // action bar overriding
    override val actionBarObserveSearchQuery: (String) -> Unit = { searchTerm ->
        //store search term in shared preferences
        viewModel.currentQuery = searchTerm
        //update files list
        updateAdapterBySearchQuery(searchTerm)
    }
    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.folder_sort_by_name -> {
                SortByPreference.setSortBySelectSongFragment(requireContext(), 0)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.folder_sort_by_artist -> {
                SortByPreference.setSortBySelectSongFragment(requireContext(), 1)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.folder_sort_by_date_added -> {
                SortByPreference.setSortBySelectSongFragment(requireContext(), 2)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true                        }
            R.id.folder_sort_by_ascending_order -> {
                SortByPreference.setAscDescSelectSongFragment(requireContext(), 0)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            R.id.folder_sort_by_descending_order -> {
                SortByPreference.setAscDescSelectSongFragment(requireContext(), 1)
                updateAdapterBySearchQuery(viewModel.currentQuery)
                super.rearwardActionButton()
                true
            }
            else -> {
                false
            }
        }
    }
    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.sort_by
    }
    override val actionBarPopUpMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }
    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = "Choose Song"
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = {
        it.apply {
            setOnClickListener {
                onBackPressed()
            }
            setImageResource(R.drawable.back_green)
        }
    }
    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {
        //set up checked item
        val menuItem = it.menu

        val nameArtistDateOrder =
            SortByPreference.getSortBySelectSongFragment(requireContext())
        when(nameArtistDateOrder) {
            0 -> { menuItem.getItem(0).isChecked = true }
            1 -> { menuItem.getItem(1).isChecked = true }
            2 -> { menuItem.getItem(2).isChecked = true }
        }

        val ascDescOrder = SortByPreference.getAscDescSelectSongFragment(requireContext())
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
                    if (fileCheckBox.isChecked)
                        viewModel.checked -= file.path
                    else
                        viewModel.checked += file.path
                    fileCheckBox.isChecked = !(fileCheckBox.isChecked)
                }
            }
            pathTextView.setOnClickListener {
                if (fileCheckBox.isChecked)
                    viewModel.checked -= file.path
                else
                    viewModel.checked += file.path
                fileCheckBox.isChecked = !(fileCheckBox.isChecked)
            }

            fileCheckBox.setOnClickListener {
                //this is strange but it's right behaviour
                if (fileCheckBox.isChecked)
                    viewModel.checked += file.path
                else
                    viewModel. checked -= file.path
            }
        }

        fun bindItem(file: File, position: Int) {
            setOnClickAndImageResource(file)
            pathTextView.text = FileNameParser.removeExtension(file)

            if (file.path in  viewModel.checked)
                fileCheckBox.isChecked = true
            else
                fileCheckBox.isChecked = false
        }
    }

    private inner class FileAdapter(val items: Array<out File>):
        RecyclerView.Adapter<FileHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.add_song_item, parent, false
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