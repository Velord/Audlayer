package velord.university.model

import java.io.File

object FileExtension {
    //https://developer.android.com/guide/topics/media/media-formats
    fun checkCompatibleFileExtension(file: File): FileExtensionModifier =
        when {
            file.isDirectory -> FileExtensionModifier.DIRECTORY
            file.extension == "mp3" || file.extension == "3gp" ||
                    file.extension == "mp4" || file.extension == "m4a" ||
                    file.extension == "aac" || file.extension == "ts" ||
                    file.extension == "flac" || file.extension == "gsm" ||
                    file.extension == "mid" || file.extension == "xmf" ||
                    file.extension == "mxmf" || file.extension == "rtttl" ||
                    file.extension == "rtx" || file.extension == "ota" ||
                    file.extension == "imy" || file.extension == "mkv" ||
                    file.extension == "wav" || file.extension == "ogg"
            -> FileExtensionModifier.AUDIO
            else -> FileExtensionModifier.NOTCOMPATIBLE
        }
}

enum class FileExtensionModifier {
    DIRECTORY,
    AUDIO,
    NOTCOMPATIBLE;
}
