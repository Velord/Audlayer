package velord.university.application

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.repository.FolderRepository
import velord.university.repository.RadioRepository
import velord.university.repository.factory.AppDatabase
import velord.university.repository.factory.buildAppDatabase
import velord.university.repository.transaction.PlaylistTransaction


//default playlist is Favourite, Played

class AudlayerApp : Application() {

    private val TAG = "AudlayerApp"

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