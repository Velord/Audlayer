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
        updateNotification(
            context,
            updateSongTitleF,
            updateSongArtistF,
            updatePlayOrStopF
        )
        updateIcon(context, icon, iconIsSong)
    }

    fun dismiss() {
        if (::notificationManager.isInitialized)
            notificationManager.cancel(id)
    }

    private val updatePlayOrStopF: (RemoteViews, NotificationCompat.Builder) -> Unit = { view, _ ->
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

    private val updateSongTitleF: (RemoteViews, NotificationCompat.Builder) -> Unit = { view, _ ->
        view.setTextViewText(R.id.notification_title, title)
    }

    private val updateSongArtistF: (RemoteViews, NotificationCompat.Builder) -> Unit = { view, _ ->
        view.setTextViewText(R.id.notification_artist, artist)
    }

    fun updatePlayOrStop(context: Context,
                         isPlaying: Boolean) {
        this.isPlaying = isPlaying
        updateNotification(context, updatePlayOrStopF)
    }

    fun updateSongTitle(context: Context,
                        title: String) {
        this.title = title
        updateNotification(context, updateSongTitleF)
    }

    fun updateSongArtist(context: Context,
                         artist: String) {
        this.artist = artist
        updateNotification(context, updateSongArtistF)
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
        updateNotification(context, updateSongArtistF, updateSongTitleF)
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
                        R.id.notification_image,
                        icon.toInt()
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

    private fun updateNotification(context: Context,
                                   vararg f: (RemoteViews, NotificationCompat.Builder) -> Unit) {
        //create builder
        val builder = getNotificationBuilder(context)
        //change view
        f.forEach { it(view, builder) }
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