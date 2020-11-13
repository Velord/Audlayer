package velord.university.repository.db.factory

import android.content.Context
import androidx.room.Room

val buildAppDatabase: (Context) -> AppDatabase = {
    Room.databaseBuilder(
        it,
        AppDatabase::class.java, "audlayer-database")
        .addMigrations(
//            MIGRATION_1_2,
//            MIGRATION_2_3,
//            MIGRATION_3_4,
//            MIGRATION_4_5,
//            MIGRATION_5_6,
//            MIGRATION_6_7,
//            MIGRATION_7_8,
//            MIGRATION_8_9,
//            MIGRATION_9_10,
//            MIGRATION_10_11,
//            MIGRATION_11_12
        )
        .fallbackToDestructiveMigration()
        .build()
}