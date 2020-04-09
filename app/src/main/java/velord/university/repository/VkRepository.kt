package velord.university.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.webkit.WebView
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject
import velord.university.R
import velord.university.application.settings.VkPreference
import velord.university.model.entity.vk.VkAlbum
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong
import velord.university.repository.fetch.SefonFetchSequential
import velord.university.repository.fetch.makeRequestViaOkHttp
import velord.university.repository.transaction.vk.VkAlbumTransaction
import velord.university.repository.transaction.vk.VkSongTransaction
import java.io.File

private const val channelId = "velord.audlayer.notification.vk"
private const val notificationCancelExtra = "velord.audlayer.notification.vk_cancel_downloading"
private const val notificationDownloadId = 1234
private const val notificationCancelValue = notificationDownloadId

object VkRepository {

    private lateinit var downloadScope: CoroutineScope

    private lateinit var notificationManager: NotificationManager

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
                                 vkSong: VkSong): File? =
        SefonFetchSequential(context, webView, vkSong).download()

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
                            toDownload: List<VkSong>,
                            ifDownload: (VkSong, String) -> Unit) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = getNotificationBuilder(notificationManager, context)
        notificationManager.notify(notificationDownloadId, builder.build())

        var downloadedCount = 0
        val songCount = toDownload.size
        downloadScope = CoroutineScope(Job() + Dispatchers.Default)
        downloadScope.launch {
            toDownload.forEachIndexed { index, song ->
                val file = downloadViaSefon(context, webView, song)
                file?.let {
                    ifDownload(song, it.path)
                    ++downloadedCount
                }
                val howMany = "All: $songCount, " +
                        "no link: ${index - downloadedCount + 1}, " +
                        "downloaded: $downloadedCount, " +
                        "handled: ${index + 1}"
                builder.setContentText(howMany)
                notificationManager.notify(notificationDownloadId, builder.build())
            }
            builder.setContentTitle("Audlayer Vk Downloaded!")
            notificationManager.notify(notificationDownloadId, builder.build())
        }
    }

    private fun getNotificationBuilder(notificationManager: NotificationManager,
                                       context: Context): NotificationCompat.Builder {
        val description = "Downloading..."

        val broadIntent = Intent(context, VkDownloadNotificationReceiver().javaClass)
        broadIntent.putExtra(notificationCancelExtra, notificationCancelValue)
        val pendIntent = PendingIntent.getBroadcast(context,
            0, broadIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.apply {
                enableLights(true)
                enableVibration(false)
                lightColor = Color.GREEN
            }
            notificationManager.createNotificationChannel(notificationChannel)

            NotificationCompat.Builder(context, channelId)
                .setContentTitle("Audlayer Vk Downloading...")
                .setSmallIcon(R.drawable.album_gray)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.resources, R.drawable.album_gray
                    )
                )
                .addAction(R.drawable.cancel, "Cancel", pendIntent)
        } else NotificationCompat.Builder(context)
            .setContentTitle("Audlayer Vk Downloading...")
            .setSmallIcon(R.drawable.album_gray)
            .setLargeIcon(BitmapFactory.decodeResource(
                    context.resources, R.drawable.album_gray
                )
            )
            .addAction(R.drawable.cancel, "Cancel", pendIntent)
    }

    class VkDownloadNotificationReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent!!.getIntExtra(notificationCancelExtra, -1)
            if (message == notificationCancelValue) {
                downloadScope.cancel()
                notificationManager.cancel(notificationDownloadId)
            }
        }
    }
}