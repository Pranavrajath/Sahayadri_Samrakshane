package com.sahyadri.samrakshane.ui.screens

import androidx.compose.animation.*

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahyadri.samrakshane.ui.theme.*
import kotlinx.coroutines.delay

// ── Splash Screen ─────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(1500)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(ForestGreen800, DarkForest, Color.Black),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            Text("🌿", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "SAHYADRI",
                style = MaterialTheme.typography.displaySmall,
                color = ForestGreen300,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 6.sp
            )
            Text(
                "SAMRAKSHANE",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Forest Sentinel · Western Ghats",
                style = MaterialTheme.typography.bodyMedium,
                color = SageGreen
            )
            Spacer(modifier = Modifier.height(40.dp))
            CircularProgressIndicator(
                color = ForestGreen400,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

// ── Map Screen ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    onAlertClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forest Map", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            "Back",
                            tint = Color.White
                        )
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
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            // NOTE: Add Google Maps API key in local.properties for full map support
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🗺️", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Interactive Map",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen300,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Add Google Maps API key in\nlocal.properties to enable",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SageGreen,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// ── Alert Detail Screen ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alertId: Long,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alert Details", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            "Back",
                            tint = Color.White
                        )
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
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Alert #$alertId", color = ForestGreen300)
        }
    }
}
