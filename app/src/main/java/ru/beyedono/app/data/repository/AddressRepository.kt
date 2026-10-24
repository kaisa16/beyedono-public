// file: app/src/main/java/ru/beyedono/app/data/repository/AddressRepository.kt
package ru.beyedono.app.data.repository

import kotlinx.coroutines.flow.Flow
import ru.beyedono.app.data.local.dao.AddressDao
import ru.beyedono.app.data.local.entity.AddressEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepository @Inject constructor(
    private val addressDao: AddressDao
) {
    fun getAddresses(userId: String): Flow<List<AddressEntity>> = addressDao.getAddressesByUser(userId)

    suspend fun getDefaultAddress(userId: String): AddressEntity? = addressDao.getDefaultAddress(userId)

    suspend fun saveAddress(address: AddressEntity) {
        addressDao.insertAddress(address)
    }

    suspend fun deleteAddress(address: AddressEntity) {
        addressDao.deleteAddress(address)
    }
}