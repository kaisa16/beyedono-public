// file: app/src/main/java/ru/beyedono/app/ui/screen/admin/AdminOrdersScreen.kt
package ru.beyedono.app.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.domain.model.OrderStatus
import ru.beyedono.app.domain.util.PriceFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMenu: () -> Unit,
    viewModel: AdminOrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель кафе (Админ)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
                },
                actions = {
                    TextButton(onClick = { onNavigateToMenu() }) { Text("Меню", color = MaterialTheme.colorScheme.primary) }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.orders, key = { it.id }) { order ->
                    val statusEnum = try { OrderStatus.valueOf(order.status) } catch (e: Exception) { OrderStatus.PENDING }
                    var adminMsg by remember { mutableStateOf("") }
                    
                    // ФОРМАТИРОВАНИЕ ДАТЫ
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    val dateString = dateFormat.format(Date(order.createdAt))
                    
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Заказ #${order.id.take(8)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(dateString, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                                Text(
                                    if (order.isPaid) "ОПЛАЧЕН" else "НЕ ОПЛАЧЕН", 
                                    color = if (order.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (!order.userEmail.isNullOrBlank()) {
                                Text("Клиент: ${order.userEmail}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            if (!order.userPhone.isNullOrBlank()) {
                                Text("Телефон: ${order.userPhone}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Адрес: ${order.addressText ?: "Не указан"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Оплата: ${if (order.paymentMethod == "SBP") "СБП / Карта" else "Наличными"}", style = MaterialTheme.typography.bodyMedium)
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Состав: ${order.itemsSummary}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text("Сумма: ${PriceFormatter.format(order.totalPrice)}")
                            if (order.discount > 0.0) {
                                Text("Скидка: -${PriceFormatter.format(order.discount)}", color = MaterialTheme.colorScheme.error)
                            }
                            
                            if (!order.note.isNullOrBlank()) {
                                Text("Примечание клиента: ${order.note}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                            if (!order.adminNote.isNullOrBlank()) {
                                Text("Ваше сообщение: ${order.adminNote}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            
                            Text("Статус: ${statusEnum.getDisplayName()}", color = MaterialTheme.colorScheme.primary)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (statusEnum == OrderStatus.PENDING) {
                                    Button(onClick = { viewModel.changeStatus(order.id, "COOKING") }) { Text("Готовится") }
                                } else if (statusEnum == OrderStatus.COOKING) {
                                    Button(onClick = { viewModel.changeStatus(order.id, "DELIVERING") }) { Text("В пути") }
                                } else if (statusEnum == OrderStatus.DELIVERING) {
                                    Button(onClick = { viewModel.changeStatus(order.id, "DELIVERED") }) { Text("Доставлен") }
                                }
                                
                                if (statusEnum != OrderStatus.DELIVERED && statusEnum != OrderStatus.CANCELLED) {
                                    Button(
                                        onClick = { viewModel.changeStatus(order.id, "CANCELLED") },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Отменить")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = adminMsg,
                                onValueChange = { adminMsg = it },
                                label = { Text("Написать клиенту") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        viewModel.changeStatus(order.id, order.status, adminMsg)
                                        adminMsg = ""
                                    }) {
                                        Icon(Icons.Default.Send, contentDescription = "Отправить")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
