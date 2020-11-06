package velord.university.repository.fetch

import android.content.Context
import android.util.Log
import velord.university.model.entity.RadioStation
import velord.university.model.entity.file.getJsonDataFromAsset

object RadioFetch : FetchJson() {

    suspend fun getDefaultRadioStationList(context: Context): Array<RadioStation> {
        val file = getJsonDataFromAsset(
            context,
            "DefaultRadioStation"
        )!!
        Log.i("AudlayerApp", file)

        return file.deserializeJson()
    }
}