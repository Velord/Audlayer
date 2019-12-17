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

fun Context.sendBroadcastStopUI(permission: String) =
    sendBroadcast(ACTION_STOP_UI, permission)

fun Context.sendBroadcastPlayUI(permission: String) =
    sendBroadcast(ACTION_PLAY_UI, permission)

fun Context.sendBroadcastLikeUI(permission: String) =
    sendBroadcast(ACTION_LIKE_UI, permission)

fun Context.sendBroadcastUnlikeUI(permission: String) =
    sendBroadcast(ACTION_UNLIKE_UI, permission)

fun Context.sendBroadcastSkipNextUI(permission: String) =
    sendBroadcast(ACTION_SKIP_NEXT_UI, permission)

fun Context.sendBroadcastSkipPrevUI(permission: String) =
    sendBroadcast(ACTION_SKIP_PREV_UI, permission)

fun Context.sendBroadcastRewindUI(permission: String) =
    sendBroadcast(ACTION_REWIND_UI, permission)

fun Context.sendBroadcastShuffleUI(permission: String) =
    sendBroadcast(ACTION_SHUFFLE_UI, permission)

fun Context.sendBroadcastUnShuffleUI(permission: String) =
    sendBroadcast(ACTION_UN_SHUFFLE_UI, permission)

fun Context.sendBroadcastLoopUI(permission: String) =
    sendBroadcast(ACTION_LOOP_UI, permission)

fun Context.sendBroadcastLoopAllUI(permission: String) =
    sendBroadcast(ACTION_LOOP_ALL_UI, permission)

fun Context.sendBroadcastStop(permission: String) =
    sendBroadcast(ACTION_STOP, permission)

fun Context.sendBroadcastPlay(permission: String) =
    sendBroadcast(ACTION_PLAY, permission)

fun Context.sendBroadcastLike(permission: String) =
    sendBroadcast(ACTION_LIKE, permission)

fun Context.sendBroadcastUnlike(permission: String) =
    sendBroadcast(ACTION_UNLIKE, permission)

fun Context.sendBroadcastSkipNext(permission: String) =
    sendBroadcast(ACTION_SKIP_NEXT, permission)

fun Context.sendBroadcastSkipPrev(permission: String) =
    sendBroadcast(ACTION_SKIP_PREV, permission)

fun Context.sendBroadcastRewind(permission: String) =
    sendBroadcast(ACTION_REWIND, permission)

fun Context.sendBroadcastShuffle(permission: String) =
    sendBroadcast(ACTION_SHUFFLE, permission)

fun Context.sendBroadcastUnShuffle(permission: String) =
    sendBroadcast(ACTION_UN_SHUFFLE, permission)

fun Context.sendBroadcastLoop(permission: String) =
    sendBroadcast(ACTION_LOOP, permission)

fun Context.sendBroadcastLoopAll(permission: String) =
    sendBroadcast(ACTION_LOOP_ALL, permission)