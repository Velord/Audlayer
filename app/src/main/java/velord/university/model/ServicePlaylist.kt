package velord.university.model

import java.io.File

class ServicePlaylist(val songs: MutableList<File> = mutableListOf()) {

    private var currentPos: Int = 0

    val notShuffled: MutableList<File> = mutableListOf()

    init {
        notShuffled.addAll(songs)
    }

    fun shuffle() {
        songs.shuffle()
    }

    fun getNext(): File {
        if (currentPos + 1 > songs.lastIndex) {
            currentPos = -1
        }
        return songs[++currentPos]
    }

    private fun setCurrentPos(pos: Int) {
        currentPos = pos
    }

    fun getSong(pos: Int = currentPos): File = songs[pos]

    fun getSongAndResetQuery(path: String): File {
        val newCurrentPos = getSongPos(path)
        setCurrentPos(newCurrentPos)
        return getSong()
    }

    fun getSongPath(): String = getSong().path

    fun getSongPos(file: File = getSong()): Int = songs.indexOf(file)

    fun getSongPos(path: String): Int = songs.indexOf(File(path))
}