// file: app/src/main/java/ru/beyedono/app/ui/screen/admin_menu/AdminMenuScreen.kt
package ru.beyedono.app.ui.screen.admin_menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.domain.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminMenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Удалить товар?") },
            text = { Text("Вы уверены?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog?.let { viewModel.deleteProduct(it) }; showDeleteDialog = null }) { 
                    Text("Удалить", color = MaterialTheme.colorScheme.error) 
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Отмена") } }
        )
    }

    // ДИАЛОГ УПРАВЛЕНИЯ КАТЕГОРИЯМИ
    if (uiState.showCategoryDialog) {
        var catName by remember { mutableStateOf(uiState.editingCategory ?: "") }
        AlertDialog(
            onDismissRequest = { viewModel.closeCategoryDialog() },
            title = { Text(if (uiState.editingCategory == null) "Новая категория" else "Переименовать") },
            text = {
                Column {
                    OutlinedTextField(value = catName, onValueChange = { catName = it }, label = { Text("Название категории") }, modifier = Modifier.fillMaxWidth())
                    uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)) }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.saveCategory(catName) }) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { viewModel.closeCategoryDialog() }) { Text("Отмена") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление меню") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    // КНОПКА УПРАВЛЕНИЯ КАТЕГОРИЯМИ
                    IconButton(onClick = { viewModel.openCategoryDialog() }) {
                        Icon(Icons.Default.List, contentDescription = "Категории")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openAddDialog() }) { Icon(Icons.Default.Add, null) }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.products, key = { it.id }) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${product.categoryId} | ${PriceFormatter.format(product.price)}", style = MaterialTheme.typography.bodySmall)
                                if (!product.isAvailable) Text("В СТОП-ЛИСТЕ", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.openEditDialog(product) }) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                            IconButton(onClick = { showDeleteDialog = product.id }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showDialog) {
        var name by remember { mutableStateOf(uiState.editingProduct?.name ?: "") }
        var category by remember { mutableStateOf(uiState.editingProduct?.categoryId ?: uiState.availableCategories.firstOrNull() ?: "") }
        var price by remember { mutableStateOf(uiState.editingProduct?.price?.toString() ?: "") }
        var description by remember { mutableStateOf(uiState.editingProduct?.description ?: "") }
        var imageUrl by remember { mutableStateOf(uiState.editingProduct?.imageUrl ?: "") }
        var isCategoryExpanded by remember { mutableStateOf(false) }

        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? -> uri?.let { viewModel.uploadImage(it, context) { url -> imageUrl = url } } }

        AlertDialog(
            onDismissRequest = { viewModel.closeDialog() },
            title = { Text(if (uiState.editingProduct == null) "Добавить товар" else "Редактировать") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ExposedDropdownMenuBox(
                            expanded = isCategoryExpanded,
                            onExpandedChange = { isCategoryExpanded = !isCategoryExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = category, onValueChange = {}, readOnly = true, label = { Text("Категория") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isCategoryExpanded, onDismissRequest = { isCategoryExpanded = false }
                            ) {
                                uiState.availableCategories.forEach { cat ->
                                    DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; isCategoryExpanded = false })
                                }
                            }
                        }
                        IconButton(onClick = { viewModel.openCategoryDialog() }) {
                            Icon(Icons.Default.Add, contentDescription = "Новая категория", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Цена (₽)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(modifier = Modifier.fillMaxWidth().clickable { imagePickerLauncher.launch("image/*") }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (imageUrl.isBlank()) "Выбрать фото" else "Фото загружено", color = MaterialTheme.colorScheme.primary)
                        if (uiState.isUploading) CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(start = 8.dp))
                    }

                    uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.saveProduct(name, category, price.toDoubleOrNull() ?: 0.0, description, imageUrl) }, enabled = !uiState.isUploading) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { viewModel.closeDialog() }) { Text("Отмена") } }
        )
    }
}
