package velord.university.model.entity.music.playlist

import velord.university.model.entity.music.song.main.AudlayerSong

class ServicePlaylist(
    val currentPlaylist: Playlist
) {

    private var currentPos: Int = 0

    private val songQueue: MutableList<AudlayerSong> = mutableListOf()

    init {
        songQueue.addAll(currentPlaylist.songList)
    }

    fun shuffle() = songQueue.shuffle()

    fun notShuffle() {
        songQueue.apply {
            clear()
            addAll(currentPlaylist.songList)
        }
    }

    fun firstInQueue() = songQueue.first()

    fun lastInQueue() = songQueue.last()

    fun clearQueue() {
        currentPlaylist.songList = listOf()
        songQueue.clear()
    }

    fun addToQueue(vararg song: AudlayerSong): Int {
        currentPlaylist.songList += song
        songQueue.addAll(song)

        return currentPlaylist.songList.lastIndex + 1
    }

    fun getNext(): AudlayerSong {
        if (currentPos + 1 > songQueue.lastIndex) {
            currentPos = -1
        }
        return songQueue[++currentPos]
    }

    private fun setCurrentPos(pos: Int) {
        currentPos = pos
    }

    fun getSong(pos: Int = currentPos): AudlayerSong = songQueue[pos]

    fun getSongAndResetQuery(song: AudlayerSong): AudlayerSong {
        val newCurrentPos = getSongPos(song)
        setCurrentPos(newCurrentPos)
        return getSong()
    }

    fun getSongPath(): String = getSong().path

    fun getSongPos(file: AudlayerSong = getSong()): Int = songQueue.indexOf(file)

    fun getSongPos(path: String): Int = songQueue.indexOfFirst { it.path == path }
}