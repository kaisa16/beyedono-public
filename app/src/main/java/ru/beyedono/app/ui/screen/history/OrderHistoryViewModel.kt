// file: app/src/main/java/ru/beyedono/app/ui/screen/history/OrderHistoryViewModel.kt
package ru.beyedono.app.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.ProductDao
import ru.beyedono.app.data.repository.CartRepository
import ru.beyedono.app.data.repository.OrderRepository
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val productDao: ProductDao // <--- ДОБАВЛЕНО ДЛЯ ЦЕН
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

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

    fun repeatOrder(orderId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orderRepository.getOrderDetailsFromServer(orderId)
                .onSuccess { response ->
                    cartRepository.clearCart() 
                    
                    response.items.forEach { item ->
                        // БЕРЕМ АКТУАЛЬНУЮ ЦЕНУ ИЗ БАЗЫ
                        val currentProduct = productDao.getProductById(item.productId)
                        if (currentProduct != null) {
                            cartRepository.addQuantityToCart(
                                productId = currentProduct.id, 
                                price = currentProduct.price, // Актуальная цена
                                quantity = item.quantity
                            )
                        }
                    }
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, error = "Ошибка загрузки заказа") }
                }
        }
    }
}
