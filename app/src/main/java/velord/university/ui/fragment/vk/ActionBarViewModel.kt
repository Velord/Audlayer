package velord.university.ui.fragment.vk

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ActionBarViewModel(private val app: Application) : AndroidViewModel(app) {
    val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        //need retrieve from  QueryPreferences.getStoredQuery(app)
        mutableSearchTerm.value = ""
    }
}