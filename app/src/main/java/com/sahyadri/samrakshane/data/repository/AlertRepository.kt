package com.sahyadri.samrakshane.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sahyadri.samrakshane.data.local.AlertDao
import com.sahyadri.samrakshane.data.local.toDomain
import com.sahyadri.samrakshane.data.local.toEntity
import com.sahyadri.samrakshane.domain.model.AlertStatus
import com.sahyadri.samrakshane.domain.model.EcologicalAlert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepository @Inject constructor(
    private val alertDao: AlertDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    val allAlerts: Flow<List<EcologicalAlert>> = alertDao.getAllAlerts()
        .map { entities -> entities.map { it.toDomain() } }

    val unsyncedCount: Flow<Int> = alertDao.getUnsyncedCount()

    /**
     * Saves alert locally first (offline-first), then attempts Firebase sync.
     */
    suspend fun submitAlert(alert: EcologicalAlert): Result<Long> {
        return try {
            // 1. Save to local Room DB immediately
            val localId = alertDao.insertAlert(alert.toEntity())
            val alertWithId = alert.copy(localId = localId)

            // 2. Try to upload photo & sync to Firebase in background
            trySyncToFirebase(alertWithId)

            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Syncs all pending offline alerts to Firebase.
     */
    suspend fun syncPendingAlerts() {
        val pending = alertDao.getUnsyncedAlerts()
        pending.forEach { entity ->
            trySyncToFirebase(entity.toDomain())
        }
    }

    private suspend fun trySyncToFirebase(alert: EcologicalAlert) {
        try {
            // Upload photo to Firebase Storage
            val photoUrl = if (alert.photoUri.isNotEmpty()) {
                uploadPhoto(alert.localId, alert.photoUri)
            } else ""

            // Upload alert data to Firestore
            val alertData = hashMapOf(
                "alertType" to alert.alertType.name,
                "title" to alert.title,
                "description" to alert.description,
                "latitude" to alert.latitude,
                "longitude" to alert.longitude,
                "altitude" to alert.altitude,
                "accuracy" to alert.accuracy,
                "photoUrl" to photoUrl,
                "reporterName" to alert.reporterName,
                "reporterPhone" to alert.reporterPhone,
                "status" to AlertStatus.REPORTED.name,
                "timestamp" to alert.timestamp
            )

            val docRef = firestore.collection("ecological_alerts")
                .add(alertData)
                .await()

            // Mark as synced in local DB
            alertDao.markAsSynced(alert.localId, docRef.id)

        } catch (e: Exception) {
            // Silent fail — WorkManager will retry when connectivity returns
        }
    }

    private suspend fun uploadPhoto(localId: Long, photoUri: String): String {
        return try {
            val file = File(photoUri)
            if (!file.exists()) return ""

            val photoRef = storage.reference
                .child("alerts/${localId}_${UUID.randomUUID()}.jpg")

            photoRef.putFile(Uri.fromFile(file)).await()
            photoRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun getAlertById(id: Long): EcologicalAlert? {
        return alertDao.getAlertById(id)?.toDomain()
    }

    suspend fun deleteAlert(alert: EcologicalAlert) {
        alertDao.deleteAlert(alert.toEntity())
    }
}
