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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BaggageOption
import com.example.model.FareBreakdown
import com.example.model.FlightDeal
import com.example.model.PassengerCount
import com.example.model.SeatOption
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GoldenTag
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen
import com.example.util.BookingLinkHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FareBreakdownSheet(
    deal: FlightDeal,
    returnDeal: FlightDeal? = null,
    passengers: PassengerCount,
    baggageOption: BaggageOption,
    seatOption: SeatOption,
    breakdown: FareBreakdown,
    onPassengerChange: (adults: Int, children: Int, infants: Int) -> Unit,
    onBaggageChange: (BaggageOption) -> Unit,
    onSeatChange: (SeatOption) -> Unit,
    onCreateAlert: (FlightDeal) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0D1424),
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(deal.airline.brandColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = deal.airline.displayName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = deal.flightNumber,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculadora Total con TUA & Equipaje",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Flight summary banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF162035))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Outbound
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "IDA: ${deal.origin.city} (${deal.origin.code}) ➔ ${deal.destination.city} (${deal.destination.code})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    Text(
                        text = "${deal.departureDateFormatted} • Salida ${deal.departureTime} • ${deal.airline.displayName} (${deal.flightNumber})",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${deal.stopsSummary} • ${deal.aircraftModel}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deal.isNonStop) SuperDealGreen else GoldenTag
                    )
                }

                // Return (if round trip)
                if (returnDeal != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF24324D))
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightLand,
                                contentDescription = null,
                                tint = HotDiscountPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "REGRESO: ${returnDeal.origin.city} (${returnDeal.origin.code}) ➔ ${returnDeal.destination.city} (${returnDeal.destination.code})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HotDiscountPink
                            )
                        }
                        Text(
                            text = "${returnDeal.departureDateFormatted} • Salida ${returnDeal.departureTime} • ${returnDeal.airline.displayName} (${returnDeal.flightNumber})",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "${returnDeal.stopsSummary} • ${returnDeal.aircraftModel}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (returnDeal.isNonStop) SuperDealGreen else GoldenTag
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Selector de Pasajeros
            Text(
                text = "1. Número de Pasajeros",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2D)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Adults
                    PassengerRow(
                        title = "Adultos",
                        subtitle = "Mayores de 12 años",
                        count = passengers.adults,
                        min = 1,
                        max = 9,
                        onDecrement = { onPassengerChange(passengers.adults - 1, passengers.children, passengers.infants) },
                        onIncrement = { onPassengerChange(passengers.adults + 1, passengers.children, passengers.infants) },
                        testTagPrefix = "adults"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF1E2A42))
                            .padding(vertical = 4.dp)
                    )

                    // Children
                    PassengerRow(
                        title = "Niños",
                        subtitle = "2 a 11 años (-10% en tarifa base)",
                        count = passengers.children,
                        min = 0,
                        max = 8,
                        onDecrement = { onPassengerChange(passengers.adults, passengers.children - 1, passengers.infants) },
                        onIncrement = { onPassengerChange(passengers.adults, passengers.children + 1, passengers.infants) },
                        testTagPrefix = "children"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF1E2A42))
                            .padding(vertical = 4.dp)
                    )

                    // Infants
                    PassengerRow(
                        title = "Bebés",
                        subtitle = "Menores de 2 años en brazos (sin costo de TUA)",
                        count = passengers.infants,
                        min = 0,
                        max = passengers.adults,
                        onDecrement = { onPassengerChange(passengers.adults, passengers.children, passengers.infants - 1) },
                        onIncrement = { onPassengerChange(passengers.adults, passengers.children, passengers.infants + 1) },
                        testTagPrefix = "infants"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Tarifa de Uso de Aeropuerto (TUA)
            Text(
                text = "2. TUA Aeroportuaria (Obligatoria)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF131D33))
                    .border(1.dp, Color(0xFF233252), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TUA ${deal.origin.code}: ${FareBreakdown.formatCurrency(deal.tuaMxn)} por pasajero",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = FareBreakdown.formatCurrency(breakdown.totalTua),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = CyanNeon
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "La TUA se cobra por ley en el aeropuerto de salida (${deal.origin.name}). Para ${passengers.totalPayingPassengers} pasajero(s) que pagan impuestos suma ${FareBreakdown.formatCurrency(breakdown.totalTua)}.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp
                    )

                    if (deal.origin.code == "NLU") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuperDealGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = SuperDealGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Ahorro: El AIFA tiene la TUA más baja de CDMX ($309 MXN vs $570 en el AICM)",
                                    fontSize = 11.sp,
                                    color = SuperDealGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Selección de Equipaje
            Text(
                text = "3. Paquete de Equipaje",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            BaggageOption.entries.forEach { option ->
                val isSelected = baggageOption == option
                val priceText = when (option) {
                    BaggageOption.ZERO -> "Gratis"
                    BaggageOption.BASIC -> "+${FareBreakdown.formatCurrency(deal.airline.defaultCarryOnFeeMxn)} / persona"
                    BaggageOption.PLUS -> "+${FareBreakdown.formatCurrency(deal.airline.defaultCarryOnFeeMxn + deal.airline.defaultCheckedBagFeeMxn)} / persona"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBaggageChange(option) }
                        .background(if (isSelected) Color(0xFF1E2B48) else Color(0xFF111827))
                        .border(
                            1.5.dp,
                            if (isSelected) CyanNeon else Color(0xFF22304A),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                        .testTag("baggage_option_${option.name}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyanNeon else Color.White
                            )
                            Text(
                                text = option.description,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Text(
                            text = priceText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) SuperDealGreen else Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Selección de Asiento
            Text(
                text = "4. Selección de Asientos",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            SeatOption.entries.forEach { seat ->
                val isSelected = seatOption == seat
                val priceText = when (seat) {
                    SeatOption.RANDOM -> "Sin costo"
                    SeatOption.STANDARD -> "+${FareBreakdown.formatCurrency(deal.airline.defaultStandardSeatFeeMxn)} / persona"
                    SeatOption.EXTRA_LEGROOM -> "+${FareBreakdown.formatCurrency(deal.airline.defaultExtraLegroomFeeMxn)} / persona"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSeatChange(seat) }
                        .background(if (isSelected) Color(0xFF1E2B48) else Color(0xFF111827))
                        .border(
                            1.5.dp,
                            if (isSelected) CyanNeon else Color(0xFF22304A),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                        .testTag("seat_option_${seat.name}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = seat.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyanNeon else Color.White
                            )
                            Text(
                                text = seat.description,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Text(
                            text = priceText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) SuperDealGreen else Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. RESUMEN Y TOTAL FINAL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1A2640), Color(0xFF0F1728))
                        )
                    )
                    .border(1.5.dp, CyanNeon, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "RESUMEN DEL TOTAL FINAL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanNeon
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SummaryLine(
                        label = "Tarifa Base (${passengers.adults} adultos, ${passengers.children} niños):",
                        value = FareBreakdown.formatCurrency(breakdown.totalBaseFare)
                    )

                    SummaryLine(
                        label = "TUA del Aeropuerto (${passengers.totalPayingPassengers} pasajeros):",
                        value = FareBreakdown.formatCurrency(breakdown.totalTua)
                    )

                    if (breakdown.totalBaggage > 0) {
                        SummaryLine(
                            label = "Equipaje (${baggageOption.name}):",
                            value = FareBreakdown.formatCurrency(breakdown.totalBaggage)
                        )
                    }

                    if (breakdown.totalSeats > 0) {
                        SummaryLine(
                            label = "Asientos reservados:",
                            value = FareBreakdown.formatCurrency(breakdown.totalSeats)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .height(1.dp)
                            .background(Color(0xFF334566))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL COMPLETO:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${FareBreakdown.formatCurrency(breakdown.perPersonAverageMxn)} promedio / persona",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Text(
                            text = FareBreakdown.formatCurrency(breakdown.grandTotalMxn),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = HotDiscountPink
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Booking Dispatcher Section (Requested by user)
            if (returnDeal != null) {
                if (deal.airline == returnDeal.airline) {
                    // SAME AIRLINE ROUND TRIP
                    Button(
                        onClick = {
                            val url = BookingLinkHelper.getRoundTripBookingUrl(deal, returnDeal, passengers)
                            BookingLinkHelper.openWebUrl(context, url)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("sheet_reserve_roundtrip_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = deal.airline.brandColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reservar Viaje Redondo en ${deal.airline.displayName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // SMART MIX (2 DIFFERENT AIRLINES)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E1C38))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = HotDiscountPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "SMART MIX: Ida con ${deal.airline.displayName} y Regreso con ${returnDeal.airline.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HotDiscountPink
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val url = BookingLinkHelper.getDirectBookingUrl(deal, passengers)
                                BookingLinkHelper.openWebUrl(context, url)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("sheet_smart_mix_outbound"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = deal.airline.brandColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "1. Ida ${deal.airline.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                val url = BookingLinkHelper.getDirectBookingUrl(returnDeal, passengers)
                                BookingLinkHelper.openWebUrl(context, url)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("sheet_smart_mix_return"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = returnDeal.airline.brandColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "2. Vuelta ${returnDeal.airline.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            val url1 = BookingLinkHelper.getDirectBookingUrl(deal, passengers)
                            val url2 = BookingLinkHelper.getDirectBookingUrl(returnDeal, passengers)
                            BookingLinkHelper.openBothBookingUrls(context, url1, url2)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("sheet_open_both_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanNeon)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Abrir Ambos Enlaces para Reservar", fontSize = 11.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // ONE WAY DIRECT BOOKING
                Button(
                    onClick = {
                        val url = BookingLinkHelper.getDirectBookingUrl(deal, passengers)
                        BookingLinkHelper.openWebUrl(context, url)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("sheet_reserve_oneway_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = deal.airline.brandColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reservar Vuelo en ${deal.airline.displayName} (${FareBreakdown.formatCurrency(breakdown.grandTotalMxn)})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Official Promo link and Itinerary Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val promoUrl = BookingLinkHelper.getOfficialPromoPortalUrl(deal.airline)
                        BookingLinkHelper.openWebUrl(context, promoUrl)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("sheet_official_promo_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(14.dp), tint = SuperDealGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Promos ${deal.airline.displayName}", fontSize = 10.sp, color = SuperDealGreen, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val summary = BookingLinkHelper.buildTripShareSummary(deal, returnDeal, passengers, breakdown)
                        BookingLinkHelper.copyToClipboard(context, "Itinerario Cheap Travel", summary)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("sheet_copy_itinerary_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copiar Plan", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Actions (Alert & Close)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onCreateAlert(deal)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("create_alert_from_sheet"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = CyanNeon
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vigilar Precio",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("accept_sheet_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = Color(0xFF0A0F1D)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Listo / Aceptar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PassengerRow(
    title: String,
    subtitle: String,
    count: Int,
    min: Int,
    max: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    testTagPrefix: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onDecrement,
                enabled = count > min,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (count > min) Color(0xFF1E293B) else Color(0xFF111827))
                    .testTag("${testTagPrefix}_decrement")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Menos",
                    tint = if (count > min) Color.White else Color(0xFF475569),
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "$count",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.width(24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            IconButton(
                onClick = onIncrement,
                enabled = count < max,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (count < max) CyanNeon else Color(0xFF111827))
                    .testTag("${testTagPrefix}_increment")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Más",
                    tint = if (count < max) Color(0xFF0A0F1D) else Color(0xFF475569),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
