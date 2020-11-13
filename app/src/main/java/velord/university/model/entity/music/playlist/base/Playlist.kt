package velord.university.model.entity.music.playlist.base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import velord.university.ui.fragment.album.MAX_MOST_PLAYED
import java.io.File
import java.util.*


@Entity
data class Playlist(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "songs") var songs: List<String>,

    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {

    companion object {
        fun other(playlist: List<Playlist>): List<Playlist> =
            playlist.filter {
                it.name != "Favourite" && it.name != "Played" &&
                        it.name != "Vk" && it.name != "Downloaded"
            }

        fun favourite(playlist: List<Playlist>): Playlist =
            playlist.find { it.name == "Favourite" }!!

        fun otherAndFavourite(playlist: List<Playlist>): List<Playlist> =
            (other(playlist) + favourite(playlist)).map { it ->
                it.songs = it.songs.filter { it.isNotEmpty() }
                it
            }

        fun allSongFromPlaylist(playlist: List<Playlist>): List<File> =
            playlist.asSequence()
                .map { it.songs }
                .fold(mutableListOf<String>()) { joined, fromDB ->
                    joined.addAll(fromDB)
                    joined
                }
                .distinct()
                .map { File(it) }
                .filter { it.path.isNotEmpty() }

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