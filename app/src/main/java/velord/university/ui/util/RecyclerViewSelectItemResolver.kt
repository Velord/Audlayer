package velord.university.ui.util

import androidx.recyclerview.widget.RecyclerView

data class  RecyclerViewSelectItemResolver<T>(var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                                              private val countOfView: Int,
                                              private val defaultValue: T,
                                              private var isClickedHub: MutableList<T> = MutableList(countOfView) { defaultValue } ) {

    val resolver: (T) -> (Array<() -> Unit>) -> (Array<() -> Unit>) -> (Int) -> Unit =
        { storageValue ->
            { isAction ->
                { isNotAction ->
                    if (isClickedOnItem(storageValue)) isAction.forEach { it() }
                    else isNotAction.forEach { it() }
                    Unit
                    { clickedViewPos ->
                        clearClicked()
                        changeClickedOn(clickedViewPos, storageValue)
                        adapter.notifyDataSetChanged()
                    }
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