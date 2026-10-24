// file: app/src/main/java/ru/beyedono/app/ui/screen/settings/SettingsScreen.kt
package ru.beyedono.app.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showPinDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissPinDialog,
            title = { Text("Вход для сотрудников") },
            text = {
                Column {
                    Text("Введите PIN-код для доступа к панели кафе:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.pinInput,
                        onValueChange = viewModel::onPinChange,
                        label = { Text("PIN") },
                        isError = uiState.pinError,
                        supportingText = { if (uiState.pinError) Text("Неверный код") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.verifyPin(onNavigateToAdmin) }) { Text("Войти") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissPinDialog) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Push-уведомления", style = MaterialTheme.typography.bodyLarge)
                    Text("Статусы заказов и акции", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Switch(checked = uiState.isPushEnabled, onCheckedChange = viewModel::togglePush)
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Тёмная тема", style = MaterialTheme.typography.bodyLarge)
                    Text("Включать ночью", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Switch(checked = uiState.isDarkTheme, onCheckedChange = viewModel::toggleDarkTheme)
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Язык", style = MaterialTheme.typography.bodyLarge)
                    Text(uiState.language, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth().clickable { viewModel.onAdminClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("О приложении", style = MaterialTheme.typography.bodyLarge)
                    Text("Версия 1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(16.dp))
            Text("Тел: 8 914 102 29 08", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
