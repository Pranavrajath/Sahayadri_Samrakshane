package com.sahyadri.samrakshane.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sahyadri.samrakshane.domain.model.AlertType
import com.sahyadri.samrakshane.ui.components.SyncStatusBanner
import com.sahyadri.samrakshane.ui.theme.*
import com.sahyadri.samrakshane.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onReportAlert: () -> Unit,
    onViewAlerts: () -> Unit,
    onViewMap: () -> Unit,
    onViewEducation: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Animated forest canopy gradient
    val infiniteTransition = rememberInfiniteTransition(label = "canopy")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkForest,
                        DeepCanopy,
                        SurfaceDark
                    )
                )
            )
    ) {
        // Background leaf pattern overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = ForestGreen700.copy(alpha = animatedAlpha * 0.15f),
                radius = size.width * 0.6f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.08f)
            )
            drawCircle(
                color = ForestGreen600.copy(alpha = animatedAlpha * 0.1f),
                radius = size.width * 0.4f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.25f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
        ) {
            // ── Header ──────────────────────────────────────────────────────
            HomeHeader(uiState.unsyncedCount)

            Spacer(modifier = Modifier.height(8.dp))

            // ── Sync Banner ─────────────────────────────────────────────────
            if (uiState.unsyncedCount > 0) {
                SyncStatusBanner(
                    count = uiState.unsyncedCount,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── SOS Report Button ───────────────────────────────────────────
            ReportSOSButton(
                onClick = onReportAlert,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Quick Alert Types ───────────────────────────────────────────
            Text(
                text = "Alert Categories",
                style = MaterialTheme.typography.titleMedium,
                color = ForestGreen300,
                modifier = Modifier.padding(horizontal = 20.dp),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AlertType.values()) { alertType ->
                    AlertTypeChip(alertType = alertType, onClick = onReportAlert)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Quick Action Grid ───────────────────────────────────────────
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = ForestGreen300,
                modifier = Modifier.padding(horizontal = 20.dp),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Outlined.List,
                    title = "My\nAlerts",
                    subtitle = "${uiState.totalAlerts} total",
                    color = WaterBlue,
                    onClick = onViewAlerts,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Outlined.Map,
                    title = "Forest\nMap",
                    subtitle = "Live view",
                    color = ForestGreen500,
                    onClick = onViewMap,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Outlined.MenuBook,
                    title = "Eco\nGuide",
                    subtitle = "Tips & rules",
                    color = MossGreen,
                    onClick = onViewEducation,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Outlined.Insights,
                    title = "Impact\nStats",
                    subtitle = "Community",
                    color = EarthBrown500,
                    onClick = onViewAlerts,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Recent Alerts ────────────────────────────────────────────────
            if (uiState.recentAlerts.isNotEmpty()) {
                Text(
                    text = "Recent Reports",
                    style = MaterialTheme.typography.titleMedium,
                    color = ForestGreen300,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                uiState.recentAlerts.take(3).forEach { alert ->
                    RecentAlertItem(
                        alert = alert,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            // ── Footer quote ─────────────────────────────────────────────────
            Spacer(modifier = Modifier.height(24.dp))
            ForestQuote()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HomeHeader(unsyncedCount: Int) {
    val today = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH).format(Date())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "🌿 Sahyadri",
                style = MaterialTheme.typography.headlineMedium,
                color = ForestGreen300,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Samrakshane",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = today,
                style = MaterialTheme.typography.bodySmall,
                color = SageGreen
            )
        }

        // Offline indicator
        if (unsyncedCount > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AlertAmber.copy(alpha = 0.15f))
                    .border(1.dp, AlertAmber.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.CloudOff,
                        contentDescription = null,
                        tint = AlertAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$unsyncedCount pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = AlertAmber
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(StatusDispatched.copy(alpha = 0.15f))
                    .border(1.dp, StatusDispatched.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.CloudDone,
                        contentDescription = null,
                        tint = StatusDispatched,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Synced",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusDispatched
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportSOSButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FireOrange
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Filled.Campaign,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = "REPORT ECOLOGICAL ALERT",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap to capture photo + GPS location",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun AlertTypeChip(alertType: AlertType, onClick: () -> Unit) {
    val chipColor = when (alertType) {
        AlertType.FOREST_FIRE -> FireOrange
        AlertType.LANDSLIDE -> LandslideSlate
        AlertType.ILLEGAL_LOGGING -> EarthBrown300
        AlertType.WILDLIFE_SIGHTING -> WildlifeTeal
        AlertType.POACHING -> FireRed
        AlertType.WATER_POLLUTION -> WaterBlue
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = chipColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, chipColor.copy(alpha = 0.35f)),
        modifier = Modifier.height(80.dp).width(110.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = alertType.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alertType.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = chipColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SageGreen
                )
            }
        }
    }
}

@Composable
private fun RecentAlertItem(
    alert: com.sahyadri.samrakshane.domain.model.EcologicalAlert,
    modifier: Modifier = Modifier
) {
    val statusColor = Color(alert.status.color)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = BorderStroke(1.dp, ForestGreen700.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = alert.alertType.emoji, fontSize = 28.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.alertType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "%.4f°, %.4f°".format(alert.latitude, alert.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    color = SageGreen
                )
            }
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
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ForestQuote() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(ForestGreen800.copy(alpha = 0.5f), MossGreen.copy(alpha = 0.3f))
                )
            )
            .border(1.dp, ForestGreen600.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🌳", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"The Western Ghats — cradle of rivers, sanctuary of life. Guard it with every step.\"",
                style = MaterialTheme.typography.bodyMedium,
                color = ForestGreen200,
                textAlign = TextAlign.Center,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
