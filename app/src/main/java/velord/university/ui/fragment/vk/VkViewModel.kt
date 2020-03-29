package velord.university.ui.fragment.vk

import android.app.Application
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.apache.commons.text.similarity.LevenshteinDistance
import org.json.JSONObject
import velord.university.R
import velord.university.application.broadcast.MiniPlayerBroadcastHub
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
import velord.university.repository.fetch.SefonFetch
import velord.university.repository.fetch.makeRequestViaOkHttp
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.repository.transaction.vk.VkAlbumTransaction
import velord.university.repository.transaction.vk.VkSongTransaction
import velord.university.ui.util.RecyclerViewSelectItemResolver
import java.io.File


class VkViewModel(private val app: Application) : AndroidViewModel(app) {

    private val TAG = "VkViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    lateinit var ordered: List<VkSong>
    private lateinit var vkPlaylist: List<VkSong>
    private lateinit var vkAlbums: List<VkAlbum>

    lateinit var currentQuery: String

    lateinit var rvResolver: RecyclerViewSelectItemResolver<String>

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

    fun playAudioNext(song: VkSong) {
        val file = File(song.path)
        //don't remember for SongQuery Interactor it will be used between this and service
        SongPlaylistInteractor.songs = arrayOf(file)
        //add to queue one song
        MiniPlayerBroadcastHub.apply {
            app.addToQueueService(file.path)
        }
    }

    fun checkPathThenPlay(vkSong: VkSong, webView: WebView) {
        scope.launch {
            if (needDownload(vkSong)) download(vkSong, webView)
            else playAudioAndAllSong(vkSong)
        }
    }

    suspend fun refreshByToken() {
        //from vk
        val byTokenSongs = getVkPlaylistByToken().items
        //from db
        val fromDbSongs = getSongsFromDb()
        //compare with existed and insert
        compareAndInsert(byTokenSongs, fromDbSongs)
        //compare with existed and delete
        compareAndDelete(byTokenSongs, fromDbSongs)
        //create vkPlaylist
        initVkPlaylist()
    }

    suspend fun download(vkSong: VkSong, webView: WebView) {
        //refresh path to blank
        applyNewPath(vkSong, "")
        //if download will be success
        val ifDownload: (File) -> Unit = {
            scope.launch {
                applyNewPath(vkSong, it.path)
                playAudioAndAllSong(vkSong)
                withContext(Dispatchers.Main) {
                    Toast.makeText(app,
                        "Song success downloaded", Toast.LENGTH_LONG).show()
                }
            }
        }
        //download
        SefonFetch(app, webView, vkSong).download(ifDownload)
    }

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
                it.date
            }
            3 -> filtered.sortedBy { it.duration }
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


    private suspend fun getNoExistInDbSong(byTokenSongs: Array<VkSong>,
                                           fromDbSongs: List<VkSong>): List<VkSong> {
        val fromDbSongsId = fromDbSongs.map { it.id }
        val allSongFromDb = Playlist
            .allSongFromPlaylist(PlaylistTransaction.getAllPlaylist())
        val allSongPath = allSongFromDb.map { it.path }
        val allSongName = allSongFromDb.map { FileNameParser.removeExtension(it) }
        return byTokenSongs.fold(mutableListOf<VkSong>()) { notExist, byToken ->
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

    //path must be not blank and file can be created by that path
    private fun needDownload(vkSong: VkSong): Boolean =
        (vkSong.path.isNotBlank()) and (File(vkSong.path).exists().not())

    private fun playAudioAndAllSong(song: VkSong) {
        scope.launch {
            val file = File(song.path)
            MiniPlayerBroadcastHub.apply {
                SongPlaylistInteractor.songs = ordered
                    .filter { it.path.isNotBlank() }
                    .map { File(it.path) }
                    .toTypedArray()
                app.playByPathService(file.path)
                app.loopAllService()
            }
        }
    }

    private suspend fun getAlbumsFromDb(): List<VkAlbum> =
        VkAlbumTransaction.getAlbums()

    private suspend fun getSongsFromDb(): List<VkSong> =
        VkSongTransaction.getSongs()

    private suspend fun getVkPlaylistByToken(): VkPlaylist {
        val userId = VkPreference.getPageId(app)
        val token = VkPreference.getAccessToken(app)
        val baseUrl = app.getString(R.string.vk_base_url)
        val music = "${baseUrl}audio.get?user_ids=$userId&access_token=$token&v=5.80"

        return withContext(Dispatchers.IO) {
            val gson = Gson()
            val response = music.makeRequestViaOkHttp()
            val json = JSONObject(response).getJSONObject("response")

            return@withContext gson
                .fromJson(json.toString(), VkPlaylist::class.java)
        }
    }

    private suspend fun applyNewPath(vkSong: VkSong, path: String) {
        val index = vkPlaylist.indexOf(vkSong)
        val song = vkPlaylist[index]
        vkPlaylist[index].path = path
        val orderedIndex = ordered.indexOf(song)
        ordered[orderedIndex].path = path

        VkSongTransaction.update(song)
    }

    private suspend fun compareAndInsert(byTokenSongs: Array<VkSong>,
                                         fromDbSongs: List<VkSong>) {
        //song
        val notExistSong = getNoExistInDbSong(byTokenSongs, fromDbSongs)
        //album
        val fromDbAlbums = getAlbumsFromDb()
        val fromDbAlbumsTitle = fromDbAlbums.map { it.title }
        val notExistAlbum =
            getNoExistInDbAlbum(notExistSong, fromDbAlbumsTitle)
        //insert
        VkAlbumTransaction.addAlbum(*notExistAlbum.toTypedArray())
        VkSongTransaction.addSong(*notExistSong.toTypedArray())
    }

    private suspend fun compareAndDelete(byTokenSongs: Array<VkSong>,
                                         fromDbSongs: List<VkSong>) {
        val toDelete = mutableListOf<VkSong>()
        fromDbSongs.forEach { fromDb ->
            if (byTokenSongs.find { it.id == fromDb.id } == null)
                toDelete.add(fromDb)
        }
        VkSongTransaction.delete(*toDelete.toTypedArray())
    }

    private suspend fun initVkPlaylist() {
        vkAlbums = getAlbumsFromDb()
        vkPlaylist = getSongsFromDb().map { song ->
            song.albumId?.let { albumId ->
                val indexAlbum = vkAlbums.find { it.id == albumId }
                indexAlbum?.let {
                    song.album = it
                }
            }
            song
        }
    }
}