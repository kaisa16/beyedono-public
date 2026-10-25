// file: app/src/main/java/ru/beyedono/app/MainActivity.kt
package ru.beyedono.app

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.UserDao
import ru.beyedono.app.data.repository.AuthRepository
import ru.beyedono.app.ui.navigation.BeyedonoNavHost
import ru.beyedono.app.ui.screen.offline.NoInternetScreen
import ru.beyedono.app.ui.screen.settings.SettingsViewModel
import ru.beyedono.app.ui.theme.BeyedonoTheme
import ru.beyedono.app.util.NetworkUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userDao: UserDao
    @Inject lateinit var authRepository: AuthRepository

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ОБНОВЛЯЕМ FCM ТОКЕН ПРИ ЗАПУСКЕ
        lifecycleScope.launch {
            val user = userDao.getUser().first()
            if (user != null) {
                authRepository.updateFcmToken(user.email)
            }
        }

        setContent {
            val uiState by settingsViewModel.uiState.collectAsState()
            val systemDarkTheme = isSystemInDarkTheme()

            val isDark = when (uiState.theme) {
                "dark" -> true
                "light" -> false
                else -> systemDarkTheme // "system" по умолчанию
            }

            val context = LocalContext.current
            var isConnected by remember { mutableStateOf(NetworkUtils.isNetworkAvailable(context)) }

            DisposableEffect(context) {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) { isConnected = true }
                    override fun onLost(network: Network) { isConnected = false }
                }
                cm.registerDefaultNetworkCallback(callback)
                onDispose { cm.unregisterNetworkCallback(callback) }
            }

            BeyedonoTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isConnected) {
                        NoInternetScreen()
                    } else {
                        LaunchedEffect(Unit) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        BeyedonoNavHost()
                    }
                }
            }
        }
    }
}
