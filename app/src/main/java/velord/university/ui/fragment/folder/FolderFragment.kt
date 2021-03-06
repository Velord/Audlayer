package velord.university.ui.fragment.folder

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.util.activity.toastError
import velord.university.ui.util.activity.toastWarning
import velord.university.ui.util.view.makeCheck
import velord.university.ui.util.view.setupAndShowPopupMenuOnClick
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.behaviour.MiniPlayerIconClickReceiver
import velord.university.application.broadcast.behaviour.SongPathReceiver
import velord.university.application.broadcast.hub.*
import velord.university.application.settings.SortByPreference
import velord.university.databinding.ActionBarSearchBinding
import velord.university.databinding.FolderFragmentBinding
import velord.university.databinding.GeneralRvBinding
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.converter.SongBitrate
import velord.university.model.converter.roundOfDecimalToUp
import velord.university.model.entity.fileType.file.FileExtension
import velord.university.model.entity.fileType.file.FileExtensionModifier
import velord.university.model.entity.fileType.file.FileRetrieverConverter
import velord.university.model.entity.fileType.file.FileNameParser
import velord.university.model.entity.fileType.file.FileRetrieverConverter.getSize
import velord.university.model.entity.fileType.file.FileRetrieverConverter.toAudlayer
import velord.university.model.entity.fileType.file.FileRetrieverConverter.toAudlayerSong
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.ui.behaviour.backPressed.BackPressedHandlerZero
import velord.university.ui.fragment.actionBar.ActionBarSearchFragment
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.RVSelection
import velord.university.ui.util.view.between
import java.io.File

class FolderFragment :
    ActionBarSearchFragment(),
    BackPressedHandlerZero,
    SongPathReceiver,
    MiniPlayerIconClickReceiver {

    override val TAG: String = "FolderFragment"

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return if (viewModel.directory.isRoot()) true
        else {
            val newFile = viewModel.directory.setParent()
            setupAdapter(newFile)
            false
        }
    }

    //Required interface for hosting activities
    interface Callbacks {

        fun openCreateNewPlaylistFragment()

        fun openAddToPlaylistFragment()
    }

    private var callbacks: Callbacks? =  null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null

        scope.cancel()
    }

    companion object {
        fun newInstance() = FolderFragment()
    }

    private val viewModel: FolderViewModel by viewModels()

    private val scope = getScope()

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = { it ->
        when (it.itemId) {
            R.id.action_folder_add_to_home_screen -> {
                TODO()
            }
            R.id.action_folder_show_incompatible_files -> {
                //TODO()
                requireActivity().toastError(
                    requireContext().run {
                        getString(
                            R.string.not_implemented_operation,
                            this.getString(R.string.show_incompatible_files)
                        )
                    }
                )
                true
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

                    val nameArtistDateOrder = SortByPreference(
                        requireContext()).sortByFolderFragment
                    when(nameArtistDateOrder) {
                        0 -> { menuItem.makeCheck(0) }
                        1 -> { menuItem.makeCheck(1) }
                        2 -> { menuItem.makeCheck(2) }
                    }

                    val ascDescOrder = SortByPreference(
                        requireContext()).ascDescFolderFragment
                    when(ascDescOrder) {
                        0 -> { menuItem.makeCheck(3) }
                        1 -> { menuItem.makeCheck(4) }
                        else -> {}
                    }
                }

                super.bindingActionBar.action.setupAndShowPopupMenuOnClick(
                    requireContext(),
                    initActionMenuStyle,
                    initActionMenuLayout,
                    initActionMenuItemClickListener
                ).also {
                    actionBarPopUpMenu(it)
                }

                true
            }
            R.id.action_folder_add_to_playlist -> {
                openAddToPlaylistFragmentByQuery()
                true
            }
            R.id.action_folder_create_playlist -> {
                val songs = viewModel.onlyAudio()
                openCreatePlaylistFragment(songs)
                true
            }
            else -> { false }
        }
    }
    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = getString(R.string.action_bar_hint_folder)
    }
    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.folder_fragment_pop_up
    }
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

    private val receivers = songPathReceiverList() +
            getIconClickedReceiverList()

    override val songPathF: (Intent?) -> Unit = { nullableIntent ->
        nullableIntent?.apply {
            val extra = BroadcastExtra.playByPathUI
            val songPath = getStringExtra(extra)!!
            scope.launch {
                changeRVItem(songPath)
            }
        }
    }

    override val iconClicked: (Intent?) -> Unit = {
        it?.apply {
            scope.launch {
                viewModel.rvResolver.scroll(bindingRv.fastScrollRv)
            }
        }
    }

    private suspend fun changeRVItem(path: String) {
        if (viewModel.rvResolverIsInitialized()) {
            viewModel.rvResolver.apply {
                val fileList = viewModel.ordered
                val file = fileList.find { it.path == path } ?: return

                clearAndChangeSelectedItem(file)
                //apply to ui
                val containF: (File) -> Boolean = {
                    it == file
                }
                refreshAndScroll(fileList, bindingRv.fastScrollRv, containF)
                //send new icon
                viewModel.sendIconToMiniPlayer(file)
            }
            return
        }
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

        scope.cancel()
    }
    //view
    private var _binding: FolderFragmentBinding? = null
    override var _bindingActionBar: ActionBarSearchBinding? = null
    private var _bindingRv: GeneralRvBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")
    private val bindingRv get() = _bindingRv ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.folder_fragment,
        container, false).run {
        //bind
        _binding = FolderFragmentBinding.bind(this)
        _bindingActionBar = binding.actionBarInclude
        _bindingRv = binding.rvInclude
        scope.launch {
            between("onCreateView") {
                initViewModel()
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
                    setupAdapter(viewModel.directory.getDirectory())
                }
            }
        }
        binding.root
    }

    private fun initViewModel() {
        viewModel
    }

    private fun initView() {
        initSwipeAndRv()
        initDirectoryPath()

        if (viewModel.rvResolverIsInitialized())
            updateAdapterBySearchQuery(viewModel.currentQuery)
    }

    private fun initDirectoryPath() {
        binding.currentDirectory.setOnClickListener {
            //change ui
            if (viewModel.directory.isRoot()) binding
                .currentDirectoryLayout
                .setBackgroundResource(R.color.amberA200)
            else binding
                .currentDirectoryLayout
                .setBackgroundResource(R.drawable.current_folder_layout_shape)
            //change root
            viewModel.directory.changeRoot(requireContext())
            //implement
            refreshValue()
        }
    }

    private fun setupAdapter(file: File) {
        scope.launch {
            viewModel.directory.setDirectory(file)
            val query = viewModel.getSearchQuery()
            onMain {
                super.viewModelActionBarSearch.setupSearchQuery(query)
            }
        }
    }

    private fun openAddToPlaylistFragment(songs: List<File>) {
        callbacks?.let {
            if (songs.isNotEmpty()) {
                SongPlaylistInteractor.songList = viewModel.toAudlayer(songs)
                it.openAddToPlaylistFragment()
            }
            else requireActivity().toastWarning(
                requireContext().getString(R.string.no_one_song)
            )
        }
    }

    private fun openCreatePlaylistFragment(songs: List<File>) {
        if (songs.isNotEmpty()) {
            SongPlaylistInteractor.songList = viewModel.toAudlayer(songs)
            callbacks?.openCreateNewPlaylistFragment()
        }
        else requireActivity().toastWarning(
            requireContext().getString(R.string.no_one_song)
        )
    }

    private fun openAddToPlaylistFragmentByQuery() {
        val files = viewModel.filterAndSortFiles()

        val audio = FileRetrieverConverter.filterOnlyAudio(files)

        openAddToPlaylistFragment(audio)
    }

    private fun initSwipeAndRv() {
        bindingRv.fastScrollSwipe.setOnRefreshListener { refreshValue() }
        bindingRv.fastScrollRv.layoutManager =
            LinearLayoutManager(requireActivity())
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(
            bindingRv.fastScrollRv,
            50, -5
        )
    }

    private fun refreshValue() {
        updateAdapterBySearchQuery(viewModel.currentQuery)
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference(requireContext()).sortByFolderFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference(requireContext()).ascDescFolderFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        scope.launch {
            between("updateAdapterBySearchQuery") {
                //now do everything to setup adapter
                //apply all filters to recycler view
                val songList = viewModel.filterAndSortFiles(searchQuery)
                //change ui
                onMain {
                    changeCurrentTextView(viewModel.directory.getDirectory())
                    bindingRv.fastScrollRv.adapter = FileAdapter(songList)
                }
            }
        }
    }

    private fun changeCurrentTextView(file: File) {
        val pathToUI = FileNameParser.slashReplaceArrow(file.path)
        binding.currentDirectory.text = pathToUI
    }

    private suspend fun between(tag: String,
                                f: suspend () -> Unit) =
        bindingRv.fastScrollSwipe.between(requireActivity(), tag, f)

    private fun getRecyclerViewResolver(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ): RVSelection<File> {
        return if (viewModel.rvResolverIsInitialized()) {
            viewModel.rvResolver.adapter = adapter
            viewModel.rvResolver
        }
        //new
        else {
            viewModel.rvResolver = RVSelection(adapter, 0)
            viewModel.rvResolver
        }
    }

    private inner class FileHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val icon: ImageButton =
            itemView.findViewById(R.id.general_item_icon)
        private val path: TextView =
            itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton =
            itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout =
            itemView.findViewById(R.id.general_action_frame)

        private fun setOnClickAndImageResource(value: File,
                                               rvSelectResolver: RVSelection<File>) {
            when(FileExtension.getFileExtension(value)) {
                FileExtensionModifier.DIRECTORY -> {
                    val action = { setupAdapter(value) }
                    val popUpAction = {
                        val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                        val initActionMenuLayout = { R.menu.folder_item_is_folder_pop_up }
                        val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                            when (it.itemId) {
                                R.id.folder_recyclerView_item_isFolder_play -> {
                                    viewModel.playAllInFolder(value)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_play_next -> {
                                    viewModel.playAllInFolderNext(value)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_add_to_playlist -> {
                                    val songs = viewModel.onlyAudio(value)
                                    openAddToPlaylistFragment(songs)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_create_playlist -> {
                                    val songs = viewModel.onlyAudio(value)
                                    openCreatePlaylistFragment(songs)
                                    true
                                }
                                R.id.folder_recyclerView_item_isFolder_shuffle -> {
                                    viewModel.shuffleAndPlayAllInFolder(value)
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

                        actionImageButton.setupAndShowPopupMenuOnClick(
                            requireContext(),
                            initActionMenuStyle,
                            initActionMenuLayout,
                            initActionMenuItemClickListener
                        )

                        Unit
                    }
                    setOnClick(value, rvSelectResolver, action, popUpAction)
                    icon.setImageResource(R.drawable.extension_file_folder)
                }
                FileExtensionModifier.AUDIO -> {
                    val action = { viewModel.playAudioFile(value) }
                    val popUpAction = {
                        val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                        val initActionMenuLayout = { R.menu.folder_item_is_audio_pop_up }
                        val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                            when (it.itemId) {
                                R.id.folder_recyclerView_item_isAudio_play -> {
                                    viewModel.playAudio(value)
                                    true
                                }
                                R.id.folder_recyclerView_item_isAudio_play_next -> {
                                    viewModel.playAudioNext(value)
                                    true
                                }
                                R.id.folder_recyclerView_item_isAudio_add_to_playlist -> {
                                    callbacks?.let { callback ->
                                        SongPlaylistInteractor.songList = listOf(
                                            value.toAudlayerSong(viewModel.mediaRetriever)
                                        )
                                        callback.openAddToPlaylistFragment()
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

                        actionImageButton.setupAndShowPopupMenuOnClick(
                            requireContext(),
                            initActionMenuStyle,
                            initActionMenuLayout,
                            initActionMenuItemClickListener
                        )

                        Unit
                    }
                    setOnClick(value, rvSelectResolver, action, popUpAction)
                }
                FileExtensionModifier.NOT_COMPATIBLE -> {
                    icon.setImageResource(R.drawable.extension_file_not_important)
                }
            }
        }

        private inline fun setOnClick(value: File,
                                      rvSelectResolver: RVSelection<File>,
                                      crossinline f: () -> Unit,
                                      crossinline popUpF: () -> Unit) {
            itemView.setOnClickListener {
                scope.launch {
                    rvSelectResolver.singleSelectionPrinciple(value)
                    f()
                }
            }
            icon.setOnClickListener {
                scope.launch {
                    rvSelectResolver.singleSelectionPrinciple(value)
                    f()
                }
            }
            path.setOnClickListener {
                scope.launch {
                    rvSelectResolver.singleSelectionPrinciple(value)
                    f()
                }
            }
            actionImageButton.setOnClickListener {
                popUpF()
            }
            actionFrame.setOnClickListener {
                popUpF()
            }
        }

        private val selected: (File) -> Array<() -> Unit> = { value ->
            arrayOf(
                {
                    path.text = if (viewModel.isAudio(value)) {
                        val size: Double = roundOfDecimalToUp(
                            (value.getSize().toDouble() / 1024)
                        )

                        val bitrate = SongBitrate.getKbpsString(value)

                        getString(
                            R.string.folder_fragment_rv_item,
                            FileNameParser.removeExtension(value),
                            size.toString(),
                            bitrate
                        )
                    }
                    else FileNameParser.removeExtension(value)

                },
                {
                    itemView.setBackgroundResource(R.color.sapphire_opacity_40)
                },
                {
                    icon.setImageResource(R.drawable.song_item_playing)
                }
            )
        }

        private val notSelected: (File) -> Array<() -> Unit> = { value ->
            arrayOf(
                {
                    path.text = FileNameParser.removeExtension(value)
                },
                {
                    itemView.setBackgroundResource(R.color.opacity)
                },
                {
                    if (viewModel.isAudio(value))
                        DrawableIcon.loadRandomSongIcon(requireContext(), icon)
                }
            )
        }

        private fun applyState(value: File,
                               rvSelectResolver: RVSelection<File>) {
            when(rvSelectResolver.state) {
                0 -> rvSelectResolver.isContains(
                    value,
                    selected,
                    notSelected
                )
            }
        }

        fun bindItem(value: File,
                     position: Int,
                     rvSelectResolver: RVSelection<File>) {

            applyState(value, rvSelectResolver)
            setOnClickAndImageResource(value, rvSelectResolver)
        }
    }

    private inner class FileAdapter(val items: Array<out File>):
        RecyclerView.Adapter<FileHolder>(),
        FastScrollRecyclerView.SectionedAdapter {

        private val rvSelectResolver = getRecyclerViewResolver(
            this as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): FileHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )

            return FileHolder(view)
        }

        override fun onBindViewHolder(holder: FileHolder,
                                      position: Int) {
            items[position].apply {
                holder.bindItem(this, position, rvSelectResolver)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }
}
