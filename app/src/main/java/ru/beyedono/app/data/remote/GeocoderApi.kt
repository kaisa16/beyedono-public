package ru.beyedono.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class NominatimResponse(
    val display_name: String?
)

interface GeocoderApi {
    @GET("reverse?format=json")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("User-Agent") userAgent: String = "BeyedonoApp/1.0"
    ): NominatimResponse
}
