package com.sahyadri.samrakshane.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahyadri.samrakshane.data.repository.AlertRepository
import com.sahyadri.samrakshane.domain.model.*
import com.sahyadri.samrakshane.utils.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Home ViewModel ─────────────────────────────────────────────────────────────

data class HomeUiState(
    val unsyncedCount: Int = 0,
    val totalAlerts: Int = 0,
    val recentAlerts: List<EcologicalAlert> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AlertRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.unsyncedCount,
        repository.allAlerts
    ) { unsyncedCount, alerts ->
        HomeUiState(
            unsyncedCount = unsyncedCount,
            totalAlerts = alerts.size,
            recentAlerts = alerts.take(5)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}

// ── Report Alert ViewModel ────────────────────────────────────────────────────

data class ReportAlertUiState(
    val selectedAlertType: AlertType = AlertType.FOREST_FIRE,
    val photoUri: String? = null,
    val locationData: LocationData? = null,
    val description: String = "",
    val reporterName: String = "",
    val reporterPhone: String = "",
    val isLocating: Boolean = true,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ReportAlertViewModel @Inject constructor(
    private val repository: AlertRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportAlertUiState())
    val uiState: StateFlow<ReportAlertUiState> = _uiState.asStateFlow()

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationManager.startLocationUpdates().collect { location ->
                _uiState.update { it.copy(locationData = location, isLocating = false) }
            }
        }
    }

    fun setAlertType(type: AlertType) = _uiState.update { it.copy(selectedAlertType = type) }
    fun setDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun setReporterName(name: String) = _uiState.update { it.copy(reporterName = name) }
    fun setReporterPhone(phone: String) = _uiState.update { it.copy(reporterPhone = phone) }
    fun setPhotoUri(uri: String) = _uiState.update { it.copy(photoUri = uri) }

    fun refreshLocation() {
        _uiState.update { it.copy(isLocating = true) }
        viewModelScope.launch {
            val location = locationManager.getCurrentPreciseLocation()
            _uiState.update { it.copy(locationData = location, isLocating = false) }
        }
    }

    fun submitAlert() {
        val state = _uiState.value
        val location = state.locationData ?: return

        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val alert = EcologicalAlert(
                alertType = state.selectedAlertType,
                title = state.selectedAlertType.displayName,
                description = state.description,
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude,
                accuracy = location.accuracy,
                photoUri = state.photoUri ?: "",
                reporterName = state.reporterName.ifBlank { "Anonymous" },
                reporterPhone = state.reporterPhone
            )

            val result = repository.submitAlert(alert)
            if (result.isSuccess) {
                _uiState.update { it.copy(isSubmitting = false, submitSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "Failed to save alert. Please try again."
                    )
                }
            }
        }
    }
}

// ── Alerts List ViewModel ─────────────────────────────────────────────────────

@HiltViewModel
class AlertsListViewModel @Inject constructor(
    repository: AlertRepository
) : ViewModel() {
    val alerts: StateFlow<List<EcologicalAlert>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

// ── Camera ViewModel ──────────────────────────────────────────────────────────

data class CameraUiState(
    val locationData: LocationData? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            locationManager.startLocationUpdates().collect { location ->
                _uiState.update { it.copy(locationData = location) }
            }
        }
    }
}
