package velord.university.repository.db.factory

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import velord.university.model.converter.IntListConverter
import velord.university.model.converter.LocalDateTimeConverter
import velord.university.model.converter.StringListConverter
import velord.university.model.entity.music.newGeneration.playlist.Playlist
import velord.university.model.entity.music.newGeneration.playlist.PlaylistDao
import velord.university.model.entity.music.song.serviceSong.MiniPlayerServiceSong
import velord.university.model.entity.music.radio.RadioStation
import velord.university.model.entity.music.newGeneration.song.AudlayerSong
import velord.university.model.entity.music.newGeneration.song.AudlayerSongDao
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPos
import velord.university.model.entity.music.newGeneration.song.withPos.SongWithPosDao
import velord.university.model.entity.music.song.serviceSong.MiniPlayerServiceSongDao
import velord.university.model.entity.music.radio.RadioStationDao

@Database(
    entities = [
        MiniPlayerServiceSong::class, RadioStation::class,
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

    abstract fun serviceDao(): MiniPlayerServiceSongDao

    abstract fun radioDao(): RadioStationDao

    abstract fun songDao(): AudlayerSongDao

    abstract fun songWithPosDao(): SongWithPosDao

    abstract fun playlistDao(): PlaylistDao
}