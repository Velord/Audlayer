package velord.university.application.settings

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_MINI_PLAYER_SERVICE_CURRENT_POS = "serviceCurrentPos"
private const val PREF_MINI_PLAYER_SERVICE_IS_PLAYING = "serviceIsPlaying"
private const val PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION = "serviceCurrentDuration"

object MiniPlayerServicePreferences {

    fun setMiniPlayerServiceCurrentPos(
        context: Context,
        pos: Int,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_POS
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, pos)
            }

    fun getMiniPlayerServiceCurrentPos(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_POS
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setMiniPlayerServiceCurrentDuration(
        context: Context,
        duration: Int,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, duration)
            }

    fun getMiniPlayerServiceCurrentDuration(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_CURRENT_DURATION
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun setMiniPlayerServiceIsPlaying(
        context: Context,
        isPlaying: Boolean,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_PLAYING
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(key, isPlaying)
            }

    fun getMiniPlayerServiceIsPlaying(
        context: Context,
        key: String = PREF_MINI_PLAYER_SERVICE_IS_PLAYING
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key, false)
}