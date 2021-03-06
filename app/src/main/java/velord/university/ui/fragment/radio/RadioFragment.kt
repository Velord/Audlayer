package velord.university.ui.fragment.radio

import android.content.Intent
import android.content.IntentFilter
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
import velord.university.ui.util.view.setupAndShowPopupMenuOnClick
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.behaviour.RadioIconClickReceiver
import velord.university.application.broadcast.behaviour.RadioNameArtistUIReceiver
import velord.university.application.broadcast.behaviour.RadioUnavailableUIReceiver
import velord.university.application.broadcast.hub.*
import velord.university.application.settings.SortByPreference
import velord.university.databinding.*
import velord.university.model.entity.music.radio.RadioStation
import velord.university.ui.fragment.actionBar.ActionBarSearchFragment
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.RVSelection

class RadioFragment :
    ActionBarSearchFragment(),
    RadioNameArtistUIReceiver,
    RadioIconClickReceiver,
    RadioUnavailableUIReceiver {

    override val TAG: String = "RadioFragment"

    private val scope = getScope()

    private val viewModel: RadioViewModel by viewModels()

    companion object {
        fun newInstance() = RadioFragment()
    }

    override val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean = { it ->
        when (it.itemId) {
            R.id.radio_fragment_add_to_home_screen -> {
                TODO()
            }
            R.id.radio_fragment_sort_by -> {
                val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
                val initActionMenuLayout = { R.menu.radio_sort_by }
                val initActionMenuItemClickListener: (MenuItem) -> Boolean = { menuItem ->
                    when (menuItem.itemId) {
                        R.id.radio_sort_by_name -> sortBy(0)
                        R.id.radio_sort_by_like -> sortBy(1)

                        R.id.radio_sort_by_ascending_order ->
                            sortByAscDesc(0)
                        R.id.radio_sort_by_descending_order ->
                            sortByAscDesc(1)
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
                        SortByPreference(requireContext()).sortByRadioFragment
                    when(sortBy) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                    }

                    val ascDescOrder =
                        SortByPreference(requireContext()).ascDescRadioFragment
                    when(ascDescOrder) {
                        0 -> { menuItem.getItem(2).isChecked = true }
                        1 -> { menuItem.getItem(3).isChecked = true }
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
            else -> false
        }
    }
    override val actionBarHintArticle: (TextView) -> Unit = {
        it.text = getString(R.string.action_bar_hint_radio)
    }
    override val actionBarPopUpMenuLayout: () -> Int = {
        R.menu.radio_fragment_pop_up
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.round_format_list_bulleted_deep_purple_a200_48dp)
    }
    override val actionBarPopUp: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.arrow_down_deep_purple_a200)
    }
    override val actionBarObserveSearchQuery: (String) -> Unit = { searchQuery ->
        //-1 is default value, just ignore it
        val correctQuery =
            if (searchQuery == "-1") ""
            else searchQuery
        //store search term in shared preferences
        viewModel.storeSearchQuery(correctQuery)
        //update files list
        updateAdapterBySearchQuery(correctQuery)
    }

    private val receivers = getRadioNameArtistUIReceiverList() +
            getRadioIconReceiverList() +
            getRadioUnavailableUIReceiverList()

    override val nameRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.radioNameUI
            val radioName = getStringExtra(extra)

            if (viewModel.rvResolverIsInitialized()) {
                scope.launch {
                    viewModel.rvResolver.state = 1
                    val stations = viewModel.ordered
                    val station = stations.find { it.name == radioName }
                    val f: (RadioStation) -> Boolean = { radio ->
                        radio == station
                    }

                    viewModel.rvResolver.refreshAndScroll(stations, bindingRv.fastScrollRv, f)
                }
            }
        }
    }

    override val artistRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.radioArtistUI
            val value = getStringExtra(extra)
        }
    }

    override val iconRadioClicked: (Intent?) -> Unit = {
        it?.apply {
            scope.launch {
                viewModel.rvResolver.scroll(bindingRv.fastScrollRv)
            }
        }
    }

    override val radioPlayerUnavailableUIF: (Intent?) -> Unit = {
        it?.apply {
            scope.launch {

            }
        }

    }

    override val radioUrlIsWrongUIF: (Intent?) -> Unit = {
        it?.apply {
            scope.launch {
                val extra = BroadcastExtra.radioStationUrlUI
                val radioUrl = getStringExtra(extra)

                if (viewModel.rvResolverIsInitialized()) {
                    scope.launch {
                        viewModel.rvResolver.state = 1
                        val stations = viewModel.ordered
                        val station = stations.find { it.url == radioUrl }
                        val f: (RadioStation) -> Boolean = { radio ->
                            radio == station
                        }

                        viewModel.rvResolver.refreshAndScroll(
                            stations, bindingRv.fastScrollRv, f
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, IntentFilter(it.second), PERM_PRIVATE_RADIO
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
    //view
    private var _binding: RadioFragmentBinding? = null
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
    ): View? = inflater.inflate(R.layout.radio_fragment,
        container, false).run {
        //bind
        _binding = RadioFragmentBinding.bind(this)
        _bindingActionBar = binding.actionBarInclude
        _bindingRv = binding.rvInclude
        scope.launch {
            viewModel
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
        initRV()
    }

    private fun setupAdapter() {
        scope.launch {
            val query = viewModel.getSearchQuery()
            onMain {
                super.viewModelActionBarSearch.setupSearchQuery(query)
            }
        }
    }

    private fun initRV() {
        bindingRv.fastScrollRv.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(
            bindingRv.fastScrollRv,
            50, -5
        )
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference(requireContext()).sortByRadioFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference(requireContext()).ascDescRadioFragment = index
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        scope.launch {
            val radioStationFiltered = viewModel.filterByQuery(searchQuery)
            onMain {
                bindingRv.fastScrollRv.adapter =
                    RadioAdapter(radioStationFiltered.toTypedArray())
            }
        }
    }

    private fun playRadio(radio: RadioStation) {
        viewModel.playRadio(radio)
    }

    private fun loadRadioStationIcon(radio: RadioStation, view: ImageView) =
        DrawableIcon.loadRadioIconAsset(
            requireContext(), view, radio.icon)

    private fun setName(radio: RadioStation, view: TextView) {
        view.text = radio.name
    }

    private fun getRecyclerViewResolver(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ): RVSelection<RadioStation> {
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

    private inner class RadioHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val text: TextView =
            itemView.findViewById(R.id.radio_item_name)
        private val action: ImageButton =
            itemView.findViewById(R.id.general_action_ImageButton)
        private val frame: FrameLayout =
            itemView.findViewById(R.id.general_action_frame)
        private val icon: ImageButton =
            itemView.findViewById(R.id.radio_item_icon)
        private val pb: ProgressBar =
            itemView.findViewById(R.id.radio_item_pb)

        private val selectedGeneral: (RadioStation) -> Array<() -> Unit> = { radio ->
            arrayOf(
                {
                    Log.d(TAG, "selectedGeneral")
                    icon.setImageResource(R.drawable.song_item_playing)
                }, {
                    itemView.setBackgroundResource(R.color.sapphire_opacity_40)
                }, {
                    setName(radio, text)
                }, {
                    loadRadioStationIcon(radio, icon)
                }
            )
        }

        private val selectedFirst: (RadioStation) -> Array<() -> Unit> = { radio ->
            selectedGeneral(radio) + arrayOf(
                {
                    pb.visibility = View.VISIBLE
                }
            )
        }

        private val notSelectedFirst: (RadioStation) -> Array<() -> Unit> = { radio ->
            arrayOf(
                {
                    Log.d(TAG, "notSelectedFirst")
                    itemView.setBackgroundResource(R.color.opacity)
                }, {
                    setName(radio, text)
                }, {
                    loadRadioStationIcon(radio, icon)
                }, {
                    pb.visibility = View.GONE
                }
            )
        }

        private val selectedSecond: (RadioStation) -> Array<() -> Unit> = { radio ->
            selectedGeneral(radio) + arrayOf(
                {
                    pb.visibility = View.GONE
                }
            )
        }

        private val actionPopUpMenu: (RadioStation) -> Unit = { radio ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.radio_item }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.radio_rv_item_play_next -> { TODO() }
                    R.id.radio_rv_item_add_to_home_screen -> { TODO() }
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

        private fun setOnClickAndImageResource(radio: RadioStation,
                                               rvSelectResolver: RVSelection<RadioStation>) {
            itemView.setOnClickListener {
                scope.launch {
                    rvSelectResolver.singleSelectionPrinciple(radio)
                    playRadio(radio)
                }
            }
            text.setOnClickListener {
                scope.launch {
                    rvSelectResolver.singleSelectionPrinciple(radio)
                    playRadio(radio)
                }
            }
            icon.setOnClickListener {
                scope.launch {
                    rvSelectResolver.singleSelectionPrinciple(radio)
                    playRadio(radio)
                }
            }
            action.setOnClickListener {
                actionPopUpMenu(radio)
            }
            frame.setOnClickListener {
                actionPopUpMenu(radio)
            }
        }

        private fun applyState(radio: RadioStation,
                               rvSelectResolver: RVSelection<RadioStation>) {
            when(rvSelectResolver.state) {
                0 -> isContains(rvSelectResolver, radio,
                    selectedFirst,
                    notSelectedFirst
                )
                1 -> isContains(rvSelectResolver, radio,
                        selectedSecond,
                        notSelectedFirst
                )
            }
        }

        private fun isContains(rvSelectResolver: RVSelection<RadioStation>,
                               radio: RadioStation,
                               isContains: (RadioStation) -> Array<() -> Unit>,
                               isNotContains: (RadioStation) -> Array<() -> Unit>) {
            if (rvSelectResolver.selected.contains(radio)) isContains(radio).forEach { it() }
            else isNotContains(radio).forEach { it() }
        }

        fun bindItem(radio: RadioStation, position: Int,
                     rvSelectResolver: RVSelection<RadioStation>) {
            applyState(radio, rvSelectResolver)
            setOnClickAndImageResource(radio, rvSelectResolver)
        }
    }

    private inner class RadioAdapter(val items: Array<out RadioStation>):
        RecyclerView.Adapter<RadioHolder>(),
        FastScrollRecyclerView.SectionedAdapter {

        private val rvSelectResolver =
            getRecyclerViewResolver(this as RecyclerView.Adapter<RecyclerView.ViewHolder>)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.radio_rv_item, parent, false
            )
            return RadioHolder(view)
        }

        override fun onBindViewHolder(holder: RadioHolder, position: Int) {
            items[position].apply {
                holder.bindItem(this, position, rvSelectResolver)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }
}

