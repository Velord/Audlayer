package velord.university.ui.fragment.actionBar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import velord.university.application.QueryPreferences

class ActionBarViewModel(private val app: Application) : AndroidViewModel(app) {
    val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        //need retrieve from shared preferences
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
    }
}