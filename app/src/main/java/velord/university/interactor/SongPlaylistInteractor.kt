package velord.university.interactor

import velord.university.model.entity.music.song.main.AudlayerSong

object SongPlaylistInteractor {
    lateinit var songs: Array<AudlayerSong>

    val songsPath: Array<String>
        get() = songs.map { it.path }.toTypedArray()
}