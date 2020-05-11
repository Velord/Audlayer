package velord.university.ui.fragment.radio

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.PERM_PRIVATE_RADIO
import velord.university.application.broadcast.behaviour.RadioServiceReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.application.settings.SortByPreference
import velord.university.model.entity.RadioStation
import velord.university.ui.fragment.actionBar.ActionBarFragment
import velord.university.ui.util.RecyclerViewSelectItemResolver
import velord.university.ui.util.setupPopupMenuOnClick

class RadioFragment : ActionBarFragment(),
    RadioServiceReceiver {

    override val TAG: String = "RadioFragment"

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var rv: RecyclerView

    companion object {
        fun newInstance() = RadioFragment()
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(RadioViewModel::class.java)
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
                        SortByPreference.getSortByRadioFragment(requireContext())
                    when(sortBy) {
                        0 -> { menuItem.getItem(0).isChecked = true }
                        1 -> { menuItem.getItem(1).isChecked = true }
                    }

                    val ascDescOrder =
                        SortByPreference.getAscDescRadioFragment(requireContext())
                    when(ascDescOrder) {
                        0 -> { menuItem.getItem(2).isChecked = true }
                        1 -> { menuItem.getItem(3).isChecked = true }
                    }
                }

                setupPopupMenuOnClick(
                    requireContext(),
                    super.actionButton,
                    initActionMenuStyle,
                    initActionMenuLayout,
                    initActionMenuItemClickListener
                ).also {
                    actionBarPopUpMenu(it)
                }
                //invoke immediately popup menu
                super.actionButton.callOnClick()
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
    override val actionBarPopUpMenuStyle: () -> Int = {
        R.style.PopupMenuOverlapAnchorFolder
    }
    override val actionBarLeftMenu: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.action_bar_settings_gold)
    }
    override val actionBarPopUpMenu: (PopupMenu) -> Unit = {  }
    override val actionBarPopUp: (ImageButton) -> Unit = {
        it.setImageResource(R.drawable.action_bar_pop_up_gold)
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

    private val receivers = receiverList()

    override val likeRadioF: (Intent?) -> Unit = {
        viewModel.changeLike(true)
    }

    override val unlikeRadioF: (Intent?) -> Unit = {
        viewModel.changeLike(false)
    }

    override val playByUrlRadioF: (Intent?) -> Unit = {}

    override val stopRadioF: (Intent?) -> Unit = {}

    override val playRadioF: (Intent?) -> Unit = {}

    override val getInfoRadioF: (Intent?) -> Unit = {}

    override val playOrStopRadioF: (Intent?) -> Unit = {}

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.radio_fragment, container, false).apply {
            scope.launch {
                viewModel
                withContext(Dispatchers.Main) {
                    //init action bar
                    super.initActionBar(this@apply)
                    super.changeUIAfterSubmitTextInSearchView(super.searchView)
                    //init self view
                    initViews(this@apply)
                    //observe changes in search view
                    super.observeSearchQuery()
                    //setup adapter by invoke change in search view
                    setupAdapter()
                }
            }
        }
    }

    private fun initViews(view: View) {
        initRV(view)
    }

    private fun setupAdapter() {
        scope.launch {
            val query = viewModel.getSearchQuery()
            withContext(Dispatchers.Main) {
                super.viewModelActionBar.setupSearchQuery(query)
            }
        }
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.general_RecyclerView)
        rv.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity)
        }
        //controlling action bar frame visibility when recycler view is scrolling
        super.setScrollListenerByRecyclerViewScrolling(rv, 50, -5)
    }

    private fun sortBy(index: Int): Boolean {
        SortByPreference.setSortByRadioFragment(requireContext(), index)
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun sortByAscDesc(index: Int): Boolean {
        SortByPreference.setAscDescRadioFragment(requireContext(), index)
        updateAdapterBySearchQuery(viewModel.currentQuery)
        super.rearwardActionButton()
        return true
    }

    private fun updateAdapterBySearchQuery(searchQuery: String) {
        scope.launch {
            val radioStationFiltered = viewModel.filterByQuery(searchQuery)
            withContext(Dispatchers.Main) {
                rv.adapter = RadioAdapter(radioStationFiltered.toTypedArray())
            }
        }
    }

    private fun playRadio(radio: RadioStation) =
        viewModel.playRadio(radio)

    private fun loadRadioStationIcon(radio: RadioStation, view: ImageView) {
        radio.icon?.let {
            val imageName = resources.getIdentifier(
                it, "drawable", requireContext().packageName)

            Glide.with(requireActivity())
                .load(imageName)
                .placeholder(R.drawable.radio_record)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(view)
        }
    }

    private fun setName(radio: RadioStation, view: TextView) {
        view.text = radio.name
    }

    private inner class RadioHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val text: TextView = itemView.findViewById(R.id.general_item_path)
        private val action: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val frame: FrameLayout = itemView.findViewById(R.id.general_action_frame)
        private val icon: ImageButton = itemView.findViewById(R.id.general_item_icon)

        val selected: (RadioStation) -> Array<() -> Unit> = { radio ->
            arrayOf(
                {
                    icon.setImageResource(R.drawable.song_item_playing)
                }, {
                    itemView.setBackgroundResource(R.color.fragmentBackgroundOpacity)
                }, {
                    setName(radio, text)
                }, {
                    loadRadioStationIcon(radio, icon)
                }
            )
        }

        val notSelected: (RadioStation) -> Array<() -> Unit> = { radio ->
            arrayOf(
                {
                    itemView.setBackgroundResource(R.color.opacity)
                }, {
                    setName(radio, text)
                }, {
                    loadRadioStationIcon(radio, icon)
                }
            )
        }

        private val actionPopUpMenu: (RadioStation) -> Unit = { radio ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.radio_item }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {
                when (it.itemId) {
                    R.id.radio_rv_item_play_next -> {
                        playRadio(radio)
                        true
                    }
                    R.id.radio_rv_item_add_to_home_screen -> {
                        TODO()
                    }
                    else -> {
                        false
                    }
                }
            }

            setupPopupMenuOnClick(
                requireContext(),
                action,
                initActionMenuStyle,
                initActionMenuLayout,
                initActionMenuItemClickListener
            )
            Unit
        }

        private fun setOnClickAndImageResource(radio: RadioStation, f: (Int) -> Unit) {
            itemView.setOnClickListener {
                f(0)
                playRadio(radio)
            }
            text.setOnClickListener {
                f(1)
                playRadio(radio)
            }
            icon.setOnClickListener {
                f(2)
                playRadio(radio)
            }
            action.setOnClickListener {
                actionPopUpMenu(radio)
            }
            frame.setOnClickListener {
                actionPopUpMenu(radio)
            }
        }

        fun bindItem(radio: RadioStation,
                     position: Int,
                     f: (Array<() -> Unit>) -> (Array<() -> Unit>) -> (Int) -> Unit) {
            val setBackground = f(selected(radio))(notSelected(radio))
            setOnClickAndImageResource(radio, setBackground)
        }
    }

    private inner class RadioAdapter(val items: Array<out RadioStation>):
        RecyclerView.Adapter<RadioHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        private val rvSelectResolver =
            //just change old adapter to new
            if (viewModel.rvResolverIsInitialized()) {
                viewModel.rvResolver.adapter = this as RecyclerView.Adapter<RecyclerView.ViewHolder>
                viewModel.rvResolver
            }
            //new
            else {
                viewModel.rvResolver = RecyclerViewSelectItemResolver(
                    this as RecyclerView.Adapter<RecyclerView.ViewHolder>, 3, ""
                )
                viewModel.rvResolver
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )
            return RadioHolder(view)
        }

        override fun onBindViewHolder(holder: RadioHolder, position: Int) {
            items[position].apply {
                val f = rvSelectResolver.resolver(this.name)
                holder.bindItem(this, position, f)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }
}
