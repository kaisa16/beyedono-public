// file: app/src/main/java/ru/beyedono/app/ui/screen/menu/MenuViewModel.kt
package ru.beyedono.app.ui.screen.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.repository.FavoritesRepository
import ru.beyedono.app.data.repository.MenuRepository
import ru.beyedono.app.domain.model.Product
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    init {
        loadMenu()
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.favorites.collect { ids -> _uiState.update { it.copy(favoriteIds = ids) } }
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch { favoritesRepository.toggleFavorite(productId) }
    }

    fun loadMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val cats = menuRepository.syncMenu().getOrDefault(emptyList())
            
            menuRepository.getLocalProducts().collect { entities ->
                allProducts = entities.map {
                    Product(
                        id = it.id, categoryId = it.categoryId, name = it.name, description = it.description,
                        price = it.price, imageUrl = it.imageUrl, isAvailable = it.isAvailable, isSpicy = it.isSpicy, isVegetarian = it.isVegetarian
                    )
                }
                _uiState.update { it.copy(categories = listOf("Все") + cats, isLoading = false) }
                filterProducts()
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        filterProducts()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterProducts()
    }

    fun toggleSpicyFilter() {
        _uiState.update { it.copy(showOnlySpicy = !it.showOnlySpicy) }
        filterProducts()
    }

    fun toggleVegetarianFilter() {
        _uiState.update { it.copy(showOnlyVegetarian = !it.showOnlyVegetarian) }
        filterProducts()
    }

    private fun filterProducts() {
        val state = _uiState.value
        var filtered = allProducts

        if (state.selectedCategory != "Все") {
            filtered = filtered.filter { it.categoryId.equals(state.selectedCategory, ignoreCase = true) }
        }
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
        }
        if (state.showOnlySpicy) {
            filtered = filtered.filter { it.isSpicy }
        }
        if (state.showOnlyVegetarian) {
            filtered = filtered.filter { it.isVegetarian }
        }

        _uiState.update { it.copy(products = filtered) }
    }
}