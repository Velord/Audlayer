package velord.university.interactor

import velord.university.model.entity.music.Song

object SongPlaylistInteractor {
    lateinit var songs: Array<Song>

    val songsPath: Array<String>
        get() = songs.map { it.file.path }.toTypedArray()
}