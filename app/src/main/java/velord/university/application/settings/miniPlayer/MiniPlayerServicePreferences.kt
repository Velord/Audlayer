package velord.university.application.settings.miniPlayer

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_MINI_PLAYER_SERVICE_CURRENT_POS = "serviceCurrentPos"
private const val PREF_MINI_PLAYER_SERVICE_IS_PLAYING = "serviceIsPlaying"
private const val PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION = "serviceCurrentDuration"
private const val PREF_MINI_PLAYER_SERVICE_IS_SHUFFLE = "serviceIsShuffle"
private const val PREF_MINI_PLAYER_SERVICE_IS_LOOP = "serviceIsLoop"

object MiniPlayerServicePreferences {

    fun setCurrentPos(
        context: Context,
        pos: Int,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_POS
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getCurrentPos(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_POS
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setCurrentDuration(
        context: Context,
        duration: Int,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, duration)
            }

    fun getCurrentDuration(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setIsPlaying(
        context: Context,
        isPlaying: Boolean,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_PLAYING
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(key, isPlaying)
            }

    fun getIsPlaying(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_PLAYING
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key, false)

    fun setIsShuffle(
        context: Context,
        isShuffle: Boolean,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_SHUFFLE
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(key, isShuffle)
            }

    fun getIsShuffle(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_SHUFFLE
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key, false)

    fun setLoopState(
        context: Context,
        loopState: Int,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_LOOP
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, loopState)
            }

    fun getLoopState(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_LOOP
    ) = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)
}