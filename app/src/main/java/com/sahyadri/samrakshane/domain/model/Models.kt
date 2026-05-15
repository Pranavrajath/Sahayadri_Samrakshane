package com.sahyadri.samrakshane.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EcologicalAlert(
    val id: String = "",
    val alertType: AlertType = AlertType.FOREST_FIRE,
    val title: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val photoUri: String = "",
    val photoUrl: String = "",  // Firebase storage URL
    val reporterName: String = "Anonymous",
    val reporterPhone: String = "",
    val status: AlertStatus = AlertStatus.REPORTED,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val localId: Long = 0L
)

@Serializable
enum class AlertType(
    val displayName: String,
    val emoji: String,
    val description: String
) {
    FOREST_FIRE(
        displayName = "Forest Fire",
        emoji = "🔥",
        description = "Active fire or smoke spotted in forest area"
    ),
    LANDSLIDE(
        displayName = "Landslide",
        emoji = "⛰️",
        description = "Soil/rock movement, blocked roads or debris"
    ),
    ILLEGAL_LOGGING(
        displayName = "Illegal Tree Cutting",
        emoji = "🪓",
        description = "Unauthorized felling of trees or deforestation"
    ),
    WILDLIFE_SIGHTING(
        displayName = "Wildlife Sighting",
        emoji = "🐆",
        description = "Rare or endangered species spotted"
    ),
    POACHING(
        displayName = "Poaching Activity",
        emoji = "⚠️",
        description = "Illegal hunting or wildlife trade detected"
    ),
    WATER_POLLUTION(
        displayName = "Water Pollution",
        emoji = "💧",
        description = "River/stream contamination or dumping"
    )
}

@Serializable
enum class AlertStatus(val displayName: String, val color: Long) {
    REPORTED(displayName = "Reported", color = 0xFFF59E0B),
    VERIFIED(displayName = "Verified", color = 0xFF3B82F6),
    TEAM_DISPATCHED(displayName = "Team Dispatched", color = 0xFF10B981),
    RESOLVED(displayName = "Resolved", color = 0xFF6B7280)
}

@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val bearing: Float = 0f,
    val speed: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class EcoTip(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val category: TipCategory
)

enum class TipCategory {
    FIRE_SAFETY, WILDLIFE, TREKKING, REPORTING, CONSERVATION
}
