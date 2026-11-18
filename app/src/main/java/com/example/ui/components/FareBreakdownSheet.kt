package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.BaggagePackage
import com.example.model.FareCalculator
import com.example.model.FlightDeal
import com.example.model.SeatOption
import com.example.ui.theme.AppTheme
import com.example.util.BookingLinkHelper
import com.example.util.PendingRedirectRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FareBreakdownSheet(
    outbound: FlightDeal,
    returnFlight: FlightDeal?,
    initialBaggage: BaggagePackage = BaggagePackage.BASICA_10KG,
    onRequestExternalRedirect: (PendingRedirectRequest) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val colors = AppTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var adults by remember { mutableIntStateOf(1) }
    var children by remember { mutableIntStateOf(0) }
    var infants by remember { mutableIntStateOf(0) }
    var selectedBaggage by remember(initialBaggage) { mutableStateOf(initialBaggage) }
    var selectedSeat by remember { mutableStateOf(SeatOption.ALEATORIO) }

    val breakdown = remember(outbound, returnFlight, adults, children, infants, selectedBaggage, selectedSeat) {
        FareCalculator.calculate(
            outbound = outbound,
            returnFlight = returnFlight,
            adults = adults,
            children = children,
            infants = infants,
            baggagePackage = selectedBaggage,
            seatOption = selectedSeat
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceNavy,
        contentColor = colors.textPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Calculadora TUA y Extras",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary
                    )
                    val routeLabel = if (returnFlight != null) {
                        "${outbound.originCode} <-> ${outbound.destinationCode} (Redondo)"
                    } else {
                        "${outbound.originCode} -> ${outbound.destinationCode} (Solo Ida)"
                    }
                    Text(
                        text = "$routeLabel · ${outbound.airline.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.neonCyan
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("sheet_close_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar calculadora", tint = colors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Passengers Section
            Text(
                text = "Pasajeros",
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = colors.cardElevated,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PassengerCounterRow(
                        title = "Adultos (12+ años)",
                        subtitle = "Tarifa completa + TUA",
                        count = adults,
                        minCount = 1,
                        onDecrement = { if (adults > 1) adults-- },
                        onIncrement = { if (adults < 9) adults++ },
                        tagPrefix = "adults"
                    )
                    HorizontalDivider(color = colors.cardBorder)
                    PassengerCounterRow(
                        title = "Niños (2 a 11 años)",
                        subtitle = "-10% en tarifa base · Pagan TUA",
                        count = children,
                        minCount = 0,
                        onDecrement = { if (children > 0) children-- },
                        onIncrement = { if (children < 8) children++ },
                        tagPrefix = "children"
                    )
                    HorizontalDivider(color = colors.cardBorder)
                    PassengerCounterRow(
                        title = "Bebés en brazos (< 2 años)",
                        subtitle = "Exentos de TUA y tarifa base",
                        count = infants,
                        minCount = 0,
                        onDecrement = { if (infants > 0) infants-- },
                        onIncrement = { if (infants < adults) infants++ },
                        tagPrefix = "infants"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Baggage Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Luggage, contentDescription = null, tint = colors.neonCyan, modifier = Modifier.size(18.dp))
                Text("Paquete de Equipaje", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BaggagePackage.entries.forEach { pkg ->
                    val fee = FareCalculator.baggageFeeForAirline(outbound.airline, pkg) +
                        (returnFlight?.let { FareCalculator.baggageFeeForAirline(it.airline, pkg) } ?: 0)
                    val selected = selectedBaggage == pkg
                    Surface(
                        color = if (selected) colors.cyanTint else colors.cardElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (selected) colors.neonCyan else colors.cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBaggage = pkg }
                            .testTag("baggage_option_${pkg.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pkg.label, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                                Text(pkg.description, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                            Text(
                                text = if (fee == 0) "Incluido" else "+$$fee MXN/pax",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (fee == 0) colors.neonGreen else colors.neonCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Seat Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.AirlineSeatReclineExtra, contentDescription = null, tint = colors.neonPink, modifier = Modifier.size(18.dp))
                Text("Selección de Asiento", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SeatOption.entries.forEach { opt ->
                    val fee = FareCalculator.seatFeeForAirline(outbound.airline, opt) +
                        (returnFlight?.let { FareCalculator.seatFeeForAirline(it.airline, opt) } ?: 0)
                    val selected = selectedSeat == opt
                    Surface(
                        color = if (selected) colors.cyanTint else colors.cardElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (selected) colors.neonCyan else colors.cardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSeat = opt }
                            .testTag("seat_option_${opt.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = opt.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) colors.neonCyan else colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (fee == 0) "$0" else "+$$fee",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (fee == 0) colors.neonGreen else colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown Summary Card
            Surface(
                color = colors.cardHighest,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BreakdownRow("Tarifa base (${adults} adulto${if (adults > 1) "s" else ""})", "$${breakdown.adultBaseTotalMxn} MXN")
                    if (children > 0) {
                        BreakdownRow(
                            "Niños ($children x -10% base)",
                            "$${breakdown.childBaseTotalMxn} MXN (-$${breakdown.childDiscountSavingsMxn})"
                        )
                    }
                    if (infants > 0) {
                        BreakdownRow("Bebés en brazos ($infants)", "$0 MXN (Exento de TUA)")
                    }
                    BreakdownRow("Equipaje (${selectedBaggage.label})", "$${breakdown.baggageTotalMxn} MXN")
                    BreakdownRow("Asientos (${selectedSeat.label})", "$${breakdown.seatTotalMxn} MXN")
                    BreakdownRow("TUA salida (${outbound.originCode})", "$${breakdown.outboundTuaTotalMxn} MXN")
                    if (returnFlight != null) {
                        BreakdownRow("TUA regreso (${returnFlight.originCode})", "$${breakdown.returnTuaTotalMxn} MXN")
                    }
                    HorizontalDivider(color = colors.cardBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL FINAL MXN",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "$${breakdown.grandTotalMxn} MXN",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.neonGreen,
                            modifier = Modifier.testTag("calculator_grand_total")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TUA Insight Card
            Surface(
                color = colors.greenTint,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Outlined.Savings, contentDescription = null, tint = colors.neonGreen)
                    Text(
                        text = breakdown.tuaInsightNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.neonCyan,
                    contentColor = colors.onAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("calculator_verify_google_flights")
            ) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buscar en ${outbound.airline.displayName}.com", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PassengerCounterRow(
    title: String,
    subtitle: String,
    count: Int,
    minCount: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    tagPrefix: String
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
                onClick = onDecrement,
                enabled = count > minCount,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("${tagPrefix}_minus")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Restar $title", tint = if (count > minCount) colors.neonCyan else colors.textSecondary)
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier = Modifier.testTag("${tagPrefix}_count")
            )
            IconButton(
                onClick = onIncrement,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("${tagPrefix}_plus")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Sumar $title", tint = colors.neonCyan)
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
    }
}
