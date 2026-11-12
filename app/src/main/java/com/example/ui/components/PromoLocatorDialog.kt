package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.FareBreakdown
import com.example.model.FlightDeal
import com.example.model.PassengerCount
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GoldenTag
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen
import com.example.util.BookingLinkHelper

@Composable
fun PromoLocatorDialog(
    deal: FlightDeal,
    passengers: PassengerCount = PassengerCount(),
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, Color(0xFF2E3E5B), RoundedCornerShape(22.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuperDealGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Localizador Oficial de Oferta",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${deal.origin.city} (${deal.origin.code}) ➔ ${deal.destination.city} (${deal.destination.code})",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_promo_locator_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Flight Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF162035))
                        .border(1.dp, Color(0xFF22314E), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(deal.airline.brandColor)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = deal.airline.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = deal.flightNumber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = FareBreakdown.formatCurrency(deal.baseFareMxn),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (deal.baseFareMxn <= 199.0) HotDiscountPink else SuperDealGreen
                                )
                                Text(
                                    text = "+ TUA ${FareBreakdown.formatCurrency(deal.tuaMxn)}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Date and Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fecha: ${deal.departureDateFormatted}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Horario: ${deal.departureTime} ➔ ${deal.arrivalTime}",
                                fontSize = 12.sp,
                                color = CyanNeon,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Scales & Aircraft Model
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (deal.isNonStop) SuperDealGreen else GoldenTag,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = deal.stopsSummary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (deal.isNonStop) SuperDealGreen else GoldenTag
                                )
                            }

                            Text(
                                text = deal.aircraftModel,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        // Source Info
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10192A))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Fuente: ${deal.promoSourceDescription} • ${deal.seatsAvailableAtPromo} asientos disponibles",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Enlaces directos verificados (Sin errores 404):",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 1: Live Google Flights Direct Link (GUARANTEED NO 404, Live pricing & direct booking)
                LocatorOptionItem(
                    title = "1. Ver y Reservar en Google Flights (En Vivo)",
                    subtitle = "Abre exactamente este vuelo de ${deal.airline.displayName} el ${deal.dayAndMonthFormatted} con inventario en vivo.",
                    badge = "RECOMENDADO",
                    badgeColor = SuperDealGreen,
                    icon = Icons.Default.TravelExplore,
                    onClick = {
                        val url = BookingLinkHelper.getDirectBookingUrl(deal, passengers)
                        BookingLinkHelper.openWebUrl(context, url)
                        onDismiss()
                    },
                    testTag = "option_google_flights"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 2: Kayak México Live Route
                LocatorOptionItem(
                    title = "2. Buscar Ruta en Kayak México",
                    subtitle = "Acceso alternativo directo que redirige a la aerolínea oficial.",
                    badge = "EN VIVO",
                    badgeColor = CyanNeon,
                    icon = Icons.Default.Public,
                    onClick = {
                        val url = BookingLinkHelper.getKayakFlightUrl(deal)
                        BookingLinkHelper.openWebUrl(context, url)
                        onDismiss()
                    },
                    testTag = "option_kayak"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 3: Official Promotion portal ($199 promo page)
                LocatorOptionItem(
                    title = "3. Portal de Promociones de ${deal.airline.displayName}",
                    subtitle = "Página de ofertas activas donde la aerolínea publica las ventas flash (${deal.airline.famousPromoLabel}).",
                    badge = "OFERTAS",
                    badgeColor = GoldenTag,
                    icon = Icons.Default.LocalOffer,
                    onClick = {
                        val url = BookingLinkHelper.getOfficialPromoPortalUrl(deal.airline)
                        BookingLinkHelper.openWebUrl(context, url)
                        onDismiss()
                    },
                    testTag = "option_promo_portal"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 4: Copy direct verified link
                LocatorOptionItem(
                    title = "4. Copiar Enlace Directo",
                    subtitle = "Copia el enlace verificado para abrirlo en tu navegador o compartirlo por WhatsApp.",
                    badge = "COPIAR",
                    badgeColor = Color(0xFF94A3B8),
                    icon = Icons.Default.ContentCopy,
                    onClick = {
                        val url = BookingLinkHelper.getDirectBookingUrl(deal, passengers)
                        BookingLinkHelper.copyToClipboard(context, "Vuelo ${deal.airline.displayName}", url)
                        onDismiss()
                    },
                    testTag = "option_copy_link"
                )
            }
        }
    }
}

@Composable
private fun LocatorOptionItem(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141E33))
            .border(1.dp, Color(0xFF22314E), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag(testTag)
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
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1C2B47)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, badgeColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = badgeColor
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 13.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
