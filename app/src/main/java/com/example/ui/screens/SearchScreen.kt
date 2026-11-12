package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FareBreakdown
import com.example.ui.CheapTravelUiState
import com.example.ui.CheapTravelViewModel
import com.example.ui.components.FlightCard
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen

@Composable
fun SearchScreen(
    uiState: CheapTravelUiState,
    viewModel: CheapTravelViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Buscador Multi-Aeropuerto",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Elige múltiples aeropuertos de salida a la vez (ej. AICM y AIFA juntos) para comparar precios y ahorro en la TUA.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        // Selected airports removable chips row (Instant removal with ✕)
        if (uiState.selectedOrigins.isNotEmpty() || uiState.selectedDestinations.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Filtros activos (Toca ✕ para desmarcar rápidamente):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.selectedOrigins, key = { "search_chip_org_${it.code}" }) { airport ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF16233B))
                                    .border(1.dp, CyanNeon, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.removeOrigin(airport) }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("search_remove_origin_${airport.code}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FlightTakeoff,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${airport.city} (${airport.code})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Quitar ${airport.code}",
                                        tint = HotDiscountPink,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        items(uiState.selectedDestinations, key = { "search_chip_dst_${it.code}" }) { airport ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF26182C))
                                    .border(1.dp, HotDiscountPink, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.removeDestination(airport) }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("search_remove_dest_${airport.code}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FlightLand,
                                        contentDescription = null,
                                        tint = HotDiscountPink,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${airport.city} (${airport.code})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Quitar ${airport.code}",
                                        tint = HotDiscountPink,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Form Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF26334D), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Origins selector (Multiple origins)
                    Text(
                        text = "Aeropuertos de Origen (${uiState.selectedOrigins.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A2338))
                            .border(1.dp, Color(0xFF2E3E5C), RoundedCornerShape(12.dp))
                            .clickable { viewModel.setShowOriginPicker(true) }
                            .padding(12.dp)
                            .testTag("search_origins_box")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    val codes = uiState.selectedOrigins.joinToString(", ") { "${it.code} (${it.city})" }
                                    Text(
                                        text = codes,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 2
                                    )
                                    Text(
                                        text = "Toca para añadir o cambiar aeropuertos",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Modificar",
                                tint = CyanNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destination selector
                    val destCount = uiState.selectedDestinations.size
                    Text(
                        text = "Aeropuertos de Destino de Llegada (${if (destCount == 0) "Todos" else destCount.toString()})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A2338))
                            .border(1.dp, Color(0xFF2E3E5C), RoundedCornerShape(12.dp))
                            .clickable { viewModel.setShowDestinationPicker(true) }
                            .padding(12.dp)
                            .testTag("search_destination_box")
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
                                Icon(
                                    imageVector = Icons.Default.FlightLand,
                                    contentDescription = null,
                                    tint = HotDiscountPink,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    val destText = if (uiState.selectedDestinations.isEmpty()) {
                                        "Cualquier destino (Mostrar las mejores ofertas)"
                                    } else {
                                        uiState.selectedDestinations.joinToString(", ") { "${it.code} (${it.city})" }
                                    }
                                    Text(
                                        text = destText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 2
                                    )
                                    Text(
                                        text = if (uiState.selectedDestinations.isEmpty()) "Playas, nacionales e internacionales" else "Toca para agregar o cambiar destinos",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Text(
                                text = "Modificar",
                                fontSize = 12.sp,
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Passengers counter summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF151C2C))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = SuperDealGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Pasajeros: ${uiState.passengers.summaryText}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Ajustable en cada vuelo",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Results header with accessible sorting toggle
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Resultados (${uiState.displayedDeals.size} vuelos)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Accessible Sort Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF131A2C))
                            .border(1.dp, Color(0xFF26334D), RoundedCornerShape(10.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (uiState.sortByPrice) CyanNeon else Color.Transparent)
                                .clickable { viewModel.setSortByPrice(true) }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                                .testTag("search_sort_price")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sell,
                                    contentDescription = null,
                                    tint = if (uiState.sortByPrice) Color(0xFF0A0F1D) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Menor Precio",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.sortByPrice) Color(0xFF0A0F1D) else Color(0xFF94A3B8)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!uiState.sortByPrice) SuperDealGreen else Color.Transparent)
                                .clickable { viewModel.setSortByPrice(false) }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                                .testTag("search_sort_date")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = if (!uiState.sortByPrice) Color(0xFF0A0F1D) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Por Fecha",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!uiState.sortByPrice) Color(0xFF0A0F1D) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                if (uiState.displayedDeals.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.sortByPrice) "Ordenados de menor a mayor precio base" else "Vuelos agrupados cronológicamente por día de salida",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Results items: grouped by date or sorted by price
        if (uiState.displayedDeals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron vuelos con los filtros seleccionados.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                }
            }
        } else if (uiState.sortByPrice) {
            items(uiState.displayedDeals, key = { it.id }) { deal ->
                FlightCard(
                    deal = deal,
                    onClick = { viewModel.openFlightDetail(deal) },
                    onAlertClick = { viewModel.openCreateAlert(deal) },
                    onRoundTripClick = { viewModel.startRoundTripFromDeal(deal) },
                    onLocatePromoClick = { viewModel.openPromoLocator(deal) }
                )
            }
        } else {
            // Group by date & show head-to-head competition
            val groupedByDate = uiState.displayedDeals.groupBy { it.departureDateFormatted }
            groupedByDate.forEach { (dateFormatted, dealsOnDate) ->
                item(key = "header_$dateFormatted") {
                    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = dateFormatted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = CyanNeon
                            )
                        }

                        // Head-to-head competition check
                        val distinctAirlines = dealsOnDate.map { it.airline }.distinct()
                        if (distinctAirlines.size > 1) {
                            val sortedOnDate = dealsOnDate.sortedBy { it.baseFareMxn }
                            val cheapest = sortedOnDate.first()
                            val competitor = sortedOnDate.last()
                            val difference = (competitor.baseFareMxn - cheapest.baseFareMxn).toInt()

                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF221634))
                                    .border(1.dp, HotDiscountPink.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CompareArrows,
                                        contentDescription = null,
                                        tint = HotDiscountPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Batalla de Precios: ${cheapest.airline.displayName} ($${cheapest.baseFareMxn.toInt()}) vs ${competitor.airline.displayName} ($${competitor.baseFareMxn.toInt()})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (difference > 0) "${cheapest.airline.displayName} es $$difference MXN más barata en este día" else "Empate en precio mínimo en este día.",
                                            fontSize = 10.sp,
                                            color = SuperDealGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                items(dealsOnDate, key = { it.id }) { deal ->
                    FlightCard(
                        deal = deal,
                        onClick = { viewModel.openFlightDetail(deal) },
                        onAlertClick = { viewModel.openCreateAlert(deal) },
                        onRoundTripClick = { viewModel.startRoundTripFromDeal(deal) },
                        onLocatePromoClick = { viewModel.openPromoLocator(deal) }
                    )
                }
            }
        }
    }
}
