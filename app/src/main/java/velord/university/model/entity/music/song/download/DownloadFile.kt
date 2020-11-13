package velord.university.model.entity.music.song.download

import android.net.Uri
import android.os.Environment
import java.io.File

data class DownloadFile(
    val song: DownloadSong
) {
    val ext = ".mp3"
    val name = "${song.artist} - ${song.title}"
    val fileName = "$name$ext"
    val downloadDir = "${Environment.getExternalStorageDirectory().path}" +
            "/Audlayer/Download/"
    val fullPath = "$downloadDir$fileName"
    val downloadedFile = File(downloadDir, fileName)
    val uriFromFile: Uri = Uri.fromFile(downloadedFile)
}