// file: app/src/main/java/ru/beyedono/app/ui/screen/checkout/AddressPickerViewModel.kt
package ru.beyedono.app.ui.screen.checkout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel // <--- ДОБАВЛЕН ЭТОТ ИМПОРТ
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.beyedono.app.data.remote.GeocoderApi
import javax.inject.Inject

@HiltViewModel
class AddressPickerViewModel @Inject constructor(
    private val geocoderApi: GeocoderApi
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    suspend fun getAddressFromCoords(lat: Double, lon: Double): String {
        _isLoading.value = true
        return try {
            val response = withContext(Dispatchers.IO) {
                geocoderApi.reverseGeocode(lat, lon, "BeyedonoApp/1.0")
            }
            val fullAddress = response.display_name ?: "Неизвестный адрес"
            val parts = fullAddress.split(",").take(3).joinToString(",")
            parts.ifBlank { "Выбранная точка на карте" }
        } catch (e: Exception) {
            "г. Якутск, координаты: $lat, $lon"
        } finally {
            _isLoading.value = false
        }
    }
}
