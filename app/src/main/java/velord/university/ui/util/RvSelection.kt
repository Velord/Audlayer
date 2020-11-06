package velord.university.ui.util

import androidx.recyclerview.widget.RecyclerView
import velord.university.model.coroutine.onMain
import kotlinx.coroutines.*

data class RvSelectionOld<T>(var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                             private val countOfView: Int,
                             private val defaultValue: T,
                             val isClickedHub: MutableList<T> = MutableList(countOfView) { defaultValue },
                             private var rvPosition: Int = 0,
                             private var maxScroll: Int = 50) {

    val scope = CoroutineScope(Job() + Dispatchers.Default)

    val resolver: (T) -> (Array<() -> Unit>) -> (Array<() -> Unit>) -> (Int) -> Unit =
        { storageValue ->
            { selected ->
                { notSelected ->
                    if (isClickedOnItem(storageValue)) selected.forEach { it() }
                    else notSelected.forEach { it() }
                    Unit

                    { clickedViewPos ->
                        userChangeItem(storageValue, clickedViewPos)
                    }
                }
            }
        }

    fun userChangeItem(key: T, pos: Int = 0) {
        scope.launch {
            clearClicked()
            changeClickedOn(pos, key)
        }
    }

    private fun scrollTo(index: Int,
                         rv: RecyclerView) {
        if ((rvPosition + index < rvPosition + maxScroll) &&
            (rvPosition - index < rvPosition - maxScroll))
            rv.smoothScrollToPosition(index)
        else
            rv.scrollToPosition(index)
    }

    suspend fun refreshAndScroll(items: List<T>,
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
        isClickedHub.map { defaultValue }.toMutableList()
    }

    private suspend fun changeClickedOn(posInHub: Int, value: T) {
        isClickedHub[posInHub] = value
        withContext(Dispatchers.Main) {
            adapter.notifyDataSetChanged()
        }
    }
}

data class RVSelection<T>(
    var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    var state: Int,
    val selected: MutableList<T> = mutableListOf(),
    var currentPosition: Int = 0
) {

    fun addSelected(value: T) {
        selected += value
    }

    fun clearSelected() {
        selected.clear()
    }

    suspend fun singleSelectionPrinciple(value: T) = onMain {
        state = 0
        clearAndChangeSelectedItem(value)
        adapter.notifyDataSetChanged()
    }

    fun clearAndChangeSelectedItem(value: T) {
        clearSelected()
        addSelected(value)
    }

    suspend fun scroll(rv: RecyclerView) = onMain {
        rv.scrollToPosition(currentPosition)
    }

    suspend inline fun refreshAndScroll(items: List<T>,
                                        rv: RecyclerView,
                                        crossinline f: (T) -> Boolean
    ) = onMain {
        adapter.notifyDataSetChanged()
        items.forEachIndexed { index, value ->
            if (f(value)) {
                rv.scrollToPosition(index)
                currentPosition = index
                return@forEachIndexed
            }
        }
    }

    inline fun isContains(value: T,
                          isContains: (T) -> Array<() -> Unit>,
                          isNotContains: (T) -> Array<() -> Unit>) {
        if (selected.contains(value)) isContains(value).forEach { it() }
        else isNotContains(value).forEach { it() }
    }
}