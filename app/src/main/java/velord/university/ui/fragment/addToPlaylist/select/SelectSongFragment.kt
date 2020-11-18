package velord.university.ui.fragment.addToPlaylist.select

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.util.activity.toastInfo
import velord.university.ui.util.view.makeCheck
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.settings.SortByPreference
import velord.university.databinding.ActionBarSearchBinding
import velord.university.databinding.GeneralRvBinding
import velord.university.databinding.SelectSongFragmentBinding
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.fileType.file.FileNameParser
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.ui.behaviour.backPressed.BackPressedHandlerFirst
import velord.university.ui.fragment.actionBar.ActionBarSearchFragment
import java.io.File

class SelectSongFragment :
    ActionBarSearchFragment(),
    BackPressedHandlerFirst {
    //Required interface for hosting activities
    interface Callbacks {

        fun openAddToPlaylistFragment()

        fun toZeroLevel()
    }
    private var callbacks: Callbacks? =  null

    override val TAG: String = "AddSongFragment"

    companion object {
        fun newInstance() = SelectSongFragment()
    }

    private val viewModel: SelectSongViewModel by viewModels()

    private val scope = getScope()
    //view
    private var _binding: SelectSongFragmentBinding? = null
    override var _bindingActionBar: ActionBarSearchBinding? = null
    private var _bindingRv: GeneralRvBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")
    private val bindingRv get() = _bindingRv ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    // action bar overriding
    override val actionBarObserveSearchQuery: (String) -> Unit = { searchQuery ->
        val correctQuery =
            if (searchQuery == "-1") ""
            else searchQuery
        viewModel.currentQuery = correctQuery
        //update files list
        updateAdapterBySearchQuery(correctQuery)
    }
    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.sort_by_name -> sortBy(0)
            R.id.sort_by_artist -> sortBy(1)
            R.id.sort_by_date_added -> sortBy(2)
            R.id.sort_by_ascending_order -> sortByAscDesc(0)
            R.id.sort_by_descending_order -> sortByAscDesc(1)
            else -> {
                false
            }
        }
    }
    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.general_sort_by
    }
    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = getString(R.string.action_bar_hint_select_song)
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = {
        it.apply {
            setOnClickListener {
                callbacks?.toZeroLevel()
            }
            setImageResource(R.drawable.back_arrow_deep_purple_a200)
        }
    }
    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {
        //set up checked item
        val menuItem = it.menu

        val nameArtistDateOrder =
            SortByPreference(requireContext()).sortBySelectSongFragment
        when(nameArtistDateOrder) {
            0 -> { menuItem.makeCheck(0) }
            1 -> { menuItem.makeCheck(1) }
            2 -> { menuItem.makeCheck(2) }
        }

        when(SortByPreference(requireContext()).ascDescSelectSongFragment) {
            0 -> { menuItem.makeCheck(3) }
            1 -> { menuItem.makeCheck(4) }
            else -> {}
        }
    }

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null

        scope.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.select_song_fragment,
        container, false).run {
        //bind
        _binding = SelectSongFragmentBinding.bind(this)
        _bindingActionBar = binding.actionBarInclude
        _bindingRv = binding.generalRvInclude
        scope.launch {
            onMain {
                //init action bar
                super.initActionBar()
                super.changeUIAfterSubmitTextInSearchView(
                    super.bindingActionBar.search
                )
                //init self view
                initView()
                //observe changes in search view
                super.observeSearchQuery()
                setupAdapter()
            }
        }
        binding.root
    }

    private fun initView() {
        super.initActionBar()
        initSelectAll()
        initContinue()
        initRV()
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference(requireContext()).sortBySelectSongFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference(requireContext()).ascDescSelectSongFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun initSelectAll() {
        binding.actionSelectAll.setOnClickListener {
            scope.launch {
                val checkedAll = viewModel.checked.size == viewModel.songList.size
                viewModel.checked.clear()
                if (checkedAll.not()) viewModel.songList.forEach {
                    viewModel.checked += it
                }

                onMain {
                    updateAdapterBySearchQuery(viewModel.currentQuery)
                }
            }
        }
    }

    private fun initContinue() {
        binding.actionContinue.setOnClickListener {
            callbacks?.let { it ->
                if (viewModel.checked.isNotEmpty()) {
                    SongPlaylistInteractor.songList = viewModel.songList

                    it.openAddToPlaylistFragment()
                }
                else requireActivity().toastInfo(
                    requireContext().getString(R.string.choose_anyone_song)
                )
            }
        }
    }

    private fun initRV() {
        bindingRv.fastScrollRv.layoutManager = LinearLayoutManager(activity)
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(
            bindingRv.fastScrollRv, 50, -5
        )
    }

    private fun updateAdapterBySearchQuery(searchTerm: String) {
        //while permission is not granted
        if (viewModel.checkPermission(requireActivity()).not()) setupAdapter()
        //apply all filters to recycler view
        val filteredAndSortered =
            viewModel.filterAndSortFiles(requireContext(), searchTerm)
        bindingRv.fastScrollRv.adapter = FileAdapter(filteredAndSortered.toTypedArray())
        setupAdapter()
    }

    private fun setupAdapter() {
        viewModel.songList = SongPlaylistInteractor.songList
        updateAdapterBySearchQuery("")
    }

    private inner class FileHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val fileIconImageButton: ImageButton =
            itemView.findViewById(R.id.add_song_item_icon)
        private val pathTextView: TextView =
            itemView.findViewById(R.id.add_song_item_path)
        private val fileCheckBox: CheckBox =
            itemView.findViewById(R.id.add_song_item_checkBox)

        private fun setOnClickAndImageResource(song: AudlayerSong) {
            fileIconImageButton.apply {
                setImageResource(R.drawable.select_song_icon)
                setOnClickListener {
                    if (fileCheckBox.isChecked) viewModel.checked -= song
                    else viewModel.checked += song
                    fileCheckBox.isChecked = !(fileCheckBox.isChecked)
                }
            }
            pathTextView.setOnClickListener {
                if (fileCheckBox.isChecked) viewModel.checked -= song
                else viewModel.checked += song
                fileCheckBox.isChecked = !(fileCheckBox.isChecked)
            }

            fileCheckBox.setOnClickListener {
                //this is strange but it's right behaviour
                if (fileCheckBox.isChecked) viewModel.checked += song
                else viewModel.checked -= song
            }
        }

        fun bindItem(song: AudlayerSong, position: Int) {
            setOnClickAndImageResource(song)
            pathTextView.text = FileNameParser.removeExtension(File(song.path))

            fileCheckBox.isChecked = song in viewModel.checked
        }
    }

    private inner class FileAdapter(val items: Array<out AudlayerSong>):
        RecyclerView.Adapter<FileHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.select_song_item, parent, false
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
            "${items[position].title[0]}"
    }
}