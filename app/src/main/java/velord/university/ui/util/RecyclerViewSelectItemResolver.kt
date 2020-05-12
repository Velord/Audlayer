package velord.university.ui.util

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

data class RecyclerViewSelectItemResolver<T>(var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
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
        isClickedHub.map { defaultValue }.toMutableList()
    }

    private suspend fun changeClickedOn(posInHub: Int, value: T): Unit {
        isClickedHub[posInHub] = value
        withContext(Dispatchers.Main) {
            adapter.notifyDataSetChanged()
        }
    }
}

data class RVSelection<T>(
    var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    var state: Int,
    val selected: MutableList<T> = mutableListOf()
) {

    fun addSelected(value: T) {
        selected += value
    }

    fun clearSelected() {
        selected.clear()
    }

    suspend fun singleSelectionPrinciple(value: T) = withContext(Dispatchers.Main) {
        state = 0
        clearSelected()
        addSelected(value)
        adapter.notifyDataSetChanged()
    }
}

data class Layer<T>(val index: Int,
    //Pair is: first -> selected, second -> notSelected functions to implement
                    val elements: MutableList<LayerElement<T>> = mutableListOf())

data class LayerElement<T>(val value: T,
                           val f: Pair<(T) -> Array<() -> Unit>,
                                       (T) -> Array<() -> Unit>>)

data class Experement<T>(var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                         private var rvPosition: Int = 0,
                         private var maxScroll: Int = 50) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val selected: MutableList<T> = mutableListOf()
    //represent functions which will be invoke to every items by selection principle
    //first mutable list is layer
    //second mutable list is elements
    //Pair is first -> selected, second -> notSelected functions to implement
    private val layers: MutableList<Layer<T>> = mutableListOf()

    fun addLayer(indexLayer: Int,
                 value: T,
                 selected: (T) -> Array<() -> Unit>,
                 notSelected: (T) -> Array<() -> Unit>) {
        //create new if not exist
        if (layers.lastIndex < indexLayer)
            layers.add(Layer(indexLayer))

        layers[indexLayer]
            .elements
            .add(LayerElement(
                value,
                Pair(selected, notSelected)
            ))
    }

    suspend fun applyLayer(value: T, indexLayer: Int) = withContext(Dispatchers.Main) {
        val element = layers[indexLayer].elements.find { it.value == value }
        if (isClickedValue(element!!.value)) element.f.first(value).forEach { it() }
        else  element.f.second(value).forEach { it() }
    }

    fun addSelected(value: T) {
        selected += value
    }

    fun clearSelected() {
        selected.clear()
    }

    suspend fun singleSelectionPrinciple(value: T) = withContext(Dispatchers.Main) {
        clearSelected()
        addSelected(value)
        adapter.notifyDataSetChanged()
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

    private fun scrollTo(index: Int,
                         rv: RecyclerView) {
        if ((rvPosition + index < rvPosition + maxScroll) &&
            (rvPosition - index < rvPosition - maxScroll))
            rv.smoothScrollToPosition(index)
        else
            rv.scrollToPosition(index)
    }

    private fun isClickedValue(value: T): Boolean  {
        var isClicked = false
        selected.forEach {
            if (it == value) isClicked = true
        }
        return isClicked
    }
}