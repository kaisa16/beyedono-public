// file: app/src/main/java/ru/beyedono/app/di/DatabaseModule.kt
package ru.beyedono.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.beyedono.app.data.local.AppDatabase
import ru.beyedono.app.data.local.dao.AddressDao
import ru.beyedono.app.data.local.dao.CartDao
import ru.beyedono.app.data.local.dao.OrderDao
import ru.beyedono.app.data.local.dao.ProductDao
import ru.beyedono.app.data.local.dao.UserDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "beyedono.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()

    @Provides
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()

    @Provides
    fun provideAddressDao(db: AppDatabase): AddressDao = db.addressDao()
}