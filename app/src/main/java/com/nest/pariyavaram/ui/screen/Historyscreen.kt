package com.nest.pariyavaram.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nest.pariyavaram.local.ReportStatus
import com.nest.pariyavaram.data.model.WasteReport
import com.nest.pariyavaram.ui.theme.KarmaGold
import com.nest.pariyavaram.ui.theme.StatusCleaned
import com.nest.pariyavaram.ui.theme.StatusPending
import com.nest.pariyavaram.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class HistoryFilter { ALL, PENDING, CLEANED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var filter  by remember { mutableStateOf(HistoryFilter.ALL) }

    val filtered = remember(uiState.reports, filter) {
        when (filter) {
            HistoryFilter.ALL     -> uiState.reports
            HistoryFilter.PENDING -> uiState.reports.filter { it.status == ReportStatus.PENDING.value }
            HistoryFilter.CLEANED -> uiState.reports.filter { it.status == ReportStatus.CLEANED.value }
        }.sortedByDescending { it.timestamp }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "Report History",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChipItem(
                    label    = "All (${uiState.reports.size})",
                    selected = filter == HistoryFilter.ALL,
                    color    = MaterialTheme.colorScheme.primary,
                    onClick  = { filter = HistoryFilter.ALL }
                )
            }
            item {
                FilterChipItem(
                    label    = "Pending (${uiState.pendingCount})",
                    selected = filter == HistoryFilter.PENDING,
                    color    = StatusPending,
                    onClick  = { filter = HistoryFilter.PENDING }
                )
            }
            item {
                FilterChipItem(
                    label    = "Cleaned (${uiState.cleanedCount})",
                    selected = filter == HistoryFilter.CLEANED,
                    color    = StatusCleaned,
                    onClick  = { filter = HistoryFilter.CLEANED }
                )
            }
        }

        if (filtered.isEmpty()) {
            EmptyHistoryState(filter)
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier            = Modifier.fillMaxSize()
            ) {
                items(items = filtered, key = { it.id }) { report ->
                    ReportHistoryCard(
                        report        = report,
                        onMarkCleaned = { viewModel.markAsCleaned(report) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun FilterChipItem(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor   = color.copy(alpha = 0.15f),
            selectedLabelColor       = color,
            selectedLeadingIconColor = color
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled             = true,
            selected            = selected,
            selectedBorderColor = color,
            selectedBorderWidth = 1.5.dp
        )
    )
}

@Composable
fun ReportHistoryCard(report: WasteReport, onMarkCleaned: () -> Unit) {
    val isPending   = report.status == ReportStatus.PENDING.value
    val statusColor = if (isPending) StatusPending else StatusCleaned
    val dateStr     = remember(report.timestamp) {
        SimpleDateFormat("dd MMM yy · hh:mm a", Locale.getDefault()).format(Date(report.timestamp))
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier         = Modifier.size(50.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = report.wasteTypeEnum.emoji, fontSize = 22.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    Text(text = report.wasteTypeEnum.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text       = if (isPending) "Pending" else "✅ Cleaned",
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color      = statusColor
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "%.4f, %.4f".format(report.latitude, report.longitude),
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (report.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text     = report.description,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = KarmaGold, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("+${report.ecoKarmaPoints} Eco-Karma", style = MaterialTheme.typography.labelMedium, color = KarmaGold, fontWeight = FontWeight.SemiBold)
                    }
                    if (isPending) {
                        FilledTonalButton(
                            onClick        = onMarkCleaned,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors         = ButtonDefaults.filledTonalButtonColors(containerColor = StatusCleaned.copy(alpha = 0.15f), contentColor = StatusCleaned),
                            shape          = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Mark Cleaned", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState(filter: HistoryFilter) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            Text(text = if (filter == HistoryFilter.ALL) "🌿" else "🔍", fontSize = 64.sp)
            Text(
                text       = when (filter) {
                    HistoryFilter.ALL     -> "No reports yet"
                    HistoryFilter.PENDING -> "No pending reports"
                    HistoryFilter.CLEANED -> "No cleaned spots yet"
                },
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text      = when (filter) {
                    HistoryFilter.ALL     -> "Tap 'Report' to submit your first waste spot!"
                    HistoryFilter.PENDING -> "All waste spots are cleaned. Great work! 🎉"
                    HistoryFilter.CLEANED -> "Help clean a pending spot to see it here."
                },
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}