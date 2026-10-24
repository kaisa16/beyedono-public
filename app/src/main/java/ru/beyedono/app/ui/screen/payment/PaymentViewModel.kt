// file: app/src/main/java/ru/beyedono/app/ui/screen/payment/PaymentViewModel.kt
package ru.beyedono.app.ui.screen.payment

import androidx.lifecycle.SavedStateHandle
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
class PaymentViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = savedStateHandle["orderId"] ?: ""
    
    private val _uiState = MutableStateFlow(PaymentUiState(orderId = orderId))
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        loadOrderAmount()
    }

    private fun loadOrderAmount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Берем реальную сумму из локальной базы данных (Room)
            orderRepository.getLocalOrders().collect { orders ->
                val order = orders.find { it.id == orderId }
                if (order != null) {
                    _uiState.update { it.copy(amount = order.totalPrice, isLoading = false) }
                }
            }
        }
    }

    fun markAsPaid(onSuccess: () -> Unit) {
        viewModelScope.launch {
            orderRepository.markAsPaid(orderId)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
}
