package velord.university.application

import android.app.Application
import kotlinx.coroutines.*
import velord.university.model.entity.Playlist
import velord.university.repository.AppDatabase
import velord.university.repository.buildAppDatabase

//default playlist is Favourite, Played

class AudlayerApp : Application() {

    private val scope: CoroutineScope =
        CoroutineScope(Job() + Dispatchers.Default)

    companion object {
        var db: AppDatabase? = null

        suspend fun checkDbTableColumn() = withContext(Dispatchers.IO) {
            db?.apply {
                val playlist = playlistDao().getAll()

                var favouriteExist = false
                var playedSongExist = false

                playlist.forEach {
                    if (it.name == "Favourite")
                        favouriteExist = true
                    if (it.name == "Played")
                        playedSongExist = true
                }

                if (favouriteExist.not())
                    playlistDao().insertAll(Playlist("Favourite", listOf()))

                if (playedSongExist.not())
                    playlistDao().insertAll(Playlist("Played", listOf()))
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        //init db and create tables if not exist
        db = buildAppDatabase(this)
        scope.launch {
            checkDbTableColumn()
        }
    }
}