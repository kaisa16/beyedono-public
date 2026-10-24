// file: app/src/main/java/ru/beyedono/app/data/local/dao/AddressDao.kt
package ru.beyedono.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.beyedono.app.data.local.entity.AddressEntity

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId")
    fun getAddressesByUser(userId: String): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE isDefault = 1 AND userId = :userId LIMIT 1")
    suspend fun getDefaultAddress(userId: String): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("DELETE FROM addresses")
    suspend fun clearAddresses()
}