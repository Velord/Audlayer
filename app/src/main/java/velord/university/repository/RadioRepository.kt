package velord.university.repository

import android.content.Context
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import velord.university.application.AudlayerApp
import velord.university.model.entity.RadioStation
import velord.university.model.util.getJsonDataFromAsset

object RadioRepository {

    fun checkDefaultRadioStation(context: Context) {
        AudlayerApp.db?.let {
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

        AudlayerApp.db?.let {
            it.radioDao().insertAll(*stations!!.toTypedArray())
        }
    }
}