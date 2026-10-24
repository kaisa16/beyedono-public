package ru.beyedono.app.ui.screen.checkout

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressPickerScreen(
    onNavigateBack: () -> Unit,
    onAddressSelected: (String, Double, Double) -> Unit,
    viewModel: AddressPickerViewModel = hiltViewModel()
) {
    var mapView: MapView? by remember { mutableStateOf(null) }
    var myLocationOverlay: MyLocationNewOverlay? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val startPoint = GeoPoint(62.0355, 129.6755)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            @SuppressLint("MissingPermission")
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                val myPoint = GeoPoint(location.latitude, location.longitude)
                mapView?.controller?.animateTo(myPoint)
                mapView?.controller?.setZoom(16.0)
                myLocationOverlay?.enableMyLocation()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор адреса (OSM)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setMultiTouchControls(true)
                        setTileSource(TileSourceFactory.MAPNIK)
                        controller.setZoom(15.0)
                        controller.setCenter(startPoint)
                        
                        // Добавляем синюю точку "Где я"
                        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this).apply {
                            enableMyLocation()
                            enableFollowLocation()
                        }
                        overlays.add(myLocationOverlay)
                        
                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Иконка в центре экрана (выбираемый адрес)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Маркер",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .offset(y = (-24).dp) // Сдвигаем вверх, чтобы острие смотрело точно в центр
            )

            // Кнопка "Где я"
            FloatingActionButton(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Моё местоположение", tint = Color.White)
            }

            // Кнопка выбрать
            Button(
                onClick = {
                    mapView?.let { view ->
                        val center = view.mapCenter as GeoPoint
                        scope.launch {
                            val addressText = viewModel.getAddressFromCoords(center.latitude, center.longitude)
                            onAddressSelected(addressText, center.latitude, center.longitude)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text("Выбрать этот адрес", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
