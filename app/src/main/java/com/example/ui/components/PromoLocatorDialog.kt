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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.BookingLinkHelper

@Composable
fun PromoLocatorDialog(
    deal: FlightDeal,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val context = LocalContext.current
    var showOtherOptions by remember { mutableStateOf(false) }

    val originCity = AirportData.findByCode(deal.originCode)?.city ?: deal.originCode
    val destCity = AirportData.findByCode(deal.destinationCode)?.city ?: deal.destinationCode

    val primaryDayUrl = remember(deal) {
        BookingLinkHelper.oneWayPromoDayUrl(
            airline = deal.airline,
            originCode = deal.originCode,
            destinationCode = deal.destinationCode,
            departureDateIso = deal.departureDate.isoString
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = SurfaceNavy,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
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
                            tint = NeonCyan
                        )
                        Text(
                            text = "Promoción Localizada",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("promo_dialog_close")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = CardElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CardBorder),
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
                                color = NeonCyan
                            )
                            Surface(
                                color = CyanTint,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Solo Ida",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${deal.originCode} ($originCity) -> ${deal.destinationCode} ($destCity)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${deal.departureDate.fullDayLabelEs} · ${deal.departureTime} - ${deal.arrivalTime} (${deal.stopsLabel})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "Base $${deal.baseFareMxn} MXN + TUA $${deal.originTuaMxn} MXN = Total $${deal.totalWithTuaMxn} MXN",
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary link: directly to that exact day & route (Solo Ida)
                Button(
                    onClick = { BookingLinkHelper.openExternalUrl(context, primaryDayUrl) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = DeepNightBg
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
                        text = "Ir al día de la reserva (${deal.departureDate.shortLabelEs})",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Collapsible secondary options trigger
                Surface(
                    color = CardElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CardBorder),
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
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = if (showOtherOptions) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (showOtherOptions) "Ocultar opciones" else "Mostrar otras opciones",
                            tint = NeonCyan
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
                        HorizontalDivider(color = CardBorder)

                        OutlinedButton(
                            onClick = {
                                BookingLinkHelper.openExternalUrl(
                                    context,
                                    BookingLinkHelper.kayakOneWayUrl(
                                        originCode = deal.originCode,
                                        destinationCode = deal.destinationCode,
                                        departureDateIso = deal.departureDate.isoString,
                                        airlineIataCode = deal.airline.iataCode
                                    )
                                )
                            },
                            border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                                .testTag("promo_kayak_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = NeonPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ver en Kayak México (${deal.airline.displayName})",
                                color = NeonPink
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                BookingLinkHelper.openExternalUrl(
                                    context,
                                    BookingLinkHelper.googleFlightsOneWayUrl(
                                        originCode = deal.originCode,
                                        destinationCode = deal.destinationCode,
                                        departureDateIso = deal.departureDate.isoString
                                    )
                                )
                            },
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                                .testTag("promo_google_all_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google Flights (Todas las aerolíneas)",
                                color = NeonCyan
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                BookingLinkHelper.openExternalUrl(context, deal.airline.officialPromoUrl)
                            },
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                                .testTag("promo_official_airline_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Buscador web de ${deal.airline.displayName}",
                                color = TextPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val summary = "${deal.originCode} -> ${deal.destinationCode} (Solo Ida) | ${deal.departureDate.isoString} | ${deal.airline.displayName} ${deal.flightNumber} | Base $${deal.baseFareMxn} MXN"
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                cm?.setPrimaryClip(ClipData.newPlainText("Vuelo Cheap Travel", summary))
                                Toast.makeText(context, "Ruta y fecha copiadas", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 44.dp)
                                .testTag("promo_copy_details_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Copiar ruta y fecha",
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
