package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AppTheme
import com.example.util.PendingRedirectRequest
import kotlinx.coroutines.delay

@Composable
fun RedirectNoticeDialog(
    request: PendingRedirectRequest,
    onCountdownFinished: (PendingRedirectRequest) -> Unit,
    onCancel: () -> Unit
) {
    BackHandler(onBack = onCancel)
    val colors = AppTheme.colors

    var secondsRemaining by remember(request) { mutableIntStateOf(5) }
    var progress by remember(request) { mutableFloatStateOf(1f) }

    LaunchedEffect(request) {
        val totalMs = 5_000L
        val tickMs = 100L
        var elapsedMs = 0L
        while (elapsedMs < totalMs) {
            delay(tickMs)
            elapsedMs += tickMs
            val remainingMs = (totalMs - elapsedMs).coerceAtLeast(0L)
            secondsRemaining = ((remainingMs + 999L) / 1000L).toInt().coerceIn(0, 5)
            progress = (remainingMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
        }
        onCountdownFinished(request)
    }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            color = colors.surfaceNavy,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, colors.neonCyan.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("redirect_notice_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.cyanTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = colors.neonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Antes de abrir ${request.airlineName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ruta ${request.routeLabel} (Solo Ida)",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.neonCyan
                            )
                        }
                    }

                    Surface(
                        color = colors.pinkTint,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("redirect_notice_countdown")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = colors.neonPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${secondsRemaining}s",
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.neonPink,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "Es posible que tengas que insertar o corregir los datos en el buscador del sitio para mostrar la fecha de la promoción.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary
                )

                Surface(
                    color = colors.cardHighest,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("redirect_notice_promo_date")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = colors.neonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (request.returnDateLabel != null) {
                                    "Fechas de la promoción"
                                } else {
                                    "Fecha de la promoción"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.neonGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = colors.greenTint,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                Text(
                                    text = if (request.returnDateLabel != null) {
                                        "Salida: ${request.promoDateLabel} (${request.promoIsoDate})"
                                    } else {
                                        "${request.promoDateLabel} (${request.promoIsoDate})"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.neonGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                if (request.returnDateLabel != null && request.returnIsoDate != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Regreso: ${request.returnDateLabel} (${request.returnIsoDate})",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = colors.neonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (request.baseFareMxn != null) {
                            Text(
                                text = "Tarifa base detectada: $${request.baseFareMxn} MXN (+ TUA al pagar)",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        color = colors.neonCyan,
                        trackColor = colors.cardHighest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = if (secondsRemaining > 0) {
                            "Abriendo enlace en $secondsRemaining segundo${if (secondsRemaining == 1) "" else "s"}..."
                        } else {
                            "Abriendo navegador..."
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textSecondary
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("redirect_notice_cancel_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cancelar",
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}
