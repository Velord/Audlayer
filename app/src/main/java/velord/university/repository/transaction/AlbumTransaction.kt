package velord.university.repository.transaction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import velord.university.application.AudlayerApp
import velord.university.model.entity.Album

object AlbumDb {

    suspend fun saveAlbum(album: List<Album>) = withContext(Dispatchers.IO) {
        AudlayerApp.db?.apply {
            albumDao().nukeTable()
            albumDao().insertAll(*(album.toTypedArray()))
        }
    }

    suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        AudlayerApp.db?.run {
            albumDao().getAll()
        } ?: listOf()
    }
}