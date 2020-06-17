package velord.university.model.entity

import java.io.File

data class Song(
    val file: File,
    val icon: String = DrawableIcon.getRandomSongIconName()
)