package velord.university.application.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.widget.Toast
import kotlinx.coroutines.*
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState


class MediaButtonEventReceiver: BroadcastReceiver() {

    private val TAG = "MediaButtonEvent"

    override fun onReceive(context: Context?, intent: Intent) {
        val intentAction = intent.action
        Log.d(TAG, "onReceive action: $intentAction")

        //wtf? 6.11.2020
        if (intentAction == AudioManager.ACTION_AUDIO_BECOMING_NOISY) { }

        if (Intent.ACTION_MEDIA_BUTTON == intentAction) {
            val event: KeyEvent = intent
                .getParcelableExtra<Parcelable>(Intent.EXTRA_KEY_EVENT) as KeyEvent
            Log.d(TAG, "onReceive event: $event")

            when (event.keyCode) {
                KeyEvent.KEYCODE_HEADSETHOOK -> {
                    Log.d(TAG, "Keycode: HEADSETHOOK")
                    when(event.action) {
                        ACTION_DOWN ->
                            MediaButtonEventHeadSeatHook.eventHook(context!!)
                        //implement if need
                        ACTION_UP ->
                            Log.d(TAG, "Keycode: HEADSETHOOK ACTION_UP")
                    }
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    Log.d(TAG, "Keycode: VOLUME_UP")
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    Log.d(TAG, "Keycode: VOLUME_DOWN")
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    Log.d(TAG, "Keycode: MEDIA_PLAY")
                }
                else -> return
            }
        }
    }
}

object MediaButtonEventHeadSeatHook {

    private var pressCount = 0

    private var scope =
        CoroutineScope(Job() + Dispatchers.Default)

    fun eventHook(context: Context) {
        //increase
        ++pressCount
        //cancel action
        scope.cancel()
        scope = CoroutineScope(Job() + Dispatchers.Default)

        scope.launch {
            //wait
            delay(300)
            //invoke
            doAction(context)
            pressCount = 0
        }
    }

    private fun doAction(context: Context): Any =
        when(pressCount) {
            1 -> playOrStop(context)
            2 -> skipNext(context)
            3 -> skipPrev(context)
            else -> scope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context,
                            "Unexpected event",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    private fun skipNext(context: Context) {
        val playerState = MiniPlayerRepository.getState(context)
        if (playerState == MiniPlayerLayoutState.GENERAL)
            AppBroadcastHub.run { context.skipNextService() }
    }

    private fun skipPrev(context: Context) {
        val playerState = MiniPlayerRepository.getState(context)
        if (playerState == MiniPlayerLayoutState.GENERAL)
            AppBroadcastHub.run { context.skipPrevService() }
    }

    private fun playOrStop(context: Context) {
        when (MiniPlayerRepository.getState(context)) {
            MiniPlayerLayoutState.GENERAL ->
                AppBroadcastHub.run { context.playOrStopService() }
            MiniPlayerLayoutState.RADIO ->
                AppBroadcastHub.run { context.playOrStopRadioService() }
        }
    }
}