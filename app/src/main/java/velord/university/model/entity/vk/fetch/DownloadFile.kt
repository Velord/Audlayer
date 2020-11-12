package velord.university.model.entity.vk.fetch

import android.net.Uri
import android.os.Environment
import velord.university.model.entity.music.song.DownloadSong
import velord.university.model.entity.vk.entity.VkSong
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