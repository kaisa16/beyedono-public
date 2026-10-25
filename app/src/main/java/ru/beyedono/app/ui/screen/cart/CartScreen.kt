// file: app/src/main/java/ru/beyedono/app/ui/screen/cart/CartScreen.kt
package ru.beyedono.app.ui.screen.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.domain.util.PriceFormatter
import ru.beyedono.app.ui.components.CartItemRow
import ru.beyedono.app.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onCheckoutClick: (String) -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Корзина") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
                }
            )
        },
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Товары:", style = MaterialTheme.typography.bodyLarge)
                        Text(PriceFormatter.format(uiState.subtotal), style = MaterialTheme.typography.bodyLarge)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Доставка:", style = MaterialTheme.typography.bodyLarge)
                        Text(if (uiState.deliveryPrice == 0.0) "Бесплатно" else PriceFormatter.format(uiState.deliveryPrice), style = MaterialTheme.typography.bodyLarge)
                    }
                    if (uiState.discount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Скидка:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                            Text("-${PriceFormatter.format(uiState.discount)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Итого:", style = MaterialTheme.typography.titleLarge)
                        Text(PriceFormatter.format(uiState.total), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onCheckoutClick(uiState.appliedPromoCode) }, // <--- ПЕРЕДАЕМ ПРИМЕНЕННЫЙ ПРОМОКОД
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Перейти к оформлению")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.items.isEmpty()) {
            EmptyState(
                icon = Icons.Default.ShoppingCart, title = "Корзина пуста", description = "Добавьте товары из меню, чтобы сделать заказ",
                actionText = "В меню", onActionClick = onNavigateBack, modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.items) { item ->
                    CartItemRow(
                        item = item, onIncrease = { viewModel.increaseQuantity(item.productId, item.price) },
                        onDecrease = { viewModel.decreaseQuantity(item.productId) }, onDelete = { viewModel.deleteItem(item.productId) }
                    )
                    HorizontalDivider()
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = uiState.promoCode, onValueChange = viewModel::onPromoCodeChange,
                            label = { Text("Промокод") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        TextButton(onClick = viewModel::applyPromoCode) { Text("Применить") }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
