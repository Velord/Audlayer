package velord.university.model.entity.music.song.main

import androidx.room.*
import velord.university.model.entity.music.song.download.DownloadSong
import java.io.File
import java.time.LocalDateTime

@Fts4
@Entity
data class AudlayerSong(
    val artist: String,
    val title: String,
    val duration: Int,
    @ColumnInfo(name = "img_url")
    val imgUrl: String = "",
    @ColumnInfo(name = "date_added")
    val dateAdded: LocalDateTime = LocalDateTime.now(),
    val url: String = "",
    val path: String = "",
    @PrimaryKey @ColumnInfo(name = "rowid") val id: Int = 0,
) {

    fun toDownloadSong(): DownloadSong =
        DownloadSong(artist, title, path)

    fun filterByQuery(query: String): Boolean =
        "${artist}${title}".contains(query)

    fun getFullName(): String = "$artist$title"

    fun getWithNewPath(newPath: String): AudlayerSong =
        AudlayerSong(artist, title, duration, imgUrl, dateAdded, url, newPath, id)

    fun toFile(): File = File(path)

    companion object {

        fun Array<AudlayerSong>.filterByQuery(query: String): List<AudlayerSong> =
            this.filter { it.filterByQuery(query) }
    }
}