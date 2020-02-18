package velord.university.model.miniPlayer.broadcast

import android.content.Context
import android.content.IntentFilter


abstract class MiniPlayerBroadcastHub {
    //what can receive service
    open val actionService: String = ""
    //what can receive ui
    open val actionUI: String = ""

    open val filterService: IntentFilter = IntentFilter()

    open  val filterUI: IntentFilter = IntentFilter()

    open val extraValueService: Any = ""

    open val extraValueUI: Any = ""
}


object MiniPlayerBroadcastStop: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.STOP"

    override val actionUI: String = "velord.university.STOP_UI"

    override val filterService: IntentFilter = IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)


    fun Context.sendBroadcastStop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastStopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)

}

object MiniPlayerBroadcastPlay: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.PlAY"

    override val actionUI: String = "velord.university.PlAY_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastPlay(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastPlayUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastLike: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.LIKE"

    override val actionUI: String = "velord.university.LIKE_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastLike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastLikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastUnlike: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.UNLIKE"

    override val actionUI: String = "velord.university.UNLIKE_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastUnlike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastUnlikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastSkipNext: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.SKIP_NEXT"

    override val actionUI: String = "velord.university.SKIP_NEXT_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastSkipNext(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastSkipNextUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastSkipPrev: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.SKIP_PREV"

    override val actionUI: String = "velord.university.SKIP_PREV_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastSkipPrev(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastSkipPrevUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastRewind: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.REWIND"

    override val actionUI: String = "velord.university.REWIND_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    override val extraValueService: String = "PROGRESS"

    override val extraValueUI: String = "PROGRESS_UI"

    fun Context.sendBroadcastRewind(duration: Int, permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission, extraValueService, duration)

    fun Context.sendBroadcastRewindUI(duration: Int, permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission, extraValueUI, duration)
}

object MiniPlayerBroadcastShuffle: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.SHUFFLE"

    override val actionUI: String = "velord.university.SHUFFLE_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastUnShuffle: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.UN_SHUFFLE"

    override val actionUI: String = "velord.university.UN_SHUFFLE_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastUnShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastUnShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastLoop: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.LOOP"

    override val actionUI: String = "velord.university.LOOP_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastLoopAll: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.LOOP_ALL"

    override val actionUI: String = "velord.university.LOOP_ALL_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastLoopAll(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastLoopAllUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastNotLoop: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.NOT_LOOP"

    override val actionUI: String = "velord.university.NOT_LOOP_UI"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    fun Context.sendBroadcastNotLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionService, permission)

    fun Context.sendBroadcastNotLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission)
}

object MiniPlayerBroadcastPlayByPath: MiniPlayerBroadcastHub() {

    override val actionService: String = "velord.university.PLAY_BY_PATH"

    override val filterService: IntentFilter =  IntentFilter(actionService)

    override val extraValueService: String = "AUDIO_FILE_PATH"

    fun Context.sendBroadcastPlayByPath(permission: String = PERM_PRIVATE_MINI_PLAYER, path: String) =
        sendBroadcast(actionService, permission, extraValueService, path)
}

object MiniPlayerBroadcastSongArtist: MiniPlayerBroadcastHub() {

    override val actionUI: String = "velord.university.SONG_ARTIST_UI"

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    override val extraValueUI: String = "SONG_ARTIST_UI"

    fun Context.sendBroadcastSongArtistUI(artist: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission, extraValueUI, artist)
}

object MiniPlayerBroadcastSongName: MiniPlayerBroadcastHub() {

    override val actionUI: String = "velord.university.SONG_NAME_UI"

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    override val extraValueUI: String = "SONG_NAME_UI"

    fun Context.sendBroadcastSongNameUI(name: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission, extraValueUI , name)
}

object MiniPlayerBroadcastSongHQ: MiniPlayerBroadcastHub() {

    override val actionUI: String = "velord.university.SONG_HQ_UI"

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    override val extraValueUI: String = "SONG_HQ_UI"

    fun Context.sendBroadcastSongHQUI(isHQ: Boolean, permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission, extraValueUI, isHQ)
}

object MiniPlayerBroadcastSongDuration: MiniPlayerBroadcastHub() {

    override val actionUI: String = "velord.university.SONG_DURATION_UI"

    override val filterUI: IntentFilter = IntentFilter(actionUI)

    override val extraValueUI: String = "SONG_DURATION_UI"

    fun Context.sendBroadcastSongDurationUI( duration: Int, permission: String = PERM_PRIVATE_MINI_PLAYER) =
        sendBroadcast(actionUI, permission, extraValueUI, duration)
}

