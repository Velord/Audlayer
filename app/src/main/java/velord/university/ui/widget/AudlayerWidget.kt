package velord.university.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.URLUtil
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import velord.university.R
import velord.university.application.broadcast.WidgetBroadcastReceiver
import velord.university.application.notification.MiniPlayerServiceNotification
import velord.university.ui.util.DrawableIcon


/**
 * Implementation of App Widget functionality.
 */
class AudlayerWidget : AppWidgetProvider() {

    companion object {
        private val TAG = "AudlayerWidget"

        var widgetArtist = ""
        var widgetTitle = ""
        var widgetIsPlaying = false
        var widgetIcon: String = ""
        var iconIsSong = true

        fun invokeUpdate(context: Context) {
            Log.d(TAG, "invokeUpdate")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetClassName = "velord.university.ui.widget.AudlayerWidget"
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, widgetClassName))
            //widget is exist
            if (ids.isNotEmpty()) {
                AppWidgetManager.getInstance(context)
                    .notifyAppWidgetViewDataChanged(ids, android.R.id.list)
                updateAppWidget(context, appWidgetManager, ids[0])
            }
        }

        private fun loadIcon(remoteViews: RemoteViews,
                             context: Context,
                             value: String,
                             appWidgetId: Int) {
            val widgetTarget: AppWidgetTarget = object : AppWidgetTarget(
                context,
                R.id.audlayer_widget_image,
                remoteViews,
                appWidgetId
            ) {}

            if (widgetIcon.isNotEmpty()) {
                when(iconIsSong) {
                    true -> {
                        if (URLUtil.isHttpUrl(value) ||
                            URLUtil.isHttpsUrl(value)
                        ) {
                            Glide.with(context)
                                .asBitmap()
                                .load(value)
                                .placeholder(R.drawable.song_item_black)
                                .into(widgetTarget)
                        } else remoteViews.setImageViewResource(
                            R.id.audlayer_widget_image,
                            widgetIcon.toInt()
                        )
                    }
                    false -> {
                        val radioIconAsset =
                            DrawableIcon.getResourceIdIcon(context, value)

                        Glide.with(context)
                            .asBitmap()
                            .load(radioIconAsset)
                            .placeholder(R.drawable.song_item_black)
                            .into(widgetTarget)
                    }
                }
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Log.d(TAG, "updateAppWidget")
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.audlayer_widget)
            //construct artist and title
            val widgetArtist = widgetArtist
            views.setTextViewText(R.id.audlayer_widget_artist, widgetArtist)
            val widgetTitle = widgetTitle
            views.setTextViewText(R.id.audlayer_widget_title, widgetTitle)
            //construct icon
            loadIcon(views, context, widgetIcon, appWidgetId)
            //construct isPlaying
            when(widgetIsPlaying) {
                true ->
                    views.setImageViewResource(
                        R.id.audlayer_widget_play_or_stop,
                        R.drawable.pause
                    )
                false ->
                    views.setImageViewResource(
                        R.id.audlayer_widget_play_or_stop,
                        R.drawable.play
                    )
            }
            //construct pending intent
            views.setListener(context)
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun Context.intentPrevious(): PendingIntent {
            val intentPrevious = Intent(
                this, WidgetBroadcastReceiver::class.java)
                .setAction(MiniPlayerServiceNotification.NOTIFICATION_ACTION_PREVIUOS)
            return  PendingIntent.getBroadcast(
                this, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        private fun Context.intentPlayOrStop(): PendingIntent {
            val intentPlay = Intent(
                this, WidgetBroadcastReceiver::class.java
            )
                .setAction(MiniPlayerServiceNotification.NOTIFICATION_ACTION_PLAY_OR_STOP)
            return PendingIntent.getBroadcast(
                this, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        private fun Context.intentNext(): PendingIntent {
            val intentPrevious = Intent(
                this, WidgetBroadcastReceiver::class.java)
                .setAction(MiniPlayerServiceNotification.NOTIFICATION_ACTION_NEXT)
            return PendingIntent.getBroadcast(
                this, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        private fun RemoteViews.setListener(context: Context) {
            setOnClickPendingIntent(R.id.audlayer_widget_previous, context.intentPrevious())
            setOnClickPendingIntent(R.id.audlayer_widget_play_or_stop, context.intentPlayOrStop())
            setOnClickPendingIntent(R.id.audlayer_widget_next, context.intentNext())
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        //invoke service to give info
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}