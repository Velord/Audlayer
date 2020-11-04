package velord.university.repository.db.factory

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //very important upper case and
        //order of property
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Playlist` " +
                    "(`name` TEXT NOT NULL, `songs` TEXT NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))"
        )
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //very important upper case and
        //order of property
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Album` " +
                    "(`name` TEXT NOT NULL, `genre` TEXT, `songs` TEXT NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))"
        )
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //very important upper case and
        //order of property
        //change id type of Album from String to Long
        database.execSQL(
            "CREATE TABLE Album_New (`name` TEXT NOT NULL, `genre` TEXT, `songs` TEXT NOT NULL, `id` LONG NOT NULL, PRIMARY KEY(`id`))"
        )
        database.execSQL(
            "INSERT INTO Album_New (name, genre, songs) SELECT name, genre, songs FROM Album"
        )
        database.execSQL(
            "DROP TABLE Album"
        )
        database.execSQL(
            "ALTER TABLE Album_New RENAME TO Album"
        )
        //change id type of MiniPlayerServiceSong from String to Long
        database.execSQL(
            "CREATE TABLE MiniPlayerServiceSong_New (`path` TEXT NOT NULL, `position` INTEGER NOT NULL, `id` LONG NOT NULL, PRIMARY KEY(`id`))"
        )
        database.execSQL(
            "INSERT INTO MiniPlayerServiceSong_New (path, position) SELECT path, position FROM MiniPlayerServiceSong"
        )
        database.execSQL(
            "DROP TABLE MiniPlayerServiceSong"
        )
        database.execSQL(
            "ALTER TABLE MiniPlayerServiceSong_New RENAME TO MiniPlayerServiceSong"
        )
        //change id type of Playlist from String to Long
        database.execSQL(
            "CREATE TABLE Playlist_New (`name` TEXT NOT NULL, `songs` TEXT NOT NULL, `id` LONG NOT NULL, PRIMARY KEY(`id`))"
        )
        database.execSQL(
            "INSERT INTO Playlist_New (name, songs) SELECT name, songs FROM Playlist"
        )
        database.execSQL(
            "DROP TABLE Playlist"
        )
        database.execSQL(
            "ALTER TABLE Playlist_New RENAME TO Playlist"
        )
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //very important upper case and
        //order of property
        database.execSQL("DROP TABLE VkAlbum")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `VkAlbum` " +
                    "(`title` TEXT NOT NULL, `duration` INTEGER NOT NULL," +
                    "`owner_id` INTEGER NOT NULL, `access_key` TEXT NOT NULL, " +
                    "`width` TEXT NOT NULL, `height` TEXT NOT NULL," +
                    "`photo_34` TEXT NOT NULL, `photo_68` TEXT NOT NULL," +
                    "`photo_135` TEXT NOT NULL,`photo_270` TEXT NOT NULL," +
                    "`photo_300` TEXT NOT NULL,`photo_600` TEXT NOT NULL, " +
                    "`photo_1200` TEXT NOT NULL, `vk_album_id` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`vk_album_id`))"
        )
        database.execSQL("CREATE TABLE IF NOT EXISTS `VkSong` " +
                "(`artist` TEXT NOT NULL, `owner_id` INTEGER NOT NULL," +
                "`title` TEXT NOT NULL," +
                "`access_key` TEXT NOT NULL, `is_licensed` INTEGER NOT NULL," +
                " `is_hq` INTEGER NOT NULL, `track_genre_id` INTEGER NOT NULL," +
                "`vk_song_id` INTEGER NOT NULL, `album_id` INTEGER," +
                "`url` TEXT NOT NULL, `path` TEXT NOT NULL, PRIMARY KEY(`vk_song_id`))"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_VkAlbum_title ON VkAlbum(title)"
        )
        database.execSQL(
            "CREATE INDEX index_VkSong_artist_title_path_album_id ON VkSong(artist, title, path, album_id)"
        )
    }
}

val MIGRATION_5_6: Migration = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //add duration, date and foreign key, change BOOLEAN to INTEGER to VKSong
        //change height, width in VKAlbum
        database.execSQL(
            "CREATE TABLE VkSong_New (`artist` TEXT NOT NULL, `owner_id` INTEGER NOT NULL, `title` TEXT NOT NULL, `duration` INTEGER NOT NULL, `access_key` TEXT NOT NULL, `is_licensed` INTEGER NOT NULL, `date` INTEGER NOT NULL, `is_hq` INTEGER NOT NULL, `track_genre_id` INTEGER NOT NULL, `vk_song_id` INTEGER NOT NULL, `album_id` INTEGER, `url` TEXT NOT NULL, `path` TEXT NOT NULL, PRIMARY KEY(`vk_song_id`), FOREIGN KEY(`album_id`) REFERENCES `VkAlbum`(`vk_album_id`) ON UPDATE NO ACTION ON DELETE NO ACTION)"
        )
        database.execSQL(
            "CREATE TABLE VkAlbum_New (`title` TEXT NOT NULL, `owner_id` INTEGER NOT NULL, `access_key` TEXT NOT NULL,`width` INTEGER NOT NULL, `height` INTEGER NOT NULL,`photo_34` TEXT NOT NULL, `photo_68` TEXT NOT NULL, `photo_135` TEXT NOT NULL,`photo_270` TEXT NOT NULL, `photo_300` TEXT NOT NULL,`photo_600` TEXT NOT NULL, `photo_1200` TEXT NOT NULL, `vk_album_id` INTEGER NOT NULL, PRIMARY KEY(`vk_album_id`))"
        )
        database.execSQL(
            "DROP TABLE VkSong"
        )
        database.execSQL(
            "DROP TABLE VkAlbum"
        )
        database.execSQL(
            "ALTER TABLE VkAlbum_New RENAME TO VkAlbum"
        )
        database.execSQL(
            "ALTER TABLE VkSong_New RENAME TO VkSong"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_VkAlbum_title ON VkAlbum(title)"
        )
        database.execSQL(
            "CREATE INDEX index_VkSong_artist_title_path_album_id ON VkSong(artist, title, path, album_id)"
        )
    }
}

val MIGRATION_6_7: Migration = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //change VkThumb to all nullable column
        database.execSQL(
            "CREATE TABLE VkAlbum_New (`title` TEXT NOT NULL, `owner_id` INTEGER NOT NULL, `access_key` TEXT NOT NULL,`width` INTEGER, `height` INTEGER, `photo_34` TEXT, `photo_68` TEXT, `photo_135` TEXT,`photo_270` TEXT, `photo_300` TEXT,`photo_600` TEXT, `photo_1200` TEXT, `vk_album_id` INTEGER NOT NULL, PRIMARY KEY(`vk_album_id`))"
        )
        database.execSQL(
            "DROP TABLE VkAlbum"
        )
        database.execSQL(
            "ALTER TABLE VkAlbum_New RENAME TO VkAlbum"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_VkAlbum_title ON VkAlbum(title)"
        )
    }
}

val MIGRATION_7_8: Migration = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //change VkSong remove foreign key
        database.execSQL(
            "CREATE TABLE VkSong_New (`artist` TEXT NOT NULL, `owner_id` INTEGER NOT NULL, `title` TEXT NOT NULL, `duration` INTEGER NOT NULL, `access_key` TEXT NOT NULL, `is_licensed` INTEGER NOT NULL, `date` INTEGER NOT NULL, `is_hq` INTEGER NOT NULL, `track_genre_id` INTEGER NOT NULL, `vk_song_id` INTEGER NOT NULL, `album_id` INTEGER, `url` TEXT NOT NULL, `path` TEXT NOT NULL, PRIMARY KEY(`vk_song_id`))"
        )
        database.execSQL(
            "DROP TABLE VkSong"
        )
        database.execSQL(
            "ALTER TABLE VkSong_New RENAME TO VkSong"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_VkSong_artist_title_path_album_id ON VkSong(artist, title, path, album_id)"
        )
    }
}

val MIGRATION_8_9: Migration = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //change VkSong access_key is nullable
        database.execSQL(
            "CREATE TABLE VkSong_New (`artist` TEXT NOT NULL, `owner_id` INTEGER NOT NULL, `title` TEXT NOT NULL, `duration` INTEGER NOT NULL, `access_key` TEXT, `is_licensed` INTEGER NOT NULL, `date` INTEGER NOT NULL, `is_hq` INTEGER NOT NULL, `track_genre_id` INTEGER NOT NULL, `vk_song_id` INTEGER NOT NULL, `album_id` INTEGER, `url` TEXT NOT NULL, `path` TEXT NOT NULL, PRIMARY KEY(`vk_song_id`))"
        )
        database.execSQL(
            "DROP TABLE VkSong"
        )
        database.execSQL(
            "ALTER TABLE VkSong_New RENAME TO VkSong"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_VkSong_artist_title_path_album_id ON VkSong(artist, title, path, album_id)"
        )
    }
}

val MIGRATION_9_10: Migration = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //add radio station entity
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `RadioStation` (`name` TEXT NOT NULL, `url` TEXT NOT NULL, `icon` INTEGER, `id` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_RadioStation_name_url ON RadioStation(name, url)"
        )
    }
}

val MIGRATION_10_11: Migration = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //change radio station icon to string
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `RadioStation_New` (`name` TEXT NOT NULL, `url` TEXT NOT NULL, `icon` TEXT, `id` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
        database.execSQL(
            "DROP TABLE RadioStation"
        )
        database.execSQL(
            "ALTER TABLE RadioStation_New RENAME TO RadioStation"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_RadioStation_name_url ON RadioStation(name, url)"
        )
    }
}

val MIGRATION_11_12: Migration = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //add radio station liked field
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `RadioStation_New` (`name` TEXT NOT NULL, `url` TEXT NOT NULL, `icon` TEXT, `liked` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
        database.execSQL(
            "DROP TABLE RadioStation"
        )
        database.execSQL(
            "ALTER TABLE RadioStation_New RENAME TO RadioStation"
        )
        //create indices
        database.execSQL(
            "CREATE INDEX index_RadioStation_name_url ON RadioStation(name, url)"
        )
    }
}