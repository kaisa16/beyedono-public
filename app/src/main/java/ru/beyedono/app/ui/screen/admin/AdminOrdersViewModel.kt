// file: app/src/main/java/ru/beyedono/app/ui/screen/admin/AdminOrdersViewModel.kt
package ru.beyedono.app.ui.screen.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.repository.OrderRepository
import javax.inject.Inject

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminOrdersUiState())
    val uiState: StateFlow<AdminOrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orderRepository.syncOrders()
            orderRepository.getLocalOrders().collect { orders ->
                _uiState.update { it.copy(orders = orders, isLoading = false) }
            }
        }
    }

    fun changeStatus(orderId: String, newStatus: String, adminNote: String? = null) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, newStatus, adminNote)
            orderRepository.syncOrders()
        }
    }
}
