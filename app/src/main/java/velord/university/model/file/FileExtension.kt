package velord.university.model.file

import java.io.File

object FileExtension {

    private val audioExtension = arrayOf("mp3", "3gp", "mp4", "m4a",
        "aac", "ts", "flac", "gsm", "mid", "xmf", "mxmf",
        "rtttl", "rtx", "ota", "imy", "mkv", "wav", "ogg")

    //https://developer.android.com/guide/topics/media/media-formats
    fun getFileExtension(file: File): FileExtensionModifier =
        when {
            file.isDirectory ->
                FileExtensionModifier.DIRECTORY
            file.extension in audioExtension ->
                FileExtensionModifier.AUDIO
            else -> FileExtensionModifier.NOT_COMPATIBLE
        }

    fun isAudio(ext: String): Boolean = ext in audioExtension
}

enum class FileExtensionModifier {
    DIRECTORY,
    AUDIO,
    NOT_COMPATIBLE;
}
