// file: app/src/main/java/ru/beyedono/app/BeyedonoApp.kt
package ru.beyedono.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class BeyedonoApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().userAgentValue = packageName
        
        // СОЗДАЕМ КАНАЛ УВЕДОМЛЕНИЙ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "beyedono_orders",
                "Статусы заказов",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о статусе ваших заказов"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.25).build() }
            .diskCache { DiskCache.Builder().directory(File(cacheDir, "image_cache")).maxSizeBytes(50 * 1024 * 1024).build() }
            .crossfade(true)
            .build()
    }
}
