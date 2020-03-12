package velord.university.ui.fragment.actionBar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import velord.university.application.settings.SearchQueryPreferences
import java.io.File

class ActionBarViewModel(private val app: Application) : AndroidViewModel(app) {
    val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        //need retrieve from shared preferences
        mutableSearchTerm.value = "-1"
    }

    fun setupSearchQueryByAlbumPreference() {
        mutableSearchTerm.value =
            SearchQueryPreferences.getStoredQueryAlbum(app)
    }

    fun setupSearchQueryByFilePath(file: File) {
        mutableSearchTerm.value =
            SearchQueryPreferences.getStoredQueryFolder(app, file.path)
    }
}