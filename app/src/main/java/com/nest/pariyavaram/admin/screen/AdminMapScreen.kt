@file:OptIn(ExperimentalMaterial3Api::class)

package com.nest.pariyavaram.admin.screen

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nest.pariyavaram.viewmodel.ReportViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AdminMapScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    // ✅ LOCATION PERMISSION (same as user)
    val locationPermission = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // ✅ DEFAULT LOCATION (you can change)
    val defaultLocation = LatLng(12.9716, 77.5946)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (locationPermission.allPermissionsGranted) {

                // ✅ REAL GOOGLE MAP
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    )
                ) {

                    // 🔥 SHOW ALL REPORT MARKERS
                    uiState.reports.forEach { report ->

                        val position = LatLng(report.latitude, report.longitude)

                        Marker(
                            state = MarkerState(position = position),
                            title = "Report #${report.id}",
                            snippet = report.description
                        )
                    }
                }

            } else {
                // ❗ Permission UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Location permission required")

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = {
                        locationPermission.launchMultiplePermissionRequest()
                    }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}