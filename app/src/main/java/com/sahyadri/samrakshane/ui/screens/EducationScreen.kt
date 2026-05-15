package com.sahyadri.samrakshane.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.sahyadri.samrakshane.ui.theme.*

data class EcoTipUi(
    val emoji: String,
    val title: String,
    val content: String,
    val color: Color,
    val category: String
)

val ECO_TIPS = listOf(
    EcoTipUi("🔥", "Fire Safety in Forests",
        "Never light campfires during dry seasons. Carry water to douse any embers. Report smoke immediately — a small fire can engulf hectares within hours in the Western Ghats.",
        FireOrange, "Fire Safety"),
    EcoTipUi("🐆", "Wildlife Encounters",
        "Keep distance from wild animals. Never feed them. If you spot a leopard or elephant, stand still, avoid eye contact, and back away slowly. Do not run.",
        WildlifeTeal, "Wildlife"),
    EcoTipUi("🚶", "Trekking Responsibly",
        "Stay on marked trails. Pack out all garbage — including biodegradable items. Use eco-friendly toiletries. Avoid plastic bottles; carry a reusable one.",
        ForestGreen400, "Trekking"),
    EcoTipUi("📍", "How to Report Accurately",
        "Enable location before opening the app. Wait for GPS accuracy below 10m. Take photos in good lighting. Describe visible damage, direction, and estimated area affected.",
        WaterBlue, "Reporting"),
    EcoTipUi("🌊", "Protecting Water Sources",
        "The Ghats feed 7 major rivers. Never dump waste near streams. Report any industrial dumping immediately. Use only biodegradable soaps when camping near water.",
        WaterBlue, "Conservation"),
    EcoTipUi("🌳", "Tree Protection",
        "It is illegal to cut trees without Forest Dept permission. Note vehicle numbers and photographs of illegal loggers. Report to the nearest forest range office or this app.",
        EarthBrown300, "Conservation"),
    EcoTipUi("⛰️", "Landslide Signs",
        "Watch for sudden cracks in soil, tilting trees, or unusual water seeping from slopes. Evacuate immediately and report. Do not approach the slip zone.",
        LandslideSlate, "Disaster"),
    EcoTipUi("📵", "Low Signal Areas",
        "This app saves your report offline. Your alert is never lost. Once you regain signal, it auto-syncs. Always carry a fully charged power bank when trekking.",
        MossGreen, "Reporting"),
    EcoTipUi("🦋", "Biodiversity Hotspot",
        "The Western Ghats have 5,000+ species of flowering plants, 139 mammal species, and 508 bird species. Every sighting you report helps conservation research.",
        ForestGreen300, "Wildlife"),
    EcoTipUi("🏕️", "Leave No Trace",
        "Remove all equipment after camping. Bury human waste 200m from water sources. Never carve into trees or rocks. Take only photographs, leave only footprints.",
        SageGreen, "Trekking"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(onNavigateBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Fire Safety", "Wildlife", "Trekking", "Reporting", "Conservation", "Disaster")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Eco Guide", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Western Ghats Sentinel Tips", style = MaterialTheme.typography.bodySmall, color = SageGreen)
                    }
                },
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
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category filter
                item {
                    ScrollableTabRow(
                        selectedTabIndex = categories.indexOf(selectedCategory),
                        containerColor = Color.Transparent,
                        contentColor = ForestGreen400,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        categories.forEach { category ->
                            Tab(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                text = {
                                    Text(
                                        category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selectedCategory == category) ForestGreen400 else SageGreen
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val filtered = if (selectedCategory == "All") ECO_TIPS
                else ECO_TIPS.filter { it.category == selectedCategory }

                items(filtered) { tip ->
                    EcoTipCard(tip = tip)
                }
            }
        }
    }
}

@Composable
private fun EcoTipCard(tip: EcoTipUi) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = BorderStroke(1.dp, tip.color.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tip.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(tip.emoji, fontSize = 26.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = tip.color,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tip.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = ForestGreen200,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tip.color.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tip.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = tip.color
                    )
                }
            }
        }
    }
}
