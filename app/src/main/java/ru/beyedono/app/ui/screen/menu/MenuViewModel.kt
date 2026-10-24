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
import ru.beyedono.app.data.repository.MenuRepository
import ru.beyedono.app.domain.model.Product
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            menuRepository.syncMenu()
            menuRepository.getLocalProducts().collect { entities ->
                allProducts = entities.map {
                    Product(
                        id = it.id,
                        categoryId = it.categoryId,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        isAvailable = it.isAvailable,
                        isSpicy = it.isSpicy,
                        isVegetarian = it.isVegetarian
                    )
                }
                filterProducts()
                _uiState.update { it.copy(isLoading = false) }
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

    private fun filterProducts() {
        val state = _uiState.value
        var filtered = allProducts

        if (state.selectedCategory != "Все") {
            filtered = filtered.filter { 
                it.categoryId.equals(state.selectedCategory, ignoreCase = true) 
            }
        }

        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
        }

        _uiState.update { it.copy(products = filtered) }
    }
}