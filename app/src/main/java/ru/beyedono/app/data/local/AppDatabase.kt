// file: app/src/main/java/ru/beyedono/app/data/local/AppDatabase.kt
package ru.beyedono.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.beyedono.app.data.local.dao.*
import ru.beyedono.app.data.local.entity.*

@Database(
    entities = [UserEntity::class, ProductEntity::class, CartItemEntity::class, OrderEntity::class, AddressEntity::class],
    version = 3, // <--- ОБНОВЛЕНО ДО 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
}
