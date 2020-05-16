package velord.university.repository

import android.content.Context
import android.webkit.WebView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import velord.university.R
import velord.university.application.notification.VkDownloadNotification
import velord.university.application.settings.VkPreference
import velord.university.model.entity.vk.VkAlbum
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong
import velord.university.model.functionalDataSctructure.result.Result
import velord.university.repository.fetch.SefonFetchSequential
import velord.university.repository.fetch.makeRequestViaOkHttp
import velord.university.repository.transaction.vk.VkAlbumTransaction
import velord.university.repository.transaction.vk.VkSongTransaction
import java.io.File

object VkRepository {

    suspend fun getAlbumsFromDb(): List<VkAlbum> =
        VkAlbumTransaction.getAlbums()

    suspend fun getSongsFromDb(): List<VkSong> =
        VkSongTransaction.getSongs()

    suspend fun getPlaylistByToken(context: Context): VkPlaylist {
        val userId = VkPreference.getPageId(context)
        val token = VkPreference.getAccessToken(context)
        val baseUrl = context.getString(R.string.vk_base_url)
        val music = "${baseUrl}audio.get?user_ids=$userId&access_token=$token&v=5.80"

        return withContext(Dispatchers.IO) {
            val gson = Gson()
            val response = music.makeRequestViaOkHttp()
            val json = JSONObject(response).getJSONObject("response")

            return@withContext gson
                .fromJson(json.toString(), VkPlaylist::class.java)
        }
    }

    suspend fun downloadViaSefon(context: Context,
                                 webView: WebView,
                                 vkSong: VkSong): Result<File?> =
        Result.ofAsync { SefonFetchSequential(context, webView, vkSong).download() }

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
            val file =
                downloadViaSefon(context, webView, song).getOrElse(null)
            file?.let {
                song.path = it.path
                downloaded.add(song)
                ++downloadedCount
            }
            //refresh notification
            val progress = "All: $songCount, " +
                    "no link: ${index - downloadedCount + 1}, " +
                    "downloaded: $downloadedCount"
            VkDownloadNotification.setText(progress)
        }
        //finalize notification
        val downloadedText = "Audlayer Vk Downloaded!"
        VkDownloadNotification.setText(downloadedText)

        return downloaded
    }
}