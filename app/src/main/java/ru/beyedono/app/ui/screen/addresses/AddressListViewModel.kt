// file: app/src/main/java/ru/beyedono/app/ui/screen/addresses/AddressListViewModel.kt
package ru.beyedono.app.ui.screen.addresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.AddressDao
import ru.beyedono.app.data.local.dao.UserDao
import ru.beyedono.app.data.local.entity.AddressEntity
import javax.inject.Inject

data class AddressListState(
    val addresses: List<AddressEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val addressDao: AddressDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressListState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            val user = userDao.getUser().first()
            val userId = user?.id ?: "guest"
            addressDao.getAddressesByUser(userId).collect { addresses ->
                _uiState.update { it.copy(addresses = addresses, isLoading = false) }
            }
        }
    }

    fun deleteAddress(address: AddressEntity) {
        viewModelScope.launch {
            addressDao.deleteAddress(address)
        }
    }
}
