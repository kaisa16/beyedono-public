// file: app/src/main/java/ru/beyedono/app/util/DeepLinkHandler.kt
package ru.beyedono.app.util

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkHandler @Inject constructor() {

    fun handleDeepLink(uri: Uri?): DeepLinkAction? {
        if (uri == null) return null
        
        return when {
            uri.scheme == "beyedono" && uri.host == "payment" -> {
                DeepLinkAction.PaymentReturn
            }
            uri.scheme == "beyedono" && uri.host == "order" -> {
                val orderId = uri.lastPathSegment
                if (orderId != null) DeepLinkAction.OpenOrder(orderId) else null
            }
            else -> null
        }
    }
}

sealed class DeepLinkAction {
    data object PaymentReturn : DeepLinkAction()
    data class OpenOrder(val orderId: String) : DeepLinkAction()
}