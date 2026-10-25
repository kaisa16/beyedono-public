// file: app/src/main/java/ru/beyedono/app/ui/screen/checkout/CheckoutViewModel.kt
package ru.beyedono.app.ui.screen.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.AddressDao
import ru.beyedono.app.data.local.dao.UserDao
import ru.beyedono.app.data.local.entity.AddressEntity
import ru.beyedono.app.data.repository.AddressRepository
import ru.beyedono.app.data.repository.CartRepository
import ru.beyedono.app.data.repository.OrderRepository
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val addressRepository: AddressRepository,
    private val userDao: UserDao,
    private val addressDao: AddressDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialPromoCode = savedStateHandle.get<String>("promoCode") ?: ""

    private val _uiState = MutableStateFlow(CheckoutUiState(promoCode = initialPromoCode))
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow("picked_address", "").collect { address ->
                if (address.isNotBlank()) _uiState.update { it.copy(street = address) }
            }
        }

        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                val subtotal = items.sumOf { it.totalPrice }
                val delivery = if (subtotal >= 1500 || subtotal == 0.0) 0.0 else 200.0
                val discount = if (initialPromoCode == "BEYEDONO") subtotal * 0.10 else 0.0
                
                _uiState.update {
                    it.copy(cartItems = items, subtotal = subtotal, deliveryPrice = delivery, discount = discount, total = subtotal + delivery - discount)
                }
            }
        }
        
        // СЛЕДИМ ЗА ПОЛЬЗОВАТЕЛЕМ В РЕАЛЬНОМ ВРЕМЕНИ
        viewModelScope.launch {
            userDao.getUser().flatMapLatest { user ->
                val userId = user?.id ?: "guest"
                _uiState.update { it.copy(isGuest = user == null) }
                addressDao.getAddressesByUser(userId)
            }.collect { saved ->
                val uniqueAddresses = saved.distinctBy { "${it.street}_${it.house}_${it.apartment}" }
                _uiState.update { it.copy(savedAddresses = uniqueAddresses) }
            }
        }
    }

    fun onSelectSavedAddress(address: AddressEntity) {
        _uiState.update { it.copy(street = address.street, house = address.house, apartment = address.apartment ?: "") }
    }

    fun onStreetChange(value: String) = _uiState.update { it.copy(street = value) }
    fun onHouseChange(value: String) = _uiState.update { it.copy(house = value) }
    fun onApartmentChange(value: String) = _uiState.update { it.copy(apartment = value) }
    fun onNoteChange(value: String) = _uiState.update { it.copy(note = value) }
    fun onPaymentMethodChange(value: String) = _uiState.update { it.copy(paymentMethod = value) }

    fun placeOrder() {
        val state = _uiState.value
        if (state.isGuest) { _uiState.update { it.copy(error = "Войдите в аккаунт") }; return }
        if (state.cartItems.isEmpty()) { _uiState.update { it.copy(error = "Корзина пуста") }; return }
        if (state.street.isBlank() || state.house.isBlank()) { _uiState.update { it.copy(error = "Заполните улицу и дом") }; return }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = userDao.getUser().first()
            val userId = user?.id ?: "guest"
            val userEmail = user?.email
            
            val existingAddress = state.savedAddresses.find { it.street == state.street && it.house == state.house && (it.apartment ?: "") == state.apartment }
            val addressId = existingAddress?.id ?: UUID.randomUUID().toString()
            
            if (existingAddress == null) {
                val address = AddressEntity(id = addressId, userId = userId, city = state.city, street = state.street, house = state.house, apartment = state.apartment, entrance = null, floor = null, intercom = null, lat = null, lng = null, isDefault = false)
                addressRepository.saveAddress(address)
            }

            val items = state.cartItems.map { it.productId to it.quantity }
            val fullAddress = "${state.street}, д. ${state.house}" + (if (state.apartment.isNotBlank()) ", кв. ${state.apartment}" else "")
            
            val result = orderRepository.createOrder(
                addressId = addressId, addressText = fullAddress, items = items, note = state.note, paymentMethod = state.paymentMethod,
                discount = state.discount, deliveryPrice = state.deliveryPrice, userEmail = userEmail, promoCode = state.promoCode
            )

            result.onSuccess { orderId ->
                cartRepository.clearCart()
                _uiState.update { it.copy(isLoading = false, createdOrderId = orderId) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Ошибка создания заказа") }
            }
        }
    }
}
