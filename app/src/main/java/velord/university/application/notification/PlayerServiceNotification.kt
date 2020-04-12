package velord.university.application.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import velord.university.R
import velord.university.application.broadcast.MiniPlayerBroadcastHub
import velord.university.model.FileFilter
import velord.university.model.FileNameParser
import java.io.File

private const val NOTIFICATION_ACTION_PREVIUOS = "actionPrevious"
private const val NOTIFICATION_ACTION_PLAY_OR_STOP = "actionPlayOrStop"
private const val NOTIFICATION_ACTION_NEXT = "actionNext"
private const val NOTIFICATION_ACTION_CANCEL = "actionCancel"
private const val channelId = "velord.audlayer.notification.miniPlayerService"

object PlayerServiceNotification {

    const val id = 2345

    private lateinit var view: RemoteViews

    private lateinit var notificationManager: NotificationManager

    private fun Context.intentPrevious(): PendingIntent {
        val intentPrevious = Intent(
            this, MiniPlayerNotificationBroadcastReceiver::class.java)
            .setAction(NOTIFICATION_ACTION_PREVIUOS)
        return  PendingIntent.getBroadcast(
            this, 0,
            intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun Context.intentPlayOrStop(): PendingIntent {
        val intentPlay =Intent(
            this, MiniPlayerNotificationBroadcastReceiver::class.java
        )
            .setAction(NOTIFICATION_ACTION_PLAY_OR_STOP)
        return PendingIntent.getBroadcast(
            this, 0,
            intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun Context.intentNext(): PendingIntent {
        val intentPrevious = Intent(
            this, MiniPlayerNotificationBroadcastReceiver::class.java)
            .setAction(NOTIFICATION_ACTION_NEXT)
        return PendingIntent.getBroadcast(
            this, 0,
            intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun Context.intentCancel(): PendingIntent {
        val intentCancel = Intent(
            this, MiniPlayerNotificationBroadcastReceiver::class.java)
            .setAction(NOTIFICATION_ACTION_CANCEL)
        return PendingIntent.getBroadcast(
            this, 0,
            intentCancel, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun dismiss() {
        notificationManager.cancel(id)
    }

    fun updatePlayOrStop(isPlaying: Boolean) =
        when(isPlaying) {
            true -> {
                view.setImageViewResource(R.id.notification_play_or_stop, R.drawable.pause)
            }
            false -> {
                view.setImageViewResource(R.id.notification_play_or_stop, R.drawable.play)
            }
        }

    fun updateSongTitleAndArtist(file: File) {
        val artist = FileNameParser.getSongArtist(file)
        val title = FileFilter.getName(file)
        view.setTextViewText(R.id.notification_artist, artist)
        view.setTextViewText(R.id.notification_title, title)
    }

    fun getNotificationBuilder(context: Context): NotificationCompat.Builder {
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = "Audlayer"
            createNotificationChannel(
                context,
                channelId,
                description
            )
        }
        //create view
        view = RemoteViews(context.packageName, R.layout.mini_player_notification_control)
        setListener(view, context)
        //create builder
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.album_gray)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources, R.drawable.album_gray)
            )
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCustomBigContentView(view)
    }

    private fun setListener(views: RemoteViews,
                            context: Context) {
        views.setOnClickPendingIntent(R.id.notification_previous, context.intentPrevious())
        views.setOnClickPendingIntent(R.id.notification_play_or_stop, context.intentPlayOrStop())
        views.setOnClickPendingIntent(R.id.notification_next, context.intentNext())
        views.setOnClickPendingIntent(R.id.notification_cancel, context.intentCancel())
    }

    class MiniPlayerNotificationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                NOTIFICATION_ACTION_PLAY_OR_STOP ->
                    MiniPlayerBroadcastHub.run { context!!.playOrStopService() }
                NOTIFICATION_ACTION_NEXT ->
                    MiniPlayerBroadcastHub.run { context!!.skipNextService() }
                NOTIFICATION_ACTION_PREVIUOS ->
                    MiniPlayerBroadcastHub.run { context!!.skipPrevService() }
                NOTIFICATION_ACTION_CANCEL -> dismiss()
            }
        }
    }
}