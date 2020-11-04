package velord.university.ui.fragment.vk

import android.app.Application
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.statuscasellc.statuscase.model.coroutine.onDef
import kotlinx.coroutines.*
import org.apache.commons.text.similarity.LevenshteinDistance
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.Playlist
import velord.university.model.entity.Song
import velord.university.model.entity.vk.VkAlbum
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong
import velord.university.model.entity.vk.VkSongFetch
import velord.university.model.file.FileFilter
import velord.university.model.file.FileNameParser
import velord.university.repository.hub.FolderRepository
import velord.university.repository.hub.VkRepository
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.ui.util.RVSelection
import java.io.File


class VkViewModel(private val app: Application) : AndroidViewModel(app) {

    private val TAG = "VkViewModel"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    lateinit var vkSongList: List<VkSong>
    private lateinit var vkAlbums: List<VkAlbum>

    val repository = VkRepository

    lateinit var ordered: List<VkSong>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<VkSong>

    fun orderedIsInitialized() = ::ordered.isInitialized

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences(app).storedQueryVk = currentQuery
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

    fun vkPlaylistIsInitialized() = ::vkSongList.isInitialized

    fun getSearchQuery(): String =
        SearchQueryPreferences(app).storedQueryVk

    fun playAudioNext(song: VkSong) {
        //don't remember for SongQuery Interactor it will be used between this and service
        addToInteractor()
        //add to queue one song
        AppBroadcastHub.apply {
            app.addToQueueService(song.path)
        }
    }

    //path must be not blank and file can be created by that path
    fun needDownload(vkSong: VkSong): Boolean =
        (vkSong.path.isBlank()) and (File(vkSong.path).exists().not())

    fun playAudioAndAllSong(song: VkSong) {
        scope.launch {
            addToInteractor()

            AppBroadcastHub.apply {
                app.playByPathService(song.path)
                app.loopAllService()
                //sendIcon
                sendIconToMiniPlayer(song)
            }
        }
    }

    suspend fun downloadThenPlay(vkSong: VkSong,
                                 webView: WebView,
                                 onSuccess: (VkSong, CoroutineScope) -> Unit,
                                 onFailure: (CoroutineScope) -> Unit) {
        val filePath =  download(vkSong, webView)
        //if download will be success ->  not null
        if (filePath != null) {
            applyNewPath(vkSong, filePath)
            playAudioAndAllSong(vkSong)
            onSuccess(vkSong, scope)
        }
        else onFailure(scope)
    }

    fun shuffle(): Array<VkSong> {
        ordered = ordered.shuffled()
        return ordered.toTypedArray()
    }

    fun sendIconToMiniPlayer(song: VkSong) {
        song.getAlbumIcon()?.let {
            AppBroadcastHub.apply {
                app.iconUI(it)
            }
        }
    }

    suspend fun initVkPlaylist() {
        vkAlbums = repository.getAlbumsFromDb()
        vkSongList = repository.getSongsFromDb().map { song ->
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
        vkSongList.find { it.path == path }?.let {
            applyNewPath(it, "")
        }
    }

    suspend fun deleteSong(vkSong: VkSong) {
        FolderRepository.getApplicationVkDir()
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
        try {
            //from vk
            val byTokenSongs = repository
                .getPlaylistByToken(app)
                .items
            //from db
            val fromDbSongs = repository.getSongsFromDb()
            //compare with existed and insert
            compareAndInsert(byTokenSongs, fromDbSongs)
            //compare with existed and delete
            compareAndDelete(byTokenSongs, fromDbSongs)
            //create vkPlaylist
            initVkPlaylist()
        }
        catch (e: Exception) {
            Log.d(TAG, e.message.toString())
        }
    }

    suspend fun downloadAll(webView: WebView) {
        //which
        val toDownload = vkSongList
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

    suspend fun deleteAll() {
        VkRepository.deleteAllTables()
        vkAlbums = repository.getAlbumsFromDb()
        vkSongList = repository.getSongsFromDb()
    }

    suspend fun filterByQuery(query: String): List<VkSong> = onDef {
        val filtered = vkSongList.filter {
            FileFilter.filterBySearchQuery("${it.artist} - ${it.title}", query)
        }
        //sort by name or artist or date added or duration or size
        val sorted = when(SortByPreference(app).sortByVkFragment) {
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
        val ascDescOrder = SortByPreference(app).ascDescVkFragment
        ordered = when(ascDescOrder) {
            0 -> sorted
            1 ->  sorted.reversed()
            else -> sorted
        }

        ordered
    }

    private fun addToInteractor() {
        SongPlaylistInteractor.songs = ordered
            .filter { it.path.isNotBlank() }
            .map {
                Song(
                    File(it.path),
                    it.getAlbumIcon()
                )
            }
            .toTypedArray()
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

    private suspend fun applyNewPath(vkSong: VkSong, path: String) {
        val index = vkSongList.indexOf(vkSong)
        val song = vkSongList[index]
        vkSongList[index].path = path

        val orderedIndex = ordered.indexOf(song)
        ordered[orderedIndex].path = path

        repository.updateSong(song)
    }

    private suspend fun download(vkSong: VkSong, webView: WebView): String? {
        //refresh path to blank
        applyNewPath(vkSong, "")
        //download
        return repository.download(app, webView, vkSong)
    }

    private suspend fun getNoExistInDbSong(byTokenSongs: Array<VkSongFetch>,
                                           fromDbSongs: List<VkSong>): List<VkSong> {
        val fromDbSongsId = fromDbSongs.map { it.id }
        val allSongFromDb = Playlist
            .allSongFromPlaylist(PlaylistTransaction.getAllPlaylist())
        val allSongPath = allSongFromDb.map { it.path }
        val allSongName = allSongFromDb.map { FileNameParser.removeExtension(it) }
        return byTokenSongs.fold(mutableListOf<VkSong>()) { notExist, byToken ->
            if (fromDbSongsId.contains(byToken.id).not())
                notExist.add(byToken.toVkSong())
            notExist
        }.map {
            if (it.album?.id != 0)
                it.albumId = it.album?.id
            val index = getPathIndex(it, allSongName)
            if (index != -1) {
                it.path = allSongPath[index]
                it.artist = FileNameParser.getSongArtist(allSongFromDb[index])
                it.title = FileNameParser.getSongTitle(allSongFromDb[index])
            } else it.path = ""
            it
        }
    }

    private suspend fun compareAndInsert(byTokenSongs: Array<VkSongFetch>,
                                         fromDbSongs: List<VkSong>) {
        if (byTokenSongs.isEmpty()) return
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

    private suspend fun compareAndDelete(byTokenSongs: Array<VkSongFetch>,
                                         fromDbSongs: List<VkSong>) {
        if (byTokenSongs.isEmpty()) return

        val toDelete = mutableListOf<VkSong>()
        fromDbSongs.forEach { fromDb ->
            if (byTokenSongs.find { it.id == fromDb.id } == null)
                toDelete.add(fromDb)
        }
        repository.deleteSong(toDelete.toTypedArray())
    }
}