package velord.university.application.settings

import android.content.Context

class VkPreference(context: Context)  {

    companion object {
        private const val PREF_VK_LOGIN_PAGE_ID = "vkLoginPageId"
        private const val PREF_VK_LOGIN_EMAIL = "vkLoginEmail"
        private const val PREF_VK_LOGIN_ACCESS_TOKEN = "vkLoginAccessToken"
        private const val PREF_VK_LOGIN = "vkLogin"
        private const val PREF_VK_LOGIN_PASSWORD = "vkLoginPassword"
    }

    var pageId: Int by PreferencesDelegate(
        context,
        PREF_VK_LOGIN_PAGE_ID,
        -1
    )

    var email: String by PreferencesDelegate(
        context,
        PREF_VK_LOGIN_EMAIL,
        ""
    )

    var accessToken: String by PreferencesDelegate(
        context,
        PREF_VK_LOGIN_ACCESS_TOKEN,
        ""
    )

    var login: String by PreferencesDelegate(
        context,
        PREF_VK_LOGIN,
        ""
    )

    var password: String by PreferencesDelegate(
        context,
        PREF_VK_LOGIN_PASSWORD,
        ""
    )
}