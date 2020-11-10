package velord.university.repository.db.transaction

import velord.university.model.entity.music.Album
import velord.university.repository.db.dao.AlbumDao
import velord.university.repository.db.dao.PlaylistDao
import velord.university.repository.db.transaction.hub.BaseTransaction
import velord.university.repository.db.transaction.hub.HubTransaction.albumTransaction

object AlbumTransaction : BaseTransaction() {

    override val TAG: String = "AlbumTransaction"

    suspend fun clearThenSave(album: List<Album>) =
        albumTransaction("saveAlbum") {
            nukeTable()
            insertAll(*(album.toTypedArray()))
        }
}