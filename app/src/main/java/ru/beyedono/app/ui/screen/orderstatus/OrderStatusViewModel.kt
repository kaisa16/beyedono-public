// file: app/src/main/java/ru/beyedono/app/ui/screen/orderstatus/OrderStatusViewModel.kt
package ru.beyedono.app.ui.screen.orderstatus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.OrderDao
import ru.beyedono.app.data.repository.OrderRepository
import javax.inject.Inject

@HiltViewModel
class OrderStatusViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = savedStateHandle["orderId"] ?: ""

    private val _uiState = MutableStateFlow(OrderStatusUiState())
    val uiState: StateFlow<OrderStatusUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            orderDao.getOrderById(orderId).collect { entity ->
                if (entity != null) {
                    _uiState.update { it.copy(order = entity, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Заказ не найден") }
                }
            }
        }
    }

    fun cancelOrder(onSuccess: () -> Unit) {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId)
                .onSuccess {
                    orderRepository.syncOrders()
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
}
