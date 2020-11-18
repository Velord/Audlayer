package velord.university.application

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.application.service.hub.player.MiniPlayerServiceBroadcastReceiver
import velord.university.application.service.radio.RadioServiceBroadcastReceiver
import velord.university.model.coroutine.getScope
import velord.university.repository.hub.FolderRepository
import velord.university.repository.hub.RadioRepository
import velord.university.repository.db.factory.AppDatabase
import velord.university.repository.db.factory.buildAppDatabase
import velord.university.repository.db.transaction.PlaylistTransaction

class AudlayerApp : Application() {

    private val TAG = "AudlayerApp"

    override fun onCreate() {
        super.onCreate()
        //service mini player general
        startService(this, MiniPlayerServiceBroadcastReceiver())
        //service mini player radio
        startService(this, RadioServiceBroadcastReceiver())
        //service widget
        //for undefined times comment this 6.11.2020
//        startService(this, WidgetService())
//        //service notification
//        startService(this, AudlayerNotificationService())
    }

    companion object {

        private val TAG = "AudlayerAppCompanion"

        private var db: AppDatabase? = null

        private val scope: CoroutineScope = getScope()

        fun getDatabase(): AppDatabase? = db

        fun startService(context: Context,
                         service: Service) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(Intent(context, service::class.java))
            else context.startService(Intent(context, service::class.java))


        fun initApp(context: Context) {
            //init working folder
            FolderRepository.createFolder()
            //init db and create tables if not exist
            db = buildAppDatabase(context)
            scope.launch {
                try {
                    PlaylistTransaction.checkDbTableColumn()
                    RadioRepository.checkDefaultRadioStation(context)
                }
                catch (e: Exception) {
                    Log.d(TAG, e.message.toString())
                }
            }
        }
    }
}