package velord.university.model.entity.music.playlist

import androidx.room.*
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.model.entity.music.song.withPos.SongWithPos

@Fts4
@Entity
data class Playlist(
    val name: String,
    val songIdList: MutableList<Int>,
    @PrimaryKey @ColumnInfo(name = "rowid") val id: Int = 0
) {

    @Ignore
    var songWithPosList: List<SongWithPos> = listOf()

    @Ignore var songList: List<AudlayerSong> = songWithPosList.map { it.song }

    fun isDefault(): Boolean = defaultPlaylistName.contains(name)

    fun take(value: Int): Playlist =
        Playlist(name, songIdList.take(value).toMutableList())
            .also {
                it.songWithPosList = this.songWithPosList.take(value)
            }

    companion object {

        val defaultPlaylistName: Array<String> = arrayOf(
            "Favourite", "Played", "Vk", "Downloaded", "Current"
        )


        fun other(playlist: List<Playlist>): List<Playlist> =
            playlist.filter {
                 defaultPlaylistName.contains(it.name).not()
            }
    }
}