package com.waitinglobby.waveplan.network


import com.squareup.moshi.Json

data class GetBeachDetails(
    @Json(name = "hours")
    val hours: List<Hour>,
    @Json(name = "meta")
    val meta: Meta?
) {
    data class Hour(
        @Json(name = "airTemperature")
        val airTemperature: AirTemperature?,
        @Json(name = "cloudCover")
        val cloudCover: CloudCover?,
        @Json(name = "currentDirection")
        val currentDirection: CurrentDirection?,
        @Json(name = "currentSpeed")
        val currentSpeed: CurrentSpeed?,
        @Json(name = "humidity")
        val humidity: Humidity?,
        @Json(name = "secondarySwellDirection")
        val secondarySwellDirection: SecondarySwellDirection?,
        @Json(name = "secondarySwellHeight")
        val secondarySwellHeight: SecondarySwellHeight?,
        @Json(name = "secondarySwellPeriod")
        val secondarySwellPeriod: SecondarySwellPeriod?,
        @Json(name = "swellDirection")
        val swellDirection: SwellDirection?,
        @Json(name = "swellHeight")
        val swellHeight: SwellHeight?,
        @Json(name = "swellPeriod")
        val swellPeriod: SwellPeriod?,
        @Json(name = "time")
        val time: String?,
        @Json(name = "visibility")
        val visibility: Visibility?,
        @Json(name = "waterTemperature")
        val waterTemperature: WaterTemperature?,
        @Json(name = "waveDirection")
        val waveDirection: WaveDirection?,
        @Json(name = "waveHeight")
        val waveHeight: WaveHeight?,
        @Json(name = "wavePeriod")
        val wavePeriod: WavePeriod?,
        @Json(name = "windDirection")
        val windDirection: WindDirection?,
        @Json(name = "windSpeed")
        val windSpeed: WindSpeed?,
        @Json(name = "windWaveDirection")
        val windWaveDirection: WindWaveDirection?,
        @Json(name = "windWaveHeight")
        val windWaveHeight: WindWaveHeight?,
        @Json(name = "windWavePeriod")
        val windWavePeriod: WindWavePeriod?
    ) {
        data class AirTemperature(
            @Json(name = "sg")
            val sg: Double?
        )

        data class CloudCover(
            @Json(name = "sg")
            val sg: Double?
        )

        data class CurrentDirection(
            @Json(name = "sg")
            val sg: Double?
        )

        data class CurrentSpeed(
            @Json(name = "sg")
            val sg: Double?
        )

        data class Humidity(
            @Json(name = "sg")
            val sg: Double?
        )

        data class SecondarySwellDirection(
            @Json(name = "sg")
            val sg: Double?
        )

        data class SecondarySwellHeight(
            @Json(name = "sg")
            val sg: Double?
        )

        data class SecondarySwellPeriod(
            @Json(name = "sg")
            val sg: Double?
        )

        data class SwellDirection(
            @Json(name = "sg")
            val sg: Double?
        )

        data class SwellHeight(
            @Json(name = "sg")
            val sg: Double?
        )

        data class SwellPeriod(
            @Json(name = "sg")
            val sg: Double?
        )

        data class Visibility(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WaterTemperature(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WaveDirection(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WaveHeight(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WavePeriod(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WindDirection(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WindSpeed(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WindWaveDirection(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WindWaveHeight(
            @Json(name = "sg")
            val sg: Double?
        )

        data class WindWavePeriod(
            @Json(name = "sg")
            val sg: Double?
        )
    }

    data class Meta(
        @Json(name = "cost")
        val cost: Int?,
        @Json(name = "dailyQuota")
        val dailyQuota: Int?,
        @Json(name = "end")
        val end: String?,
        @Json(name = "lat")
        val lat: Double?,
        @Json(name = "lng")
        val lng: Double?,
        @Json(name = "params")
        val params: List<String?>?,
        @Json(name = "requestCount")
        val requestCount: Int?,
        @Json(name = "source")
        val source: List<String?>?,
        @Json(name = "start")
        val start: String?
    )
}