package velord.university.model.entity.music.song.serviceSong

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity
data class MiniPlayerServiceSong(
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "position") val pos: Int,

    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    companion object {
        fun getSongsToDb(songs: List<File>): Array<MiniPlayerServiceSong> =
            songs.mapIndexed { index, elem ->
                val path = elem.path
                MiniPlayerServiceSong(path, index)
            }.toTypedArray()

        fun getSongsToPlaylist(songs: List<MiniPlayerServiceSong>): List<File> =
            songs.filter { it.path.isNotEmpty() }
                .sortedBy { it.pos }
                .map { File(it.path) }
    }
}