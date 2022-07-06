package com.waitinglobby.waveplan.di

import android.app.Application
import com.waitinglobby.waveplan.data.FavoriteRoomDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    val database: FavoriteRoomDatabase by lazy { FavoriteRoomDatabase.getDatabase(this) }
}