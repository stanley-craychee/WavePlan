package com.waitinglobby.waveplan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favorite::class], version = 6, exportSchema = false)
abstract class FavoriteRoomDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    // add .allowMainThreadQueries() after "favorite_database") in order to allow main thread queries
    // but be aware this makes queries, including those which are livedata, all run on main thread
    // which is a terrible idea. Only do this for testing / debugging.
    companion object {
        @Volatile
        private var INSTANCE: FavoriteRoomDatabase? = null
        fun getDatabase(context: Context): FavoriteRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteRoomDatabase::class.java,
                    "favorite_database"
                )
//                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}