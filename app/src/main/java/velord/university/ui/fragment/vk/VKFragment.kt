package velord.university.ui.fragment.vk

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.statuscasellc.statuscase.model.entity.openFragment.general.FragmentCaller
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.util.view.gone
import velord.university.ui.util.view.setupAndShowPopupMenuOnClick
import velord.university.ui.util.view.visible
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.behaviour.MiniPlayerIconClickReceiver
import velord.university.application.broadcast.behaviour.VkReceiver
import velord.university.application.broadcast.hub.*
import velord.university.application.settings.SortByPreference
import velord.university.application.settings.VkPreference
import velord.university.databinding.ActionBarSearchBinding
import velord.university.databinding.GeneralRvBinding
import velord.university.databinding.VkFragmentBinding
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.converter.SongBitrate
import velord.university.model.converter.roundOfDecimalToUp
import velord.university.model.entity.music.song.Song
import velord.university.model.entity.fileType.file.FileFilter
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.song.download.DownloadSong
import velord.university.model.entity.openFragment.general.OpenFragmentEntity
import velord.university.model.entity.openFragment.returnResult.OpenFragmentForResultWithData
import velord.university.model.entity.openFragment.returnResult.ReturnResultFromFragment
import velord.university.model.entity.vk.VkCredential
import velord.university.ui.activity.VkLoginActivity
import velord.university.ui.fragment.actionBar.ActionBarSearchFragment
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.RVSelection
import velord.university.ui.util.view.between
import java.io.File

class VKFragment :
    ActionBarSearchFragment(),
    VkReceiver,
    MiniPlayerIconClickReceiver {

    override val TAG: String = "VKFragment"
    //Required interface for hosting activities
    interface Callbacks {

        fun openAddToPlaylistFragment()

        fun openDownloadSong(open: OpenFragmentEntity)
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

    private val receivers = receiverList() +
            getIconClickedReceiverList()

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
    }

    companion object {
        fun newInstance() = VKFragment()
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference(requireContext()).sortByVkFragment = index
        updateAdapterBySearchQuery()
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference(requireContext()).ascDescVkFragment = index
        updateAdapterBySearchQuery()
        super.rearwardActionButton()
        return true
    }

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = { it ->
        when (it.itemId) {
            R.id.vk_fragment_add_to_home_screen -> {
                TODO()
            }
            R.id.vk_fragment_shuffle -> {
                updateAdapterWithShuffled()
                super.rearwardActionButton()
                true
            }
            R.id.vk_fragment_sort_by -> {
                val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                val initActionMenuLayout = { R.menu.song_fragment_sort_by }
                val initActionMenuItemClickListener: (MenuItem) -> Boolean = { menuItem ->
                    when (menuItem.itemId) {
                        R.id.song_sort_by_name -> sortBy(0)
                        R.id.song_sort_by_artist -> sortBy(1)
                        R.id.song_sort_by_date_added ->sortBy(2)
                        R.id.song_sort_by_duration ->sortBy(3)
                        R.id.song_sort_by_size -> sortBy(4)

                        R.id.song_sort_by_ascending_order -> {
                            sortByAscDesc(0)
                        }
                        R.id.song_sort_by_descending_order -> {
                            sortByAscDesc(1)
                        }
                        else -> {
                            super.rearwardActionButton()
                            false
                        }
                    }
                }
                val actionBarPopUpMenu: (PopupMenu) -> Unit = { menu ->
                    //set up checked item
                    val menuItem = menu.menu

                    val sortBy =
                        SortByPreference(requireContext()).sortByVkFragment
                    when(sortBy) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                        2 -> { menuItem.getItem(2).isChecked = true }
                        3 -> { menuItem.getItem(3).isChecked = true }
                        4 -> { menuItem.getItem(4).isChecked = true }
                    }

                    val ascDescOrder =
                        SortByPreference(requireContext()).ascDescVkFragment
                    when(ascDescOrder) {
                        0 -> { menuItem.getItem(5).isChecked = true }
                        1 -> { menuItem.getItem(6).isChecked = true }
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
            R.id.vk_fragment_log_out -> {
                logOut()
                true
            }
            R.id.vk_fragment_download_all -> {
                scope.launch {
                    val list = viewModel.needDownloadList()
                    openDownloadSong(*list)
                }
                true
            }
            R.id.vk_fragment_delete_all -> {
                scope.launch {
                    viewModel.deleteAll()
                    updateAdapterBySearchQuery()
                }
                true
            }
            else -> false
        }
    }
    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = getString(R.string.action_bar_hint_vk)
    }
    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.vk_fragment_pop_up
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.round_format_list_bulleted_amber_a400_48dp)
    }
    override val actionBarObserveSearchQuery: (String) -> Unit = { searchQuery ->
        //-1 is default value, just ignore it
        if (searchQuery != "-1") {
            //store search term in shared preferences
            viewModel.storeSearchQuery(searchQuery)
            //update files list
            updateAdapterBySearchQuery(searchQuery)
        }
        else viewModel.storeSearchQuery("")
    }
    override val actionBarPopUp: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.arrow_down_amber_a400)
    }

    override val songPathF: (Intent?) -> Unit = { nullableIntent ->
            nullableIntent?.apply {
                val extra = BroadcastExtra.playByPathUI
                val path = getStringExtra(extra)!!
                scope.launch {
                    changeRVItem(path)
                }
            }
        }
    override val songPathIsWrongF: (Intent?) -> Unit = { nullableIntent ->
        nullableIntent?.apply {
            val extra = BroadcastExtra.playByPathUI
            val songPath = getStringExtra(extra)!!
            scope.launch {
                viewModel.pathIsWrong(songPath)
                withContext(Dispatchers.Main) {
                    viewModel.rvResolver.adapter.notifyDataSetChanged()
                }
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

    private suspend fun changeRVItem(songPath: String) {
        if (viewModel.rvResolverIsInitialized() &&
            viewModel.orderedIsInitialized()) {
            viewModel.rvResolver.apply {
                val song = viewModel.songList.get().find {
                    it.path == songPath
                } ?: return

                clearAndChangeSelectedItem(song)
                //apply to ui
                val files = viewModel.ordered
                val containF: (AudlayerSong) -> Boolean = {
                    it == song
                }
                refreshAndScroll(
                    files.toList(),
                    bindingRv.fastScrollRv, containF
                )
                //send new icon
                //this covers case when app is launch
                viewModel.sendIconToMiniPlayer(song)
            }
        }
    }

    private val viewModel: VkViewModel by viewModels()

    private val scope = getScope()
    //view
    private var _binding: VkFragmentBinding? = null
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
    ): View = inflater.inflate(R.layout.vk_fragment,
        container, false).run {
        //bind
        _binding = VkFragmentBinding.bind(this)
        _bindingActionBar = binding.actionBarInclude
        _bindingRv = binding.rvInclude
        scope.launch {
            onMain {
                //init action bar
                super.initActionBar()
                super.changeUIAfterSubmitTextInSearchView(
                    super.bindingActionBar.search
                )
                //observe changes in search view
                super.observeSearchQuery()
                //init self
                initView()
            }
        }
        binding.root
    }

    private fun initView() {
        initSwipeAndRv()
        initLogin()

        refreshValue()
    }

    private fun logOut() {
        viewModel.logout()
        refreshValue()
    }

    fun userInputLoginCredential(
        result: ReturnResultFromFragment<VkCredential>
    ) {
        //save new data
        VkPreference(requireContext())
            .login = result.value!!.login
        VkPreference(requireContext())
            .password = result.value.password
        //update ui
        refreshValue()
    }

    private fun credentialIsBlank()  {
        bindingRv.fastScrollSwipe.gone()
        binding.vkLogin.visible()
    }

    private fun credentialIsExist() {
        binding.vkLogin.gone()
        bindingRv.fastScrollSwipe.visible()
        //update ui
        setupAdapter()
    }

    private fun initSwipeAndRv() {
        binding.swipeRefresh.setOnRefreshListener { refreshValue() }
        bindingRv.fastScrollRv.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(
            bindingRv.fastScrollRv, 50, -5
        )
    }

    private fun refreshValue() {
        scope.launch {
            between("refreshValue") {
                onMain {
                    if (viewModel.checkAuth()) {
                        val songList = viewModel.songList.get()
                        if (songList.isEmpty())
                            viewModel.refreshByCredential()
                        credentialIsExist()
                    }
                    else {
                        credentialIsBlank()
                    }
                }
            }
            //for XIAOMI
            onMain { binding.swipeRefresh.isRefreshing = false }
        }
    }

    private fun initLogin() {
        binding.vkLogin.setOnClickListener {
            val intent = VkLoginActivity.newIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun setupAdapter() {
        val query = viewModel.getSearchQuery()
        super.viewModelActionBarSearch.setupSearchQuery(query)
    }

    private fun updateAdapterBySearchQuery(
        searchQuery: String = viewModel.currentQuery
    ) {
        scope.launch {
            between("updateAdapterBySearchQuery") {
                val songsFiltered = viewModel.filterByQuery(searchQuery)
                onMain {
                    bindingRv.fastScrollRv.adapter = SongAdapter(songsFiltered)
                }
            }
        }
    }

    private fun updateAdapterWithShuffled() {
        scope.launch {
            between("updateAdapterWithShuffled") {
                val shuffled = viewModel.shuffle()
                bindingRv.fastScrollRv.adapter = SongAdapter(shuffled)
            }
        }
    }

    private fun openDownloadSong(vararg song: DownloadSong) {
        val forResult = OpenFragmentForResultWithData(
            FragmentCaller.VK,
            song as Array<DownloadSong>
        )
        callbacks?.openDownloadSong(forResult)
    }

    fun songDownloaded(song: DownloadSong) {
        scope.launch {
            Log.d(TAG, song.toString())
            viewModel.applyNewPath(song)
            onMain {
                viewModel.rvResolver.adapter.notifyDataSetChanged()
            }
        }
    }

    private suspend fun between(tag: String,
                                f: suspend () -> Unit) =
        bindingRv.fastScrollSwipe.between(requireActivity(), tag, f)

    private fun getRecyclerViewResolver(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ): RVSelection<AudlayerSong> {
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

    private inner class SongHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val text: TextView =
            itemView.findViewById(R.id.general_item_path)
        private val action: ImageButton =
            itemView.findViewById(R.id.general_action_ImageButton)
        private val icon: ImageButton =
            itemView.findViewById(R.id.general_item_icon)

        private inline fun needDownload(song: AudlayerSong,
                                        need: () -> Unit,
                                        notNeed: () -> Unit = { } ) {
            if (viewModel.needDownload(song)) need()
            else notNeed()
        }

        private fun needDownloadAction(song: AudlayerSong) {
            val need: () -> Unit = {
                Glide.with(requireActivity())
                    .load(R.drawable.download_200_amber_a400)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(action)
            }

            val notNeed: () -> Unit = {
                Glide.with(requireActivity())
                    .load(R.drawable.action_item)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(action)
            }

            needDownload(song, need, notNeed)
        }

        private fun needDownloadBackground(song: AudlayerSong) {
            val need: () -> Unit = {
                itemView.setBackgroundResource(R.color.mortar_opacity_65)
            }

            val notNeed: () -> Unit = {
                itemView.setBackgroundResource(R.color.opacity)
            }

            needDownload(song, need, notNeed)
        }

        val general: (AudlayerSong) -> Array<() -> Unit> = { song ->
            arrayOf(
                { needDownloadAction(song) },
                {
                    scope.launch {
                        onMain {
                            //todo()
                            Glide.with(requireActivity())
                                .load(DrawableIcon.getRandomSongIconName())
                                .placeholder(DrawableIcon.getRandomSongIconName())
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(icon)
                        }
                    }
                },
                { text.setTextColor(resources.getColor(R.color.white_opacity_80)) }
            )
        }

        val selected: (AudlayerSong) -> Array<() -> Unit> = { song ->
            general(song) + arrayOf(
                {
                    val size: Double =
                        roundOfDecimalToUp((FileFilter.getSize(File(song.path)).toDouble() / 1024))

                    val bitrate = SongBitrate.getKbpsString(File(song.path))

                    text.text = getString(
                        R.string.vk_rv_item_selected,
                        song.artist,
                        song.title,
                        size.toString(),
                        bitrate
                    )
                },
                { itemView.setBackgroundResource(R.color.sapphire_opacity_40) }
            )
        }

        val notSelected: (AudlayerSong) -> Array<() -> Unit> = { song ->
            general(song) + arrayOf(
                {
                    text.text = getString(R.string.vk_rv_item_not_selected,
                        song.artist, song.title)
                },
                { needDownloadBackground(song) },
                {
                    if (viewModel.needDownload(song)) {
                        text.setTextColor(resources.getColor(R.color.white_opacity_65))
                    }
                }
            )
        }

        private val actionPopUpMenu: (AudlayerSong) -> Unit = { song ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.vk_item }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.vk_rv_item_play_next -> {
                        viewModel.playAudioNext(song)
                        true
                    }
                    R.id.vk_rv_item_add_to_playlist -> {
                        callbacks?.let { callback ->

                            SongPlaylistInteractor.songs = arrayOf(Song(File(song.path)))
                            callback.openAddToPlaylistFragment()

                        }
                        true
                    }
                    R.id.vk_rv_item_set_as_ringtone -> {
                        TODO()
                    }
                    R.id.vk_rv_item_add_to_home_screen -> {
                        TODO()
                    }
                    R.id.vk_rv_item_delete -> {
                        scope.launch {
                            viewModel.deleteSong(song)
                            onMain {
                                viewModel.rvResolver.adapter.notifyDataSetChanged()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }

            action.setupAndShowPopupMenuOnClick(
                requireContext(),
                initActionMenuStyle,
                initActionMenuLayout,
                initActionMenuItemClickListener
            )
            Unit
        }

        private fun play(song: AudlayerSong,
                         rvSelectResolver: RVSelection<AudlayerSong>) {
            val need: () -> Unit = {
                Toast.makeText(requireContext(),
                    "Click Download Icon", Toast.LENGTH_SHORT).show()
            }

            val notNeed: () -> Unit = {
                scope.launch {
                    rvSelectResolver.singleSelectionPrinciple(song)
                    viewModel.playAudioAndAllSong(song)
                }
            }

            needDownload(song, need, notNeed)
        }

        private fun setOnClickAndImageResource(song: AudlayerSong,
                                               rvSelectResolver: RVSelection<AudlayerSong>
        ) {
            itemView.setOnClickListener {
                play(song, rvSelectResolver)
            }
            text.setOnClickListener {
                play(song, rvSelectResolver)
            }
            icon.setOnClickListener {
                play(song, rvSelectResolver)
            }
            action.setOnClickListener {
                val need: () -> Unit = {
                    openDownloadSong(song.toDownloadSong())
                }
                val notNeed: () -> Unit = {
                    actionPopUpMenu(song)
                }
                needDownload(song, need, notNeed)
            }
        }

        private fun applyState(value: AudlayerSong,
                               rvSelectResolver: RVSelection<AudlayerSong>) {
            when(rvSelectResolver.state) {
                0 -> rvSelectResolver.isContains(
                    value,
                    selected,
                    notSelected
                )
            }
        }

        fun bindItem(song: AudlayerSong, position: Int,
                     rvSelectResolver: RVSelection<AudlayerSong>) {
            applyState(song, rvSelectResolver)
            setOnClickAndImageResource(song, rvSelectResolver)
        }
    }

    private inner class SongAdapter(val items: Array<out AudlayerSong>):
        RecyclerView.Adapter<SongHolder>(),
        FastScrollRecyclerView.SectionedAdapter{

        private val rvSelectResolver = getRecyclerViewResolver(
            this as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )
            return SongHolder(view)
        }

        override fun onBindViewHolder(holder: SongHolder, position: Int) {
            items[position].apply {
                holder.bindItem(this, position, rvSelectResolver)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].artist[0]}"
    }
}
