package velord.university.interactor

import java.io.File

object SongPlaylistInteractor {
    lateinit var songs: Array<File>

    val songsPath: Array<String>
        get() = songs.map { it.path }.toTypedArray()
}