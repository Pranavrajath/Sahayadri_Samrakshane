package com.sahyadri.samrakshane.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.sahyadri.samrakshane.domain.model.LocationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    val currentLocation: StateFlow<LocationData?> = _currentLocation

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): LocationData? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.toLocationData()
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(): Flow<LocationData> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L // 2 second interval
        ).apply {
            setMinUpdateIntervalMillis(1000L)
            setMinUpdateDistanceMeters(1f)
        }.build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val data = location.toLocationData()
                    _currentLocation.value = data
                    trySend(data)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentPreciseLocation(): LocationData? {
        return try {
            val cts = com.google.android.gms.tasks.CancellationTokenSource()
            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .await()
            location?.toLocationData()
        } catch (e: Exception) {
            getLastKnownLocation()
        }
    }
}

private fun Location.toLocationData() = LocationData(
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    accuracy = accuracy,
    bearing = bearing,
    speed = speed,
    timestamp = time
)

fun LocationData.toFormattedString(): String {
    val latDir = if (latitude >= 0) "N" else "S"
    val lonDir = if (longitude >= 0) "E" else "W"
    return "%.6f°%s, %.6f°%s".format(
        Math.abs(latitude), latDir,
        Math.abs(longitude), lonDir
    )
}

fun LocationData.toDegreesMinutesSeconds(): String {
    fun decimalToDMS(decimal: Double): String {
        val degrees = decimal.toInt()
        val minutesDecimal = (decimal - degrees) * 60
        val minutes = minutesDecimal.toInt()
        val seconds = (minutesDecimal - minutes) * 60
        return "%d°%d'%.1f\"".format(degrees, minutes, seconds)
    }
    val latDir = if (latitude >= 0) "N" else "S"
    val lonDir = if (longitude >= 0) "E" else "W"
    return "${decimalToDMS(Math.abs(latitude))}$latDir  ${decimalToDMS(Math.abs(longitude))}$lonDir"
}
