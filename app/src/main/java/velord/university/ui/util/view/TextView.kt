package velord.university.ui.util.view

import android.widget.TextView

fun TextView.setAutoScrollable() {
    setSingleLine()
    isSelected = false
}