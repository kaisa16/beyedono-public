// file: app/src/main/java/ru/beyedono/app/data/repository/PaymentRepository.kt
package ru.beyedono.app.data.repository

import ru.beyedono.app.data.remote.PaymentApi
import ru.beyedono.app.data.remote.dto.PaymentRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentApi: PaymentApi
) {
    suspend fun createPayment(orderId: String, amount: Double): Result<String> = runCatching {
        val request = PaymentRequest(
            orderId = orderId,
            amount = amount,
            returnUrl = "beyedono://payment/return"
        )
        val response = paymentApi.createPayment(request)
        response.confirmationUrl
    }
}