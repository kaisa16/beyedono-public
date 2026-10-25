// file: app/src/main/java/ru/beyedono/app/data/repository/OrderRepository.kt
package ru.beyedono.app.data.repository

import kotlinx.coroutines.flow.Flow
import ru.beyedono.app.data.local.dao.OrderDao
import ru.beyedono.app.data.local.entity.OrderEntity
import ru.beyedono.app.data.remote.OrderApi
import ru.beyedono.app.data.remote.dto.CreateOrderRequest
import ru.beyedono.app.data.remote.dto.OrderItemRequest
import ru.beyedono.app.data.remote.dto.OrderResponse
import ru.beyedono.app.data.remote.dto.StatusUpdateRequest
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderApi: OrderApi,
    private val orderDao: OrderDao
) {
    fun getLocalOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun createOrder(
        addressId: String?, addressText: String?, items: List<Pair<String, Int>>, note: String?,
        paymentMethod: String, discount: Double, deliveryPrice: Double, userEmail: String?, promoCode: String? = null
    ): Result<String> = runCatching {
        val request = CreateOrderRequest(
            addressId = addressId, addressText = addressText, items = items.map { OrderItemRequest(it.first, it.second) },
            note = note, deliveryTime = null, paymentMethod = paymentMethod, discount = discount,
            deliveryPrice = deliveryPrice, userEmail = userEmail, promoCode = promoCode
        )
        val response = orderApi.createOrder(request)
        
        val itemsStr = response.items.joinToString(", ") { "${it.name} x${it.quantity}" }
        val entity = OrderEntity(
            id = response.id, userId = userEmail ?: "", addressId = addressId, addressText = response.addressText,
            isPaid = response.isPaid, status = response.status, totalPrice = response.totalPrice, deliveryPrice = response.deliveryPrice,
            discount = response.discount, note = response.note, adminNote = response.adminNote, paymentMethod = response.paymentMethod,
            itemsSummary = itemsStr, userEmail = response.userEmail, userPhone = response.userPhone,
            createdAt = System.currentTimeMillis(), deliveryTime = null
        )
        orderDao.insertOrder(entity)
        response.id
    }

    suspend fun syncOrders(userEmail: String? = null, isAdmin: Boolean = false): Result<Unit> = runCatching {
        val orders = orderApi.getOrders(userEmail, isAdmin)
        
        val entities = orders.map {
            val timestamp = try {
                if (it.createdAt.endsWith("Z")) java.time.Instant.parse(it.createdAt).toEpochMilli()
                else LocalDateTime.parse(it.createdAt).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } catch (e: Exception) { System.currentTimeMillis() }
            
            val itemsStr = it.items.joinToString(", ") { item -> "${item.name} x${item.quantity}" }
            OrderEntity(
                id = it.id, userId = "", addressId = null, addressText = it.addressText, isPaid = it.isPaid, status = it.status,
                totalPrice = it.totalPrice, deliveryPrice = it.deliveryPrice, discount = it.discount, note = it.note, adminNote = it.adminNote,
                paymentMethod = it.paymentMethod, itemsSummary = itemsStr, userEmail = it.userEmail, userPhone = it.userPhone,
                createdAt = timestamp, deliveryTime = null
            )
        }
        orderDao.replaceAllOrders(entities)
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String, adminNote: String? = null): Result<Unit> = runCatching {
        orderApi.updateOrderStatus(orderId, StatusUpdateRequest(newStatus, adminNote))
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> = runCatching {
        orderApi.updateOrderStatus(orderId, StatusUpdateRequest("CANCELLED", null))
    }

    suspend fun markAsPaid(orderId: String): Result<Unit> = runCatching {
        orderApi.payOrder(orderId)
    }

    suspend fun getOrderDetailsFromServer(orderId: String): Result<OrderResponse> = runCatching {
        orderApi.getOrderById(orderId)
    }
}
