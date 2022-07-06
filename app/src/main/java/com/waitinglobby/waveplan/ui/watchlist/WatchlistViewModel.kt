package com.waitinglobby.waveplan.ui.watchlist

import androidx.lifecycle.*
import com.waitinglobby.waveplan.data.DataSource.beaches
import com.waitinglobby.waveplan.data.DataSource.cities
import com.waitinglobby.waveplan.data.DataSource.countries
import com.waitinglobby.waveplan.data.DataSource.states
import com.waitinglobby.waveplan.data.FavoriteDao
import com.waitinglobby.waveplan.model.Beach
import kotlinx.coroutines.*

/**
 * The [ViewModel] that is attached to the [WatchlistFragment].
 */

class WatchlistViewModel(private val favoriteDao: FavoriteDao) : ViewModel() {

    var datasetOfFavorites: List<Beach> = listOf()
    var favoriteID: Int = 0

    // Creates a list of favorite beaches by referencing the original beaches list and favorite db
    private suspend fun generateFavoriteList(): List<Beach> {
        var allFavorites: MutableList<Int> = favoriteDao.getFavorites()
        var favoriteList: MutableList<Beach> = mutableListOf()
        for (i in allFavorites.indices) {
            favoriteList.add(beaches[allFavorites[i]])
        }
        return favoriteList
    }

    private suspend fun updateFavoritesDataset() {
        withContext(Dispatchers.Main) {
            datasetOfFavorites = generateFavoriteList()
        }
    }

    suspend fun updateDatasetOfFavorites() {
        updateFavoritesDataset()
    }

    private suspend fun updateFavoriteID(positionClicked: Int) {
        favoriteID = favoriteDao.getIdOfFavorite(
            datasetOfFavorites[positionClicked].name,
            countries[datasetOfFavorites[positionClicked].parentCountryID].name,
            states[datasetOfFavorites[positionClicked].parentStateID].name,
            cities[datasetOfFavorites[positionClicked].parentCityID].name,
        )
    }

    suspend fun getIdOfFavorite(positionClicked: Int): Int {
        withContext(Dispatchers.Main) {
            updateFavoriteID(positionClicked)
        }
        return favoriteID
    }

}

class WatchlistViewModelFactory(private val favoriteDao: FavoriteDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WatchlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WatchlistViewModel(favoriteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}