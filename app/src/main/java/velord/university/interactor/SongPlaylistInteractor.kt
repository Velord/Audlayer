package velord.university.interactor

import velord.university.model.entity.music.song.main.AudlayerSong

object SongPlaylistInteractor {

    var songList: List<AudlayerSong> = listOf()

    val songPathList: Array<String>
        get() = songList.map { it.path }.toTypedArray()
}