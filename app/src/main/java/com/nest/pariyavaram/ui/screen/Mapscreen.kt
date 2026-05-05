package com.nest.pariyavaram.ui.screen

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nest.pariyavaram.local.ReportStatus
import com.nest.pariyavaram.data.model.WasteReport
import com.nest.pariyavaram.ui.theme.KarmaGold
import com.nest.pariyavaram.ui.theme.StatusCleaned
import com.nest.pariyavaram.ui.theme.StatusPending
import com.nest.pariyavaram.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scope   = rememberCoroutineScope()

    val locationPerm = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    )

    val bengaluru   = LatLng(12.9716, 77.5946)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bengaluru, 12f)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.selectedReport) {
        if (uiState.selectedReport != null) sheetState.show()
        else if (sheetState.isVisible) sheetState.hide()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (locationPerm.allPermissionsGranted) {
            GoogleMap(
                modifier            = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties          = MapProperties(isMyLocationEnabled = true, mapType = MapType.NORMAL),
                uiSettings          = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false, compassEnabled = true)
            ) {
                uiState.reports.forEach { report ->
                    WasteMarker(report = report, onClick = { viewModel.selectReport(report) })
                }
            }
        } else {
            PermissionPrompt { locationPerm.launchMultiplePermissionRequest() }
        }

        MapStatsBar(
            pending  = uiState.pendingCount,
            cleaned  = uiState.cleanedCount,
            karma    = uiState.totalKarma,
            modifier = Modifier.align(Alignment.TopCenter).padding(horizontal = 16.dp, vertical = 12.dp)
        )

        FloatingActionButton(
            onClick        = { scope.launch { cameraState.animate(CameraUpdateFactory.newLatLngZoom(bengaluru, 13f), durationMs = 800) } },
            modifier       = Modifier.align(Alignment.BottomEnd).padding(bottom = 24.dp, end = 16.dp),
            shape          = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor   = MaterialTheme.colorScheme.onPrimary,
            elevation      = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "My Location")
        }

        if (uiState.selectedReport != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectReport(null) },
                sheetState       = sheetState,
                containerColor   = MaterialTheme.colorScheme.surface,
                shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle       = {
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 8.dp)
                            .width(40.dp).height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            ) {
                uiState.selectedReport?.let { report ->
                    ReportDetailSheet(
                        report        = report,
                        onMarkCleaned = { viewModel.markAsCleaned(report); viewModel.selectReport(null) },
                        onDismiss     = { viewModel.selectReport(null) }
                    )
                }
            }
        }
    }
}

@Composable
fun WasteMarker(report: WasteReport, onClick: () -> Unit) {
    val hue = if (report.status == ReportStatus.PENDING.value) BitmapDescriptorFactory.HUE_RED
    else BitmapDescriptorFactory.HUE_GREEN

    MarkerInfoWindowContent(
        state   = MarkerState(LatLng(report.latitude, report.longitude)),
        icon    = BitmapDescriptorFactory.defaultMarker(hue),
        onClick = { onClick(); true }
    ) {
        Card(
            shape     = RoundedCornerShape(10.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(text = report.wasteTypeEnum.displayName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                Text(
                    text  = if (report.status == ReportStatus.PENDING.value) "⏳ Pending cleanup" else "✅ Cleaned",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun MapStatsBar(pending: Int, cleaned: Int, karma: Int, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Paryavaran Kavalu", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Cleanliness Map", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            StatChip("Pending", pending.toString(), StatusPending)
            Spacer(Modifier.width(8.dp))
            StatChip("Cleaned", cleaned.toString(), StatusCleaned)
            Spacer(Modifier.width(8.dp))
            StatChip("Karma",   karma.toString(),   KarmaGold)
        }
    }
}

@Composable
fun StatChip(label: String, value: String, chipColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.clip(CircleShape).background(chipColor.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text = value, color = chipColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ReportDetailSheet(report: WasteReport, onMarkCleaned: () -> Unit, onDismiss: () -> Unit) {
    val dateStr   = remember(report.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(report.timestamp))
    }
    val isPending = report.status == ReportStatus.PENDING.value

    Column(
        modifier            = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier.size(52.dp).clip(CircleShape).background(if (isPending) StatusPending.copy(alpha = 0.12f) else StatusCleaned.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = report.wasteTypeEnum.emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.wasteTypeEnum.displayName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusPillSheet(isPending)
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        InfoRow(icon = Icons.Filled.LocationOn, label = "Location", value = "%.5f, %.5f".format(report.latitude, report.longitude), tint = MaterialTheme.colorScheme.primary)

        report.aiCategory?.let {
            InfoRow(icon = Icons.Filled.AutoAwesome, label = "AI Classification", value = it, tint = KarmaGold)
        }

        if (report.description.isNotBlank()) {
            InfoRow(icon = Icons.AutoMirrored.Filled.Notes, label = "Notes", value = report.description, tint = MaterialTheme.colorScheme.secondary)
        }

        InfoRow(icon = Icons.Filled.Star, label = "Eco-Karma awarded", value = "+${report.ecoKarmaPoints} points", tint = KarmaGold)

        if (isPending) {
            Button(
                onClick  = onMarkCleaned,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = StatusCleaned, contentColor = Color.White)
            ) {
                Icon(Icons.Filled.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("Mark as Cleaned", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        } else {
            OutlinedButton(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = StatusCleaned)
            ) {
                Icon(Icons.Filled.CheckCircle, null, tint = StatusCleaned)
                Spacer(Modifier.width(8.dp))
                Text("Already Cleaned ✅", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun StatusPillSheet(isPending: Boolean) {
    val bg   = if (isPending) StatusPending.copy(alpha = 0.12f) else StatusCleaned.copy(alpha = 0.12f)
    val fg   = if (isPending) StatusPending else StatusCleaned
    val text = if (isPending) "Pending" else "Cleaned"
    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bg).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(text = text, color = fg, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, tint: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun PermissionPrompt(onRequest: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Filled.LocationOff, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
            Text("Location Permission Needed", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(
                "To show the cleanliness map and geo-tag your waste reports, we need access to your location.",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRequest, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                Text("Grant Location Permission")
            }
        }
    }
}