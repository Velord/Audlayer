package velord.university.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.util.*

@Entity
data class MiniPlayerServiceSong(
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "position") val pos: Int,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) {
    companion object {
        fun getSongsToDb(songs: List<File>, getPosF: (String) -> Int): Array<MiniPlayerServiceSong> =
            songs.map {
                val path = it.path
                val pos = getPosF(path)
                MiniPlayerServiceSong(path, pos)
            }.toTypedArray()

        fun getSongsToPlaylist(songs: List<MiniPlayerServiceSong>): List<File> =
            songs.filter { it.path.isNotEmpty() }
                .sortedBy { it.pos }
                .map { File(it.path) }
    }
}