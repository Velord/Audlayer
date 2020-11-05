package velord.university.repository.hub

import android.content.Context
import android.webkit.WebView
import com.statuscasellc.statuscase.model.coroutine.onIO
import velord.university.application.notification.VkDownloadNotification
import velord.university.model.entity.vk.VkAlbum
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong
import velord.university.model.functionalDataSctructure.result.Result
import velord.university.repository.fetch.IMusicFetch
import velord.university.repository.fetch.SefonFetchSequential
import velord.university.repository.db.transaction.vk.VkAlbumTransaction
import velord.university.repository.db.transaction.vk.VkSongTransaction
import velord.university.repository.fetch.VkFetch

object VkRepository : BaseRepository() {

    suspend fun getAlbumsFromDb(): List<VkAlbum> =
        VkAlbumTransaction.getAlbums()

    suspend fun getSongsFromDb(): List<VkSong> =
        VkSongTransaction.getSongs()

    suspend fun getPlaylistByToken(context: Context): VkPlaylist = onIO {
            VkFetch.fetchPlaylist(context)
        }
//            val gson = Gson()
//            val response = music.makeRequestViaOkHttp()
//
//            val json = JSONObject(response).getJSONObject("response")
//
//            gson.fromJson(json.toString(), VkPlaylist::class.java)

    suspend fun getVkPlaylist() = VkSongTransaction.getPlaylist()

    suspend fun deleteAllTables() {
        VkAlbumTransaction.deleteAll()
        VkSongTransaction.deleteAll()
    }

    suspend fun downloadViaSefon(context: Context,
                                 webView: WebView,
                                 vkSong: VkSong): Result<String?> =
        Result.ofAsync { SefonFetchSequential(context, webView, vkSong).download() }

    suspend fun downloadViaIMusic(context: Context,
                                  webView: WebView,
                                  vkSong: VkSong): Result<String?> =
        Result.ofAsync { IMusicFetch(context, webView, vkSong).download() }


    suspend fun download(context: Context,
                         webView: WebView,
                         vkSong: VkSong): String?  =
        downloadViaIMusic(context, webView, vkSong).getOrElse(null) ?:
        downloadViaSefon(context, webView, vkSong).getOrElse(null)

    suspend fun updateSong(vkSong: VkSong) =
        VkSongTransaction.update(vkSong)

    suspend fun insertAlbumAndSong(album: Array<VkAlbum>,
                                   song: Array<VkSong>) {
        VkAlbumTransaction.addAlbum(*album)
        VkSongTransaction.addSong(*song)
    }

    suspend fun deleteSong(song: Array<VkSong>) =
        VkSongTransaction.delete(*song)

    suspend fun downloadAll(context: Context,
                            webView: WebView,
                            toDownload: List<VkSong>): List<VkSong> {
        //build notification
        VkDownloadNotification.build(context)
        //help variable
        var downloadedCount = 0
        val songCount = toDownload.size
        val downloaded = mutableListOf<VkSong>()

        VkDownloadNotification.reassignmentDownloadState()

        toDownload.forEachIndexed { index, song ->
            //if user cancel download
            if (VkDownloadNotification.downloadIsCanceled())
                return downloaded
            //download file
            val file = download(context, webView, song)
            file?.let {
                song.path = it
                downloaded.add(song)
                ++downloadedCount
            }
            //refresh notification
            val progress = "All: $songCount, " +
                    "no link: ${index - downloadedCount + 1}, " +
                    "downloaded: $downloadedCount"
            VkDownloadNotification.setText(context, progress)
        }
        //finalize notification
        val downloadedText = "Audlayer Vk Downloaded!"
        VkDownloadNotification.setText(context, downloadedText)

        return downloaded
    }
}