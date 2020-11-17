package velord.university.repository.hub

import android.content.Context
import velord.university.repository.db.transaction.hub.DB.radioTransaction
import velord.university.repository.fetch.RadioFetch

object RadioRepository : BaseRepository() {

    override val TAG: String = "RadioRepository"

    suspend fun checkDefaultRadioStation(context: Context) =
        fetch("checkDefaultRadioStation") {
            val stations = db?.run {
                radioDao().getAll()
            } ?: listOf()
            if (stations.isEmpty())
                insertDefaultRadioStation(context)
        }

    suspend fun likeByUrl(url: String) =
        radioTransaction("likeByUrl") {
            updateLikeByUrl(url, true)
        }

    suspend fun unlikeByUrl(url: String) =
        radioTransaction("unlikeByUrl") {
            updateLikeByUrl(url, false)
        }

    private suspend fun insertDefaultRadioStation(context: Context) =
        fetch("insertDefaultRadioStation") {
            val stations = RadioFetch.getDefaultRadioStationList(context)
            radioTransaction("insertDefaultRadioStation") {
                insertAll(*stations)
            }
        }
}