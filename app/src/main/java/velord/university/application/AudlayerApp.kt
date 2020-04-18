package velord.university.application

import android.app.Application
import android.content.Context
import android.os.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.repository.factory.AppDatabase
import velord.university.repository.factory.buildAppDatabase
import velord.university.repository.transaction.PlaylistTransaction
import java.io.File


//default playlist is Favourite, Played

class AudlayerApp : Application() {

    companion object {
        var db: AppDatabase? = null

        private val scope: CoroutineScope =
            CoroutineScope(Job() + Dispatchers.Default)

        fun getApplicationDir() =
            File(Environment.getExternalStorageDirectory(), "Audlayer")

        fun getApplicationVkDir() =
            File(getApplicationDir(), "Vk")

        fun initApp(context: Context) {
            //init working folder
            createFolder()
            //init db and create tables if not exist
            db = buildAppDatabase(context)
            scope.launch {
                PlaylistTransaction.checkDbTableColumn()
            }
        }

        private fun createFolder() {
            val mainExist = getApplicationDir()
            if (mainExist.exists().not()) {
                mainExist.mkdirs()
                val vkExist = getApplicationVkDir()
                if (vkExist.exists().not())
                    vkExist.mkdirs()
            }
        }
    }
}