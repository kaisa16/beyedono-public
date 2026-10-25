// file: app/src/main/java/ru/beyedono/app/fcm/BeyedonoFcmService.kt
package ru.beyedono.app.fcm

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.beyedono.app.data.repository.AuthRepository
import javax.inject.Inject

@AndroidEntryPoint
class BeyedonoFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Бэйэ Дьоно"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Обновление статуса заказа"
        val orderId = remoteMessage.data["order_id"]

        notificationHelper.showNotification(title, body, orderId)
    }

    // САМОЕ ВАЖНОЕ: Ловим обновление токена и отправляем на сервер
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            // Запускаем в фоне, чтобы не блокировать главный поток
            CoroutineScope(Dispatchers.IO).launch {
                authRepository.sendFcmTokenToServer(email, token)
            }
        }
    }
}
