// file: app/src/main/java/ru/beyedono/app/data/remote/PaymentApi.kt
package ru.beyedono.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import ru.beyedono.app.data.remote.dto.PaymentRequest
import ru.beyedono.app.data.remote.dto.PaymentResponse

interface PaymentApi {
    @POST("payments")
    suspend fun createPayment(@Body request: PaymentRequest): PaymentResponse
}