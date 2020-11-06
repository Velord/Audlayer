package velord.university.ui.util.view

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu

fun View.setupAndShowPopupMenuOnClick(context: Context,
                                      initActionMenuStyle: () -> Int,
                                      initActionMenuLayout: () -> Int,
                                      initActionMenuItemClickListener: (MenuItem) -> Boolean): PopupMenu  =
    setupPopupMenuOnClick(context,
        initActionMenuStyle, initActionMenuLayout,
        initActionMenuItemClickListener
    ).apply { show() }


fun View.setupPopupMenuOnClick(context: Context,
                               initActionMenuStyle: () -> Int,
                               initActionMenuLayout: () -> Int,
                               initActionMenuItemClickListener: (MenuItem) -> Boolean): PopupMenu {
    val style = initActionMenuStyle()
    val contextThemeWrapper = ContextThemeWrapper(context, style)
    val popupMenu = PopupMenu(contextThemeWrapper, this)
    popupMenu.setOnMenuItemClickListener { menuItem ->
        initActionMenuItemClickListener(menuItem)
    }
    val inflater = popupMenu.menuInflater
    val layout = initActionMenuLayout()
    inflater.inflate(layout, popupMenu.menu)

    setOnClickListener {
        popupMenu.show()
    }

    return popupMenu
}

fun Menu.makeCheck(index: Int) {
    this.getItem(index).isChecked = true
}