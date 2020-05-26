package velord.university.repository

import android.content.Context
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.RadioStation
import velord.university.model.file.getJsonDataFromAsset

object RadioRepository {

    private val db = AudlayerApp.db!!

    suspend fun getAll(): List<RadioStation> =
        withContext(Dispatchers.IO) {
            AudlayerApp.db!!.radioDao().getAll()
        }

    suspend fun checkDefaultRadioStation(context: Context) =
        withContext(Dispatchers.IO) {
            val stations = db.radioDao().getAll()
            if (stations.isEmpty())
                insertDefaultRadioStation(context)
        }

    suspend fun likeByUrl(url: String) =
        withContext(Dispatchers.IO) {
            db.radioDao().updateLikeByUrl(url, true)
        }

    suspend fun unlikeByUrl(url: String) =
        withContext(Dispatchers.IO) {
            db.radioDao().updateLikeByUrl(url, false)
        }

    suspend fun getById(id: Int): RadioStation =
        withContext(Dispatchers.IO) {
            db.radioDao().getById(id)
        }

    suspend fun isLike(url: String): Boolean =
        withContext(Dispatchers.IO) {
            db.radioDao().getByUrl(url).liked
        }

    private suspend fun insertDefaultRadioStation(context: Context) =
        withContext(Dispatchers.IO) {
            val file = getJsonDataFromAsset(
                context,
                "DefaultRadioStation"
            )
            Log.i("AudlayerApp", file)

            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, RadioStation::class.java)
            val adapter: JsonAdapter<List<RadioStation>> = moshi.adapter(type)
            val stations = adapter.fromJson(file)

            db.radioDao().insertAll(*stations!!.toTypedArray())
        }
}