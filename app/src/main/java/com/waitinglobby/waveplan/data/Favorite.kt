package com.waitinglobby.waveplan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey
    val id: Int = 0,
    val name: String = "",
    val country: String = "",
    val state: String = "",
    val city: String = "",
)