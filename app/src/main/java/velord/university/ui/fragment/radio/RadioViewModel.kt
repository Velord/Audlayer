package velord.university.ui.fragment.radio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.RadioInteractor
import velord.university.model.entity.RadioStation
import velord.university.repository.RadioRepository
import velord.university.ui.util.RVSelection

class RadioViewModel(private val app: Application) : AndroidViewModel(app) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    lateinit var currentQuery: String

    private lateinit var currentRadio: RadioStation

    lateinit var rvResolver: RVSelection<RadioStation>

    private lateinit var radioPlaylist: List<RadioStation>

    lateinit var ordered: List<RadioStation>

    init {
        scope.launch {
            reassignmentRadioPlaylist()
        }
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun currentRadioIsInitialized(): Boolean = ::currentRadio.isInitialized

    fun playRadio(radio: RadioStation) {
        scope.launch {
            currentRadio = radio
            //first of all reassignment interactor
            RadioInteractor.radioStation = radio
            AppBroadcastHub.apply {
                app.showUI()
                app.playByUrlRadioService(radio.url)
            }
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
                    it.name
                }
                //like
                1 -> filtered.sortedBy {
                    it.liked
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

    private suspend fun reassignmentRadioPlaylist() {
        radioPlaylist = RadioRepository.getAll()
    }
}
