package velord.university.model.entity.music.newGeneration.playlist

import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import java.io.File

class ServicePlaylist(val notShuffled: Playlist) {

    private var currentPos: Int = 0

    private val songQueue: MutableList<AudlayerSong> = mutableListOf()

    init {
        songQueue.addAll(notShuffled.songList)
    }

    fun shuffle() = songQueue.shuffle()

    fun notShuffle() {
        songQueue.apply {
            clear()
            addAll(notShuffled.songList)
        }
    }

    fun firstInQueue() = songQueue.first()

    fun lastInQueue() = songQueue.last()

    fun clearQueue() {
        notShuffled.songWithPosList.
        songQueue.clear()
    }

    fun addToQueue(vararg song: File): Int {
        notShuffled.addAll(song)
        songQueue.addAll(song)

        return notShuffled.lastIndex + 1
    }

    fun getNext(): File {
        if (currentPos + 1 > songQueue.lastIndex) {
            currentPos = -1
        }
        return songQueue[++currentPos]
    }

    private fun setCurrentPos(pos: Int) {
        currentPos = pos
    }

    fun getSong(pos: Int = currentPos): File = songQueue[pos]

    fun getSongAndResetQuery(path: String): File {
        val newCurrentPos = getSongPos(path)
        setCurrentPos(newCurrentPos)
        return getSong()
    }

    fun getSongPath(): String = getSong().path

    fun getSongPos(file: File = getSong()): Int = songQueue.indexOf(file)

    fun getSongPos(path: String): Int = songQueue.indexOf(File(path))
}