package velord.university.application

import android.app.Application
import velord.university.repository.AppDatabase
import velord.university.repository.buildAppDatabase

class AudlayerApp : Application() {

    companion object {
        var db: AppDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()

        db = buildAppDatabase(this)
    }

}