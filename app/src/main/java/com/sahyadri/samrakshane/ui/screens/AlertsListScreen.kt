package com.sahyadri.samrakshane.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sahyadri.samrakshane.domain.model.EcologicalAlert
import com.sahyadri.samrakshane.ui.theme.*
import com.sahyadri.samrakshane.ui.viewmodel.AlertsListViewModel
import com.sahyadri.samrakshane.utils.toFormattedString
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsListScreen(
    onNavigateBack: () -> Unit,
    onAlertClick: (Long) -> Unit,
    viewModel: AlertsListViewModel = hiltViewModel()
) {
    val alerts by viewModel.alerts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Reports", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DarkForest, DeepCanopy, SurfaceDark)))
                .padding(padding)
        ) {
            if (alerts.isEmpty()) {
                EmptyAlertsState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alerts, key = { it.localId }) { alert ->
                        AlertCard(alert = alert, onClick = { onAlertClick(alert.localId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: EcologicalAlert, onClick: () -> Unit) {
    val statusColor = Color(alert.status.color)
    val typeColor = when (alert.alertType.name) {
        "FOREST_FIRE" -> FireOrange
        "LANDSLIDE" -> LandslideSlate
        "ILLEGAL_LOGGING" -> EarthBrown300
        "WILDLIFE_SIGHTING" -> WildlifeTeal
        "POACHING" -> FireRed
        else -> WaterBlue
    }
    val dateString = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.ENGLISH)
        .format(Date(alert.timestamp))

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = BorderStroke(1.dp, typeColor.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Photo banner if available
            if (alert.photoUri.isNotEmpty() || alert.photoUrl.isNotEmpty()) {
                AsyncImage(
                    model = if (alert.photoUrl.isNotEmpty()) alert.photoUrl else alert.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = alert.alertType.emoji, fontSize = 32.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alert.alertType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, tint = SageGreen, modifier = Modifier.size(12.dp))
                        Text(
                            text = "%.4f°, %.4f°".format(alert.latitude, alert.longitude),
                            style = MaterialTheme.typography.bodySmall,
                            color = SageGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = ForestGreen300
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = alert.status.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    if (!alert.isSynced) {
                        Icon(Icons.Filled.CloudOff, null, tint = AlertAmber, modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Filled.CloudDone, null, tint = StatusDispatched, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (alert.description.isNotEmpty()) {
                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ForestGreen200,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 14.dp),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun EmptyAlertsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🌿", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No alerts reported yet",
            style = MaterialTheme.typography.titleMedium,
            color = ForestGreen300,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Be the first forest sentinel in your area",
            style = MaterialTheme.typography.bodyMedium,
            color = SageGreen
        )
    }
}
