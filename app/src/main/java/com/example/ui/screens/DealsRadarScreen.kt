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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Airline
import com.example.model.FareBreakdown
import com.example.model.FlightDeal
import com.example.ui.CheapTravelUiState
import com.example.ui.CheapTravelViewModel
import com.example.ui.components.FlightCard
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen

@Composable
fun DealsRadarScreen(
    uiState: CheapTravelUiState,
    viewModel: CheapTravelViewModel,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner: Volaris & Viva $199 Flash Promo
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2E0854), Color(0xFF0F1E38), Color(0xFF00363A))
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(HotDiscountPink, CyanNeon)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HotDiscountPink)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VENTA RELÁMPAGO MEXICANA",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Text(
                            text = "DESDE $199 MXN",
                            color = CyanNeon,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vuelos con Tarifa Zero & Promos Flash",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Detectamos fechas futuras con tarifas base ultra bajas en Volaris, Viva Aerobus y Aeroméxico. Calcula el total real sumando la TUA por aeropuerto.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Departure origins & Arrival destinations quick bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Origins bar
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x66000000))
                                .clickable { viewModel.setShowOriginPicker(true) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Salidas: ",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            val originsSummary = uiState.selectedOrigins.joinToString("+") { it.code }
                            Text(
                                text = originsSummary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanNeon,
                                maxLines = 1
                            )
                        }

                        // Destinations bar
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x66000000))
                                .clickable { viewModel.setShowDestinationPicker(true) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightLand,
                                contentDescription = null,
                                tint = HotDiscountPink,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Destinos: ",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            val destSummary = if (uiState.selectedDestinations.isEmpty()) "Todos" else uiState.selectedDestinations.joinToString("+") { it.code }
                            Text(
                                text = destSummary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HotDiscountPink,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Active removable airport chips (Instant remove with ✕)
        if (uiState.selectedOrigins.isNotEmpty() || uiState.selectedDestinations.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.selectedOrigins, key = { "radar_org_${it.code}" }) { airport ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF16233B))
                                .border(1.dp, CyanNeon, RoundedCornerShape(16.dp))
                                .clickable { viewModel.removeOrigin(airport) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("radar_chip_origin_${airport.code}")
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
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar",
                                    tint = HotDiscountPink,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    items(uiState.selectedDestinations, key = { "radar_dst_${it.code}" }) { airport ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF26182C))
                                .border(1.dp, HotDiscountPink, RoundedCornerShape(16.dp))
                                .clickable { viewModel.removeDestination(airport) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("radar_chip_dest_${airport.code}")
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
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar",
                                    tint = HotDiscountPink,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Category Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = !uiState.onlyBeaches && !uiState.onlyInternational && uiState.selectedAirlineFilter == null,
                        onClick = {
                            viewModel.setBeachFilter(false)
                            viewModel.setInternationalFilter(false)
                            viewModel.setAirlineFilter(null)
                        },
                        label = { Text("Todas las Ofertas", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanNeon,
                            selectedLabelColor = Color(0xFF0A0F1D)
                        ),
                        modifier = Modifier.testTag("filter_all_deals")
                    )
                }

                item {
                    FilterChip(
                        selected = uiState.onlyBeaches,
                        onClick = { viewModel.setBeachFilter(!uiState.onlyBeaches) },
                        label = { Text("Playas (Cancún, Vallarta, Cabos)", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.BeachAccess, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuperDealGreen,
                            selectedLabelColor = Color(0xFF0A0F1D)
                        ),
                        modifier = Modifier.testTag("filter_beaches")
                    )
                }

                item {
                    FilterChip(
                        selected = uiState.onlyInternational,
                        onClick = { viewModel.setInternationalFilter(!uiState.onlyInternational) },
                        label = { Text("Internacionales (EE.UU., Centroam.)", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanNeon,
                            selectedLabelColor = Color(0xFF0A0F1D)
                        ),
                        modifier = Modifier.testTag("filter_international")
                    )
                }

                item {
                    FilterChip(
                        selected = uiState.selectedAirlineFilter == Airline.VOLARIS,
                        onClick = {
                            viewModel.setAirlineFilter(if (uiState.selectedAirlineFilter == Airline.VOLARIS) null else Airline.VOLARIS)
                        },
                        label = { Text("Solo Volaris ($199)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Airline.VOLARIS.brandColor,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_volaris")
                    )
                }

                item {
                    FilterChip(
                        selected = uiState.selectedAirlineFilter == Airline.VIVA_AEROBUS,
                        onClick = {
                            viewModel.setAirlineFilter(if (uiState.selectedAirlineFilter == Airline.VIVA_AEROBUS) null else Airline.VIVA_AEROBUS)
                        },
                        label = { Text("Solo Viva Aerobus", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Airline.VIVA_AEROBUS.brandColor,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_viva")
                    )
                }
            }
        }

        // Section Title with results count, accessible sort toggle & max price filter
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Vuelos encontrados (${uiState.displayedDeals.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Hasta ${FareBreakdown.formatCurrency(uiState.maxBaseFareFilter)} base",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
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
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("radar_sort_price")
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
                                        text = "Precio",
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
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("radar_sort_date")
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
                                        text = "Fecha",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!uiState.sortByPrice) Color(0xFF0A0F1D) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { showFilterSheet = !showFilterSheet },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF162035))
                                .testTag("toggle_filters_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtros",
                                tint = CyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (showFilterSheet) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141D30))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Filtrar tarifa base máxima:", fontSize = 12.sp, color = Color.White)
                            Text(
                                text = FareBreakdown.formatCurrency(uiState.maxBaseFareFilter),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HotDiscountPink
                            )
                        }
                        Slider(
                            value = uiState.maxBaseFareFilter.toFloat(),
                            onValueChange = { viewModel.setMaxBaseFare(it.toDouble()) },
                            valueRange = 199f..3000f,
                            steps = 28,
                            colors = SliderDefaults.colors(
                                thumbColor = CyanNeon,
                                activeTrackColor = CyanNeon
                            )
                        )
                    }
                }
            }
        }

        // Flight Cards List
        if (uiState.displayedDeals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No encontramos vuelos con estos filtros",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Aumenta la tarifa máxima o prueba seleccionando cualquier destino.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
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
                item(key = "radar_header_$dateFormatted") {
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
