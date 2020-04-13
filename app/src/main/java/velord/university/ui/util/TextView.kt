package velord.university.ui.util

import android.widget.TextView

fun TextView.setAutoScrollable() {
    setSingleLine()
    isSelected = false
}