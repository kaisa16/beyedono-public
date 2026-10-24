// file: app/src/main/java/ru/beyedono/app/ui/screen/history/OrderHistoryScreen.kt
package ru.beyedono.app.ui.screen.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.domain.model.OrderStatus
import ru.beyedono.app.domain.util.PriceFormatter
import ru.beyedono.app.ui.components.EmptyState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История заказов") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        } else if (uiState.orders.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Receipt,
                title = "Нет заказов",
                description = "Здесь появятся ваши прошлые заказы",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.orders, key = { it.id }) { order ->
                    val statusEnum = try { OrderStatus.valueOf(order.status) } catch (e: Exception) { OrderStatus.PENDING }
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    val dateString = dateFormat.format(Date(order.createdAt))
                    var expanded by remember { mutableStateOf(false) }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Заказ #${order.id.take(8)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(dateString, style = MaterialTheme.typography.bodySmall)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = statusEnum.getDisplayName(),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Icon(Icons.Default.ExpandMore, contentDescription = null)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(PriceFormatter.format(order.totalPrice), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            
                            AnimatedVisibility(visible = expanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    Text("Состав: ${order.itemsSummary}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Оплата: ${if (order.paymentMethod == "SBP") "СБП / Карта" else "Наличными"}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Адрес: ${order.addressText}", style = MaterialTheme.typography.bodyMedium)
                                    if (order.discount > 0.0) {
                                        Text("Скидка: -${PriceFormatter.format(order.discount)}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { onOrderClick(order.id) }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Подробнее о статусе")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
