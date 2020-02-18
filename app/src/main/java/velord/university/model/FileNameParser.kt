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

    fun arrowreplaceSlash(path: String): String =
        "/${path.replace(" > ", "/")}"
}