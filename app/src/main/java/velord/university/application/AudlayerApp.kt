package velord.university.application

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.model.entity.RadioStation
import velord.university.model.util.getJsonDataFromAsset
import velord.university.repository.factory.AppDatabase
import velord.university.repository.factory.buildAppDatabase
import velord.university.repository.transaction.PlaylistTransaction
import java.io.File


//default playlist is Favourite, Played

class AudlayerApp : Application() {

    private val TAG = "AudlayerApp"

    companion object {
        var db: AppDatabase? = null

        private val scope: CoroutineScope =
            CoroutineScope(Job() + Dispatchers.Default)

        fun getApplicationDir() =
            File(Environment.getExternalStorageDirectory(), "Audlayer")

        fun getApplicationVkDir() =
            File(getApplicationDir(), "Vk")

        fun getApplicationRadioDir() =
            File(getApplicationDir(), "Radio")

        fun initApp(context: Context) {
            //init working folder
            createFolder()
            //init db and create tables if not exist
            db = buildAppDatabase(context)
            scope.launch {
                PlaylistTransaction.checkDbTableColumn()
                checkDefaultRadioStation(context)
            }
        }

        private fun checkDefaultRadioStation(context: Context) {
            db?.let {
                val stations = it.radioDao().getAll()
                if (stations.isEmpty())
                    insertDefaultRadioStation(context)
            }
        }

        private fun insertDefaultRadioStation(context: Context) {
            val file = getJsonDataFromAsset(context, "DefaultRadioStation")
            Log.i("AudlayerApp", file)

            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, RadioStation::class.java)
            val adapter: JsonAdapter<List<RadioStation>> = moshi.adapter(type)
            val stations = adapter.fromJson(file)

            db?.let {
                it.radioDao().insertAll(*stations!!.toTypedArray())
            }
        }

        private fun createFolder() {
            val mainExist = getApplicationDir()
            if (mainExist.exists().not()) {
                mainExist.mkdirs()

                val vkExist = getApplicationVkDir()
                if (vkExist.exists().not())
                    vkExist.mkdirs()

                val radioExist = getApplicationRadioDir()
                if (radioExist.exists().not())
                    radioExist.mkdirs()
            }
        }
    }
}