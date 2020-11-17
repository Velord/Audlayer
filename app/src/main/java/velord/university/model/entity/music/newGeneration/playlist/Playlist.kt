package velord.university.model.entity.music.newGeneration.playlist

import androidx.room.*
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPos

@Fts4
@Entity
data class Playlist(
    val name: String,
    val songIdList: MutableList<Int>,
    @PrimaryKey @ColumnInfo(name = "rowid") val id: Int = 0
) {

    @Ignore
    lateinit var songWithPosList: List<SongWithPos>

    @Ignore var songList: List<AudlayerSong> = songWithPosList.map { it.song }

    fun isDefault(): Boolean = defaultPlaylist.contains(name)

    fun take(value: Int): Playlist = Playlist(name, songIdList.take(value).toMutableList()).also {
        it.songWithPosList = this.songWithPosList.take(value)
    }

    companion object {

        val defaultPlaylist: Array<String> = arrayOf(
            "Favourite", "Played", "Vk", "Downloaded", "Current"
        )


        fun other(playlist: List<Playlist>): List<Playlist> =
            playlist.filter {
                 defaultPlaylist.contains(it.name).not()
            }
    }
}