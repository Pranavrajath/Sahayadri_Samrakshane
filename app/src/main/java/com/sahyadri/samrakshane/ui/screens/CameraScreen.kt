package com.sahyadri.samrakshane.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.sahyadri.samrakshane.ui.components.LocationCard
import com.sahyadri.samrakshane.ui.theme.*
import com.sahyadri.samrakshane.ui.viewmodel.CameraViewModel
import com.sahyadri.samrakshane.utils.toFormattedString
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
    alertType: String,
    onPhotoCaptured: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var flashEnabled by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Camera Preview ────────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }

        // ── Top Overlay ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "📸 Evidence Capture",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { flashEnabled = !flashEnabled }) {
                    Icon(
                        if (flashEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        "Flash",
                        tint = if (flashEnabled) AlertAmber else Color.White
                    )
                }
            }
        }

        // ── GPS Overlay (live coordinates) ────────────────────────────────
        uiState.locationData?.let { location ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, ForestGreen500.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        null,
                        tint = ForestGreen400,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = location.toFormattedString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "±%.0fm  Alt: %.0fm".format(location.accuracy, location.altitude),
                            color = ForestGreen300,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = AlertAmber,
                        strokeWidth = 2.dp
                    )
                    Text("Acquiring GPS...", color = AlertAmber, fontSize = 12.sp)
                }
            }
        }

        // ── Crosshair Overlay ─────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.Center)) {
            // Corner brackets
            val cornerSize = 28.dp
            val strokeColor = ForestGreen400.copy(alpha = 0.8f)
            val strokeWidth = 3.dp

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center)
            ) {
                // Top-left
                Box(Modifier.size(cornerSize).align(Alignment.TopStart)
                    .border(BorderStroke(strokeWidth, strokeColor), RoundedCornerShape(topStart = 8.dp)))
                // Top-right
                Box(Modifier.size(cornerSize).align(Alignment.TopEnd)
                    .border(BorderStroke(strokeWidth, strokeColor), RoundedCornerShape(topEnd = 8.dp)))
                // Bottom-left
                Box(Modifier.size(cornerSize).align(Alignment.BottomStart)
                    .border(BorderStroke(strokeWidth, strokeColor), RoundedCornerShape(bottomStart = 8.dp)))
                // Bottom-right
                Box(Modifier.size(cornerSize).align(Alignment.BottomEnd)
                    .border(BorderStroke(strokeWidth, strokeColor), RoundedCornerShape(bottomEnd = 8.dp)))
            }
        }

        // ── Bottom Controls ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .navigationBarsPadding()
                .padding(bottom = 32.dp, top = 40.dp)
        ) {
            // Shutter button
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                IconButton(
                    onClick = {
                        if (!isCapturing) {
                            isCapturing = true
                            val capture = imageCapture ?: return@IconButton
                            capturePhoto(
                                context = context,
                                imageCapture = capture,
                                flashEnabled = flashEnabled,
                                onCaptured = { uri ->
                                    isCapturing = false
                                    onPhotoCaptured(uri.toString())
                                },
                                onError = {
                                    isCapturing = false
                                }
                            )
                        }
                    },
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (isCapturing) Color.Gray else Color.White)
                            .border(4.dp, ForestGreen400, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = ForestGreen400,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    flashEnabled: Boolean,
    onCaptured: (Uri) -> Unit,
    onError: () -> Unit
) {
    val photoDir = File(context.filesDir, "alerts").also { it.mkdirs() }
    val photoFile = File(
        photoDir,
        "sahyadri_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
    )

    imageCapture.flashMode = if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onCaptured(Uri.fromFile(photoFile))
            }

            override fun onError(exc: ImageCaptureException) {
                onError()
            }
        }
    )
}
