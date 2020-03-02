package velord.university.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import velord.university.model.converter.StringListConverter
import velord.university.model.entity.MiniPlayerServiceSong
import velord.university.model.entity.MiniPlayerServiceSongDao
import velord.university.model.entity.Playlist
import velord.university.model.entity.PlaylistDao

@Database(
    entities = arrayOf(MiniPlayerServiceSong::class, Playlist::class),
    version = 2,
    exportSchema = true)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serviceDao(): MiniPlayerServiceSongDao

    abstract fun playlistDao(): PlaylistDao
}