package com.waitinglobby.waveplan.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL =
    "https://api.stormglass.io/v2/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BeachDetailsApiService {
    @Headers(
        "Authorization: 7e40dd66-5ee2-11ec-b9d3-0242ac130002-7e40de2e-5ee2-11ec-b9d3-0242ac130002",
        "Content-type: application/json"
    )
    @GET("weather/point")
    suspend fun getBeachDetails(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("params") params: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("source") source: String
    ): GetBeachDetails
}

object BeachDetailsApi {
    val retrofitService: BeachDetailsApiService by lazy {
        retrofit.create(BeachDetailsApiService::class.java) }
}