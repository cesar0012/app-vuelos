package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConnectingAirports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AirportData
import com.example.model.FlightDeal
import com.example.model.PriceDisplayOptions
import com.example.ui.theme.AppTheme

@Composable
fun FlightDealCard(
    deal: FlightDeal,
    priceOptions: PriceDisplayOptions = PriceDisplayOptions(),
    isHeadToHeadCompetition: Boolean = false,
    isSelectedInPlan: Boolean = false,
    selectButtonLabel: String? = null,
    onSelectForPlan: (() -> Unit)? = null,
    onOpenCalculator: () -> Unit,
    onVerifyGoogleFlights: () -> Unit,
    onOpenPromoLocator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val originCity = AirportData.findByCode(deal.originCode)?.city ?: deal.originCode
    val destCity = AirportData.findByCode(deal.destinationCode)?.city ?: deal.destinationCode
    val effectivePrice = deal.effectivePriceMxn(priceOptions)
    val formulaSummary = deal.fareBreakdownSummary(priceOptions)
    val cheapThreshold = if (priceOptions.includeTua) 1150 else 500
    val borderColor = when {
        isSelectedInPlan -> colors.neonCyan
        isHeadToHeadCompetition -> colors.neonGold.copy(alpha = 0.7f)
        else -> colors.cardBorder
    }

    Surface(
        color = colors.cardElevated,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelectedInPlan) 1.5.dp else 1.dp, borderColor),
        shadowElevation = if (colors.isDark) 0.dp else 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("flight_deal_card_${deal.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top badge row: Airline + flight number + live/referential status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 4.dp, height = 20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(deal.airline.brandColor)
                    )
                    Surface(
                        color = deal.airline.brandColor.copy(alpha = if (colors.isDark) 0.18f else 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${deal.airline.displayName} · ${deal.flightNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (isHeadToHeadCompetition) {
                        Surface(
                            color = colors.goldTint,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, colors.neonGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = "Competencia directa",
                                    tint = colors.neonGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Comp.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.neonGold
                                )
                            }
                        }
                    }
                }

                if (deal.isLiveVerified) {
                    Surface(
                        color = colors.greenTint,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.55f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "Tarifa verificada en vivo",
                                tint = colors.neonGreen,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "En vivo",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.neonGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Surface(
                        color = colors.cardHighest,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Text(
                            text = "Referencial",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route + Times + Transparent Price Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = deal.originCode,
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "hacia",
                            tint = colors.neonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = deal.destinationCode,
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "$originCity a $destCity",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${deal.departureTime} - ${deal.arrivalTime} · ${deal.formattedDuration}",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.neonCyan
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$$effectivePrice MXN",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (effectivePrice <= cheapThreshold) colors.neonGreen else colors.neonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = colors.cardHighest,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = priceOptions.shortSummaryLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = formulaSummary,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata pills: Non-stop / Layover + Aircraft
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stopColor = if (deal.isNonStop) colors.neonGreen else colors.neonGold
                val stopBg = if (deal.isNonStop) colors.greenTint else colors.goldTint
                Surface(
                    color = stopBg,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, stopColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (deal.isNonStop) Icons.Default.CheckCircle else Icons.Default.ConnectingAirports,
                            contentDescription = null,
                            tint = stopColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = deal.stopsLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = stopColor,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    color = colors.cardHighest,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AirplanemodeActive,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = deal.aircraftModel,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = deal.departureDate.fullDayLabelEs,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = colors.neonPink,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = deal.promoSourceDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpenCalculator,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                        .testTag("btn_calc_${deal.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Calculate,
                        contentDescription = null,
                        tint = colors.neonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TUA/Extra", style = MaterialTheme.typography.labelMedium, color = colors.textPrimary)
                }

                OutlinedButton(
                    onClick = onOpenPromoLocator,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                        .testTag("btn_promo_${deal.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = colors.neonPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver Promo", style = MaterialTheme.typography.labelMedium, color = colors.neonPink)
                }

                Button(
                    onClick = onSelectForPlan ?: onVerifyGoogleFlights,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelectedInPlan) colors.neonGreen else colors.neonCyan,
                        contentColor = colors.onAccent
                    ),
                    modifier = Modifier
                        .weight(1.1f)
                        .heightIn(min = 44.dp)
                        .testTag("btn_primary_${deal.id}")
                ) {
                    Icon(
                        imageVector = if (onSelectForPlan != null) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectButtonLabel ?: "Ir a ${deal.airline.iataCode}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
