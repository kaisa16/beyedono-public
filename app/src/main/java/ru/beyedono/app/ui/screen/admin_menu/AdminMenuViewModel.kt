// file: app/src/main/java/ru/beyedono/app/ui/screen/admin_menu/AdminMenuViewModel.kt
package ru.beyedono.app.ui.screen.admin_menu

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.beyedono.app.data.local.entity.ProductEntity
import ru.beyedono.app.data.remote.AdminApi
import ru.beyedono.app.data.remote.ProductCreateRequest
import ru.beyedono.app.data.repository.AdminRepository
import ru.beyedono.app.data.repository.MenuRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AdminMenuViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val menuRepository: MenuRepository,
    private val adminApi: AdminApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminMenuUiState())
    val uiState: StateFlow<AdminMenuUiState> = _uiState.asStateFlow()

    init { loadProducts() }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            menuRepository.syncMenu()
            menuRepository.getLocalProducts().collect { prods ->
                val cats = mutableListOf("Пиццы", "Роллы", "Основные блюда", "Салаты", "Фаст-Фуд", "Гамбургеры", "Напитки") 
                cats.addAll(prods.map { it.categoryId })
                
                _uiState.update { 
                    it.copy(
                        products = prods, 
                        availableCategories = cats.distinct().sorted(), 
                        isLoading = false 
                    ) 
                }
            }
        }
    }

    fun openAddDialog() = _uiState.update { it.copy(showDialog = true, editingProduct = null, error = null) }
    fun openEditDialog(product: ProductEntity) = _uiState.update { it.copy(showDialog = true, editingProduct = product, error = null) }
    fun closeDialog() = _uiState.update { it.copy(showDialog = false) }

    fun addCategory(name: String) {
        viewModelScope.launch {
            adminRepository.addCategory(name).onFailure { err -> _uiState.update { it.copy(error = err.message) } }
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            adminRepository.renameCategory(oldName, newName).onFailure { err -> _uiState.update { it.copy(error = err.message) } }
        }
    }

    fun deleteCategory(name: String) {
        viewModelScope.launch {
            adminRepository.deleteCategory(name).onFailure { err ->
                _uiState.update { it.copy(error = err.message) }
            }
        }
    }

    fun uploadImage(uri: Uri, context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, error = null) }
            try {
                val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                val requestFile = withContext(Dispatchers.IO) { file.asRequestBody("image/*".toMediaTypeOrNull()) }
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val response = withContext(Dispatchers.IO) { adminApi.uploadImage(body) }
                onSuccess(response.url)
                _uiState.update { it.copy(isUploading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUploading = false, error = "Ошибка фото: ${e.message}") }
            }
        }
    }

    fun saveProduct(name: String, category: String, price: Double, description: String, imageUrl: String) {
        viewModelScope.launch {
            val request = ProductCreateRequest(
                category_id = category.ifBlank { "Разное" },
                name = name,
                description = description.ifBlank { null },
                price = price,
                image_url = imageUrl.ifBlank { null }
            )
            val editing = _uiState.value.editingProduct
            val result = if (editing == null) adminRepository.addProduct(request) else adminRepository.updateProduct(editing.id, request)
            result.onSuccess { closeDialog() }.onFailure { err -> _uiState.update { it.copy(error = err.message) } }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch { adminRepository.deleteProduct(productId) }
    }
}
