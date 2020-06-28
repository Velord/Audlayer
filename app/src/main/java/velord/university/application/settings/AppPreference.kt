package velord.university.application.settings

import android.content.Context

class AppPreference(context: Context) {

    companion object {
        private const val AUDLAYER_APP_DESTROYED = "appIsDestroyed"
    }

    var appIsDestroyed: Boolean by PreferencesDelegate(
        context,
        AUDLAYER_APP_DESTROYED,
        false
    )
}