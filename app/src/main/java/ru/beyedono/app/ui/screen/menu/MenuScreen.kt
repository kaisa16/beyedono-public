// file: app/src/main/java/ru/beyedono/app/ui/screen/menu/MenuScreen.kt
package ru.beyedono.app.ui.screen.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Бэйэ Дьоно") },
                actions = {
                    IconButton(onClick = onProfileClick) { Icon(Icons.Default.Person, contentDescription = "Профиль") }
                    IconButton(onClick = onCartClick) { Icon(Icons.Default.ShoppingCart, contentDescription = "Корзина") }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по меню") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(category) }
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = uiState.favoriteIds.contains(product.id), // <--- ТЕПЕРЬ ПЕРЕДАЕМ
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { onProductClick(product.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(product.id) } // <--- И ЭТО
                        )
                    }
                }
            }
        }
    }
}
