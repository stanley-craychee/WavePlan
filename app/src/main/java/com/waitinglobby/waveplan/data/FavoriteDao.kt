package com.waitinglobby.waveplan.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("SELECT id FROM favorite ORDER BY id ASC")
    suspend fun getFavorites(): MutableList<Int>

    @Query("SELECT EXISTS (SELECT 1 FROM favorite WHERE id = :id)")
    fun exists(id: Int): Flow<Int>

    @Query("SELECT id FROM favorite where name = :name and country = :country and state = :state and city = :city")
    suspend fun getIdOfFavorite (name: String, country: String, state: String, city: String): Int
}