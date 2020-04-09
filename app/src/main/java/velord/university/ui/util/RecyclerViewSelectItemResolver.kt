package velord.university.ui.util

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class RecyclerViewSelectItemResolver<T>(var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                                              private val countOfView: Int,
                                              private val defaultValue: T,
                                              private var isClickedHub: MutableList<T> = MutableList(countOfView) { defaultValue },
                                              private var rvPosition: Int = 0,
                                              private var maxScroll: Int = 50) {

    val resolver: (T) -> (Array<() -> Unit>) -> (Array<() -> Unit>) -> (Int) -> Unit =
        { storageValue ->
            { isAction ->
                { isNotAction ->
                    if (isClickedOnItem(storageValue)) isAction.forEach { it() }
                    else isNotAction.forEach { it() }
                    Unit
                    { clickedViewPos ->
                        userChangeSong(storageValue, clickedViewPos)
                    }
                }
            }
        }

    fun userChangeSong(path: T, pos: Int = 0) {
        clearClicked()
        changeClickedOn(pos, path)
    }

    private fun scrollTo(index: Int,
                         rv: RecyclerView) {
        if ((rvPosition + index < rvPosition + maxScroll) &&
            (rvPosition - index < rvPosition - maxScroll))
            rv.smoothScrollToPosition(index)
        else
            rv.scrollToPosition(index)
    }

    suspend fun applyToRvItem(items: List<T>,
                              rv: RecyclerView,
                              f: (T) -> Boolean) =
        withContext(Dispatchers.Main) {
            adapter.notifyDataSetChanged()
            items.forEachIndexed { index, value ->
                if (f(value)) {
                    scrollTo(index, rv)
                    return@forEachIndexed
                }
            }
        }

    private fun isClickedOnItem(value: T): Boolean  {
        var isClicked = false
        isClickedHub.forEach {
            if (it == value) isClicked = true
        }
        return isClicked
    }

    private fun clearClicked() {
        isClickedHub = isClickedHub.map { defaultValue }.toMutableList()
    }

    private val changeClickedOn: (Int, T) -> Unit = { posInHub, value ->
        isClickedHub[posInHub] = value
    }
}