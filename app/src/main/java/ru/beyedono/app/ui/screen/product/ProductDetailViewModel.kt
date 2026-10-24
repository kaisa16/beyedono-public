// file: app/src/main/java/ru/beyedono/app/ui/screen/product/ProductDetailViewModel.kt
package ru.beyedono.app.ui.screen.product

import androidx.lifecycle.SavedStateHandle
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
import ru.beyedono.app.domain.model.Product
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle["productId"] ?: ""

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            val entity = productDao.getProductById(productId)
            if (entity != null) {
                val product = Product(
                    id = entity.id,
                    categoryId = entity.categoryId,
                    name = entity.name,
                    description = entity.description,
                    price = entity.price,
                    imageUrl = entity.imageUrl,
                    isAvailable = entity.isAvailable,
                    isSpicy = entity.isSpicy,
                    isVegetarian = entity.isVegetarian
                )
                _uiState.update { it.copy(product = product, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Товар не найден") }
            }
        }
    }

    fun increaseQuantity() {
        _uiState.update { it.copy(quantity = it.quantity + 1) }
    }

    fun decreaseQuantity() {
        _uiState.update { if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else it }
    }

    fun addToCart(onSuccess: () -> Unit) {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            repeat(_uiState.value.quantity) {
                cartRepository.addToCart(product.id, product.price)
            }
            onSuccess()
        }
    }
}