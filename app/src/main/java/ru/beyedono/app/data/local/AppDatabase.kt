// file: app/src/main/java/ru/beyedono/app/data/local/AppDatabase.kt
package ru.beyedono.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.beyedono.app.data.local.dao.AddressDao
import ru.beyedono.app.data.local.dao.CartDao
import ru.beyedono.app.data.local.dao.OrderDao
import ru.beyedono.app.data.local.dao.ProductDao
import ru.beyedono.app.data.local.dao.UserDao
import ru.beyedono.app.data.local.entity.AddressEntity
import ru.beyedono.app.data.local.entity.CartItemEntity
import ru.beyedono.app.data.local.entity.OrderEntity
import ru.beyedono.app.data.local.entity.ProductEntity
import ru.beyedono.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        AddressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
}