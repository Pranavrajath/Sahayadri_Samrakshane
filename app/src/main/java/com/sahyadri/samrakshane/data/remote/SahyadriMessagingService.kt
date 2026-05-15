package com.sahyadri.samrakshane.data.remote

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SahyadriMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle FCM push notifications (alert status updates from forest dept)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Update token in Firestore for push notifications
    }
}
