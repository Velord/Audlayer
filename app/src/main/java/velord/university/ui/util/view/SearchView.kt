package velord.university.ui.util.view

import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.SearchView

inline fun SearchView.initSearchWithHint(
    TAG: String,
    hint: TextView,
    crossinline getQuery: () -> String,
    crossinline onClose: () -> Unit,
    crossinline onTextSubmit: (String) -> Unit,
) {

    setOnCloseListener {
        hint.visible()
        onClose()
        false
    }

    setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean {
            Log.d(TAG, "QueryTextSubmit: $query")
            query?.let {
                onTextSubmit(it)
                return false
            }
            hint.visible()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            Log.d(TAG, "QueryTextChange: $newText")
            hint.gone()
            return false
        }
    })

    //on icon
    setOnSearchClickListener {
        hint.gone()
        setQuery(getQuery(), false)
    }
    //on all search view
    setOnClickListener {
        this.isIconified = (false)
        setQuery(getQuery(), false)
    }
}

inline fun SearchView.initSearch(
    TAG: String,
    crossinline getQuery: () -> String,
    crossinline onClose: () -> Unit,
    crossinline onTextSubmit: (String) -> Unit,
) {

    setOnCloseListener {
        onClose()
        false
    }

    setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean {
            Log.d(TAG, "QueryTextSubmit: $query")
            query?.let {
                onTextSubmit(it)
                return false
            }
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            Log.d(TAG, "QueryTextChange: $newText")
            return false
        }
    })

    //on icon
    setOnSearchClickListener {
        setQuery(getQuery(), false)
    }
    //on all search view
    setOnClickListener {
        this.isIconified = (false)
        setQuery(getQuery(), false)
    }
}