package velord.university.application

import android.app.Application
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.application.service.MiniPlayerServiceBroadcastReceiver
import velord.university.application.service.RadioServiceBroadcastReceiver
import velord.university.repository.FolderRepository
import velord.university.repository.RadioRepository
import velord.university.repository.factory.AppDatabase
import velord.university.repository.factory.buildAppDatabase
import velord.university.repository.transaction.PlaylistTransaction


//default playlist is Favourite, Played

class AudlayerApp : Application() {

    private val TAG = "AudlayerApp"

    override fun onCreate() {
        super.onCreate()

        //service mini player general
        startService(
            Intent(
                this,
                MiniPlayerServiceBroadcastReceiver().javaClass
            )
        )
        //service mini player radio
        startService(
            Intent(
                this,
                RadioServiceBroadcastReceiver().javaClass
            )
        )
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        stopService(Intent(this,
            MiniPlayerServiceBroadcastReceiver().javaClass))
        stopService(Intent(this,
            RadioServiceBroadcastReceiver().javaClass))
    }

    companion object {
        var db: AppDatabase? = null

        private val scope: CoroutineScope =
            CoroutineScope(Job() + Dispatchers.Default)

        fun initApp(context: Context) {
            //init working folder
            FolderRepository.createFolder()
            //init db and create tables if not exist
            db = buildAppDatabase(context)
            scope.launch {
                PlaylistTransaction.checkDbTableColumn()
                RadioRepository.checkDefaultRadioStation(context)
            }
        }
    }
}