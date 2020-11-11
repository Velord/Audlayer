package velord.university.model.converter

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File
import java.io.IOException

object SongBitrate {

    //can throw exception
    fun getBitrate(song: File): Int {
        val mex = MediaExtractor()
        mex.setDataSource(song.absolutePath)
        val mf = mex.getTrackFormat(0)
        return mf.getInteger(MediaFormat.KEY_BIT_RATE)
    }

    fun getKbps(song: File): Int =
        try { getBitrate(song) / 1000 }
        catch (e: Exception) {
            e.printStackTrace()
            -1
        }

    fun getKbpsString(song: File): String =
        "${getKbps(song)} kbit/s"
}