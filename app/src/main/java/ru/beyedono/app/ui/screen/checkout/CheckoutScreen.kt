// file: app/src/main/java/ru/beyedono/app/ui/screen/checkout/CheckoutScreen.kt
package ru.beyedono.app.ui.screen.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.domain.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToLogin: () -> Unit, // <--- НОВЫЙ ПАРАМЕТР
    onOrderPlaced: (String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.createdOrderId) {
        uiState.createdOrderId?.let { onOrderPlaced(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Оформление заказа") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                if (uiState.isGuest) {
                    // Если гость - показываем кнопку входа вместо "Заказать"
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Войти и заказать", color = MaterialTheme.colorScheme.onSecondary)
                    }
                } else {
                    Button(
                        onClick = viewModel::placeOrder,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !uiState.isLoading && uiState.cartItems.isNotEmpty()
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.height(24.dp))
                        else Text("Заказать за ${PriceFormatter.format(uiState.total)}")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Адрес доставки", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (uiState.savedAddresses.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    items(uiState.savedAddresses) { address ->
                        SuggestionChip(
                            onClick = { viewModel.onSelectSavedAddress(address) },
                            label = { Text("${address.street}, ${address.house}") },
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            OutlinedTextField(value = uiState.city, onValueChange = {}, label = { Text("Город") }, modifier = Modifier.fillMaxWidth(), enabled = false)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = uiState.street, onValueChange = viewModel::onStreetChange, label = { Text("Улица") }, modifier = Modifier.fillMaxWidth(), isError = uiState.error != null && uiState.street.isBlank())
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = uiState.house, onValueChange = viewModel::onHouseChange, label = { Text("Дом") }, modifier = Modifier.weight(1f), isError = uiState.error != null && uiState.house.isBlank())
                OutlinedTextField(value = uiState.apartment, onValueChange = viewModel::onApartmentChange, label = { Text("Кв.") }, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToMap, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Map, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Выбрать на карте")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Способ оплаты", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().selectable(selected = uiState.paymentMethod == "SBP", onClick = { viewModel.onPaymentMethodChange("SBP") })) {
                RadioButton(selected = uiState.paymentMethod == "SBP", onClick = { viewModel.onPaymentMethodChange("SBP") })
                Text("СБП (Система быстрых платежей)")
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().selectable(selected = uiState.paymentMethod == "CASH", onClick = { viewModel.onPaymentMethodChange("CASH") })) {
                RadioButton(selected = uiState.paymentMethod == "CASH", onClick = { viewModel.onPaymentMethodChange("CASH") })
                Text("Наличными курьеру")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Примечание к заказу", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = uiState.note, onValueChange = viewModel::onNoteChange, label = { Text("Комментарий для курьера") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Товары (${uiState.cartItems.size})", style = MaterialTheme.typography.bodyLarge)
                Text(PriceFormatter.format(uiState.subtotal), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Доставка", style = MaterialTheme.typography.bodyLarge)
                Text(if (uiState.deliveryPrice == 0.0) "Бесплатно" else PriceFormatter.format(uiState.deliveryPrice), style = MaterialTheme.typography.bodyLarge)
            }
            if (uiState.discount > 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Скидка", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                    Text("-${PriceFormatter.format(uiState.discount)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Итого", style = MaterialTheme.typography.titleLarge)
                Text(PriceFormatter.format(uiState.total), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
            
            uiState.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
