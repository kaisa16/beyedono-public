// file: app/src/main/java/ru/beyedono/app/ui/screen/cart/CartViewModel.kt
package ru.beyedono.app.ui.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.repository.CartRepository
import ru.beyedono.app.domain.model.CartItem
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _uiState.update { state ->
                    recalculateTotals(items, state.appliedPromoCode, state.promoCode)
                }
            }
        }
    }

    // Вынесенная функция пересчета
    private fun recalculateTotals(items: List<CartItem>, appliedPromo: String, rawPromo: String): CartUiState {
        val subtotal = items.sumOf { it.totalPrice }
        val delivery = if (subtotal >= 1500 || subtotal == 0.0) 0.0 else 200.0
        
        var discountPercent = 0.0
        if (appliedPromo == "BEYEDONO") discountPercent = 0.10
        else if (appliedPromo == "YAKUTIA") discountPercent = 0.15
        else if (appliedPromo == "WELCOME") discountPercent = 0.05
        
        val discountAmount = subtotal * discountPercent

        return CartUiState(
            items = items,
            subtotal = subtotal,
            deliveryPrice = delivery,
            discount = discountAmount,
            promoCode = rawPromo,
            appliedPromoCode = appliedPromo,
            total = subtotal + delivery - discountAmount
        )
    }

    fun increaseQuantity(productId: String, price: Double) {
        viewModelScope.launch { cartRepository.addToCart(productId, price) }
    }

    fun decreaseQuantity(productId: String) {
        viewModelScope.launch { cartRepository.removeFromCart(productId) }
    }

    fun deleteItem(productId: String) {
        viewModelScope.launch {
            var item = _uiState.value.items.find { it.productId == productId }
            while (item != null) {
                cartRepository.removeFromCart(productId)
                delay(100)
                item = _uiState.value.items.find { it.productId == productId }
            }
        }
    }

    fun onPromoCodeChange(code: String) {
        _uiState.update { it.copy(promoCode = code) }
    }

    fun applyPromoCode() {
        val code = _uiState.value.promoCode.uppercase()
        val isValid = (code == "BEYEDONO" || code == "YAKUTIA" || code == "WELCOME")
        val applied = if (isValid) code else ""
        
        // Моментально пересчитываем итоги
        _uiState.update { state ->
            recalculateTotals(state.items, applied, state.promoCode)
        }
    }
}
