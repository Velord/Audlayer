package velord.university.ui.fragment

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import velord.university.R

abstract class ActionBarFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String = "ActionBarFragment"

    protected lateinit var actionBarFrame: FrameLayout
    protected lateinit var menu: ImageButton
    protected lateinit var hint: TextView
    protected lateinit var seek: ImageButton
    protected lateinit var action: ImageButton

    protected fun initActionBar(view: View) {
        actionBarFrame = view.findViewById(R.id.action_bar_frame_layout)

        menu = view.findViewById(R.id.top_menu_settings)

        hint = view.findViewById(R.id.top_menu_hint)

        seek = view.findViewById(R.id.top_menu_seek)

        action =  view.findViewById(R.id.top_menu_action)
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
}