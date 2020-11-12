package velord.university.ui.fragment.vk

import android.app.Application
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import velord.university.model.coroutine.onDef
import kotlinx.coroutines.*
import org.apache.commons.text.similarity.LevenshteinDistance
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.application.broadcast.hub.BroadcastActionType
import velord.university.application.settings.SearchQueryPreferences
import velord.university.application.settings.SortByPreference
import velord.university.application.settings.VkPreference
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.music.playlist.Playlist
import velord.university.model.entity.music.song.Song
import velord.university.model.entity.vk.entity.VkAlbum
import velord.university.model.entity.vk.entity.VkSong
import velord.university.model.entity.vk.fetch.VkSongFetch
import velord.university.model.entity.fileType.file.FileFilter
import velord.university.model.entity.fileType.file.FileNameParser
import velord.university.model.entity.fileType.json.general.Loadable
import velord.university.model.entity.vk.entity.VkSong.Companion.filterByQuery
import velord.university.model.entity.vk.entity.VkSong.Companion.mapWithAlbum
import velord.university.repository.hub.FolderRepository
import velord.university.repository.hub.VkRepository
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.hub.HubTransaction
import velord.university.repository.db.transaction.vk.VkAlbumTransaction
import velord.university.repository.db.transaction.vk.VkSongTransaction
import velord.university.repository.hub.HubRepository.vkRepository
import velord.university.ui.util.RVSelection
import java.io.File


class VkViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val TAG = "VkViewModel"

    val vkSongList: Loadable<Array<VkSong>> = Loadable {
        val vkAlbumList = HubTransaction.vkAlbumTransaction("vkSongList") {
            getAll().toTypedArray()
        }
        HubTransaction.vkSongTransaction("vkSongList") {
            getAll().toTypedArray()
                .mapWithAlbum(vkAlbumList)
                .toTypedArray()
        }
    }

    lateinit var ordered: Array<VkSong>

    lateinit var currentQuery: String

    lateinit var rvResolver: RVSelection<VkSong>

    fun orderedIsInitialized() = ::ordered.isInitialized

    fun storeSearchQuery(query: String) {
        //store search term in shared preferences
        currentQuery = query
        SearchQueryPreferences(app).storedQueryVk = currentQuery
    }

    fun rvResolverIsInitialized(): Boolean = ::rvResolver.isInitialized

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
        viewModelScope.launch {
            addToInteractor()

            AppBroadcastHub.apply {
                app.playByPathService(song.path)
                app.doAction(BroadcastActionType.LOOP_ALL_PLAYER_SERVICE)
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
            onSuccess(vkSong, viewModelScope)
        }
        else onFailure(viewModelScope)
    }

    fun shuffle(): Array<VkSong> {
        ordered.shuffle()
        return ordered
    }

    fun sendIconToMiniPlayer(song: VkSong) {
        song.getAlbumIcon()?.let {
            AppBroadcastHub.apply {
                app.iconUI(it)
            }
        }
    }

    fun checkAuth(): Boolean {
        val token = VkPreference(app).accessToken
        return (token.isNotEmpty())
    }

    fun logout() {
        viewModelScope.launch { deleteAll() }
        VkPreference(app).accessToken = ""
    }

    suspend fun pathIsWrong(path: String) {
        vkSongList.get().find { it.path == path }?.let {
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

    suspend fun refreshByCredential() = onDef {
        try {
            //from vk
            val byTokenSongs = app.vkRepository {
                getPlaylistViaCredential(app).items
            }
            //from db
            val fromDbSongs = vkSongList.get()
            //compare with existed and insert
            compareAndInsert(byTokenSongs, fromDbSongs)
            //compare with existed and delete
            compareAndDelete(byTokenSongs, fromDbSongs)
            //create vkPlaylist
            load()
        }
        catch (e: Exception) {
            Log.d(TAG, e.message.toString())
        }
    }

    private suspend fun load() { vkSongList.load() }

    suspend fun downloadAll(webView: WebView) {
        //which
        val toDownload = vkSongList.get()
            .filter { needDownload(it) }
            .reversed()
        //download
        val downloaded = app.vkRepository {
            downloadAll(app, webView, toDownload)
        }
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
        load()
    }

    suspend fun filterByQuery(query: String): Array<VkSong> = onDef {
        val filtered: List<VkSong> = vkSongList
            .get()
            .filterByQuery(query)
        //sort by name or artist or date added or duration or size
        val sortBy = SortByPreference(app).sortByVkFragment
        val sorted: List<VkSong> = when(sortBy) {
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
        }.toTypedArray()

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
        val index = vkSongList.get().indexOf(vkSong)
        val song = vkSongList.get()[index]
        vkSongList.get()[index].path = path

        val orderedIndex = ordered.indexOf(song)
        ordered[orderedIndex].path = path

        VkSongTransaction.update(song)
    }

    private suspend fun download(vkSong: VkSong,
                                 webView: WebView): String? {
        //refresh path to blank
        applyNewPath(vkSong, "")
        //download
        return app.vkRepository { download(app, webView, vkSong) }
    }

    private suspend fun getNoExistInDbSong(byTokenSongs: Array<VkSongFetch>,
                                           fromDbSongs: Array<VkSong>
    ): List<VkSong> {
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
                                         fromDbSongs: Array<VkSong>) {
        if (byTokenSongs.isEmpty()) return
        //song
        val notExistSong = getNoExistInDbSong(byTokenSongs, fromDbSongs)
        //album
        val fromDbAlbums = VkAlbumTransaction.getAlbums()
        val fromDbAlbumsTitle = fromDbAlbums.map { it.title }
        val notExistAlbum =
            getNoExistInDbAlbum(notExistSong, fromDbAlbumsTitle)
        //insert
        app.vkRepository {
            insertAlbumAndSong(
                notExistAlbum.toTypedArray(),
                notExistSong.toTypedArray()
            )
        }
    }

    private suspend fun compareAndDelete(byTokenSongs: Array<VkSongFetch>,
                                         fromDbSongs: Array<VkSong>) {
        if (byTokenSongs.isEmpty()) return

        val toDelete = mutableListOf<VkSong>()
        fromDbSongs.forEach { fromDb ->
            if (byTokenSongs.find { it.id == fromDb.id } == null)
                toDelete.add(fromDb)
        }
        VkSongTransaction.delete(*toDelete.toTypedArray())
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
    }
}