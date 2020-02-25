package velord.university.ui

import android.content.Context
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper

fun initActionButton(context: Context,
                     actionButton: ImageButton,
                     initActionMenuStyle: () -> Int,
                     initActionMenuLayout: () -> Int,
                     initActionMenuItemClickListener: (MenuItem) -> Boolean) {
    actionButton.setOnClickListener {
        val style = initActionMenuStyle()
        val contextThemeWrapper = ContextThemeWrapper(context, style)
        val popupMenu = PopupMenu(contextThemeWrapper, it)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            initActionMenuItemClickListener(menuItem)
        }
        val inflater = popupMenu.menuInflater
        val layout = initActionMenuLayout()
        inflater.inflate(layout, popupMenu.menu)
        popupMenu.show()
    }
}