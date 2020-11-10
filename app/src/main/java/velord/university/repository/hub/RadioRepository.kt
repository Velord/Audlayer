package velord.university.repository.hub

import android.content.Context
import velord.university.model.coroutine.onIO
import velord.university.application.AudlayerApp.Companion.db
import velord.university.model.entity.music.RadioStation
import velord.university.repository.fetch.RadioFetch

object RadioRepository {

    suspend fun getAll(): List<RadioStation> = onIO {
        db?.run { radioDao().getAll() } ?: listOf()
    }

    suspend fun checkDefaultRadioStation(context: Context) = onIO {
        val stations = db?.run {
            radioDao().getAll()
        } ?: listOf()
        if (stations.isEmpty())
            insertDefaultRadioStation(context)
    }

    suspend fun likeByUrl(url: String) = onIO {
        db?.run {
            radioDao().updateLikeByUrl(url, true)
        }
    }

    suspend fun unlikeByUrl(url: String) = onIO {
        db?.run {
            radioDao().updateLikeByUrl(url, false)
        }
    }

    suspend fun getById(id: Int): RadioStation? = onIO {
        db?.run { radioDao().getById(id) }
    }

    suspend fun isLike(url: String): Boolean = onIO {
        db!!.radioDao().getByUrl(url).liked
    }

    suspend fun clearTable() = onIO {
        db?.run { radioDao().nudeTable() }
    }

    private suspend fun insertDefaultRadioStation(context: Context) = onIO {
        val stations = RadioFetch.getDefaultRadioStationList(context)
        db?.run {
            radioDao().insertAll(*stations)
        }
    }
}