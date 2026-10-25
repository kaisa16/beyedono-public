// file: app/src/main/java/ru/beyedono/app/ui/screen/favorites/FavoriteScreen.kt
package ru.beyedono.app.ui.screen.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.beyedono.app.ui.components.EmptyState
import ru.beyedono.app.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.products.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Favorite,
                title = "Нет избранных блюд",
                description = "Нажимайте на сердечко в меню, чтобы сохранить блюдо здесь",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                items(uiState.products) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = true,
                        onClick = { onProductClick(product.id) },
                        onAddToCart = { onProductClick(product.id) },
                        onFavoriteToggle = { viewModel.removeFavorite(product.id) }
                    )
                }
            }
        }
    }
}
