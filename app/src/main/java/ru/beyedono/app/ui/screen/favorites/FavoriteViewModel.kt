// file: app/src/main/java/ru/beyedono/app/ui/screen/favorites/FavoriteViewModel.kt
package ru.beyedono.app.ui.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.ProductDao
import ru.beyedono.app.data.repository.FavoritesRepository
import ru.beyedono.app.domain.model.Product
import javax.inject.Inject

data class FavoriteUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val productDao: ProductDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.favorites.collect { favIds ->
                val prods = mutableListOf<Product>()
                for (id in favIds) {
                    val entity = productDao.getProductById(id)
                    if (entity != null) {
                        prods.add(
                            Product(
                                id = entity.id, categoryId = entity.categoryId, name = entity.name,
                                description = entity.description, price = entity.price, imageUrl = entity.imageUrl,
                                isAvailable = entity.isAvailable, isSpicy = entity.isSpicy, isVegetarian = entity.isVegetarian
                            )
                        )
                    }
                }
                _uiState.update { it.copy(products = prods, isLoading = false) }
            }
        }
    }

    fun removeFavorite(productId: String) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(productId)
        }
    }
}
