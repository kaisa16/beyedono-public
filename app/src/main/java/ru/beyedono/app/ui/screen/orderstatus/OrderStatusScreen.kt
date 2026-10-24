// file: app/src/main/java/ru/beyedono/app/ui/screen/orderstatus/OrderStatusScreen.kt
package ru.beyedono.app.ui.screen.orderstatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

@Composable
fun OrderStatusScreen(
    onNavigateBack: () -> Unit,
    viewModel: OrderStatusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Отменить заказ?") },
            text = { Text("Вы уверены, что хотите отменить этот заказ?") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelOrder { onNavigateBack() }
                }) { Text("Да, отменить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Нет") }
            }
        )
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val order = uiState.order
    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.error ?: "Ошибка", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    val statusEnum = try { OrderStatus.valueOf(order.status) } catch (e: Exception) { OrderStatus.PENDING }
    val steps = listOf(OrderStatus.PENDING, OrderStatus.COOKING, OrderStatus.DELIVERING, OrderStatus.DELIVERED)
    val currentStepIndex = steps.indexOf(statusEnum)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Статус заказа", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Заказ #${order.id.take(8)}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            statusEnum.getDisplayName(),
            style = MaterialTheme.typography.displayLarge,
            color = if (statusEnum == OrderStatus.CANCELLED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        if (!order.adminNote.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Сообщение от кафе:\n${order.adminNote}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (statusEnum != OrderStatus.CANCELLED) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                steps.forEachIndexed { index, step ->
                    val isCompleted = index <= currentStepIndex
                    val isCurrent = index == currentStepIndex
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = step.getDisplayName(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCompleted) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Сумма заказа:", style = MaterialTheme.typography.titleLarge)
            Text(PriceFormatter.format(order.totalPrice), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (statusEnum == OrderStatus.PENDING) {
            OutlinedButton(
                onClick = { showCancelDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Отменить заказ")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Вернуться в меню")
        }
    }
}
