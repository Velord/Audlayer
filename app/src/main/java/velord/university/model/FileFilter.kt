package velord.university.model

import java.io.File
import java.util.*

object FileFilter {

    fun filterOnlyAudio(file: File): List<File> {
        val filesInFolder = file.listFiles() ?: arrayOf()
        return filesInFolder.filter {
            FileExtension.checkCompatibleFileExtension(it) ==
                    FileExtensionModifier.AUDIO
        }
    }

    val filterBySearchQuery: (File, String) -> Boolean = { file, query ->
        val extension =
            FileExtension.checkCompatibleFileExtension(file) !=
                    FileExtensionModifier.NOTCOMPATIBLE
        val contQuery =
            FileNameParser.removeExtension(file)
                .substringAfterLast('/')
                .toUpperCase(Locale.ROOT)
                .contains(query.toUpperCase(Locale.ROOT))

        extension && contQuery
    }

    val filterByEmptySearchQuery: (File, String) -> Boolean = { file, _ ->
        FileExtension.checkCompatibleFileExtension(file) !=
                FileExtensionModifier.NOTCOMPATIBLE
    }

    val orderByName: (File) -> String = {
        FileNameParser.getSongName(it)
    }

    val orderByArtist: (File) -> String = {
        FileNameParser.getSongArtist(it)
    }

    val orderByDateAdded: (File) -> Long = {
        it.lastModified()
    }

}