@file:OptIn(ExperimentalMaterial3Api::class)

package com.nest.pariyavaram.admin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.nest.pariyavaram.viewmodel.ReportViewModel

// 🔥 GOOGLE MAP IMPORTS
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.*

// ================= REPORTS =================

@Composable
fun AdminReportsScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit,
    onReportClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(uiState.reports) { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    onClick = { onReportClick(report.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Report #${report.id}")
                        Text(report.description)
                        Text("Status: ${report.status}")
                    }
                }
            }
        }
    }
}

// ================= USERS =================

@Composable
fun AdminUsersScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Users list coming soon 👀")
        }
    }
}

// ================= DETAIL (FINAL FIXED WITH MAP) =================

@Composable
fun AdminReportDetailScreen(
    reportId: Int,
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val report = uiState.reports.find { it.id == reportId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (report == null) {
            Text(
                "Report not found",
                modifier = Modifier.padding(16.dp)
            )
            return@Scaffold
        }

        // 📍 LOCATION
        val location = LatLng(report.latitude, report.longitude)

        // ✅ Camera state (safe)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, 15f)
        }

        // ✅ Marker state (FIXED - no warning)
        val markerState = remember {
            MarkerState(position = location)
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text("Report ID: ${report.id}")
            Spacer(Modifier.height(8.dp))

            Text("Description: ${report.description}")
            Spacer(Modifier.height(8.dp))

            Text("Status: ${report.status}")
            Spacer(Modifier.height(8.dp))

            Text("Type: ${report.wasteType}")
            Spacer(Modifier.height(16.dp))

            // 🗺️ GOOGLE MAP
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = markerState,
                    title = "Waste Location"
                )
            }

            Spacer(Modifier.height(8.dp))

            Text("Lat: ${report.latitude}")
            Text("Lng: ${report.longitude}")
        }
    }
}