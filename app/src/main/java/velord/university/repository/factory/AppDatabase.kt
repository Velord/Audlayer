package velord.university.repository.factory

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import velord.university.model.converter.StringListConverter
import velord.university.model.entity.Album
import velord.university.model.entity.MiniPlayerServiceSong
import velord.university.model.entity.Playlist
import velord.university.model.entity.RadioStation
import velord.university.model.entity.vk.VkAlbum
import velord.university.model.entity.vk.VkSong
import velord.university.repository.dao.AlbumDao
import velord.university.repository.dao.MiniPlayerServiceSongDao
import velord.university.repository.dao.PlaylistDao
import velord.university.repository.dao.RadioStationDao
import velord.university.repository.dao.vk.VkAlbumDao
import velord.university.repository.dao.vk.VkSongDao

@Database(
    entities = [
        MiniPlayerServiceSong::class, Playlist::class,
        Album::class, VkAlbum::class,
        VkSong::class, RadioStation::class
    ],
    version = 11,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serviceDao(): MiniPlayerServiceSongDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun albumDao(): AlbumDao

    abstract fun vkAlbumDao(): VkAlbumDao

    abstract fun vkSongDao(): VkSongDao

    abstract fun radioDao(): RadioStationDao
}