package velord.university.ui.fragment.vk

import android.app.Application
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import org.apache.commons.text.similarity.LevenshteinDistance
import velord.university.application.AudlayerApp
import velord.university.application.broadcast.MiniPlayerBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import velord.university.model.entity.Playlist
import velord.university.model.entity.vk.VkAlbum
import velord.university.model.entity.vk.VkSong
import velord.university.model.functionalDataSctructure.result.Result
import velord.university.repository.VkRepository
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.ui.util.RecyclerViewSelectItemResolver
import java.io.File


class VkViewModel(private val app: Application) : AndroidViewModel(app) {

    private val TAG = "VkViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    val repository = VkRepository

    lateinit var ordered: List<VkSong>
    private lateinit var vkPlaylist: List<VkSong>
    private lateinit var vkAlbums: List<VkAlbum>

    lateinit var currentQuery: String

    lateinit var rvResolver: RecyclerViewSelectItemResolver<String>

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences.setStoredQueryVk(app, currentQuery)
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
            if (needDownload(vkSong)) downloadInform(vkSong, webView)
            else playAudioAndAllSong(vkSong)
        }
    }

    //path must be not blank and file can be created by that path
    fun needDownload(vkSong: VkSong): Boolean =
        (vkSong.path.isBlank()) and (File(vkSong.path).exists().not())

    suspend fun initVkPlaylist() {
        vkAlbums = repository.getAlbumsFromDb()
        vkPlaylist = repository.getSongsFromDb().map { song ->
            song.albumId?.let { albumId ->
                val indexAlbum = vkAlbums.find { it.id == albumId }
                indexAlbum?.let {
                    song.album = it
                }
            }
            song
        }
    }

    suspend fun pathIsWrong(path: String) {
        val song = vkPlaylist.find {
            it.path == path
        }
        applyNewPath(song!!, "")
    }

    suspend fun deleteSong(vkSong: VkSong) {
        AudlayerApp.getApplicationVkDir()
            .listFiles()?.find { it.path == vkSong.path }.let {
                if (it != null) {
                    applyNewPath(vkSong, "")
                    it.delete()
                }
                else withContext(Dispatchers.Main) {
                    Toast.makeText(app, "Can't delete", Toast.LENGTH_SHORT).show()
                }
        }
    }

    suspend fun refreshByToken() {
        //from vk
        val byTokenSongs = repository.getPlaylistByToken(app).items
        //from db
        val fromDbSongs = repository.getSongsFromDb()
        //compare with existed and insert
        compareAndInsert(byTokenSongs, fromDbSongs)
        //compare with existed and delete
        compareAndDelete(byTokenSongs, fromDbSongs)
        //create vkPlaylist
        initVkPlaylist()
    }

    suspend fun downloadAll(webView: WebView) {
        //which
        val toDownload = vkPlaylist
            .filter { needDownload(it) }
            .reversed()
        //download
        val downloaded = repository.downloadAll(app, webView, toDownload)
        //save in db new info
        downloaded.forEach {
            applyNewPath(it, it.path)
        }
        //refresh ui
        withContext(Dispatchers.Main) {
            rvResolver.adapter.notifyDataSetChanged()
        }
    }

    suspend fun downloadInform(vkSong: VkSong, webView: WebView): Boolean =
        if (needDownload(vkSong)) {
            scope.launch {
                val file = download(vkSong, webView).getOrElse(null)
                //if download will be success ->  not null
                if (file != null) {
                    applyNewPath(vkSong, file.path)
                    playAudioAndAllSong(vkSong)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(app,
                            "Song success downloaded", Toast.LENGTH_LONG).show()
                    }
                }
                else withContext(Dispatchers.Main) {
                    Toast.makeText(app,
                        "Sorry we did not found any link", Toast.LENGTH_LONG).show()
                }
            }
            true
        } else false


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

    private suspend fun applyNewPath(vkSong: VkSong, path: String) {
        val index = vkPlaylist.indexOf(vkSong)
        val song = vkPlaylist[index]
        vkPlaylist[index].path = path
        val orderedIndex = ordered.indexOf(song)
        ordered[orderedIndex].path = path

        repository.updateSong(song)
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


    private suspend fun download(vkSong: VkSong, webView: WebView): Result<File?> {
        //refresh path to blank
        applyNewPath(vkSong, "")
        //download
        return repository.downloadViaSefon(app, webView, vkSong)
    }

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

    private suspend fun compareAndInsert(byTokenSongs: Array<VkSong>,
                                         fromDbSongs: List<VkSong>) {
        //song
        val notExistSong = getNoExistInDbSong(byTokenSongs, fromDbSongs)
        //album
        val fromDbAlbums = repository.getAlbumsFromDb()
        val fromDbAlbumsTitle = fromDbAlbums.map { it.title }
        val notExistAlbum =
            getNoExistInDbAlbum(notExistSong, fromDbAlbumsTitle)
        //insert
        repository.insertAlbumAndSong(
            notExistAlbum.toTypedArray(),
            notExistSong.toTypedArray()
        )
    }

    private suspend fun compareAndDelete(byTokenSongs: Array<VkSong>,
                                         fromDbSongs: List<VkSong>) {
        val toDelete = mutableListOf<VkSong>()
        fromDbSongs.forEach { fromDb ->
            if (byTokenSongs.find { it.id == fromDb.id } == null)
                toDelete.add(fromDb)
        }
        repository.deleteSong(toDelete.toTypedArray())
    }
}