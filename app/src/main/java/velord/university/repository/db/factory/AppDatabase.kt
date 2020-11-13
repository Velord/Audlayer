package velord.university.repository.db.factory

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import velord.university.model.converter.StringListConverter
import velord.university.model.entity.music.song.MiniPlayerServiceSong
import velord.university.model.entity.music.playlist.Playlist
import velord.university.model.entity.music.RadioStation
import velord.university.model.entity.vk.entity.VkSong
import velord.university.repository.db.dao.MiniPlayerServiceSongDao
import velord.university.repository.db.dao.PlaylistDao
import velord.university.repository.db.dao.RadioStationDao
import velord.university.repository.db.dao.vk.VkSongDao

@Database(
    entities = [
        MiniPlayerServiceSong::class, Playlist::class,
        VkSong::class, RadioStation::class
    ],
    version = 1
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serviceDao(): MiniPlayerServiceSongDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun vkSongDao(): VkSongDao

    abstract fun radioDao(): RadioStationDao
}