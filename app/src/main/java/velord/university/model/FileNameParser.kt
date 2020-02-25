package velord.university.model

import java.io.File

object FileNameParser {

    fun getSongArtist(file: File) =
        file.name.substringBefore(" - ")

    fun getSongName(file: File) =
        file.name
            .substringAfter(" - ")
            .substringBefore(".${file.extension}")

    fun slashReplaceArrow(path: String): String =
        path.replace("/", " > ").substringAfter(" > ")

    fun arrowReplaceSlash(path: String): String =
        "/${path.replace(" > ", "/")}"

    fun removeExtension(file: File): String =
        if(file.isDirectory.not()) {
            if (file.extension.isNotEmpty())
                file.name.substringBeforeLast(".${file.extension}")
            else file.name
        } else file.name
}