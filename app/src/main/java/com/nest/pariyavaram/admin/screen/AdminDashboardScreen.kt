package com.nest.pariyavaram.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.nest.pariyavaram.data.model.WasteReport
import com.nest.pariyavaram.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ReportViewModel,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit   // ✅ NEW
) {

    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredReports = when (selectedFilter) {
        "Pending" -> uiState.reports.filter { it.status == "Pending" }
        "Cleaned" -> uiState.reports.filter { it.status == "Cleaned" }
        else -> uiState.reports
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                actions = {

                    // ✅ PROFILE ICON
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }

                    // ✅ LOGOUT (optional later remove)
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🌈 HEADER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Welcome Admin", color = Color.White)
                        Text(
                            "Manage reports easily",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // 📊 STATS
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Total", uiState.reports.size, Modifier.weight(1f))
                    StatCard("Pending", uiState.pendingCount, Modifier.weight(1f))
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Cleaned", uiState.cleanedCount, Modifier.weight(1f))
                    StatCard("Points", uiState.totalKarma, Modifier.weight(1f))
                }
            }

            // 🔍 FILTER
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SimpleChip("All", selectedFilter) { selectedFilter = "All" }
                    SimpleChip("Pending", selectedFilter) { selectedFilter = "Pending" }
                    SimpleChip("Cleaned", selectedFilter) { selectedFilter = "Cleaned" }
                }
            }

            // 📋 REPORT LIST
            items(filteredReports) { report ->
                ReportCard(
                    report = report,
                    onClean = { viewModel.markAsCleaned(report) }
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: Int, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title)
            Text(value.toString(), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SimpleChip(
    label: String,
    selected: String,
    onClick: () -> Unit
) {
    val isSelected = label == selected

    Surface(
        color = if (isSelected) Color(0xFF6366F1) else Color.LightGray,
        shape = RoundedCornerShape(50),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White
        )
    }
}

@Composable
fun ReportCard(
    report: WasteReport,
    onClean: () -> Unit
) {
    Card {
        Column(Modifier.padding(12.dp)) {

            Text("Report #${report.id}", fontWeight = FontWeight.Bold)
            Text(report.description)

            Spacer(Modifier.height(8.dp))

            if (report.status == "Pending") {
                Button(onClick = onClean) {
                    Text("Mark Cleaned")
                }
            }
        }
    }
}