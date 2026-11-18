package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Verified
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.AirportData
import com.example.model.FlightDeal
import com.example.model.PriceDisplayOptions
import com.example.ui.theme.AppTheme
import com.example.util.BookingLinkHelper
import com.example.util.PendingRedirectRequest

@Composable
fun PromoLocatorDialog(
    deal: FlightDeal,
    priceOptions: PriceDisplayOptions = PriceDisplayOptions(),
    onRequestExternalRedirect: (PendingRedirectRequest) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val colors = AppTheme.colors
    val context = LocalContext.current
    var showOtherOptions by remember { mutableStateOf(false) }

    val originCity = AirportData.findByCode(deal.originCode)?.city ?: deal.originCode
    val destCity = AirportData.findByCode(deal.destinationCode)?.city ?: deal.destinationCode

    val officialDirectUrl = remember(deal) {
        BookingLinkHelper.officialAirlineBookingUrl(
            airline = deal.airline,
            originCode = deal.originCode,
            destinationCode = deal.destinationCode,
            liveBookingUrl = deal.liveBookingUrl
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = colors.surfaceNavy,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
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
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = colors.neonCyan
                        )
                        Text(
                            text = if (deal.isLiveVerified) "Tarifa Real Localizada" else "Promoción Localizada",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("promo_dialog_close")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = colors.textSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = colors.cardHighest,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${deal.airline.displayName} ${deal.flightNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                color = colors.neonCyan
                            )
                            Surface(
                                color = if (deal.isLiveVerified) colors.greenTint else colors.cyanTint,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    if (deal.isLiveVerified) {
                                        Icon(
                                            imageVector = Icons.Default.Wifi,
                                            contentDescription = null,
                                            tint = colors.neonGreen,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                    Text(
                                        text = if (deal.isLiveVerified) "En vivo · Solo Ida" else "Solo Ida",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (deal.isLiveVerified) colors.neonGreen else colors.neonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${deal.originCode} ($originCity) -> ${deal.destinationCode} ($destCity)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${deal.departureDate.fullDayLabelEs} · ${deal.departureTime} - ${deal.arrivalTime} (${deal.stopsLabel})",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "Calculado (${priceOptions.shortSummaryLabel}): $${deal.effectivePriceMxn(priceOptions)} MXN",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.neonGreen
                        )
                        Text(
                            text = deal.fareBreakdownSummary(priceOptions),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary link: directly to the official airline website (route page or search home)
                Button(
                    onClick = {
                        onRequestExternalRedirect(
                            BookingLinkHelper.createRedirectRequest(deal, officialDirectUrl, priceOptions = priceOptions)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.neonCyan,
                        contentColor = colors.onAccent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .testTag("promo_primary_day_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buscar en ${deal.airline.displayName}.com",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Clear guided search instructions below the official airline link
                Surface(
                    color = colors.cyanTint,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("promo_search_instructions")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = colors.neonCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Cómo localizar esta tarifa en ${deal.airline.displayName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.neonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "1. Selecciona Solo Ida: ${deal.originCode} ($originCity) -> ${deal.destinationCode} ($destCity).",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "2. Elige la fecha exacta: ${deal.departureDate.fullDayLabelEs} (${deal.departureDate.isoString}).",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary
                        )
                        val bagHint = if (deal.airline.includesCarryOnInBase) {
                            "${deal.airline.displayName} ya incluye equipaje de mano 10kg en su tarifa base ($${deal.baseFareMxn} MXN + $${deal.originTuaMxn} TUA)."
                        } else {
                            "Tarifa ${deal.airline.zeroTierLabel} base: $${deal.baseFareMxn} MXN (sin maleta de mano) + $${deal.originTuaMxn} TUA = $${deal.totalWithTuaMxn} MXN. Si eliges ${deal.airline.basicTierLabel} (+~$${deal.airline.carryOn10kgMxn} MXN) queda cerca de $${deal.totalWithTuaMxn + deal.airline.carryOn10kgMxn} MXN."
                        }
                        Text(
                            text = "3. $bagHint",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Collapsible secondary options trigger
                Surface(
                    color = colors.cardHighest,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showOtherOptions = !showOtherOptions }
                        .testTag("promo_toggle_other_options")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ver otras opciones",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textPrimary
                        )
                        Icon(
                            imageVector = if (showOtherOptions) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (showOtherOptions) "Ocultar opciones" else "Mostrar otras opciones",
                            tint = colors.neonCyan
                        )
                    }
                }

                AnimatedVisibility(visible = showOtherOptions) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(color = colors.cardBorder)

                        OutlinedButton(
                            onClick = {
                                val kayakUrl = BookingLinkHelper.kayakOneWayUrl(
                                    originCode = deal.originCode,
                                    destinationCode = deal.destinationCode,
                                    departureDateIso = deal.departureDate.isoString,
                                    airlineIataCode = deal.airline.iataCode
                                )
                                onRequestExternalRedirect(
                                    BookingLinkHelper.createRedirectRequest(deal, kayakUrl)
                                )
                            },
                            border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                                .testTag("promo_kayak_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = colors.neonPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ver en Kayak México (${deal.airline.displayName})",
                                color = colors.neonPink
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val googleUrl = BookingLinkHelper.oneWayPromoDayUrl(
                                    airline = deal.airline,
                                    originCode = deal.originCode,
                                    destinationCode = deal.destinationCode,
                                    departureDateIso = deal.departureDate.isoString
                                )
                                onRequestExternalRedirect(
                                    BookingLinkHelper.createRedirectRequest(deal, googleUrl)
                                )
                            },
                            border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                                .testTag("promo_google_all_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = colors.neonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google Flights (Solo Ida · incluye TUA)",
                                color = colors.neonCyan
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onRequestExternalRedirect(
                                    BookingLinkHelper.createRedirectRequest(deal, deal.airline.officialPromoUrl)
                                )
                            },
                            border = BorderStroke(1.dp, colors.cardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                                .testTag("promo_official_airline_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = colors.textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Portada principal ${deal.airline.displayName}",
                                color = colors.textPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val summary = "${deal.originCode} -> ${deal.destinationCode} (Solo Ida) | ${deal.departureDate.isoString} | ${deal.airline.displayName} ${deal.flightNumber} | Base $${deal.baseFareMxn} MXN (+ $${deal.originTuaMxn} TUA)"
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                cm?.setPrimaryClip(ClipData.newPlainText("Vuelo Cheap Travel", summary))
                                Toast.makeText(context, "Ruta y fecha copiadas", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, colors.cardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 44.dp)
                                .testTag("promo_copy_details_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Copiar ruta y fecha",
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
