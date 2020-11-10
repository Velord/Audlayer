package velord.university.repository.fetch

import android.content.Context
import android.util.Log
import velord.university.model.entity.music.RadioStation
import velord.university.model.entity.fileType.json.general.getJsonDataFromAsset

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