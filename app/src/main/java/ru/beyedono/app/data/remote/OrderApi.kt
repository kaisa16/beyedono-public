// file: app/src/main/java/ru/beyedono/app/data/remote/OrderApi.kt
package ru.beyedono.app.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.beyedono.app.data.remote.dto.CreateOrderRequest
import ru.beyedono.app.data.remote.dto.OrderResponse
import ru.beyedono.app.data.remote.dto.StatusUpdateRequest

interface OrderApi {
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderResponse

    @GET("orders")
    suspend fun getOrders(): List<OrderResponse>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): OrderResponse

    @PUT("orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: String, @Body request: StatusUpdateRequest): Unit

    @PUT("orders/{id}/pay")
    suspend fun payOrder(@Path("id") id: String): Unit
}
