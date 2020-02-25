package velord.university.model

import java.io.File
import java.util.*

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

    fun filterOnlyAudio(file: File): List<File> {
        val filesInFolder = file.listFiles() ?: arrayOf()
        return filesInFolder.filter {
            checkCompatibleFileExtension(it) ==
                    FileExtensionModifier.AUDIO
        }
    }

    val filterBySearchQuery: (File, String) -> Boolean = { file, query ->
        val extension =
            checkCompatibleFileExtension(file) !=
                    FileExtensionModifier.NOTCOMPATIBLE
        val contQuery =
            FileNameParser.removeExtension(file)
                .substringAfterLast('/')
                .toUpperCase(Locale.ROOT)
                .contains(query.toUpperCase(Locale.ROOT))

        extension && contQuery
    }

    val filterByEmptySearchQuery: (File, String) -> Boolean = { file, _->
        checkCompatibleFileExtension(file) !=
                FileExtensionModifier.NOTCOMPATIBLE
    }
}

enum class FileExtensionModifier {
    DIRECTORY,
    AUDIO,
    NOTCOMPATIBLE;
}
