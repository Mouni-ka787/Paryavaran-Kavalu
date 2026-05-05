package com.nest.pariyavaram.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nest.pariyavaram.ui.components.AppLogo
import com.nest.pariyavaram.ui.theme.KarmaGold
import com.nest.pariyavaram.ui.theme.StatusCleaned
import com.nest.pariyavaram.ui.theme.StatusPending
import com.nest.pariyavaram.viewmodel.ReportViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ReportViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var displayedKarma by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.totalKarma) {
        val target = uiState.totalKarma
        val start = displayedKarma
        val steps = 30
        val delta = (target - start).toFloat() / steps

        repeat(steps) { i ->
            displayedKarma = (start + delta * (i + 1)).toInt()
            delay(16)
        }

        displayedKarma = target
    }

    val rank = when {
        uiState.totalKarma < 50 -> "🌱 Sapling"
        uiState.totalKarma < 150 -> "🌿 Guardian"
        uiState.totalKarma < 300 -> "🌳 Protector"
        uiState.totalKarma < 500 -> "🦚 Warrior"
        else -> "🌏 Champion"
    }

    val nextThreshold = when {
        uiState.totalKarma < 50 -> 50
        uiState.totalKarma < 150 -> 150
        uiState.totalKarma < 300 -> 300
        uiState.totalKarma < 500 -> 500
        else -> 999
    }

    val progress =
        (uiState.totalKarma.toFloat() / nextThreshold).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        "My Profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },

            actions = {
                IconButton(onClick = onLogout) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },

            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    AppLogo(
                        imageSize = 56.dp,
                        showName = false,
                        showTagline = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    displayedKarma.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Eco-Karma Points",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        rank,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Progress to next rank",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Text(
                            "${uiState.totalKarma}/$nextThreshold",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "My Achievements",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),

            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatBox(
                Modifier.weight(1f),
                "📍",
                uiState.reports.size.toString(),
                "Total",
                MaterialTheme.colorScheme.primaryContainer
            )

            StatBox(
                Modifier.weight(1f),
                "🔴",
                uiState.pendingCount.toString(),
                "Pending",
                StatusPending.copy(alpha = 0.1f)
            )

            StatBox(
                Modifier.weight(1f),
                "✅",
                uiState.cleanedCount.toString(),
                "Cleaned",
                StatusCleaned.copy(alpha = 0.1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Badges",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            AchievementRow(
                "🌱",
                "First Report",
                "Submitted your very first waste spot report",
                uiState.reports.isNotEmpty()
            )

            AchievementRow(
                "🔟",
                "10 Reports",
                "Reported 10 or more waste spots",
                uiState.reports.size >= 10
            )

            AchievementRow(
                "🧹",
                "First Cleanup",
                "Had your first reported spot cleaned",
                uiState.cleanedCount >= 1
            )

            AchievementRow(
                "🌟",
                "100 Karma",
                "Earned 100 or more Eco-Karma points",
                uiState.totalKarma >= 100
            )

            AchievementRow(
                "🏆",
                "Community Champion",
                "Cleaned 5 or more waste spots",
                uiState.cleanedCount >= 5
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("🌏", fontSize = 32.sp)

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        "Your Environmental Impact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "You've helped keep Bengaluru cleaner by reporting ${uiState.reports.size} waste spots and getting ${uiState.cleanedCount} cleaned! 🙌",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatBox(
    modifier: Modifier,
    emoji: String,
    value: String,
    label: String,
    bgColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(12.dp),

        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold)
            Text(label)
        }
    }
}

@Composable
fun AchievementRow(
    emoji: String,
    title: String,
    desc: String,
    unlocked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(if (unlocked) emoji else "🔒", fontSize = 28.sp)

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.bodySmall)
        }
    }
}