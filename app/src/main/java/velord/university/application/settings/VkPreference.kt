package velord.university.application.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val PREF_VK_LOGIN_PAGE_ID = "vkLoginPageId"
private const val PREF_VK_LOGIN_EMAIL = "vkLoginEmail"
private const val PREF_VK_LOGIN_ACCESS_TOKEN = "vkLoginAccessToken"

object VkPreference  {

    fun setPageId(
        context: Context,
        id: Int,
        key: String = PREF_VK_LOGIN_PAGE_ID
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putInt(key, id)
            }

    fun getPageId(
        context: Context,
        key: String = PREF_VK_LOGIN_PAGE_ID
    ) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, -1)

    fun getEmail(context: Context,
                 key: String = PREF_VK_LOGIN_EMAIL): String  =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(key, "")!!

    fun setEmail(context: Context,
                 email: String,
                 key: String = PREF_VK_LOGIN_EMAIL) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(key, email)
            }

    fun getAccessToken(context: Context,
                 key: String = PREF_VK_LOGIN_ACCESS_TOKEN): String  =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(key, "")!!

    fun setAccessToken(context: Context,
                       token: String,
                       key: String = PREF_VK_LOGIN_ACCESS_TOKEN) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(key, token)
            }
}