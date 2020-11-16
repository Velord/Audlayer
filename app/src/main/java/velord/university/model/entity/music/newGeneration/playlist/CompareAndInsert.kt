package velord.university.model.entity.music.newGeneration.playlist

import org.apache.commons.text.similarity.LevenshteinDistance
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPos
import velord.university.model.entity.vk.fetch.VkSongFetch
import velord.university.model.entity.vk.fetch.VkSongFetch.Companion.toAppSong
import velord.university.repository.db.transaction.hub.HubTransaction
import velord.university.repository.hub.VkRepository
import kotlin.random.Random

object CompareAndInsert {

    suspend fun compareAndInsert(tokenSongs: List<VkSongFetch>) {
        if (tokenSongs.isEmpty()) return
        //all from db
        val allFromDb = HubTransaction.songTransaction("compareAndInsert") {
            getAll()
        }
        //get index list of song not existed in db
        val notExistInDb = getNotExistInDbSong(tokenSongs)
        if (notExistInDb.isEmpty()) return
        //next step is Levenstein
        //get song existed in db and filtered by Levenstein(not existed)
        val (existed, notExisted) = filterByLevenstein(allFromDb, notExistInDb)
        //reassignment to new not existed in db id
        var lastId = allFromDb.last().id
        val notExistedWithNewId = notExisted.map {
            it.id = ++lastId
            it
        }
        //insert to all
        HubTransaction.songTransaction("filterByLevenstein") {
            insertAll(*(notExistedWithNewId.toAppSong().toTypedArray()))
        }
        //map Song with pos
        val toPlaylist: List<SongWithPos> = (existed + notExistedWithNewId).map {
            SongWithPos(it.position, it.id)
        }
        val getLastWitPosId = HubTransaction.songWithPos()
        //insertWithPos
        //insert To Playlist new Song
        HubTransaction.playlistTransaction("insert To Vk") {
            val playlist = getByName("Vk")
            playlist.songs += toPlaylist
            Playlist
        }
    }

    suspend fun filterByLevenstein(
        allFromDb: List<AudlayerSong>,
        byTokenSongs: List<VkSongFetch>
    ) : Pair<List<VkSongFetch>, List<VkSongFetch>> {
        val existed: MutableList<VkSongFetch> = mutableListOf()
        val notExisted: MutableList<VkSongFetch> = mutableListOf()

        byTokenSongs.forEach { vkSong ->
            allFromDb.forEachIndexed { fromDbIndex, songDb ->
                val dist = LevenshteinDistance().apply(
                    vkSong.getFullName(),
                    songDb.getFullName()
                )
                //if not same
                if ((dist in 0..4).not()) notExisted.add(vkSong)
                //if same title and artist
                //need reassignment path to existed
                else {
                    vkSong.id = songDb.id
                    vkSong.path = songDb.path
                    existed.add(vkSong)
                }
            }
        }

        return existed to notExisted
    }

    suspend fun getNotExistInDbSong(
        byTokenSongs: List<VkSongFetch>
    ): List<VkSongFetch> = byTokenSongs.filter { vkSong ->
        HubTransaction.songTransaction("getNoExistInDbSong") {
            val notExist = getByNameArtistNot(vkSong.artist, vkSong.title)
            notExist
        }
    }
}