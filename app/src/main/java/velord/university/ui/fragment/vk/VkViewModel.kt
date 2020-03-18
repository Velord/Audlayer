package velord.university.ui.fragment.vk

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import velord.university.R
import velord.university.application.broadcast.MiniPlayerBroadcastAddToQueue
import velord.university.application.broadcast.MiniPlayerBroadcastLoop
import velord.university.application.broadcast.MiniPlayerBroadcastLoopAll
import velord.university.application.broadcast.MiniPlayerBroadcastPlayByPath
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.application.settings.VkPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.entity.vk.VkPlaylist
import velord.university.ui.util.RecyclerViewSelectItemResolver
import java.io.File
import java.io.IOException

class VkViewModel(private val app: Application) : AndroidViewModel(app) {

    private val TAG = "VkViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    lateinit var songs: List<File>
    lateinit var ordered: List<File>
    lateinit var vkPlaylist: VkPlaylist

    lateinit var currentQuery: String

    lateinit var rvResolver: RecyclerViewSelectItemResolver<String>

    suspend fun filterByQuery(query: String): List<File> = withContext(Dispatchers.Default) {
        val filtered = songs.filter {
            FileFilter.filterBySearchQuery(it, query)
        }
        //sort by name or artist or date added or duration or size
        val sorted = when(SortByPreference.getSortByVkFragment(app)) {
            //name
            0 -> filtered.sortedBy {
                FileFilter.getName(it)
            }
            //artist
            1 -> filtered.sortedBy {
                FileFilter.getArtist(it)
            }
            //date added
            2 -> filtered.sortedBy {
                FileFilter.getLastDateModified(it)
            }
            //duration TODO()
            3 -> {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                filtered.sortedBy {
                    mediaMetadataRetriever.setDataSource(it.absolutePath)
                    val durationStr = mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    durationStr.toLong()
                }
            }
            //file size
            4 -> filtered.sortedBy { FileFilter.getSize(it) }
            else -> filtered
        }
        // sort by ascending or descending order
        ordered = when(SortByPreference.getAscDescVkFragment(app)) {
            0 -> sorted
            1 ->  sorted.reversed()
            else -> sorted
        }

        return@withContext ordered
    }

    fun shuffle(): List<File> = ordered.shuffled()

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQuerySong(app, currentQuery)
        val check = SearchQueryPreferences.getStoredQuerySong(app)
        Log.d(TAG, "retrieved: $check")
        Log.d(TAG, "stored: $currentQuery")
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun songsIsInitialized() = ::songs.isInitialized

    fun getSearchQuery(): String = SearchQueryPreferences.getStoredQueryVk(app)

    fun playAudioAndAllSong(file: File) {
        MiniPlayerBroadcastPlayByPath.apply {
            SongPlaylistInteractor.songs = ordered.toTypedArray()
            app.sendBroadcastPlayByPath(file.path)
        }
        MiniPlayerBroadcastLoopAll.apply {
            app.sendBroadcastLoopAll()
        }
    }

    fun playAudio(file: File) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        MiniPlayerBroadcastPlayByPath.apply {
            app.sendBroadcastPlayByPath(file.path)
        }
        MiniPlayerBroadcastLoop.apply {
            app.sendBroadcastLoop()
        }
    }

    fun playAudioNext(file: File) {
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        //add to queue one song
        MiniPlayerBroadcastAddToQueue.apply {
            app.sendBroadcastAddToQueue(file.path)
        }
    }

    suspend fun getAudio(): VkPlaylist {
        val userId = VkPreference.getPageId(app)
        val token = VkPreference.getAccessToken(app)
        val baseUrl = app.getString(R.string.vk_base_url)
        val music = "${baseUrl}audio.get?user_ids=$userId&access_token=$token&v=5.80"

        val valid: (Response) -> Unit = {
            Unit
        }
        val invalid: (IOException) -> Unit = {
            Unit
        }

        return withContext(Dispatchers.IO) {
            val gson = Gson()
            val response = makeRequest(music, valid, invalid)
            val json = JSONObject(response).getJSONObject("response")
            vkPlaylist = gson
                .fromJson(json.toString(), VkPlaylist::class.java)
            return@withContext vkPlaylist
        }
    }

    private fun makeRequest(
        url: String,
        valid: (Response) -> Unit,
        invalid: (IOException) -> Unit): String {

        val client = OkHttpClient()
        val mimeType = "application/json; charset=utf-8".toMediaType()
        val requestBody = RequestBody.create(mimeType, "{}")
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return client.newCall(request).execute().body!!.string()
    }
}