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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.FlightDeal
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardHighest
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.GoldTint
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PinkTint
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.BookingLinkHelper
import com.example.viewmodel.TripMode

@Composable
fun TripPlanSummaryCard(
    tripMode: TripMode,
    outbound: FlightDeal?,
    returnFlight: FlightDeal?,
    onOpenCalculator: (FlightDeal, FlightDeal?) -> Unit,
    onClearPlan: () -> Unit,
    onPlanCopied: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (outbound == null) return
    val context = LocalContext.current

    val isRoundTripComplete = tripMode == TripMode.ROUND_TRIP && returnFlight != null
    val isSmartMix = isRoundTripComplete && outbound.airline != returnFlight!!.airline

    val baseTotal = outbound.baseFareMxn + (returnFlight?.baseFareMxn ?: 0)
    val tuaTotal = outbound.originTuaMxn + (returnFlight?.originTuaMxn ?: 0)
    val grandTotal = baseTotal + tuaTotal

    Surface(
        color = SurfaceNavy,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, if (isSmartMix) NeonPink else NeonCyan),
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
                        color = TextPrimary
                    )
                    if (isSmartMix) {
                        Surface(
                            color = GoldTint,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, NeonGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonGold, modifier = Modifier.size(12.dp))
                                Text("Smart Mix", style = MaterialTheme.typography.labelSmall, color = NeonGold)
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
                    Icon(Icons.Default.Close, contentDescription = "Limpiar plan", tint = TextSecondary)
                }
            }

            // Outbound leg
            LegSummaryRow(
                badge = "IDA",
                badgeTint = CyanTint,
                badgeColor = NeonCyan,
                icon = Icons.Default.FlightTakeoff,
                deal = outbound
            )

            // Return leg
            if (tripMode == TripMode.ROUND_TRIP) {
                Spacer(modifier = Modifier.height(6.dp))
                if (returnFlight != null) {
                    LegSummaryRow(
                        badge = "VUELTA",
                        badgeTint = PinkTint,
                        badgeColor = NeonPink,
                        icon = Icons.Default.FlightLand,
                        deal = returnFlight
                    )
                } else {
                    Surface(
                        color = PinkTint,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Paso 2: Toca una fecha de Regreso en el calendario para completar tu plan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonPink,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder)
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
                        color = TextSecondary
                    )
                    Text(
                        text = "Total Plan: $$grandTotal MXN",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonGreen,
                        modifier = Modifier.testTag("trip_plan_total_price")
                    )
                }
                OutlinedButton(
                    onClick = { onOpenCalculator(outbound, returnFlight) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .heightIn(min = 44.dp)
                        .testTag("trip_plan_calculator_btn")
                ) {
                    Icon(Icons.Outlined.Calculate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Extras", color = TextPrimary, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smart booking buttons
            if (!isRoundTripComplete) {
                Button(
                    onClick = {
                        BookingLinkHelper.openExternalUrl(
                            context,
                            BookingLinkHelper.googleFlightsOneWayUrl(
                                outbound.originCode,
                                outbound.destinationCode,
                                outbound.departureDate.isoString
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepNightBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("book_one_way_unified_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reservar Solo Ida en Google Flights", fontWeight = FontWeight.Bold)
                }
            } else if (!isSmartMix) {
                // Same airline -> 1 unified Round Trip button
                Button(
                    onClick = {
                        BookingLinkHelper.openExternalUrl(
                            context,
                            BookingLinkHelper.googleFlightsRoundTripUrl(
                                outbound.originCode,
                                outbound.destinationCode,
                                outbound.departureDate.isoString,
                                returnFlight!!.departureDate.isoString
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepNightBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("book_round_trip_unified_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reservar Viaje Redondo (${outbound.airline.displayName})",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Smart Mix (Different airlines) -> 2 independent buttons + open both + copy plan
                val ret = returnFlight!!
                val urlOut = BookingLinkHelper.googleFlightsOneWayUrl(
                    outbound.originCode,
                    outbound.destinationCode,
                    outbound.departureDate.isoString
                )
                val urlRet = BookingLinkHelper.googleFlightsOneWayUrl(
                    ret.originCode,
                    ret.destinationCode,
                    ret.departureDate.isoString
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { BookingLinkHelper.openExternalUrl(context, urlOut) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepNightBg),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 46.dp)
                            .testTag("smart_mix_btn_outbound")
                    ) {
                        Text("1. Ida ${outbound.airline.displayName}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { BookingLinkHelper.openExternalUrl(context, urlRet) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = TextPrimary),
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
                            BookingLinkHelper.openExternalUrl(context, urlOut)
                            BookingLinkHelper.openExternalUrl(context, urlRet)
                        },
                        border = BorderStroke(1.dp, NeonGold.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .testTag("smart_mix_open_both_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = NeonGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Abrir ambos", color = NeonGold, style = MaterialTheme.typography.labelMedium)
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
                        border = BorderStroke(1.dp, CardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .testTag("smart_mix_copy_plan_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copiar Plan", color = TextPrimary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun LegSummaryRow(
    badge: String,
    badgeTint: androidx.compose.ui.graphics.Color,
    badgeColor: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    deal: FlightDeal
) {
    Surface(
        color = CardHighest,
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
                    color = TextPrimary
                )
                Text(
                    text = "${deal.airline.displayName} ${deal.flightNumber} · ${deal.stopsLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Text(
                text = "$${deal.totalWithTuaMxn}",
                style = MaterialTheme.typography.titleSmall,
                color = NeonGreen
            )
        }
    }
}
