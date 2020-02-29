package velord.university.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import velord.university.model.MiniPlayerServiceSong
import velord.university.model.MiniPlayerServiceSongDao

@Database(
    entities = arrayOf(MiniPlayerServiceSong::class),
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): MiniPlayerServiceSongDao
}