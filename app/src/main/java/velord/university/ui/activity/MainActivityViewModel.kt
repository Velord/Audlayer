package velord.university.ui.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import velord.university.application.settings.AppPreference

class MainActivityViewModel(private val app: Application) : AndroidViewModel(app) {

    init {
        AppPreference.setAppIsDestroyed(app, true)
    }
}