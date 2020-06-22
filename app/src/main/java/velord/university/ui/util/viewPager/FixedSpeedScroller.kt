package velord.university.ui.util.viewPager

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

//https://github.com/Chrisvin/LiquidSwipe

internal class FixedSpeedScroller(context: Context?,
                                  interpolator: Interpolator?) :
    Scroller(context, interpolator) {

    var scrollerDuration = 1000

    override fun startScroll(
        startX: Int,
        startY: Int,
        dx: Int,
        dy: Int,
        duration: Int
    ) { // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, this.scrollerDuration)
    }

    override fun startScroll(
        startX: Int,
        startY: Int,
        dx: Int,
        dy: Int
    ) { // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, scrollerDuration)
    }
}