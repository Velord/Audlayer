package velord.university.model.isyStreamMeta

import android.util.Log
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class IcyStreamMeta {
    lateinit var urlStream: URL
    private lateinit var metadata: Map<String?, String?>
    private var isError = false
    private lateinit var data: Map<String?, String?>

    val artist: String
        get() {
            data = getMetadata()
            if (!data.containsKey("StreamTitle")) return ""
            val streamTitle = data["StreamTitle"] ?: return ""
            Log.d("IcyStreamMeta", "stream title: $streamTitle")

            if (streamTitle.contains('-').not()) return ""

            val index = streamTitle.indexOf("-")
            val title = streamTitle.substring(0, index)
            return title.trim { it <= ' ' }
        }

    val title: String
        get() {
            data = getMetadata()
            if (!data.containsKey("StreamTitle")) return ""
            val streamTitle = data["StreamTitle"] ?: return ""
            Log.d("IcyStreamMeta", "stream title: $streamTitle")

            if (streamTitle.contains('-').not()) return ""

            val index = streamTitle.indexOf("-") + 1
            val artist = streamTitle.substring(index)
            return artist.trim { it <= ' ' }
        }

    fun getArtistAndTitle(): String = "$artist - $title"

    private fun getMetadata(): Map<String?, String?> {
        if (::metadata.isInitialized.not()) {
            refreshMeta()
        }
        return metadata
    }

    @Synchronized
    @Throws(IOException::class)
    fun refreshMeta() {
        retrieveMetadata()
    }

    @Synchronized
    @Throws(IOException::class)
    private fun retrieveMetadata() {
        val con = urlStream!!.openConnection()
        con.setRequestProperty("Icy-MetaData", "1")
        con.setRequestProperty("Connection", "close")
        con.setRequestProperty("Accept", null)
        con.connect()
        var metaDataOffset = 0
        val headers =
            con.headerFields
        val stream = con.getInputStream()
        if (headers.containsKey("icy-metaint")) {
            // Headers are sent via HTTP
            metaDataOffset = headers["icy-metaint"]!![0].toInt()
        } else { // Headers are sent within a stream
            val strHeaders = StringBuilder()
            val cond = (stream.read() as Char).toInt() != -1
            while (cond) {
                strHeaders.append(cond)
                if (strHeaders.length > 5 && strHeaders.substring(
                        strHeaders.length - 4,
                        strHeaders.length
                    ) == "\r\n\r\n"
                ) { // end of headers
                    break
                }
            }
            // Match headers to get metadata offset within a stream
            val p =
                Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n")
            val m = p.matcher(strHeaders.toString())
            if (m.find()) {
                metaDataOffset = m.group(2).toInt()
            }
        }
        // In case no data was sent
        if (metaDataOffset == 0) {
            isError = true
            return
        }
        // Read metadata
        var b: Int
        var count = 0
        var metaDataLength = 4080 // 4080 is the max length
        var inData: Boolean
        val metaData = StringBuilder()
        // Stream position should be either at the beginning or right after headers
        while (stream.read().also { b = it } != -1) {
            count++
            // Length of the metadata
            if (count == metaDataOffset + 1) {
                metaDataLength = b * 16
            }
            inData = (count > metaDataOffset + 1 && count < metaDataOffset + metaDataLength)
            if (inData) {
                if (b != 0) {
                    metaData.append(b.toChar())
                }
            }
            if (count > metaDataOffset + metaDataLength) {
                break
            }
        }
        // Set the data
        metadata =
            parseMetadata(
                metaData.toString()
            )
        // Close
        stream.close()
    }

    companion object {
        fun parseMetadata(metaString: String): Map<String?, String?> {
            val metadata: MutableMap<String?, String?> = HashMap()
            val metaParts = metaString.split(";").toTypedArray()
            val p =
                Pattern.compile("^([a-zA-Z]+)=\\'([^\\']*)\\'$")
            var m: Matcher
            for (i in metaParts.indices) {
                m = p.matcher(metaParts[i])
                if (m.find()) {
                    metadata[m.group(1) as String] = m.group(2) as String
                }
            }
            return metadata
        }
    }

}