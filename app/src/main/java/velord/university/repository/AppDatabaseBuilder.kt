package velord.university.repository

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val buildAppDatabase: (Context) -> AppDatabase = {
    Room.databaseBuilder(
        it,
        AppDatabase::class.java, "audlayer-database")
        .addMigrations(MIGRATION_1_2)
        .build()
}

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