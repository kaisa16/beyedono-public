// file: app/src/main/java/ru/beyedono/app/data/remote/MenuApi.kt
package ru.beyedono.app.data.remote

import retrofit2.http.GET
import ru.beyedono.app.data.remote.dto.MenuResponse

interface MenuApi {
    @GET("menu")
    suspend fun getMenu(): MenuResponse
}