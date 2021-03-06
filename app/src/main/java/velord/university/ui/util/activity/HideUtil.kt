package velord.university.ui.util.activity

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import velord.university.R

fun Activity.hideKeyboard() {
    val view: View = findViewById(R.id.content)

    val imm: InputMethodManager = getSystemService(
        Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideVirtualButtons() {
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
}

fun Activity.hideStatusBarAndNoTitle() {
    //app will go fullscreen. no status bar, no title bar. :)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
}

inline fun View.showOrHide(show: () -> Unit = {},
                           hide: () -> Unit = {}) {
    val currentView = this.visibility == View.VISIBLE
    if (currentView) {
        this.visibility = View.GONE
        hide()
    }
    else {
        this.visibility = View.VISIBLE
        show()
    }
}