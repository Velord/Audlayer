package velord.university.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import velord.university.model.converter.StringListConverter
import velord.university.model.entity.Album
import velord.university.model.entity.MiniPlayerServiceSong
import velord.university.model.entity.Playlist
import velord.university.repository.dao.AlbumDao
import velord.university.repository.dao.MiniPlayerServiceSongDao
import velord.university.repository.dao.PlaylistDao

@Database(
    entities = [MiniPlayerServiceSong::class, Playlist::class, Album::class],
    version = 3,
    exportSchema = true)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serviceDao(): MiniPlayerServiceSongDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun albumDao(): AlbumDao
}