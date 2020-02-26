package velord.university.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = arrayOf(MiniPlayerServiceSong::class),
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): MiniPlayerServiceSongDao
}