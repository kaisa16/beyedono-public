// file: app/src/main/java/ru/beyedono/app/ui/screen/admin/AdminOrdersScreen.kt
package ru.beyedono.app.ui.screen.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.domain.model.OrderStatus
import ru.beyedono.app.domain.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminOrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель кафе (Админ)") },
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.orders, key = { it.id }) { order ->
                    val statusEnum = try { OrderStatus.valueOf(order.status) } catch (e: Exception) { OrderStatus.PENDING }
                    var adminMsg by remember { mutableStateOf("") }
                    
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Заказ #${order.id.take(8)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    if (order.isPaid) "ОПЛАЧЕН" else "НЕ ОПЛАЧЕН", 
                                    color = if (order.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("Адрес: ${order.addressText ?: "Не указан"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Оплата: ${if (order.paymentMethod == "SBP") "СБП / Карта" else "Наличными"}", style = MaterialTheme.typography.bodyMedium)
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Состав: ${order.itemsSummary}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text("Сумма: ${PriceFormatter.format(order.totalPrice)}")
                            if (order.discount > 0.0) {
                                Text("Скидка: -${PriceFormatter.format(order.discount)}", color = MaterialTheme.colorScheme.error)
                            }
                            
                            if (!order.note.isNullOrBlank()) {
                                Text("Клиент: ${order.note}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                            if (!order.adminNote.isNullOrBlank()) {
                                Text("Ваше сообщение: ${order.adminNote}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            
                            Text("Статус: ${statusEnum.getDisplayName()}", color = MaterialTheme.colorScheme.primary)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // БЛОК КНОПОК СТАТУСА
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (statusEnum == OrderStatus.PENDING) {
                                    Button(onClick = { viewModel.changeStatus(order.id, "COOKING") }) { Text("Готовится") }
                                } else if (statusEnum == OrderStatus.COOKING) {
                                    Button(onClick = { viewModel.changeStatus(order.id, "DELIVERING") }) { Text("В пути") }
                                } else if (statusEnum == OrderStatus.DELIVERING) {
                                    Button(onClick = { viewModel.changeStatus(order.id, "DELIVERED") }) { Text("Доставлен") }
                                }
                                
                                // КНОПКА ОТМЕНЫ ДЛЯ АДМИНА (Добавлена)
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
