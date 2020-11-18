package velord.university.model.entity.music.song

object QueueResolver {

    var loopAll: Boolean = false

    var loop: Boolean = false

    var shuffleState: Boolean = false

    fun loopState() {
        loop = true

        loopAll = false
    }

    fun loopAllState() {
        loopAll = true

        loop = false
    }

    fun notLoopState() {
        loop = false
        loopAll = false
    }
}