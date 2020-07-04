package velord.university.application.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.webkit.URLUtil
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import velord.university.R
import velord.university.application.broadcast.MiniPlayerNotificationBroadcastReceiver
import velord.university.ui.util.DrawableIcon
import velord.university.ui.widget.AudlayerWidget


object MiniPlayerNotification {

    const val NOTIFICATION_ACTION_PREVIUOS = "actionPrevious"
    const val NOTIFICATION_ACTION_PLAY_OR_STOP = "actionPlayOrStop"
    const val NOTIFICATION_ACTION_NEXT = "actionNext"
    const val NOTIFICATION_ACTION_CANCEL = "actionCancel"
    private const val channelId = "velord.audlayer.notification.miniPlayerService"
    const val id = 2345

    private lateinit var view: RemoteViews

    private lateinit var notificationManager: NotificationManager

    private var title = ""
    private var artist = ""
    private var isPlaying = false
    private var icon: String = ""
    private var iconIsSong = true

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
        val intentPlay = Intent(
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
    }

    fun dismiss() {
        if (::notificationManager.isInitialized)
            notificationManager.cancel(id)
    }

    fun updatePlayOrStop(context: Context,
                         isPlaying: Boolean) {
        this.isPlaying = isPlaying
        val f: (RemoteViews, NotificationCompat.Builder) -> Unit = { view, _ ->
            when (this.isPlaying) {
                true -> {
                    view.setImageViewResource(
                        R.id.notification_play_or_stop,
                        R.drawable.pause
                    )
                }
                false -> {
                    view.setImageViewResource(
                        R.id.notification_play_or_stop,
                        R.drawable.play
                    )
                }
            }
        }
        updateNotification(context, f)
    }

    fun updateSongTitle(context: Context,
                        title: String) {
        this.title = title
        val f: (RemoteViews, NotificationCompat.Builder) -> Unit = { view, _ ->
            view.setTextViewText(R.id.notification_title, title)
        }
        updateNotification(context, f)
    }

    fun updateSongArtist(context: Context,
                         artist: String) {
        this.artist = artist
        val f: (RemoteViews, NotificationCompat.Builder) -> Unit = { view, _ ->
            view.setTextViewText(R.id.notification_artist, artist)
        }
        updateNotification(context, f)
    }

    fun updateIcon(context: Context,
                   value: String,
                   isSong: Boolean = this.iconIsSong) {
        this.iconIsSong = isSong
        this.icon = value
        val f: (RemoteViews, NotificationCompat.Builder) -> Unit = { _, builder ->
            loadIcon(context, builder.notification, value)
        }
        updateNotification(context, f)
    }

    fun updateArtistAndTitle(context: Context,
                             artist: String,
                             title: String) {
        this.artist = artist
        this.title = title
        val f: (RemoteViews, NotificationCompat.Builder) -> Unit = { view, _ ->
            view.setTextViewText(R.id.notification_artist, artist)
            view.setTextViewText(R.id.notification_title, title)
        }
        updateNotification(context, f)
    }

    private fun loadIcon(context: Context,
                         notification: Notification,
                         value: String) {
        val notificationTarget = NotificationTarget(
            context,
            R.id.notification_image,
            view,
            notification,
            id
        )

        if (icon.isNotEmpty()) {
            when(iconIsSong) {
                true -> {
                    if (URLUtil.isHttpUrl(value) ||
                        URLUtil.isHttpsUrl(value))
                        Glide.with(context)
                            .asBitmap()
                            .load(value)
                            .placeholder(R.drawable.song_item_black)
                            .into(notificationTarget)
                    else view.setImageViewResource(
                        R.id.audlayer_widget_image,
                        AudlayerWidget.widgetIcon.toInt()
                    )
                }
                false -> {
                    val radioIconAsset =
                        DrawableIcon.getResourceIdIcon(context, value)

                    Glide.with(context)
                        .asBitmap()
                        .load(radioIconAsset)
                        .placeholder(R.drawable.song_item_black)
                        .into(notificationTarget)
                }
            }
        }
    }

    private inline fun updateNotification(context: Context,
                                          f: (RemoteViews, NotificationCompat.Builder) -> Unit) {
        //create builder
        val builder = getNotificationBuilder(context)
        //change view
        f(view, builder)
        //notify
        if (managerInitialized())
            notificationManager.notify(id, builder.build())
    }

    private fun managerInitialized(): Boolean =
        ::notificationManager.isInitialized

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