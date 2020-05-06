package velord.university.ui.util.viewPager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

open class DynamicHeightViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var currentView: View

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (::currentView.isInitialized) {
            currentView.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val height = Math.max(0, currentView.getMeasuredHeight())
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun measureCurrentView(currentView: View) {
        this.currentView = currentView
        requestLayout()
    }
}
