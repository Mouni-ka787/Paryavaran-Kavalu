package com.nest.pariyavaram.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nest.pariyavaram.R

/**
 * Reusable app logo component.
 *
 * Usage examples:
 *   AppLogo()                        // default size
 *   AppLogo(imageSize = 120.dp, showTagline = true)
 *   AppLogo(imageSize = 48.dp, showName = false)   // icon-only for top bar
 */
@Composable
fun AppLogo(
    modifier    : Modifier = Modifier,
    imageSize   : Dp      = 96.dp,
    showName    : Boolean = true,
    showTagline : Boolean = false
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Logo image ──────────────────────────────────────────────────────
        Image(
            painter            = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = "Paryavaran-Kavalu logo",
            modifier           = Modifier.size(imageSize)
        )

        // ── App name with two-colour text ───────────────────────────────────
        if (showName) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF1B5E20), fontWeight = FontWeight.ExtraBold)) {
                        append("Paryavaran")
                    }
                    withStyle(SpanStyle(color = Color(0xFF1565C0), fontWeight = FontWeight.ExtraBold)) {
                        append("-Kavalu")
                    }
                },
                fontSize = 24.sp,
                letterSpacing = 0.sp
            )

            // Sub-tagline: REPORT · TAG · CLEAN · PROTECT
            Text(
                text  = "REPORT  ·  TAG  ·  CLEAN  ·  PROTECT",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        // ── Optional bottom tagline ─────────────────────────────────────────
        if (showTagline) {
            Spacer(Modifier.height(2.dp))
            Text(
                text      = "🍃  Together for a Cleaner Tomorrow  🍃",
                style     = MaterialTheme.typography.bodySmall,
                color     = Color(0xFF2E7D32),
                fontWeight = FontWeight.Medium
            )
        }
    }
}