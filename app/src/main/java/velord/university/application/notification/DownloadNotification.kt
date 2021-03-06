package velord.university.application.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import velord.university.R

private const val channelId = "velord.audlayer.notification.download"
private const val notificationCancelExtra = "velord.audlayer.notification.cancel_downloading"
private const val notificationDownloadId = 1234
private const val notificationCancelValue = notificationDownloadId

object DownloadNotification {

    private lateinit var notificationManager: NotificationManager

    private var userCanceledDownload = false

    fun setText(context: Context,
                text: String) {
        val builder = getNotificationBuilder(context)
        builder.setContentText(text)
        notificationManager.notify(notificationDownloadId, builder.build())
    }

    fun downloadIsCanceled() =
        userCanceledDownload

    fun reassignmentDownloadState() {
        userCanceledDownload = false
    }

    fun build(context: Context): NotificationManager {
        notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = getNotificationBuilder(context)
        notificationManager.notify(notificationDownloadId, builder.build())

        return notificationManager
    }

    private fun getNotificationBuilder(context: Context): NotificationCompat.Builder {
        val broadIntent = Intent(context, DownloadNotificationReceiver().javaClass)
        broadIntent.putExtra(notificationCancelExtra, notificationCancelValue)
        val pendIntent = PendingIntent.getBroadcast(context,
            0, broadIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = "Downloading..."
            createNotificationChannel(
                context,
                channelId,
                description
            )
        }

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Audlayer Vk Downloading...")
            .setSmallIcon(R.drawable.song_item_playing)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources, R.drawable.song_item_playing)
            )
            .addAction(R.drawable.cancel, "Cancel", pendIntent)
    }

    class DownloadNotificationReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent!!.getIntExtra(notificationCancelExtra, -1)
            if (message == notificationCancelValue) {
                userCanceledDownload = true
                notificationManager.cancel(notificationDownloadId)
            }
        }
    }
}