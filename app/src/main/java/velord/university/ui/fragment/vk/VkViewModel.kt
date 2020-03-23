package velord.university.ui.fragment.vk

import android.app.Application
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.apache.commons.text.similarity.LevenshteinDistance
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
import velord.university.model.FileNameParser
import velord.university.model.entity.Playlist
import velord.university.model.entity.vk.VkAlbum
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.repository.transaction.vk.VkAlbumTransaction
import velord.university.repository.transaction.vk.VkSongTransaction
import velord.university.ui.util.RecyclerViewSelectItemResolver
import java.io.File


class VkViewModel(private val app: Application) : AndroidViewModel(app) {

    private val TAG = "VkViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    lateinit var ordered: List<VkSong>
    lateinit var vkPlaylist: List<VkSong>
    lateinit var vkAlbums: List<VkAlbum>

    lateinit var currentQuery: String

    lateinit var rvResolver: RecyclerViewSelectItemResolver<String>

    suspend fun refreshByToken() {
        //from vk
        val byTokenSongs = getVkPlaylistByToken().items
        //from db
        val fromDbSongs = getSongsFromDb()
        val fromDbSongsId = fromDbSongs.map { it.id }
        val fromDbAlbums = getAlbumsFromDb()
        val fromDbAlbumsTitle = fromDbAlbums.map { it.title }
        val allSongFromDb = Playlist
            .allSongFromPlaylist(PlaylistTransaction.getAllPlaylist())
        val allSongPath = allSongFromDb.map { it.path }
        val allSongName = allSongFromDb.map { FileNameParser.removeExtension(it) }
        //compare with existed
        val notExistSong = getNoExistInDbSong(byTokenSongs,
            fromDbSongsId, allSongPath, allSongFromDb, allSongName)
        val notExistAlbum =
            getNoExistInDbAlbum(notExistSong, fromDbAlbumsTitle)
        //insert
        VkAlbumTransaction.addAlbum(*notExistAlbum.toTypedArray())
        VkSongTransaction.addSong(*notExistSong.toTypedArray())

        vkPlaylist = getSongsFromDb()
        vkAlbums = getAlbumsFromDb()
    }

    private fun getNoExistInDbAlbum(notExistInDbSong: List<VkSong>,
                                    fromDbAlbumsTitle: List<String>) =
        notExistInDbSong
            .fold(hashMapOf<String, VkAlbum>()) { notExist, vkSong: VkSong ->
                vkSong.album?.let {
                    if (fromDbAlbumsTitle.contains(it.title).not())
                        if (notExist.containsKey(it.title).not())
                            notExist += Pair(it.title, it)
                }
                notExist
            }
            .map { it.component2() }
            .filter { it.id != 0 }


    private fun getNoExistInDbSong(byTokenSongs: Array<VkSong>,
                                   fromDbSongsId: List<Int>,
                                   allSongPath: List<String>,
                                   allSongFromDb: List<File>,
                                   allSongName: List<String>): List<VkSong> =
        byTokenSongs.fold(mutableListOf<VkSong>()) { notExist, byToken ->
                if (fromDbSongsId.contains(byToken.id).not())
                    notExist.add(byToken)
                notExist
            }.map {
                if (it.album?.id != 0)
                    it.albumId = it.album?.id
                val index = getPathIndex(it, allSongName)
                if (index != -1) {
                    it.path = allSongPath[index]
                    it.artist = FileNameParser.getSongArtist(allSongFromDb[index])
                    it.title = FileNameParser.getSongName(allSongFromDb[index])
                } else it.path = ""
                it
            }


    private fun getPathIndex(vkSong: VkSong, allSongFromDb: List<String>): Int {
        val name = "${vkSong.artist} - ${vkSong.title}"
        val index = allSongFromDb.indexOf(name)
        if (index == -1) {
            allSongFromDb.forEachIndexed { fromDbIndex, vkName ->
                val dist = LevenshteinDistance().apply(name, vkName)
                if (dist in 0..4) return fromDbIndex
            }
        }
        return index
    }

    private suspend fun getAlbumsFromDb(): List<VkAlbum> =
        VkAlbumTransaction.getAlbums()

    private suspend fun getSongsFromDb(): List<VkSong> =
        VkSongTransaction.getSongs()

    suspend fun filterByQuery(query: String): List<VkSong> = withContext(Dispatchers.Default) {
        val filtered = vkPlaylist.filter {
            FileFilter.filterBySearchQuery("${it.artist} - ${it.title}", query)
        }
        //sort by name or artist or date added or duration or size
        val sorted = when(SortByPreference.getSortByVkFragment(app)) {
            //name
            0 -> filtered.sortedBy {
                FileFilter.getName(File(it.path))
            }
            //artist
            1 -> filtered.sortedBy {
                FileFilter.getArtist(File(it.path))
            }
            //date added
            2 -> filtered.sortedBy {
                FileFilter.getLastDateModified(File(it.path))
            }
            //duration TODO()
            3 -> {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                filtered.sortedBy {
                    mediaMetadataRetriever.setDataSource(File(it.path).absolutePath)
                    val durationStr = mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    durationStr.toLong()
                }
            }
            //file size
            4 -> filtered.sortedBy { FileFilter.getSize(File(it.path)) }
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

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQueryVk(app, currentQuery)
        val check = SearchQueryPreferences.getStoredQueryVk(app)
        Log.d(TAG, "retrieved: $check")
        Log.d(TAG, "stored: $currentQuery")
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun vkPlaylistIsInitialized() = ::vkPlaylist.isInitialized

    fun getSearchQuery(): String = SearchQueryPreferences.getStoredQueryVk(app)

    fun needDownload(vkSong: VkSong): Boolean =
        vkSong.path.isBlank()

    suspend fun applyNewPath(index: Int, file: File) {
        val song = vkPlaylist[index]
        vkPlaylist[index].path = file.path
        val orderedIndex = ordered.indexOf(song)
        ordered[orderedIndex].path = file.path

        VkSongTransaction.update(song)
    }

    fun playAudioAndAllSong(song: VkSong) {
        scope.launch {
            val file = File(song.path)
            MiniPlayerBroadcastPlayByPath.apply {
                SongPlaylistInteractor.songs = ordered
                    .filter { it.path.isNotBlank() }
                    .map { File(it.path) }
                    .toTypedArray()
                app.sendBroadcastPlayByPath(file.path)
            }
            MiniPlayerBroadcastLoopAll.apply {
                app.sendBroadcastLoopAll()
            }
        }
    }

    fun playAudio(song: VkSong) {
        val file = File(song.path)
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        MiniPlayerBroadcastPlayByPath.apply {
            app.sendBroadcastPlayByPath(file.path)
        }
        MiniPlayerBroadcastLoop.apply {
            app.sendBroadcastLoop()
        }
    }

    fun playAudioNext(song: VkSong) {
        val file = File(song.path)
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        //add to queue one song
        MiniPlayerBroadcastAddToQueue.apply {
            app.sendBroadcastAddToQueue(file.path)
        }
    }

    private suspend fun getVkPlaylistByToken(): VkPlaylist {
        val userId = VkPreference.getPageId(app)
        val token = VkPreference.getAccessToken(app)
        val baseUrl = app.getString(R.string.vk_base_url)
        val music = "${baseUrl}audio.get?user_ids=$userId&access_token=$token&v=5.80"

        return withContext(Dispatchers.IO) {
            val gson = Gson()
            val response = makeRequest(music)
            val json = JSONObject(response).getJSONObject("response")

            return@withContext gson
                .fromJson(json.toString(), VkPlaylist::class.java)
        }
    }

    private fun makeRequest(url: String): String {
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