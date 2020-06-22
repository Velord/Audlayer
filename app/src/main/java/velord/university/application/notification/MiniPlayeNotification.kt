package velord.university.application.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import velord.university.R
import velord.university.application.broadcast.MiniPlayerNotificationBroadcastReceiver

object MiniPlayerServiceNotification {

    const val NOTIFICATION_ACTION_PREVIUOS = "actionPrevious"
    const val NOTIFICATION_ACTION_PLAY_OR_STOP = "actionPlayOrStop"
    const val NOTIFICATION_ACTION_NEXT = "actionNext"
    const val NOTIFICATION_ACTION_CANCEL = "actionCancel"
    const val channelId = "velord.audlayer.notification.miniPlayerService"
    const val id = 2345

    private lateinit var view: RemoteViews

    private lateinit var notificationManager: NotificationManager

    private var title = ""
    private var artist = ""
    private var isPlaying = false

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
    //must be invoked before all function
    fun initNotificationManager(context: Context) {
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        //build notification
        updatePlayOrStop(context, isPlaying)
        updateSongTitleAndArtist(context, title, artist)
    }

    fun dismiss() {
        if (::notificationManager.isInitialized)
            notificationManager.cancel(id)
    }

    fun updatePlayOrStop(context: Context,
                         isPlaying: Boolean) {
        this.isPlaying = isPlaying
        //create builder
        val builder = getNotificationBuilder(context)
        //change view
        when (this.isPlaying) {
            true -> {
                view.setImageViewResource(R.id.notification_play_or_stop, R.drawable.pause)
            }
            false -> {
                view.setImageViewResource(R.id.notification_play_or_stop, R.drawable.play)
            }
        }
        //notify
        if (::notificationManager.isInitialized)
            notificationManager.notify(id, builder.build())
    }

    fun updateSongTitleAndArtist(context: Context,
                                 title: String,
                                 artist: String) {
        //create builder
        val builder = getNotificationBuilder(context)
        //change view
        this.artist = artist
        this.title = title
        view.setTextViewText(R.id.notification_artist, artist)
        view.setTextViewText(R.id.notification_title, title)
        //notify
        if (::notificationManager.isInitialized)
            notificationManager.notify(id, builder.build())
    }

    private fun getNotificationBuilder(context: Context): NotificationCompat.Builder {
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
        view.setListener(context)
        //create builder
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.song_item_playing)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources, R.drawable.song_item_playing)
            )
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCustomBigContentView(view)
    }

    private fun RemoteViews.setListener(context: Context) {
        setOnClickPendingIntent(R.id.notification_previous, context.intentPrevious())
        setOnClickPendingIntent(R.id.notification_play_or_stop, context.intentPlayOrStop())
        setOnClickPendingIntent(R.id.notification_next, context.intentNext())
        setOnClickPendingIntent(R.id.notification_cancel, context.intentCancel())
    }

}