// file: app/src/main/java/ru/beyedono/app/data/local/dao/OrderDao.kt
package ru.beyedono.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.beyedono.app.data.local.entity.OrderEntity

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("DELETE FROM orders")
    suspend fun clearOrders()

    // ТРАНЗАКЦИЯ: Удаляет старые и вставляет новые за 1 раз
    @Transaction
    suspend fun replaceAllOrders(orders: List<OrderEntity>) {
        clearOrders()
        insertOrders(orders)
    }
}
