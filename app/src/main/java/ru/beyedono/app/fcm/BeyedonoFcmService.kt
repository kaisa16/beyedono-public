// file: app/src/main/java/ru/beyedono/app/fcm/BeyedonoFcmService.kt
package ru.beyedono.app.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BeyedonoFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Бэйэ Дьоно"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Обновление статуса заказа"
        val orderId = remoteMessage.data["order_id"]

        notificationHelper.showNotification(title, body, orderId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}