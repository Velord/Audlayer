package velord.university.model.entity.vk.fetch

import android.net.Uri
import android.os.Environment
import velord.university.model.entity.vk.entity.VkSong
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