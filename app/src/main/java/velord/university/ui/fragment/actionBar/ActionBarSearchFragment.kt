package velord.university.ui.fragment.actionBar

import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import velord.university.model.exception.ViewDestroyed
import velord.university.databinding.ActionBarSearchBinding
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import velord.university.ui.util.view.gone
import velord.university.ui.util.view.initSearchWithHint
import velord.university.ui.util.view.setupPopupMenuOnClick
import velord.university.ui.util.view.visible

abstract class ActionBarSearchFragment :
    LoggerSelfLifecycleFragment() {

    override val TAG: String = "ActionBarFragmentDesign"

    protected val viewModelActionBarSearch: ActionBarSearchViewModel by viewModels()
    //view
    abstract var _bindingActionBar: ActionBarSearchBinding?
    // This property is only valid between onCreateView and
    // onDestroyView.
    protected val bindingActionBar get() = _bindingActionBar ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    protected fun initActionBar() {
        actionBarLeftMenu(bindingActionBar.settings)
        initSearchView()
        initActionButton()
        actionBarHintArticle(bindingActionBar.hint)
    }

    abstract val actionBarObserveSearchQuery: (String) -> Unit
    abstract val actionBarPopUpMenuStyle: () -> Int
    abstract val actionBarPopUpMenuItemOnCLick: (MenuItem) -> Boolean
    abstract val actionBarPopUpMenuLayout: () -> Int
    abstract val actionBarLeftMenu: (ImageButton) -> Unit
    abstract val actionBarPopUpMenu: (PopupMenu) -> Unit
    abstract val actionBarPopUp: (ImageButton) -> Unit
    abstract val actionSearchView: (SearchView) -> Unit
    abstract val actionBarHintArticle: (TextView) -> Unit

    private fun initActionButton() {
        actionBarPopUp(bindingActionBar.action)
        rearwardActionButton()
    }

    protected fun rearwardActionButton() {
        bindingActionBar.action.setupPopupMenuOnClick(
            requireContext(),
            actionBarPopUpMenuStyle,
            actionBarPopUpMenuLayout,
            actionBarPopUpMenuItemOnCLick
        ).also {
            actionBarPopUpMenu(it)
        }
    }

    private fun initSearchView() {
        actionSearchView(bindingActionBar.search)

        bindingActionBar.search.initSearchWithHint(
            TAG,
            bindingActionBar.hint,
            { viewModelActionBarSearch.mutableSearchTerm.value!! },
            { viewModelActionBarSearch.mutableSearchTerm.value = "" }) {
            viewModelActionBarSearch.mutableSearchTerm.value = it
            changeUIAfterSubmitTextInSearchView(bindingActionBar.search)
        }
    }

    protected fun changeUIAfterSubmitTextInSearchView(searchView: SearchView) {
        //hide the soft keyboard and collapse the SearchView.
        searchView.onActionViewCollapsed()
        //show hint
        bindingActionBar.hint.visible()
    }

    //controlling action bar frame visibility when recycler view is scrolling
    protected fun setScrollListenerByRecyclerViewScrolling(
        rv: RecyclerView,
        hideStartFrom: Int,
        showStartFrom: Int
    ) {

        rv.setOnScrollListener(object : RecyclerView.OnScrollListener(){

            var scroll_down = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (scroll_down) {
                    bindingActionBar.actionBarContainer.gone()
                } else {
                    bindingActionBar.actionBarContainer.visible()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //scroll down
                if (dy > hideStartFrom) scroll_down = true
                //scroll up
                else if (dy < showStartFrom) scroll_down = false
            }
        })
    }

    protected fun observeSearchQuery() {
        viewModelActionBarSearch.mutableSearchTerm
            .observe(viewLifecycleOwner) { searchTerm ->
                actionBarObserveSearchQuery(searchTerm)
            }
    }
}