// file: app/src/main/java/ru/beyedono/app/ui/components/ProductCard.kt
package ru.beyedono.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.beyedono.app.R
import ru.beyedono.app.domain.model.Product
import ru.beyedono.app.domain.util.PriceFormatter

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.photo),
                    error = painterResource(id = R.drawable.photo),
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                
                // Кнопка Избранное (Сделали заметной)
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape) // Темный полупрозрачный круг
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "В избранное",
                        tint = if (isFavorite) Color.Red else Color.White // Красный или Белый
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = PriceFormatter.format(product.price),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onAddToCart, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))) {
                        Icon(Icons.Default.Add, contentDescription = "В корзину", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
