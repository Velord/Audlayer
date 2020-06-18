package velord.university.model.converter

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File
import java.io.IOException

object SongBitrate {

    fun getBitrate(song: File): Int {
        val mex = MediaExtractor()
        try {
            mex.setDataSource(song.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val mf = mex.getTrackFormat(0)
        return mf.getInteger(MediaFormat.KEY_BIT_RATE)
    }

    fun getKbps(song: File): Int {
        val bitrate = getBitrate(song)

        return bitrate / 1000
    }

    fun getKbpsString(song: File): String =
        "${getKbps(song)} kbps"
}