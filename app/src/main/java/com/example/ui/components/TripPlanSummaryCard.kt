package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.FlightDeal
import com.example.ui.theme.AppTheme
import com.example.util.BookingLinkHelper
import com.example.util.PendingRedirectRequest
import com.example.viewmodel.TripMode

@Composable
fun TripPlanSummaryCard(
    tripMode: TripMode,
    outbound: FlightDeal?,
    returnFlight: FlightDeal?,
    onOpenCalculator: (FlightDeal, FlightDeal?) -> Unit,
    onClearPlan: () -> Unit,
    onPlanCopied: (String) -> Unit,
    onRequestExternalRedirect: (PendingRedirectRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    if (outbound == null) return
    val colors = AppTheme.colors
    val context = LocalContext.current

    val isRoundTripComplete = tripMode == TripMode.ROUND_TRIP && returnFlight != null
    val isSmartMix = isRoundTripComplete && outbound.airline != returnFlight!!.airline

    val baseTotal = outbound.baseFareMxn + (returnFlight?.baseFareMxn ?: 0)
    val tuaTotal = outbound.originTuaMxn + (returnFlight?.originTuaMxn ?: 0)
    val grandTotal = baseTotal + tuaTotal

    Surface(
        color = colors.surfaceNavy,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, if (isSmartMix) colors.neonPink else colors.neonCyan),
        modifier = modifier
            .fillMaxWidth()
            .testTag("trip_plan_summary_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (tripMode == TripMode.ROUND_TRIP) "Plan Viaje Redondo" else "Plan Solo Ida",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    if (isSmartMix) {
                        Surface(
                            color = colors.goldTint,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, colors.neonGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.neonGold, modifier = Modifier.size(12.dp))
                                Text("Smart Mix", style = MaterialTheme.typography.labelSmall, color = colors.neonGold)
                            }
                        }
                    }
                }
                IconButton(
                    onClick = onClearPlan,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("clear_trip_plan_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar plan", tint = colors.textSecondary)
                }
            }

            // Outbound leg
            LegSummaryRow(
                badge = "IDA",
                badgeTint = colors.cyanTint,
                badgeColor = colors.neonCyan,
                icon = Icons.Default.FlightTakeoff,
                deal = outbound
            )

            // Return leg
            if (tripMode == TripMode.ROUND_TRIP) {
                Spacer(modifier = Modifier.height(6.dp))
                if (returnFlight != null) {
                    LegSummaryRow(
                        badge = "VUELTA",
                        badgeTint = colors.pinkTint,
                        badgeColor = colors.neonPink,
                        icon = Icons.Default.FlightLand,
                        deal = returnFlight
                    )
                } else {
                    Surface(
                        color = colors.pinkTint,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Paso 2: Toca una fecha de Regreso en el calendario para completar tu plan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.neonPink,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = colors.cardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Price summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Base $$baseTotal + TUA $$tuaTotal MXN",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "Total Plan: $$grandTotal MXN",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.neonGreen,
                        modifier = Modifier.testTag("trip_plan_total_price")
                    )
                }
                OutlinedButton(
                    onClick = { onOpenCalculator(outbound, returnFlight) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier
                        .heightIn(min = 44.dp)
                        .testTag("trip_plan_calculator_btn")
                ) {
                    Icon(Icons.Outlined.Calculate, contentDescription = null, tint = colors.neonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Extras", color = colors.textPrimary, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smart booking buttons
            if (!isRoundTripComplete) {
                Button(
                    onClick = {
                        val url = BookingLinkHelper.officialAirlineBookingUrl(
                            airline = outbound.airline,
                            originCode = outbound.originCode,
                            destinationCode = outbound.destinationCode,
                            liveBookingUrl = outbound.liveBookingUrl
                        )
                        onRequestExternalRedirect(
                            BookingLinkHelper.createRedirectRequest(outbound, url)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.neonCyan, contentColor = colors.onAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("book_one_way_unified_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buscar Solo Ida en ${outbound.airline.displayName}", fontWeight = FontWeight.Bold)
                }
            } else if (!isSmartMix) {
                Button(
                    onClick = {
                        val url = BookingLinkHelper.officialAirlineBookingUrl(
                            airline = outbound.airline,
                            originCode = outbound.originCode,
                            destinationCode = outbound.destinationCode,
                            liveBookingUrl = outbound.liveBookingUrl
                        )
                        onRequestExternalRedirect(
                            BookingLinkHelper.createRedirectRequest(
                                deal = outbound,
                                url = url,
                                returnDeal = returnFlight
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.neonCyan, contentColor = colors.onAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("book_round_trip_unified_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reservar en ${outbound.airline.displayName}.com",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                val ret = returnFlight!!
                val urlOut = BookingLinkHelper.officialAirlineBookingUrl(
                    airline = outbound.airline,
                    originCode = outbound.originCode,
                    destinationCode = outbound.destinationCode,
                    liveBookingUrl = outbound.liveBookingUrl
                )
                val urlRet = BookingLinkHelper.officialAirlineBookingUrl(
                    airline = ret.airline,
                    originCode = ret.originCode,
                    destinationCode = ret.destinationCode,
                    liveBookingUrl = ret.liveBookingUrl
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onRequestExternalRedirect(
                                BookingLinkHelper.createRedirectRequest(outbound, urlOut)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.neonCyan, contentColor = colors.onAccent),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 46.dp)
                            .testTag("smart_mix_btn_outbound")
                    ) {
                        Text("1. Ida ${outbound.airline.displayName}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onRequestExternalRedirect(
                                BookingLinkHelper.createRedirectRequest(ret, urlRet)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.neonPink, contentColor = colors.onPinkButton),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 46.dp)
                            .testTag("smart_mix_btn_return")
                    ) {
                        Text("2. Vuelta ${ret.airline.displayName}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onRequestExternalRedirect(
                                BookingLinkHelper.createRedirectRequest(
                                    deal = outbound,
                                    url = urlOut,
                                    returnDeal = ret,
                                    extraUrl = urlRet
                                )
                            )
                        },
                        border = BorderStroke(1.dp, colors.neonGold.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .testTag("smart_mix_open_both_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = colors.neonGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Abrir ambos", color = colors.neonGold, style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = {
                            val planText = buildString {
                                appendLine("Plan Cheap Travel (Smart Mix)")
                                appendLine("1. IDA: ${outbound.airline.displayName} ${outbound.flightNumber} | ${outbound.originCode}->${outbound.destinationCode} | ${outbound.departureDate.fullDayLabelEs} ${outbound.departureTime}-${outbound.arrivalTime} (${outbound.stopsLabel})")
                                appendLine("   Base $${outbound.baseFareMxn} + TUA $${outbound.originTuaMxn} = $${outbound.totalWithTuaMxn} MXN")
                                appendLine("   Enlace: $urlOut")
                                appendLine("2. VUELTA: ${ret.airline.displayName} ${ret.flightNumber} | ${ret.originCode}->${ret.destinationCode} | ${ret.departureDate.fullDayLabelEs} ${ret.departureTime}-${ret.arrivalTime} (${ret.stopsLabel})")
                                appendLine("   Base $${ret.baseFareMxn} + TUA $${ret.originTuaMxn} = $${ret.totalWithTuaMxn} MXN")
                                appendLine("   Enlace: $urlRet")
                                append("TOTAL REDONDO (1 adulto): $$grandTotal MXN")
                            }
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            cm?.setPrimaryClip(ClipData.newPlainText("Plan Cheap Travel", planText))
                            onPlanCopied("Plan Smart Mix copiado al portapapeles")
                        },
                        border = BorderStroke(1.dp, colors.cardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .testTag("smart_mix_copy_plan_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copiar Plan", color = colors.textPrimary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun LegSummaryRow(
    badge: String,
    badgeTint: Color,
    badgeColor: Color,
    icon: ImageVector,
    deal: FlightDeal
) {
    val colors = AppTheme.colors
    Surface(
        color = colors.cardHighest,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(color = badgeTint, shape = RoundedCornerShape(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(12.dp))
                    Text(badge, style = MaterialTheme.typography.labelSmall, color = badgeColor, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${deal.originCode} -> ${deal.destinationCode} · ${deal.departureDate.shortLabelEs} (${deal.departureTime})",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "${deal.airline.displayName} ${deal.flightNumber} · ${deal.stopsLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            Text(
                text = "$${deal.totalWithTuaMxn}",
                style = MaterialTheme.typography.titleSmall,
                color = colors.neonGreen
            )
        }
    }
}
