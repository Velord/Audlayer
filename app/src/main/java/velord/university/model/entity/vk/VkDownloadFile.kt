package velord.university.model.entity.vk

import android.net.Uri
import android.os.Environment
import java.io.File

data class VkDownloadFile(
    val song: VkSong
) {
    val ext = ".mp3"
    val name = "${song.artist} - ${song.title}"
    val fileName = "$name$ext"
    val vkDir = "${Environment.getExternalStorageDirectory().path}/Audlayer/Vk/"
    val fullPath = "$vkDir$fileName"
    val downloadedFile = File(vkDir, fileName)
    val uriFromFile: Uri = Uri.fromFile(downloadedFile)
}