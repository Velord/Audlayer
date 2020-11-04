package velord.university.repository.hub

import android.content.Context
import android.util.Log
import com.statuscasellc.statuscase.model.coroutine.onIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp.Companion.db
import velord.university.model.entity.RadioStation
import velord.university.model.file.getJsonDataFromAsset
import velord.university.model.functionalDataSctructure.result.Result
import velord.university.repository.fetch.RadioFetch

object RadioRepository {

    suspend fun getAll(): List<RadioStation> =
        withContext(Dispatchers.IO) {
            db?.run {
                radioDao().getAll()
            } ?: listOf()
        }

    suspend fun checkDefaultRadioStation(context: Context) =
        withContext(Dispatchers.IO) {
            val stations = db?.run {
                radioDao().getAll()
            } ?: listOf()
            if (stations.isEmpty())
                insertDefaultRadioStation(context)
        }

    suspend fun likeByUrl(url: String) =
        withContext(Dispatchers.IO) {
            db?.run {
                radioDao().updateLikeByUrl(url, true)
            }
        }

    suspend fun unlikeByUrl(url: String) =
        withContext(Dispatchers.IO) {
            db?.run {
                radioDao().updateLikeByUrl(url, false)
            }
        }

    suspend fun getById(id: Int): RadioStation? =
        withContext(Dispatchers.IO) {
            db?.run {
                radioDao().getById(id)
            }
        }

    suspend fun isLike(url: String): Result<Boolean?> =
        withContext(Dispatchers.IO) {
            Result.of {
                db!!.radioDao().getByUrl(url).liked
            }
        }

    suspend fun clearTable() =
        withContext(Dispatchers.IO) {
            db?.run {
                radioDao().nudeTable()
            }
        }

    private suspend fun insertDefaultRadioStation(context: Context) = onIO {
        val stations = RadioFetch.getDefaultRadioStationList(context)
        db?.run {
            radioDao().insertAll(*stations)
        }
    }
}