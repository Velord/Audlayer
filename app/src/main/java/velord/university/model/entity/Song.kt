package velord.university.model.entity

import velord.university.ui.util.DrawableIcon
import java.io.File

data class Song(
    val file: File,
    val getIconF: () -> String = DrawableIcon.getRandomSongIconName,
    val icon: String = getIconF()
)