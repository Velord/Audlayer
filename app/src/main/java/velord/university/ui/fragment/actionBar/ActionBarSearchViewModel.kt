package velord.university.ui.fragment.actionBar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ActionBarSearchViewModel(app: Application) : AndroidViewModel(app) {
    val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        //need retrieve from shared preferences
        mutableSearchTerm.postValue("-1")
    }

    fun setupSearchQuery(query: String) {
        mutableSearchTerm.postValue(query)
    }
}