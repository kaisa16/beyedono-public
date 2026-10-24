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
                val subtotal = items.sumOf { it.totalPrice }
                val delivery = if (subtotal >= 1500 || subtotal == 0.0) 0.0 else 200.0
                val discount = _uiState.value.discount
                _uiState.update { it.copy(
                    items = items,
                    subtotal = subtotal,
                    deliveryPrice = delivery,
                    total = subtotal + delivery - discount
                ) }
            }
        }
    }

    fun increaseQuantity(productId: String, price: Double) {
        viewModelScope.launch {
            cartRepository.addToCart(productId, price)
        }
    }

    fun decreaseQuantity(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
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
        var discountPercent = 0.0
        
        // Логика промокодов
        when (code) {
            "BEYEDONO" -> discountPercent = 0.10
            "YAKUTIA" -> discountPercent = 0.15
            "WELCOME" -> discountPercent = 0.05
        }

        val discountAmount = _uiState.value.subtotal * discountPercent
        _uiState.update { it.copy(discount = discountAmount) }
        recalculateTotal()
    }

    private fun recalculateTotal() {
        _uiState.update {
            val delivery = if (it.subtotal >= 1500 || it.subtotal == 0.0) 0.0 else 200.0
            it.copy(
                deliveryPrice = delivery,
                total = it.subtotal + delivery - it.discount
            )
        }
    }
}
