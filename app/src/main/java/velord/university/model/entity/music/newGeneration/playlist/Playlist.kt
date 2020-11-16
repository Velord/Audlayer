package velord.university.model.entity.music.newGeneration.playlist

import androidx.room.*
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPos
import velord.university.ui.fragment.album.MAX_MOST_PLAYED
import java.io.File
import java.util.HashMap

@Fts4
@Entity
data class Playlist(
    val name: String,
    val songIdList: List<Int>,
    @PrimaryKey @ColumnInfo(name = "rowid") val id: Int = 0
) {

    @Ignore
    lateinit var songs: List<SongWithPos>

    companion object {

        private val defaultPlaylist: Array<String> = arrayOf(
            "Favourite", "Played", "Vk", "Downloaded", "Current"
        )


        fun other(playlist: List<Playlist>): List<Playlist> =
            playlist.filter {
                 defaultPlaylist.contains(it.name).not()
            }

        fun getMostPlayed(playlist: List<String>) =
            playlist
                .fold(HashMap<String, Int>()) { mostPlayed, song ->
                    if (song.isNotEmpty()) {
                        mostPlayed += if (mostPlayed.containsKey(song).not())
                            Pair(song, 1)
                        else {
                            val count = mostPlayed[song]
                            Pair(song, count!!.plus(1))
                        }
                    }
                    mostPlayed
                }
                .toList()
                .sortedBy { it.second }
                .reversed()
                .map { it.first }
                .take(MAX_MOST_PLAYED)

        fun whichPlaylist(playlist: List<Playlist>,
                          path: String): String  {
            other(playlist).forEach {
                if(it.songs.contains(path))
                    return it.name
            }
            return ""
        }
    }
}