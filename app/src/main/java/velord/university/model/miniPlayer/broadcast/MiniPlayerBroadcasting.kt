package velord.university.model.miniPlayer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

fun Context.unregisterBroadcastReceiver(receiver: BroadcastReceiver) =
    unregisterReceiver(receiver)

fun Context.registerBroadcastReceiver(receiver: BroadcastReceiver,
                                      filter: IntentFilter,
                                      permission: String)  =
    registerReceiver(receiver, filter, permission, null)

fun Context.sendBroadcast(action: String, permission: String) =
    sendBroadcast(Intent(action), permission)

fun Context.sendBroadcast(action: String, permission: String,
                          valueName: String, value: String) {
    val intent = Intent(action)
    intent.putExtra(valueName, value)
    sendBroadcast(intent, permission)
}

fun Context.sendBroadcast(action: String, permission: String,
                          valueName: String, value: Boolean) {
    val intent = Intent(action)
    intent.putExtra(valueName, value)
    sendBroadcast(intent, permission)
}

fun Context.sendBroadcast(action: String, permission: String,
                          valueName: String, value: Int) {
    val intent = Intent(action)
    intent.putExtra(valueName, value)
    sendBroadcast(intent, permission)
}

// to ui
fun Context.sendBroadcastStopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_STOP_UI, permission)

fun Context.sendBroadcastPlayUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_PLAY_UI, permission)

fun Context.sendBroadcastLikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_LIKE_UI, permission)

fun Context.sendBroadcastUnlikeUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_UNLIKE_UI, permission)

fun Context.sendBroadcastSkipNextUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SKIP_NEXT_UI, permission)

fun Context.sendBroadcastSkipPrevUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SKIP_PREV_UI, permission)

fun Context.sendBroadcastShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SHUFFLE_UI, permission)

fun Context.sendBroadcastUnShuffleUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_UN_SHUFFLE_UI, permission)

fun Context.sendBroadcastLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_LOOP_UI, permission)

fun Context.sendBroadcastLoopAllUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_LOOP_ALL_UI, permission)

fun Context.sendBroadcastNotLoopUI(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_NOT_LOOP_UI, permission)

fun Context.sendBroadcastRewindUI(permission: String = PERM_PRIVATE_MINI_PLAYER, duration: Int) =
    sendBroadcast(ACTION_REWIND_UI, permission, PROGRESS_UI , duration)

fun Context.sendBroadcastSongNameUI(name: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SONG_NAME_UI, permission, SONG_NAME_UI , name)

fun Context.sendBroadcastSongArtistUI(artist: String, permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SONG_ARTIST_UI, permission, SONG_ARTIST_UI, artist)

fun Context.sendBroadcastSongHQUI(permission: String = PERM_PRIVATE_MINI_PLAYER, isHQ: Boolean) =
    sendBroadcast(ACTION_SONG_HQ, permission, SONG_HQ_UI, isHQ)

fun Context.sendBroadcastSongDurationUI(permission: String = PERM_PRIVATE_MINI_PLAYER, duration: Int) =
    sendBroadcast(ACTION_SONG_DURATION, permission, SONG_DURATION_UI, duration)

//to service
fun Context.sendBroadcastPlayByPath(permission: String = PERM_PRIVATE_MINI_PLAYER, path: String) =
    sendBroadcast(ACTION_PLAY_BY_PATH, permission, AUDIO_FILE_PATH, path)

fun Context.sendBroadcastRewind(permission: String = PERM_PRIVATE_MINI_PLAYER, duration: Int) =
    sendBroadcast(ACTION_REWIND, permission, PROGRESS, duration)

fun Context.sendBroadcastStop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_STOP, permission)

fun Context.sendBroadcastPlay(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_PLAY, permission)

fun Context.sendBroadcastLike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_LIKE, permission)

fun Context.sendBroadcastUnlike(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_UNLIKE, permission)

fun Context.sendBroadcastSkipNext(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SKIP_NEXT, permission)

fun Context.sendBroadcastSkipPrev(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SKIP_PREV, permission)

fun Context.sendBroadcastShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_SHUFFLE, permission)

fun Context.sendBroadcastUnShuffle(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_UN_SHUFFLE, permission)

fun Context.sendBroadcastLoop(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_LOOP, permission)

fun Context.sendBroadcastLoopAll(permission: String = PERM_PRIVATE_MINI_PLAYER) =
    sendBroadcast(ACTION_LOOP_ALL, permission)