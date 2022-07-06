package com.waitinglobby.waveplan.model

import androidx.annotation.DrawableRes

/**
 * A data class to represent a single beach. Usage of the data is in the beach cards
 * as well as in the API request to fetch marine data for a given beach
 */

data class Beach(
    val id: Int,
    @DrawableRes val imageResourceId: Int,
    val name: String,
    val parentCountryID: Int,
    val parentStateID: Int,
    val parentCityID: Int,
    val lat: Double,
    val lng: Double
)