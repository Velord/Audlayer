package velord.university.repository.hub

import android.content.Context
import org.apache.commons.text.similarity.LevenshteinDistance
import velord.university.model.coroutine.onIO
import velord.university.model.entity.music.newGeneration.playlist.CompareAndInsert
import velord.university.model.entity.music.newGeneration.playlist.Playlist
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.vk.fetch.VkSongFetch
import velord.university.model.entity.vk.fetch.VkSongFetch.Companion.mapWithPosition
import velord.university.model.entity.vk.fetch.VkSongFetch.Companion.toVkSong
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.hub.HubTransaction
import velord.university.repository.fetch.VkFetch

object VkRepository : BaseRepository() {

    private suspend fun getPlaylistViaCredential(
        context: Context
    ): Array<VkSongFetch> = onIO { VkFetch.fetchPlaylist(context) }

    suspend fun refreshPlaylistViaCredential(context: Context) {
        //from vk
        val byTokenSongs = getPlaylistViaCredential(context)
        //from db
        val playlist = PlaylistTransaction.getVk()
        //compare with existed and insert
        CompareAndInsert.compareAndInsert(byTokenSongs.mapWithPosition())
        //compare with existed and delete
        compareAndDelete(byTokenSongs, playlist)
    }

    private suspend fun compareAndDelete(byTokenSongs: Array<VkSongFetch>,
                                         fromDbSongs: Array<AudlayerSong>) {
        if (byTokenSongs.isEmpty()) return

        val toDelete = mutableListOf<AudlayerSong>()
        fromDbSongs.forEach { fromDb ->
            //if from web is not exist in db
            if (byTokenSongs.find { it.id == fromDb.id } == null)
                toDelete.add(fromDb)
        }
        //delete
        val toDeleteId = toDelete.map { it.id }
        HubTransaction.songTransaction("compareAndDelete") {
            deleteById(toDeleteId)
        }
    }
}