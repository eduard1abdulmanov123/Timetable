package com.abdulmanov.schedule.notifications

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ServiceNotification {

    @Async
    fun sendNotifications(title: String = "", message: String = "", tokens: List<String> = emptyList()) {
        val notification = Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build()

        val sendMessage = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(tokens)
                .build()

        FirebaseMessaging.getInstance()
                .sendMulticast(sendMessage)
    }
}