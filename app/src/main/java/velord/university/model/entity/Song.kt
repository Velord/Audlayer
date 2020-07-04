package velord.university.model.entity

import velord.university.ui.util.DrawableIcon
import java.io.File

data class Song(
    val file: File,
    val iconUrl: String? = "",
    val getIconF: () -> Int = DrawableIcon.getRandomSongIconName,
    val icon: Int = getIconF()
)