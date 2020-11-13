package velord.university.repository.db.factory

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import velord.university.model.converter.LocalDateTimeConverter
import velord.university.model.converter.StringListConverter
import velord.university.model.entity.music.song.serviceSong.MiniPlayerServiceSong
import velord.university.model.entity.music.playlist.base.Playlist
import velord.university.model.entity.music.radio.RadioStation
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.newGeneration.song.AudlayerSongDao
import velord.university.model.entity.music.song.serviceSong.MiniPlayerServiceSongDao
import velord.university.model.entity.music.playlist.base.PlaylistDao
import velord.university.model.entity.music.radio.RadioStationDao

@Database(
    entities = [
        MiniPlayerServiceSong::class, Playlist::class,
        RadioStation::class,
        AudlayerSong::class
    ],
    version = 1
)
@TypeConverters(
    StringListConverter::class,
    LocalDateTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serviceDao(): MiniPlayerServiceSongDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun radioDao(): RadioStationDao

    abstract fun songDao(): AudlayerSongDao
}