// file: app/src/main/java/ru/beyedono/app/ui/screen/profile/ProfileScreen.kt
package ru.beyedono.app.ui.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить аккаунт?") },
            text = { Text("Все ваши данные и история заказов будут безвозвратно удалены.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; viewModel.deleteAccount(onLogout) }) { Text("Удалить") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") } }
        )
    }

    if (showEditDialog && uiState.user != null) {
        var editName by remember { mutableStateOf(uiState.user!!.name) }
        var editPhone by remember { mutableStateOf(uiState.user!!.phone ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Редактировать профиль") },
            text = {
                Column {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("Телефон") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.updateProfile(editName, editPhone) { showEditDialog = false } }) { Text("Сохранить") } },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Отмена") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally) { CircularProgressIndicator() }
        } else {
            val user = uiState.user
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (user?.avatarUrl != null) {
                        AsyncImage(model = user.avatarUrl, contentDescription = "Аватар", contentScale = ContentScale.Crop, modifier = Modifier.size(72.dp).clip(CircleShape))
                    } else {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Аватар", modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(user?.name ?: "Гость", style = MaterialTheme.typography.titleLarge)
                        Text(user?.email ?: "Не авторизован", style = MaterialTheme.typography.bodyMedium)
                        Text(user?.phone ?: "Телефон не указан", style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = { showEditDialog = true }) { Icon(Icons.Default.Edit, contentDescription = "Редактировать", tint = MaterialTheme.colorScheme.primary) }
                }

                HorizontalDivider()
                ProfileMenuItem("История заказов", Icons.Default.History, onNavigateToHistory)
                ProfileMenuItem("Мои адреса", Icons.Default.LocationOn, onNavigateToAddresses)
                ProfileMenuItem("Избранное", Icons.Default.Favorite, onNavigateToFavorites)
                ProfileMenuItem("О кафе", Icons.Default.Info, onNavigateToAbout)
                ProfileMenuItem("Политика конфиденциальности", Icons.Default.PrivacyTip, onNavigateToPrivacy)
                ProfileMenuItem("Настройки", Icons.Default.Settings, onNavigateToSettings)

                HorizontalDivider()
                ProfileMenuItem("Выйти из аккаунта", Icons.Default.Logout, { viewModel.logout(onLogout) }, color = MaterialTheme.colorScheme.error)
                ProfileMenuItem("Удалить аккаунт", Icons.Default.Logout, { showDeleteDialog = true }, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = color)
    }
}