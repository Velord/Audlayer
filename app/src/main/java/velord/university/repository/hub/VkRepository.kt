package velord.university.repository.hub

import android.content.Context
import org.apache.commons.text.similarity.LevenshteinDistance
import velord.university.model.coroutine.onIO
import velord.university.model.entity.fileType.file.FileNameParser
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPos
import velord.university.model.entity.vk.fetch.VkSongFetch
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.hub.HubTransaction
import velord.university.repository.fetch.VkFetch
import java.io.File

object VkRepository : BaseRepository() {

    private suspend fun getPlaylistViaCredential(
        context: Context
    ): Array<VkSongFetch> = onIO { VkFetch.fetchPlaylist(context) }

    suspend fun refreshPlaylistViaCredential(context: Context) {
        //from vk
        val byTokenSongs = getPlaylistViaCredential(context)
        //from db
        val fromDbPlaylist = PlaylistTransaction.getVk()
        //compare with existed and insert
        compareAndInsert(byTokenSongs, fromDbSongs)
        //compare with existed and delete
        compareAndDelete(byTokenSongs, fromDbSongs)
    }

    private suspend fun compareAndInsert(byTokenSongs: Array<AudlayerSong>,
                                         fromDbSongs: Array<AudlayerSong>) {
        if (byTokenSongs.isEmpty()) return
        //song
        val notExistSong = getNoExistInDbSong(byTokenSongs, fromDbSongs)
            .toTypedArray()
        //insert
        HubTransaction.songTransaction("compareAndInsert") {
            insertAll(*notExistSong)
        }
    }

    private fun getExistedPath(
        allPathList: Array<String>,
        allFullNameList: Array<String>,
        song: AudlayerSong
    ): String? {
        val name = song.getFullName()

        val index = allFullNameList.indexOf(name)
        if (index == -1) {
            allFullNameList.forEachIndexed { fromDbIndex, vkName ->
                val dist = LevenshteinDistance().apply(name, vkName)
                if (dist in 0..4) return allPathList[fromDbIndex]
            }
            return null
        }
        return allPathList[index]
    }

    private suspend fun getNoExistInDbSong(
        byTokenSongs: Array<AudlayerSong>,
        fromDbSongs: Array<AudlayerSong>,
    ): List<AudlayerSong> {
        val fromDbSongsFullName = fromDbSongs.map { it.getFullName() }

        val fromDbSongsPath = getAllPathFromDbBasedOnPlaylist()
        val allFullNameList = fromDbSongsPath.map { File(it) }
            .map {
                val artist = FileNameParser.getSongArtist(it)
                val title = FileNameParser.getSongTitle(it)

                "$artist$title"
            }
            .toTypedArray()


        return byTokenSongs.fold(
            mutableListOf<AudlayerSong>()
        ) { notExist, byToken ->
            if (fromDbSongsFullName.contains(byToken.getFullName()).not())
                notExist.add(byToken)
            notExist
        }.map {
            //levenstein
            val existedPath = getExistedPath(fromDbSongsPath, allFullNameList, it)
            if (existedPath != null) {
                it.getWithNewPath(existedPath)
            }
            else it
        }
    }

    private suspend fun compareAndDelete(byTokenSongs: Array<AudlayerSong>,
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