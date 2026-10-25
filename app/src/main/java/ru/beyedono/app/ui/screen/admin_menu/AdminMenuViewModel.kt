// file: app/src/main/java/ru/beyedono/app/ui/screen/admin_menu/AdminMenuViewModel.kt
package ru.beyedono.app.ui.screen.admin_menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.entity.ProductEntity
import ru.beyedono.app.data.remote.ProductCreateRequest
import ru.beyedono.app.data.repository.AdminRepository
import ru.beyedono.app.data.repository.MenuRepository
import javax.inject.Inject

@HiltViewModel
class AdminMenuViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminMenuUiState())
    val uiState: StateFlow<AdminMenuUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            menuRepository.syncMenu()
            menuRepository.getLocalProducts().collect { prods ->
                _uiState.update { it.copy(products = prods, isLoading = false) }
            }
        }
    }

    fun openAddDialog() = _uiState.update { it.copy(showDialog = true, editingProduct = null) }
    
    fun openEditDialog(product: ProductEntity) = _uiState.update { it.copy(showDialog = true, editingProduct = product) }
    
    fun closeDialog() = _uiState.update { it.copy(showDialog = false) }

    fun saveProduct(
        name: String, category: String, price: Double, description: String, imageUrl: String
    ) {
        viewModelScope.launch {
            val request = ProductCreateRequest(
                category_id = category.ifBlank { "Разное" },
                name = name,
                description = description.ifBlank { null },
                price = price,
                image_url = imageUrl.ifBlank { null }
            )
            
            val editing = _uiState.value.editingProduct
            val result = if (editing == null) {
                adminRepository.addProduct(request)
            } else {
                adminRepository.updateProduct(editing.id, request)
            }

            result.onSuccess {
                closeDialog()
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message) }
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            adminRepository.deleteProduct(productId)
        }
    }
}
