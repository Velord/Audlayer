package velord.university.ui.fragment.radio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.model.entity.RadioStation
import velord.university.ui.util.RecyclerViewSelectItemResolver

class RadioViewModel(private val app: Application) : AndroidViewModel(app) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    lateinit var currentQuery: String

    lateinit var rvResolver: RecyclerViewSelectItemResolver<String>

    private val radioPlaylist: List<RadioStation> by lazy {
        AudlayerApp.db!!.run {
            radioDao().getAll()
        }
    }

    lateinit var ordered: List<RadioStation>

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun playRadio(radio: RadioStation) {
        AppBroadcastHub.apply {
            app.playByUrlRadioService(radio.url)
        }
    }

    fun getSearchQuery(): String =
        SearchQueryPreferences.getStoredQueryRadio(app)

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQueryRadio(app, currentQuery)
    }

    suspend fun filterByQuery(query: String): List<RadioStation> =
        withContext(Dispatchers.Default) {
            val filtered = radioPlaylist.filter {
                it.name.contains(query)
            }
            //sort by name or artist or date added or duration or size
            val sorted = when(SortByPreference.getSortByRadioFragment(app)) {
                //name
                0 -> filtered.sortedBy {
                    TODO()
                }
                //artist
                1 -> filtered.sortedBy {
                    TODO()
                }
                else -> filtered
            }
            // sort by ascending or descending order
            ordered = when(SortByPreference.getAscDescRadioFragment(app)) {
                0 -> sorted
                1 ->  sorted.reversed()
                else -> sorted
            }

            return@withContext ordered
        }
}
