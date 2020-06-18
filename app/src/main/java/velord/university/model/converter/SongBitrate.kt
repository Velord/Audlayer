package velord.university.model.converter

import android.media.MediaExtractor
import android.media.MediaFormat
import velord.university.model.functionalDataSctructure.result.Result
import java.io.File
import java.io.IOException

object SongBitrate {

    fun getBitrate(song: File): Result<Int> = Result.of {
        val mex = MediaExtractor()
        try {
            mex.setDataSource(song.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val mf = mex.getTrackFormat(0)
        mf.getInteger(MediaFormat.KEY_BIT_RATE)
    }

    fun getKbps(song: File): Int {
        val bitrate = getBitrate(song).getOrElse(0)

        return bitrate / 1000
    }

    fun getKbpsString(song: File): String =
        "${getKbps(song)} kbit/s"
}