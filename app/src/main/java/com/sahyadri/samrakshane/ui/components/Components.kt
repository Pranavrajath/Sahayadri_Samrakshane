package com.sahyadri.samrakshane.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahyadri.samrakshane.domain.model.LocationData
import com.sahyadri.samrakshane.ui.theme.*
import com.sahyadri.samrakshane.utils.toDegreesMinutesSeconds
import com.sahyadri.samrakshane.utils.toFormattedString

@Composable
fun LocationCard(
    locationData: LocationData?,
    isLocating: Boolean,
    onRefreshLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulse"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = BorderStroke(
            1.5.dp,
            if (locationData != null) ForestGreen500.copy(alpha = 0.5f)
            else AlertAmber.copy(alpha = pulseAlpha)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (locationData != null) ForestGreen500.copy(alpha = 0.15f)
                            else AlertAmber.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLocating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AlertAmber,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Filled.GpsFixed,
                            null,
                            tint = if (locationData != null) ForestGreen400 else AlertAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (locationData != null) "GPS Lock Acquired" else "Acquiring GPS...",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (locationData != null) ForestGreen300 else AlertAmber,
                        fontWeight = FontWeight.Bold
                    )
                    if (locationData != null) {
                        Text(
                            text = "Accuracy: ±%.1fm".format(locationData.accuracy),
                            style = MaterialTheme.typography.bodySmall,
                            color = SageGreen
                        )
                    }
                }

                IconButton(onClick = onRefreshLocation, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Filled.Refresh,
                        "Refresh",
                        tint = SageGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (locationData != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = ForestGreen700.copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Coordinate grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoordItem("LAT", "%.6f°".format(locationData.latitude), WaterBlue, Modifier.weight(1f))
                    CoordItem("LON", "%.6f°".format(locationData.longitude), WaterBlue, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoordItem("ALT", "%.0fm".format(locationData.altitude), ForestGreen400, Modifier.weight(1f))
                    CoordItem("ACC", "±%.0fm".format(locationData.accuracy), SageGreen, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ForestGreen800.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.PinDrop, null, tint = ForestGreen400, modifier = Modifier.size(14.dp))
                        Text(
                            text = locationData.toFormattedString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = ForestGreen200,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordItem(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
            Text(
                value,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SyncStatusBanner(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(AlertAmber.copy(alpha = 0.15f), EarthBrown500.copy(alpha = 0.1f))
                )
            )
            .border(1.dp, AlertAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Filled.CloudOff, null, tint = AlertAmber, modifier = Modifier.size(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$count alert${if (count > 1) "s" else ""} pending sync",
                    style = MaterialTheme.typography.labelMedium,
                    color = AlertAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Will sync automatically when connected",
                    style = MaterialTheme.typography.bodySmall,
                    color = AlertAmber.copy(alpha = 0.7f)
                )
            }
        }
    }
}
