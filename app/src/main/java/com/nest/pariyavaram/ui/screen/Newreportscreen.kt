package com.nest.pariyavaram.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.nest.pariyavaram.data.model.WasteType
import com.nest.pariyavaram.ui.theme.StatusCleaned
import com.nest.pariyavaram.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(viewModel: ReportViewModel, onSuccess: () -> Unit) {
    val formState by viewModel.formState.collectAsState()
    val uiState   by viewModel.uiState.collectAsState()
    val context   = LocalContext.current
    val scope     = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.updatePhoto(it) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val file = java.io.File(context.cacheDir, "cam_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { out -> it.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out) }
            viewModel.updatePhoto(Uri.fromFile(file))
        }
    }

    LaunchedEffect(Unit) { fetchLocation(context) { lat, lng -> viewModel.updateLocation(lat, lng) } }

    var prevSubmitting by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.submitting) {
        if (prevSubmitting && !uiState.submitting) onSuccess()
        prevSubmitting = uiState.submitting
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("New Waste Report", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier            = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            GpsBanner(
                lat           = formState.lat,
                lng           = formState.lng,
                locationReady = formState.locationReady,
                onRefresh     = { scope.launch { fetchLocation(context) { lat, lng -> viewModel.updateLocation(lat, lng) } } }
            )

            Spacer(Modifier.height(8.dp))
            SectionHeader(emoji = "📸", title = "Photo Evidence")

            PhotoPickerSection(
                photoUri       = formState.photoUri,
                isProcessing   = formState.isProcessing,
                compressedPath = formState.compressedPath,
                onCamera       = { cameraLauncher.launch(null) },
                onGallery      = { galleryLauncher.launch("image/*") }
            )

            Spacer(Modifier.height(20.dp))
            SectionHeader(emoji = "🗂️", title = "Waste Category")

            WasteTypePicker(selected = formState.wasteType, onSelect = viewModel::updateWasteType)

            Spacer(Modifier.height(20.dp))
            SectionHeader(emoji = "📝", title = "Notes (Optional)")

            OutlinedTextField(
                value         = formState.description,
                onValueChange = viewModel::updateDescription,
                modifier      = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder   = { Text("e.g. Near the temple gate, large pile of plastic bags…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                minLines      = 3,
                maxLines      = 4,
                shape         = RoundedCornerShape(16.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(Modifier.height(28.dp))
            SubmitButton(
                isSubmitting    = uiState.submitting,
                isPhotoReady    = formState.compressedPath != null,
                isLocationReady = formState.locationReady,
                onClick         = viewModel::submitReport
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun GpsBanner(lat: Double, lng: Double, locationReady: Boolean, onRefresh: () -> Unit) {
    val bgColor = if (locationReady) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(14.dp)).background(bgColor).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = if (locationReady) Icons.Filled.LocationOn else Icons.Filled.LocationOff,
                contentDescription = null,
                tint               = if (locationReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier           = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text       = if (locationReady) "📍 Location Captured" else "⏳ Getting location…",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (locationReady) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                )
                if (locationReady) {
                    Text("%.5f, %.5f".format(lat, lng), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                }
            }
        }
        IconButton(onClick = onRefresh, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refresh GPS", tint = if (locationReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun SectionHeader(emoji: String, title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun PhotoPickerSection(photoUri: Uri?, isProcessing: Boolean, compressedPath: String?, onCamera: () -> Unit, onGallery: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        AnimatedContent(targetState = photoUri, transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) }, label = "photo_preview") { uri ->
            if (uri != null) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)).clickable(onClick = onGallery)) {
                    AsyncImage(model = uri, contentDescription = "Waste photo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

                    if (isProcessing) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color.White)
                                Spacer(Modifier.height(8.dp))
                                Text("Compressing…", color = Color.White, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    compressedPath?.let {
                        val sizeKb = java.io.File(it).length() / 1024
                        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.7f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("${sizeKb}KB ✅", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).padding(6.dp)) {
                        Icon(Icons.Filled.Edit, "Change photo", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                        Text("Add Photo Evidence", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilledTonalButton(onClick = onCamera, shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Filled.CameraAlt, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Camera")
                            }
                            OutlinedButton(onClick = onGallery, shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Filled.Image, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Gallery")
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class WasteOption(val type: WasteType, val color: Color)

private val wasteOptions = listOf(
    WasteOption(WasteType.PLASTIC,  Color(0xFF1565C0)),
    WasteOption(WasteType.ORGANIC,  Color(0xFF2E7D32)),
    WasteOption(WasteType.MIXED,    Color(0xFFE65100)),
    WasteOption(WasteType.MEDICAL,  Color(0xFFB71C1C))
)

@Composable
fun WasteTypePicker(selected: WasteType, onSelect: (WasteType) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(wasteOptions) { option ->
            val isSelected = option.type == selected
            val animScale  by animateFloatAsState(targetValue = if (isSelected) 1.04f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "waste_scale")

            Card(
                modifier  = Modifier.width(110.dp).graphicsLayer(scaleX = animScale, scaleY = animScale).clickable { onSelect(option.type) },
                shape     = RoundedCornerShape(16.dp),
                border    = if (isSelected) BorderStroke(2.dp, option.color) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors    = CardDefaults.cardColors(containerColor = if (isSelected) option.color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 0.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(option.type.emoji, fontSize = 28.sp)
                    Text(
                        text       = option.type.displayName,
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun SubmitButton(isSubmitting: Boolean, isPhotoReady: Boolean, isLocationReady: Boolean, onClick: () -> Unit) {
    val ready = isPhotoReady && isLocationReady
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Button(
            onClick   = onClick,
            enabled   = !isSubmitting,
            modifier  = Modifier.fillMaxWidth().height(60.dp),
            shape     = RoundedCornerShape(18.dp),
            colors    = ButtonDefaults.buttonColors(
                containerColor = if (ready) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor   = if (ready) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (ready) 6.dp else 0.dp)
        ) {
            AnimatedContent(targetState = isSubmitting, label = "submit_state") { submitting ->
                if (submitting) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        Text("Submitting report…", style = MaterialTheme.typography.titleMedium)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                        Text("Submit Report", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        if (!ready) {
            Column(modifier = Modifier.padding(top = 68.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RequirementRow(met = isPhotoReady,    label = "Photo attached & compressed")
                RequirementRow(met = isLocationReady, label = "GPS location captured")
            }
        }
    }
}

@Composable
fun RequirementRow(met: Boolean, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector        = if (met) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint               = if (met) StatusCleaned else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = if (met) StatusCleaned else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@SuppressLint("MissingPermission")
fun fetchLocation(context: Context, onResult: (Double, Double) -> Unit) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    val cts    = CancellationTokenSource()
    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
        .addOnSuccessListener { loc -> loc?.let { onResult(it.latitude, it.longitude) } }
}