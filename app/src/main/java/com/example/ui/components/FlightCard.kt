package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import com.example.util.BookingLinkHelper
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FareBreakdown
import com.example.model.FlightDeal
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GoldenTag
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen

@Composable
fun FlightCard(
    deal: FlightDeal,
    onClick: () -> Unit,
    onAlertClick: () -> Unit,
    onRoundTripClick: (() -> Unit)? = null,
    onLocatePromoClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isSuperPromo = deal.baseFareMxn <= 249.0

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSuperPromo) 1.5.dp else 1.dp,
                brush = if (isSuperPromo) {
                    Brush.horizontalGradient(listOf(HotDiscountPink, CyanNeon))
                } else {
                    Brush.linearGradient(listOf(Color(0xFF2E3E5A), Color(0xFF1E293B)))
                },
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("flight_card_${deal.id}")
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (isSuperPromo) {
                        Brush.verticalGradient(
                            listOf(Color(0xFF161E34), Color(0xFF0F172A))
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(Color(0xFF131A2B), Color(0xFF0D1322))
                        )
                    }
                )
                .padding(16.dp)
        ) {
            // Header: Airline badge, Promo label, and Alert icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Airline Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(deal.airline.brandColor)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = deal.airline.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Flight number
                    Text(
                        text = deal.flightNumber,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    deal.promoLabel?.let { label ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (deal.baseFareMxn <= 199.0) HotDiscountPink.copy(alpha = 0.25f)
                                    else SuperDealGreen.copy(alpha = 0.2f)
                                )
                                .border(
                                    1.dp,
                                    if (deal.baseFareMxn <= 199.0) HotDiscountPink else SuperDealGreen,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (deal.baseFareMxn <= 199.0) HotDiscountPink else SuperDealGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onAlertClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("alert_button_${deal.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Crear alerta para esta ruta",
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Flight Route and Times
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Origin
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = deal.origin.code,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = deal.origin.city,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = deal.departureTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )
                }

                // Flight vector & duration
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = deal.durationFormatted,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(1.dp)
                                .background(Color(0xFF475569))
                        )
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(1.dp)
                                .background(Color(0xFF475569))
                        )
                    }
                    Text(
                        text = deal.stopsSummary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deal.isNonStop) SuperDealGreen else GoldenTag
                    )
                }

                // Destination
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = deal.destination.code,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = deal.destination.city,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = deal.arrivalTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF263248))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Date, Seats & Price details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = deal.departureDateFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = GoldenTag,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Quedan ${deal.seatsAvailableAtPromo} asientos en promo",
                            fontSize = 11.sp,
                            color = GoldenTag
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "Tarifa base:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(bottom = 2.dp, end = 4.dp)
                        )
                        Text(
                            text = FareBreakdown.formatCurrency(deal.baseFareMxn),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (deal.baseFareMxn <= 199.0) HotDiscountPink else CyanNeon
                        )
                    }
                    Text(
                        text = "+ TUA: ${FareBreakdown.formatCurrency(deal.tuaMxn)} (Total: ${FareBreakdown.formatCurrency(deal.previewTotalMxn)})",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Buttons
            FilledTonalButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("breakdown_button_${deal.id}"),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = CyanNeon
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Luggage,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calcular con Pasajeros, TUA y Equipaje",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Actions: Localizar Promo direct link & Agregar Vuelta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (onLocatePromoClick != null) {
                            onLocatePromoClick()
                        } else {
                            val url = BookingLinkHelper.getGoogleFlightsUrl(deal)
                            BookingLinkHelper.openWebUrl(context, url)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("quick_promo_link_${deal.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = SuperDealGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Localizar Promo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuperDealGreen
                    )
                }

                if (onRoundTripClick != null) {
                    OutlinedButton(
                        onClick = onRoundTripClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("add_return_flight_${deal.id}"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = HotDiscountPink
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ Elegir Vuelta",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HotDiscountPink
                        )
                    }
                }
            }
        }
    }
}
