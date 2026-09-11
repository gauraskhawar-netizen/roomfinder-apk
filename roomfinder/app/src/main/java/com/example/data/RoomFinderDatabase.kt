package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RoomListingEntity::class, FavoriteEntity::class, UserEntity::class],
    version = 3,
    exportSchema = false
)
abstract class RoomFinderDatabase : RoomDatabase() {
    abstract fun roomListingDao(): RoomListingDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: RoomFinderDatabase? = null

        fun getDatabase(context: Context): RoomFinderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomFinderDatabase::class.java,
                    "roomfinder_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
