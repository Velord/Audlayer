package velord.university.repository

import android.content.Context
import androidx.room.Room

val buildAppDatabase: (Context) -> AppDatabase = {
    Room.databaseBuilder(
        it,
        AppDatabase::class.java, "audlayer-database"
    ).build()
}