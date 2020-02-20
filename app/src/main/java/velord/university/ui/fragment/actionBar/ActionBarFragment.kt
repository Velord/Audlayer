package velord.university.ui.fragment.actionBar

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import velord.university.R
import velord.university.ui.fragment.LoggerSelfLifecycleFragment

abstract class ActionBarFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String = "ActionBarFragment"

    protected val viewModelActionBar by lazy {
        ViewModelProviders.of(this).get(ActionBarViewModel::class.java)
    }

    protected lateinit var actionBarFrame: FrameLayout
    protected lateinit var menu: ImageButton
    protected lateinit var searchView: SearchView
    protected lateinit var actionButton: ImageButton
    protected lateinit var hint: TextView

    protected fun initActionBar(view: View) {
        actionBarFrame = view.findViewById(R.id.action_bar_frame_layout)
        menu = view.findViewById(R.id.action_bar_settings)

        initSearchView(view)

        hint = view.findViewById(R.id.action_bar_hint)
        initActionButton(view)
    }

    private fun initSearchView(view: View) {
        searchView = view.findViewById(R.id.action_bar_searchView)
        searchView.apply {

            setOnCloseListener {
                hint.visibility = View.VISIBLE
                viewModelActionBar.mutableSearchTerm.value = ""
                false
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $query")
                    query?.let {
                        viewModelActionBar.mutableSearchTerm.value = it
                        changeUIAfterSubmitTextInSearchView(searchView)
                        return false
                    }
                    hint.visibility = View.VISIBLE
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "QueryTextChange: $newText")
                    hint.visibility = View.GONE
                    return false
                }
            })

            setOnSearchClickListener {
                hint.visibility = View.GONE
                searchView.setQuery(viewModelActionBar.searchTerm, false)
            }
        }
    }

    private fun initActionButton(view: View) {
        actionButton =  view.findViewById(R.id.action_bar_action)

        actionButton.apply {
            setOnClickListener {

            }
        }
    }

    protected fun changeUIAfterSubmitTextInSearchView(searchView: SearchView) {
        //hide the soft keyboard and collapse the SearchView.
        searchView.onActionViewCollapsed()
        //show hint
        hint.visibility = View.VISIBLE
    }

    //controlling action bar frame visibility when recycler view is scrolling
    protected fun setOnScrollListenerBasedOnRecyclerViewScrolling(
        rv: RecyclerView, hideStartFrom: Int, showStartFrom: Int) {
        rv.setOnScrollListener(object : RecyclerView.OnScrollListener(){

            var scroll_down = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (scroll_down) {
                    actionBarFrame.visibility = View.GONE
                } else {
                    actionBarFrame.visibility = View.VISIBLE
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //scroll down
                if (dy > hideStartFrom) scroll_down = true
                //scroll up
                else if (dy < -showStartFrom) scroll_down = false
            }
        })
    }

    abstract fun observeSearchTerm()
}