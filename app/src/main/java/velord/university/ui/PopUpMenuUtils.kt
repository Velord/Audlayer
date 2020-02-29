package velord.university.ui

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper

fun setupPopupMenuOnClick(context: Context,
                          view: View,
                          initActionMenuStyle: () -> Int,
                          initActionMenuLayout: () -> Int,
                          initActionMenuItemClickListener: (MenuItem) -> Boolean): PopupMenu {
    val style = initActionMenuStyle()
    val contextThemeWrapper = ContextThemeWrapper(context, style)
    val popupMenu = PopupMenu(contextThemeWrapper, view)
    popupMenu.setOnMenuItemClickListener { menuItem ->
        initActionMenuItemClickListener(menuItem)
    }
    val inflater = popupMenu.menuInflater
    val layout = initActionMenuLayout()
    inflater.inflate(layout, popupMenu.menu)

    view.setOnClickListener {
        popupMenu.show()
    }

    return popupMenu
}