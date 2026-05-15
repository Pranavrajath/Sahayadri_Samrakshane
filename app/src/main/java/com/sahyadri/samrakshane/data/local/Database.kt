package com.sahyadri.samrakshane.data.local

import androidx.room.*
import com.sahyadri.samrakshane.domain.model.AlertStatus
import com.sahyadri.samrakshane.domain.model.AlertType
import com.sahyadri.samrakshane.domain.model.EcologicalAlert
import kotlinx.coroutines.flow.Flow

// ─── Entity ───────────────────────────────────────────────────────────────────

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val remoteId: String = "",
    val alertType: String,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val photoUri: String,
    val photoUrl: String,
    val reporterName: String,
    val reporterPhone: String,
    val status: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)

// ─── Mappers ──────────────────────────────────────────────────────────────────

fun AlertEntity.toDomain() = EcologicalAlert(
    id = remoteId,
    alertType = AlertType.valueOf(alertType),
    title = title,
    description = description,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    accuracy = accuracy,
    photoUri = photoUri,
    photoUrl = photoUrl,
    reporterName = reporterName,
    reporterPhone = reporterPhone,
    status = AlertStatus.valueOf(status),
    timestamp = timestamp,
    isSynced = isSynced,
    localId = localId
)

fun EcologicalAlert.toEntity() = AlertEntity(
    localId = localId,
    remoteId = id,
    alertType = alertType.name,
    title = title,
    description = description,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    accuracy = accuracy,
    photoUri = photoUri,
    photoUrl = photoUrl,
    reporterName = reporterName,
    reporterPhone = reporterPhone,
    status = status.name,
    timestamp = timestamp,
    isSynced = isSynced
)

// ─── DAO ──────────────────────────────────────────────────────────────────────

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity): Long

    @Update
    suspend fun updateAlert(alert: AlertEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE isSynced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedAlerts(): List<AlertEntity>

    @Query("SELECT * FROM alerts WHERE localId = :id")
    suspend fun getAlertById(id: Long): AlertEntity?

    @Query("UPDATE alerts SET isSynced = 1, remoteId = :remoteId WHERE localId = :localId")
    suspend fun markAsSynced(localId: Long, remoteId: String)

    @Query("SELECT COUNT(*) FROM alerts WHERE isSynced = 0")
    fun getUnsyncedCount(): Flow<Int>

    @Query("DELETE FROM alerts WHERE localId = :id")
    suspend fun deleteAlertById(id: Long)
}

// ─── Database ─────────────────────────────────────────────────────────────────

@Database(
    entities = [AlertEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SahyadriDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao
}
