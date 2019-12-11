package velord.university.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import kotlin.math.abs


class MainFragment : MenuNowPlayingFragment() {

    override val TAG: String
        get() = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var  nowPlaying: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false).apply {
            super.initMenuFragmentView(this)
            initView(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(view: View) {
        nowPlaying = view.findViewById(R.id.now_playing_frame)
        val detector =
            GestureDetector(activity, Gesture(activity as Context, nowPlaying))
        val touchListener = OnTouchListener { v, event ->
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            detector.onTouchEvent(event)
        }
        nowPlaying.setOnTouchListener(touchListener)
    }
}

const val SWIPE_THRESHOLD = 100
const val SWIPE_VELOCITY_THRESHOLD = 100

class Gesture (
    private val context: Context,
    private val view: View) : GestureDetector.OnGestureListener {

    private fun onSwipeTop() {
        Toast.makeText(
            context, "Swipe Top, Close NowPlaying", Toast.LENGTH_LONG).show()
        view.visibility = View.GONE
    }

    private fun onSwipeBottom() {
        Toast.makeText(
            context, "Swipe Bottom, Close NowPlaying", Toast.LENGTH_LONG).show()
        view.visibility = View.GONE
    }

    private fun onSwipeLeft() {
        Toast.makeText(
            context, "Swipe Left, Close NowPlaying", Toast.LENGTH_LONG).show()
        view.visibility = View.GONE
    }

    private fun onSwipeRight() {
        Toast.makeText(
            context, "Swipe Right, Close NowPlaying", Toast.LENGTH_LONG).show()
        view.visibility = View.GONE
    }

    override fun onDown(e: MotionEvent?): Boolean { return true }

    override fun onFling(
        downEvent: MotionEvent?,
        moveEvent: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        var result = false
        downEvent?.let {
            moveEvent?.let {
                val diffY: Float = moveEvent.y - downEvent.y
                val diffX: Float = moveEvent.x - downEvent.x
                // which was greater?  movement across Y or X?
                // which was greater?  movement across Y or X?
                if (abs(diffX) > abs(diffY)) { // right or left swipe
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) onSwipeRight()
                        else onSwipeLeft()
                        result = true
                    }
                    // up or down swipe
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0)  onSwipeBottom()
                    else onSwipeTop()
                    result = true
                }
            }
        }

        return result
    }

    override fun onLongPress(e: MotionEvent?) { }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean { return false }

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?): Boolean { return false }
}
