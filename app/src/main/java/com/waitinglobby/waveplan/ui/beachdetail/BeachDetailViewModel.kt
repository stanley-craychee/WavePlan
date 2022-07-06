package com.waitinglobby.waveplan.ui.beachdetail

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.waitinglobby.waveplan.data.DataSource.beaches
import com.waitinglobby.waveplan.data.DataSource.cities
import com.waitinglobby.waveplan.data.DataSource.countries
import com.waitinglobby.waveplan.data.DataSource.states
import com.waitinglobby.waveplan.data.Favorite
import com.waitinglobby.waveplan.data.FavoriteDao
import com.waitinglobby.waveplan.model.Beach
import com.waitinglobby.waveplan.network.BeachDetailsApi
import com.waitinglobby.waveplan.network.GetBeachDetails
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * The [ViewModel] that is attached to the [BeachDetailFragment].
 */

class BeachDetailViewModel(private val favoriteDao: FavoriteDao) : ViewModel() {

    // The internal + external LiveData that stores the status of the most recent API request
    private val _apiResponseStatus = MutableLiveData<String>()
    val apiResponseStatus: LiveData<String> = _apiResponseStatus

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> = _loading

    // Object to hold the API response data for the selected beach
    private val _beachDetails = MutableLiveData<GetBeachDetails?>()
    val beachDetails: LiveData<GetBeachDetails?> = _beachDetails

    // Variables to hold converted data to Imperial measurement system for the current day in UI
    var windSpeed = MutableLiveData<Double?>()
    var airTemp = MutableLiveData<Double?>()
    var waterTemp = MutableLiveData<Double?>()
    var waveHeight = MutableLiveData<String?>()
    var visibility = MutableLiveData<Double?>()
    var currentSpeed = MutableLiveData<Double?>()
    var primarySwellHeight = MutableLiveData<String?>()
    var secondarySwellHeight = MutableLiveData<String?>()
    var windSwellHeight = MutableLiveData<String?>()

    // Dates to place in each of the forecast tiles in the UI
    var forecastDates: MutableLiveData<MutableList<String>>

    // Wave height for each day to place in each of the forecast tiles in the UI
    var forecastWaveHeights: MutableLiveData<MutableList<String>>

    // Wind speed for each day to place in each of the forecast tiles in the UI
    var forecastWindSpeeds: MutableLiveData<MutableList<Double>>

    init {
        forecastDates = MutableLiveData<MutableList<String>>()
        forecastDates.value = getDatesForUI()
        forecastWaveHeights = MutableLiveData<MutableList<String>>()
        forecastWaveHeights.value = mutableListOf()
        forecastWindSpeeds = MutableLiveData<MutableList<Double>>()
        forecastWindSpeeds.value = mutableListOf()
    }

    private fun loadingStatus(): LiveData<String> {
        return apiResponseStatus
    }

    fun loading(): LiveData<String> {
        return loadingStatus()
    }

    private fun getFavoriteEntry(beachSelection: Int): Favorite {
        return Favorite(
            id = beachSelection,
            name = beaches[beachSelection].name,
            country = countries[beaches[beachSelection].parentCountryID].name,
            state = states[beaches[beachSelection].parentStateID].name,
            city = cities[beaches[beachSelection].parentCityID].name
        )
    }

    private fun insertFavorite(favorite: Favorite) {
        viewModelScope.launch {
            favoriteDao.insert(favorite)
        }
    }

    fun addNewFavorite(beachSelection: Int) {
        val newFavorite = getFavoriteEntry(beachSelection)
        insertFavorite(newFavorite)
    }

    private fun deleteFavorite(favorite: Favorite) {
        viewModelScope.launch {
            favoriteDao.delete(favorite)
        }
    }

    fun removeFavorite(beachSelection: Int) {
        val existingFavorite = getFavoriteEntry(beachSelection)
        deleteFavorite(existingFavorite)
    }

    private fun isFavorite(beachSelection: Int): LiveData<Int> {
        return favoriteDao.exists(beachSelection).asLiveData()
    }

    fun isBeachFavorite(beachSelection: Int): LiveData<Int> {
        return isFavorite(beachSelection)
    }

    /**
     * Calls the [getBeachDetails] method for API call using a beach passed in from [BeachDetailFragment]
     */
    suspend fun callBeachDetailsAPI(beachSelection: Beach) {
        Log.d(TAG, "VIEWMODEL: Calling API. beachSelection is ${beachSelection.name}")
        val params =
            "waveHeight,wavePeriod,waveDirection,airTemperature,swellDirection,swellHeight" +
                    ",swellPeriod,secondarySwellDirection,secondarySwellHeight," +
                    "secondarySwellPeriod,waterTemperature,cloudCover,windDirection,windSpeed" +
                    ",currentDirection,currentSpeed,humidity,visibility,windWaveHeight," +
                    "windWavePeriod,windWaveDirection"
        val weatherDataSource = "sg"
        val forecastRangeStart: String = getStartDateTime()
        val forecastRangeEnd: String = getEndDateTime()
        viewModelScope.launch {
            getBeachDetails(
                beachSelection,
                params,
                forecastRangeStart,
                forecastRangeEnd,
                weatherDataSource
            )
        }
    }

    /**
     * Gets Beach Details from the Beach Details API Retrofit service and updates the
     * [BeachDetails] [LiveData].
     */
    private suspend fun getBeachDetails(
        beachSelection: Beach,
        params: String,
        forecastRangeStart: String,
        forecastRangeEnd: String,
        weatherDataSource: String
    ) {
        _apiResponseStatus.value = "LOADING..."
        _loading.value = true
        Log.d(TAG, "VIEWMODEL: Calling API. api response status is ${apiResponseStatus.value}")
        Log.d(TAG, "VIEWMODEL: Calling API. loading is ${loading.value}")
        try {
            _beachDetails.value = BeachDetailsApi.retrofitService.getBeachDetails(
                beachSelection.lat,
                beachSelection.lng,
                params,
                forecastRangeStart,
                forecastRangeEnd,
                weatherDataSource
            )
            _apiResponseStatus.value = "DONE"
            _loading.value = false
            Log.d(TAG, "VIEWMODEL: Calling API. api response status is ${apiResponseStatus.value}")
            Log.d(TAG, "VIEWMODEL: Calling API. loading is ${loading.value}")
            Log.d(
                TAG,
                "API: API was called and now beachDetails' waveHeight is ${_beachDetails.value!!.hours[0].waveHeight?.sg}"
            )
            //Converting response data from metric units to imperial
            _beachDetails.value!!.hours[0].waveHeight?.sg?.let { convertWaveHeight(it) }
            _beachDetails.value!!.hours[0].airTemperature?.sg?.let { convertAirTemp(it) }
            _beachDetails.value!!.hours[0].waterTemperature?.sg?.let { convertWaterTemp(it) }
            _beachDetails.value!!.hours[0].windSpeed?.sg?.let { convertWindSpeed(it) }
            _beachDetails.value!!.hours[0].visibility?.sg?.let { convertVisibility(it) }
            _beachDetails.value!!.hours[0].currentSpeed?.sg?.let { convertCurrentSpeed(it) }
            _beachDetails.value!!.hours[0].swellHeight?.sg?.let { convertPrimarySwellHeight(it) }
            _beachDetails.value!!.hours[0].secondarySwellHeight?.sg?.let {
                convertSecondarySwellHeight(
                    it
                )
            }
            _beachDetails.value!!.hours[0].windWaveHeight?.sg?.let { convertWindSwellHeight(it) }
            convertForecastWaveHeights(_beachDetails.value!!)
            convertForecastWindSpeeds(_beachDetails.value!!)
        } catch (e: Exception) {
            _apiResponseStatus.value =
                "ERROR"
            _loading.value = false
            _beachDetails.value = null
            Log.d(TAG, "VIEWMODEL: Calling API. api response status is ${apiResponseStatus.value}")
            Log.d(TAG, "VIEWMODEL: Calling API. loading is ${loading.value}")
        }
    }

    private fun getStartDateTime(): String {
        val formatter = SimpleDateFormat("yyyy-MM-ddHH:mm", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.time)
    }

    private fun getEndDateTime(): String {
        val formatter = SimpleDateFormat("yyyy-MM-ddHH:mm", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 8)
        return formatter.format(calendar.time)
    }

    private fun getDatesForUI(): MutableList<String> {
        var listOfDates = mutableListOf<String>()
        val formatter = SimpleDateFormat("EEE, MM/dd", Locale.getDefault())
        var calendar = Calendar.getInstance()
        var i = 1
        while (i < 8) {
            calendar.add(Calendar.DATE, 1)
            listOfDates.add(formatter.format(calendar.time))
            i++
        }
        return listOfDates
    }

    /**
     * Conversion functions below
     */
    private fun convertAirTemp(unconvertedAirTemp: Double) {
        airTemp.value = (unconvertedAirTemp * 1.8) + 32
    }

    private fun convertWaterTemp(unconvertedWaterTemp: Double) {
        waterTemp.value = (unconvertedWaterTemp * 1.8) + 32
    }

    private fun convertVisibility(unconvertedVisibility: Double) {
        visibility.value = unconvertedVisibility * 0.6214
    }

    private fun convertCurrentSpeed(unconvertedCurrentSpeed: Double) {
        currentSpeed.value = unconvertedCurrentSpeed * 3.281
    }

    private fun convertWindSpeed(unconvertedWindSpeed: Double) {
        windSpeed.value = unconvertedWindSpeed * 1.944
    }

    private fun convertForecastWindSpeeds(beachObject: GetBeachDetails) {
        var forecastWindSpeedsList: MutableList<Double> = mutableListOf()
        forecastWindSpeeds.value = forecastWindSpeedsList
        var i = 0
        while (i < 179) {
            i++
            forecastWindSpeedsList.add((beachObject.hours[i].windSpeed?.sg!! * 1.944))
        }
    }

    private fun convertWaveHeight(unconvertedWaveHeight: Double) {
        val converted = (unconvertedWaveHeight * 3.28084)
        if (0 <= converted && converted < 1) {
            waveHeight.value = "0 - 1ft"
        } else if (1 <= converted && converted < 2) {
            waveHeight.value = "1 - 2ft"
        } else if (2 <= converted && converted < 3) {
            waveHeight.value = "2 - 3ft"
        } else if (3 <= converted && converted < 4) {
            waveHeight.value = "3 - 4ft"
        } else if (4 <= converted && converted < 5) {
            waveHeight.value = "4 - 5ft"
        } else if (5 <= converted && converted < 6) {
            waveHeight.value = "5 - 6ft"
        } else if (6 <= converted && converted < 7) {
            waveHeight.value = "5 - 7ft"
        } else if (7 <= converted && converted < 8) {
            waveHeight.value = "6 - 8ft"
        } else if (8 <= converted && converted < 10) {
            waveHeight.value = "8 - 10ft"
        } else if (10 <= converted && converted < 12) {
            waveHeight.value = "10 - 12ft"
        } else if (12 <= converted && converted < 15) {
            waveHeight.value = "12 - 15ft"
        } else if (15 <= converted && converted < 20) {
            waveHeight.value = "15 - 20ft"
        } else if (20 <= converted && converted < 25) {
            waveHeight.value = "20 - 24ft+"
        } else if (25 <= converted && converted < 30) {
            waveHeight.value = "25 - 28ft+"
        } else if (30 <= converted && converted < 40) {
            waveHeight.value = "30 - 40ft"
        } else if (40 <= converted && converted < 50) {
            waveHeight.value = "40 - 50ft"
        } else if (50 <= converted && converted < 60) {
            waveHeight.value = "50 - 60ft"
        } else if (60 <= converted && converted < 70) {
            waveHeight.value = "60 - 70ft"
        } else if (70 <= converted && converted < 80) {
            waveHeight.value = "70 - 80ft"
        } else if (80 <= converted && converted < 90) {
            waveHeight.value = "80 - 90ft"
        } else if (90 <= converted && converted < 99) {
            waveHeight.value = "90 - 100ft"
        } else if (100 <= converted) {
            waveHeight.value = "100ft+"
        } else
            waveHeight.value = "ERROR"
    }

    private fun convertPrimarySwellHeight(unconvertedSwellHeight: Double) {
        val converted = (unconvertedSwellHeight * 3.28084)
        if (0 <= converted && converted < 1) {
            primarySwellHeight.value = "0 - 1ft"
        } else if (1 <= converted && converted < 2) {
            primarySwellHeight.value = "1 - 2ft"
        } else if (2 <= converted && converted < 3) {
            primarySwellHeight.value = "2 - 3ft"
        } else if (3 <= converted && converted < 4) {
            primarySwellHeight.value = "3 - 4ft"
        } else if (4 <= converted && converted < 5) {
            primarySwellHeight.value = "4 - 5ft"
        } else if (5 <= converted && converted < 6) {
            primarySwellHeight.value = "5 - 6ft"
        } else if (6 <= converted && converted < 7) {
            primarySwellHeight.value = "5 - 7ft"
        } else if (7 <= converted && converted < 8) {
            primarySwellHeight.value = "6 - 8ft"
        } else if (8 <= converted && converted < 10) {
            primarySwellHeight.value = "8 - 10ft"
        } else if (10 <= converted && converted < 12) {
            primarySwellHeight.value = "10 - 12ft"
        } else if (12 <= converted && converted < 15) {
            primarySwellHeight.value = "12 - 15ft"
        } else if (15 <= converted && converted < 20) {
            primarySwellHeight.value = "15 - 20ft"
        } else if (20 <= converted && converted < 25) {
            primarySwellHeight.value = "20 - 24ft+"
        } else if (25 <= converted && converted < 30) {
            primarySwellHeight.value = "25 - 28ft+"
        } else if (30 <= converted && converted < 40) {
            primarySwellHeight.value = "30 - 40ft"
        } else if (40 <= converted && converted < 50) {
            primarySwellHeight.value = "40 - 50ft"
        } else if (50 <= converted && converted < 60) {
            primarySwellHeight.value = "50 - 60ft"
        } else if (60 <= converted && converted < 70) {
            primarySwellHeight.value = "60 - 70ft"
        } else if (70 <= converted && converted < 80) {
            primarySwellHeight.value = "70 - 80ft"
        } else if (80 <= converted && converted < 90) {
            primarySwellHeight.value = "80 - 90ft"
        } else if (90 <= converted && converted < 99) {
            primarySwellHeight.value = "90 - 100ft"
        } else if (100 <= converted) {
            primarySwellHeight.value = "100ft+"
        } else
            primarySwellHeight.value = "ERROR"
    }

    private fun convertSecondarySwellHeight(unconvertedSwellHeight: Double) {
        val converted = (unconvertedSwellHeight * 3.28084)
        if (0 <= converted && converted < 1) {
            secondarySwellHeight.value = "0 - 1ft"
        } else if (1 <= converted && converted < 2) {
            secondarySwellHeight.value = "1 - 2ft"
        } else if (2 <= converted && converted < 3) {
            secondarySwellHeight.value = "2 - 3ft"
        } else if (3 <= converted && converted < 4) {
            secondarySwellHeight.value = "3 - 4ft"
        } else if (4 <= converted && converted < 5) {
            secondarySwellHeight.value = "4 - 5ft"
        } else if (5 <= converted && converted < 6) {
            secondarySwellHeight.value = "5 - 6ft"
        } else if (6 <= converted && converted < 7) {
            secondarySwellHeight.value = "5 - 7ft"
        } else if (7 <= converted && converted < 8) {
            secondarySwellHeight.value = "6 - 8ft"
        } else if (8 <= converted && converted < 10) {
            secondarySwellHeight.value = "8 - 10ft"
        } else if (10 <= converted && converted < 12) {
            secondarySwellHeight.value = "10 - 12ft"
        } else if (12 <= converted && converted < 15) {
            secondarySwellHeight.value = "12 - 15ft"
        } else if (15 <= converted && converted < 20) {
            secondarySwellHeight.value = "15 - 20ft"
        } else if (20 <= converted && converted < 25) {
            secondarySwellHeight.value = "20 - 24ft+"
        } else if (25 <= converted && converted < 30) {
            secondarySwellHeight.value = "25 - 28ft+"
        } else if (30 <= converted && converted < 40) {
            secondarySwellHeight.value = "30 - 40ft"
        } else if (40 <= converted && converted < 50) {
            secondarySwellHeight.value = "40 - 50ft"
        } else if (50 <= converted && converted < 60) {
            secondarySwellHeight.value = "50 - 60ft"
        } else if (60 <= converted && converted < 70) {
            secondarySwellHeight.value = "60 - 70ft"
        } else if (70 <= converted && converted < 80) {
            secondarySwellHeight.value = "70 - 80ft"
        } else if (80 <= converted && converted < 90) {
            secondarySwellHeight.value = "80 - 90ft"
        } else if (90 <= converted && converted < 99) {
            secondarySwellHeight.value = "90 - 100ft"
        } else if (100 <= converted) {
            secondarySwellHeight.value = "100ft+"
        } else
            secondarySwellHeight.value = "ERROR"
    }

    private fun convertWindSwellHeight(unconvertedSwellHeight: Double) {
        val converted = (unconvertedSwellHeight * 3.28084)
        if (0 <= converted && converted < 1) {
            windSwellHeight.value = "0 - 1ft"
        } else if (1 <= converted && converted < 2) {
            windSwellHeight.value = "1 - 2ft"
        } else if (2 <= converted && converted < 3) {
            windSwellHeight.value = "2 - 3ft"
        } else if (3 <= converted && converted < 4) {
            windSwellHeight.value = "3 - 4ft"
        } else if (4 <= converted && converted < 5) {
            windSwellHeight.value = "4 - 5ft"
        } else if (5 <= converted && converted < 6) {
            windSwellHeight.value = "5 - 6ft"
        } else if (6 <= converted && converted < 7) {
            windSwellHeight.value = "5 - 7ft"
        } else if (7 <= converted && converted < 8) {
            windSwellHeight.value = "6 - 8ft"
        } else if (8 <= converted && converted < 10) {
            windSwellHeight.value = "8 - 10ft"
        } else if (10 <= converted && converted < 12) {
            windSwellHeight.value = "10 - 12ft"
        } else if (12 <= converted && converted < 15) {
            windSwellHeight.value = "12 - 15ft"
        } else if (15 <= converted && converted < 20) {
            windSwellHeight.value = "15 - 20ft"
        } else if (20 <= converted && converted < 25) {
            windSwellHeight.value = "20 - 24ft+"
        } else if (25 <= converted && converted < 30) {
            windSwellHeight.value = "25 - 28ft+"
        } else if (30 <= converted && converted < 40) {
            windSwellHeight.value = "30 - 40ft"
        } else if (40 <= converted && converted < 50) {
            windSwellHeight.value = "40 - 50ft"
        } else if (50 <= converted && converted < 60) {
            windSwellHeight.value = "50 - 60ft"
        } else if (60 <= converted && converted < 70) {
            windSwellHeight.value = "60 - 70ft"
        } else if (70 <= converted && converted < 80) {
            windSwellHeight.value = "70 - 80ft"
        } else if (80 <= converted && converted < 90) {
            windSwellHeight.value = "80 - 90ft"
        } else if (90 <= converted && converted < 99) {
            windSwellHeight.value = "90 - 100ft"
        } else if (100 <= converted) {
            windSwellHeight.value = "100ft+"
        } else
            windSwellHeight.value = "ERROR"
    }

    private fun convertForecastWaveHeights(beachObject: GetBeachDetails) {
        Log.d(
            TAG,
            "VIEWMODEL: Converting forecast wave heights. First waveHeight value in meters is ${beachObject.hours[0].waveHeight?.sg}"
        )
        var forecastWaveHeightsList: MutableList<String> = mutableListOf()
        forecastWaveHeights.value = forecastWaveHeightsList
        var i = 0
        while (i < 179) {
            i++
            if (0 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 1) {
                forecastWaveHeightsList.add("0 - 1ft")
            } else if (1 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 2) {
                forecastWaveHeightsList.add("1 - 2ft")
            } else if (2 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 3) {
                forecastWaveHeightsList.add("2 - 3ft")
            } else if (3 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 4) {
                forecastWaveHeightsList.add("3 - 4ft")
            } else if (4 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 5) {
                forecastWaveHeightsList.add("4 - 5ft")
            } else if (5 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 6) {
                forecastWaveHeightsList.add("5 - 6ft")
            } else if (6 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 7) {
                forecastWaveHeightsList.add("5 - 7ft")
            } else if (7 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 8) {
                forecastWaveHeightsList.add("6 - 8ft")
            } else if (8 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 10) {
                forecastWaveHeightsList.add("8 - 10ft")
            } else if (10 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 12) {
                forecastWaveHeightsList.add("10 - 12ft")
            } else if (12 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 15) {
                forecastWaveHeightsList.add("12 - 15ft")
            } else if (15 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 20) {
                forecastWaveHeightsList.add("15 - 20ft")
            } else if (20 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 25) {
                forecastWaveHeightsList.add("20 - 24ft+")
            } else if (25 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 30) {
                forecastWaveHeightsList.add("25 - 28ft+")
            } else if (30 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 40) {
                forecastWaveHeightsList.add("30 - 40ft")
            } else if (40 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 50) {
                forecastWaveHeightsList.add("40 - 50ft")
            } else if (50 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 60) {
                forecastWaveHeightsList.add("50 - 60ft")
            } else if (60 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 70) {
                forecastWaveHeightsList.add("60 - 70ft")
            } else if (70 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 80) {
                forecastWaveHeightsList.add("70 - 80ft")
            } else if (80 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 90) {
                forecastWaveHeightsList.add("80 - 90ft")
            } else if (90 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084) && (beachObject.hours[i].waveHeight?.sg!! * 3.28084) < 99) {
                forecastWaveHeightsList.add("90 - 100ft")
            } else if (100 <= (beachObject.hours[i].waveHeight?.sg!! * 3.28084)) {
                forecastWaveHeightsList.add("100ft+")
            } else
                forecastWaveHeightsList.add("ERROR")
        }
    }
}

class BeachDetailViewModelFactory(private val favoriteDao: FavoriteDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeachDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeachDetailViewModel(favoriteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}