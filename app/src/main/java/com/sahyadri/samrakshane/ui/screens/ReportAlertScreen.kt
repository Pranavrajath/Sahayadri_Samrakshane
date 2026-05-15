package com.sahyadri.samrakshane.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sahyadri.samrakshane.domain.model.AlertType
import com.sahyadri.samrakshane.ui.components.LocationCard
import com.sahyadri.samrakshane.ui.theme.*
import com.sahyadri.samrakshane.ui.viewmodel.ReportAlertViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAlertScreen(
    onNavigateBack: () -> Unit,
    onOpenCamera: (String) -> Unit,
    onAlertSubmitted: () -> Unit,
    viewModel: ReportAlertViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe photo from camera screen
    val savedStateHandle = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            snackbarHostState.showSnackbar("✅ Alert submitted! It will sync when connected.")
            onAlertSubmitted()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Report Alert",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(DarkForest, DeepCanopy, SurfaceDark))
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                // ── Step 1: Alert Type ─────────────────────────────────────
                SectionHeader(step = "01", title = "What did you observe?")
                Spacer(modifier = Modifier.height(12.dp))

                AlertTypeSelector(
                    selectedType = uiState.selectedAlertType,
                    onTypeSelected = viewModel::setAlertType
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Step 2: Photo ──────────────────────────────────────────
                SectionHeader(step = "02", title = "Capture Evidence")
                Spacer(modifier = Modifier.height(12.dp))

                PhotoCaptureCard(
                    photoUri = uiState.photoUri,
                    onCameraClick = {
                        onOpenCamera(uiState.selectedAlertType.name)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Step 3: GPS ────────────────────────────────────────────
                SectionHeader(step = "03", title = "GPS Location")
                Spacer(modifier = Modifier.height(12.dp))

                LocationCard(
                    locationData = uiState.locationData,
                    isLocating = uiState.isLocating,
                    onRefreshLocation = viewModel::refreshLocation
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Step 4: Details ────────────────────────────────────────
                SectionHeader(step = "04", title = "Additional Details")
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::setDescription,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Describe what you saw...") },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen400,
                        unfocusedBorderColor = MossGreen,
                        focusedLabelColor = ForestGreen400,
                        cursorColor = ForestGreen400,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.reporterName,
                    onValueChange = viewModel::setReporterName,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your name (optional)") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, null, tint = SageGreen)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen400,
                        unfocusedBorderColor = MossGreen,
                        focusedLabelColor = ForestGreen400,
                        cursorColor = ForestGreen400,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.reporterPhone,
                    onValueChange = viewModel::setReporterPhone,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contact number (optional)") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Phone, null, tint = SageGreen)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen400,
                        unfocusedBorderColor = MossGreen,
                        focusedLabelColor = ForestGreen400,
                        cursorColor = ForestGreen400,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── Submit Button ──────────────────────────────────────────
                Button(
                    onClick = {
                        keyboard?.hide()
                        viewModel.submitAlert()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = !uiState.isSubmitting && uiState.locationData != null,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen500,
                        disabledContainerColor = MossGreen.copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Send, null, tint = Color.White)
                            Text(
                                "SUBMIT ALERT",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                if (uiState.locationData == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠ Waiting for GPS lock to enable submission",
                        style = MaterialTheme.typography.bodySmall,
                        color = AlertAmber,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(step: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ForestGreen600.copy(alpha = 0.2f))
                .border(1.dp, ForestGreen500.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                style = MaterialTheme.typography.labelMedium,
                color = ForestGreen300,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AlertTypeSelector(
    selectedType: AlertType,
    onTypeSelected: (AlertType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AlertType.values().toList().chunked(2).forEach { rowTypes: List<AlertType> ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTypes.forEach { alertType: AlertType ->
                    val isSelected = selectedType == alertType
                    val chipColor = when (alertType) {
                        AlertType.FOREST_FIRE -> FireOrange
                        AlertType.LANDSLIDE -> LandslideSlate
                        AlertType.ILLEGAL_LOGGING -> EarthBrown300
                        AlertType.WILDLIFE_SIGHTING -> WildlifeTeal
                        AlertType.POACHING -> FireRed
                        AlertType.WATER_POLLUTION -> WaterBlue
                    }

                    Surface(
                        onClick = { onTypeSelected(alertType) },
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) chipColor.copy(alpha = 0.2f) else CardDark,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) chipColor else MossGreen.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = alertType.emoji, fontSize = 22.sp)
                            Text(
                                text = alertType.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) chipColor else ForestGreen200,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                if (rowTypes.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PhotoCaptureCard(photoUri: String?, onCameraClick: () -> Unit) {
    Surface(
        onClick = onCameraClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = BorderStroke(1.5.dp, ForestGreen600.copy(alpha = 0.4f))
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Captured photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = ForestGreen400,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to Open Camera",
                    style = MaterialTheme.typography.titleSmall,
                    color = ForestGreen300,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "GPS coordinates auto-tagged",
                    style = MaterialTheme.typography.bodySmall,
                    color = SageGreen
                )
            }
        }
    }
}
