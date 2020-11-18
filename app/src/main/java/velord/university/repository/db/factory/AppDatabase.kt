package velord.university.repository.db.factory

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import velord.university.model.converter.IntListConverter
import velord.university.model.converter.LocalDateTimeConverter
import velord.university.model.converter.StringListConverter
import velord.university.model.entity.music.playlist.Playlist
import velord.university.model.entity.music.playlist.PlaylistDao
import velord.university.model.entity.music.radio.RadioStation
import velord.university.model.entity.music.song.main.AudlayerSong
import velord.university.model.entity.music.song.main.AudlayerSongDao
import velord.university.model.entity.music.song.withPos.SongWithPos
import velord.university.model.entity.music.song.withPos.SongWithPosDao
import velord.university.model.entity.music.radio.RadioStationDao

@Database(
    entities = [
        RadioStation::class,
        AudlayerSong::class, SongWithPos::class,
        Playlist::class
    ],
    version = 1
)
@TypeConverters(
    StringListConverter::class,
    LocalDateTimeConverter::class,
    IntListConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun radioDao(): RadioStationDao

    abstract fun songDao(): AudlayerSongDao

    abstract fun songWithPosDao(): SongWithPosDao

    abstract fun playlistDao(): PlaylistDao
}