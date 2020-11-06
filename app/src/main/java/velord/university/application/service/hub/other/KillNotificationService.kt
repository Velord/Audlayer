package velord.university.application.service.hub.other

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder


class KillNotificationsService : Service() {

    inner class KillBinder(val service: Service) : Binder()

    private lateinit var notificationManager: NotificationManager

    private val binder: IBinder = KillBinder(this)

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(PLAYER_NOTIFICATION_ID)
    }

    companion object {
        const val PLAYER_NOTIFICATION_ID = 2345
    }
}