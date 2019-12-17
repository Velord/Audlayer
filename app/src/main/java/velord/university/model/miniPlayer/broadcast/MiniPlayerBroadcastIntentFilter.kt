package velord.university.model.miniPlayer.broadcast

import android.content.IntentFilter

val filterStopService = IntentFilter(ACTION_STOP)
val filterPlayService = IntentFilter(ACTION_PLAY)
val filterLikeService = IntentFilter(ACTION_LIKE)
val filterUnlikeService = IntentFilter(ACTION_UNLIKE)
val filterSkipNextService = IntentFilter(ACTION_SKIP_NEXT)
val filterSkipPrevService = IntentFilter(ACTION_SKIP_PREV)
val filterRewindService = IntentFilter(ACTION_REWIND)
val filterShuffleService = IntentFilter(ACTION_SHUFFLE)
val filterUnShuffleService = IntentFilter(ACTION_UN_SHUFFLE)
val filterLoopService = IntentFilter(ACTION_LOOP)
val filterLoopAllService = IntentFilter(ACTION_LOOP_ALL)

val filterStopUI = IntentFilter(ACTION_STOP_UI)
val filterPlayUI = IntentFilter(ACTION_PLAY_UI)
val filterLikeUI = IntentFilter(ACTION_LIKE_UI)
val filterUnlikeUI = IntentFilter(ACTION_UNLIKE_UI)
val filterSkipNextUI = IntentFilter(ACTION_SKIP_NEXT_UI)
val filterSkipPrevUI = IntentFilter(ACTION_SKIP_PREV_UI)
val filterRewindUI = IntentFilter(ACTION_REWIND_UI)
val filterShuffleUI = IntentFilter(ACTION_SHUFFLE_UI)
val filterUnShuffleUI = IntentFilter(ACTION_UN_SHUFFLE_UI)
val filterLoopUI = IntentFilter(ACTION_LOOP_UI)
val filterLoopAllUI = IntentFilter(ACTION_LOOP_ALL_UI)