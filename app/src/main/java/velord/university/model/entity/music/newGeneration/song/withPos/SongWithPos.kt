package velord.university.model.entity.music.newGeneration.song.withPos

import androidx.room.*
import velord.university.model.entity.music.newGeneration.song.AudlayerSong


@Entity(
    indices = [Index("song_id")],
    foreignKeys = [
        ForeignKey(
        entity = AudlayerSong::class,
        parentColumns = arrayOf("rowid"),
        childColumns = arrayOf("song_id"),
        onDelete = ForeignKey.CASCADE)
    ]
)
data class SongWithPos(
    val pos: Int,

    @ColumnInfo(name = "song_id")
    val songId: Int,

    @PrimaryKey @ColumnInfo(name = "rowid") val id: Int = 0
) {

    @Ignore
    lateinit var song: AudlayerSong
}