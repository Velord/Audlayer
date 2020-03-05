package velord.university.ui.fragment.album

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import velord.university.application.settings.SearchQueryPreferences

class AlbumViewModel(private val app: Application) : AndroidViewModel(app) {

    val TAG = "AlbumViewModel"

    lateinit var currentQuery: String
    

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQueryAlbum(app, currentQuery)
        Log.d(TAG, "query: $currentQuery")
    }
}
