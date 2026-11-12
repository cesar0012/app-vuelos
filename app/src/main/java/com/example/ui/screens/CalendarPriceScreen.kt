package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapCalls
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Airline
import com.example.model.FareBreakdown
import com.example.model.FlightDeal
import com.example.ui.CalendarSelectionStep
import com.example.ui.CalendarSortOption
import com.example.ui.CalendarTripType
import com.example.ui.CalendarViewMode
import com.example.ui.CheapTravelUiState
import com.example.ui.CheapTravelViewModel
import com.example.ui.TimeOfDayFilter
import com.example.ui.components.AirportSelectorDialog
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GoldenTag
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen
import com.example.util.BookingLinkHelper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun CalendarPriceScreen(
    uiState: CheapTravelUiState,
    viewModel: CheapTravelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOriginPicker by remember { mutableStateOf(false) }
    var showDestPicker by remember { mutableStateOf(false) }

    val isRoundTripMode = uiState.calendarTripType == CalendarTripType.ROUND_TRIP
    val isReturnStep = isRoundTripMode && uiState.calendarSelectionStep == CalendarSelectionStep.RETURN

    // Active matrix depending on whether the user is choosing Outbound or Return
    val activeMatrix = if (isReturnStep) {
        val minReturnDate = uiState.selectedOutboundDeal?.departureDate ?: LocalDate.now()
        uiState.calendarReturnMatrix.filterKeys { !it.isBefore(minReturnDate) }
    } else {
        uiState.calendarMatrix
    }

    // Filter deals according to user preferences (Airline & Time of day)
    val filteredDeals = remember(
        activeMatrix,
        uiState.calendarAirlineFilter,
        uiState.calendarDirectOnly,
        uiState.calendarTimeFilter,
        uiState.calendarSortOption
    ) {
        val baseList = activeMatrix.values.filter { deal ->
            val matchesAirline = uiState.calendarAirlineFilter == null || deal.airline == uiState.calendarAirlineFilter
            val matchesDirect = !uiState.calendarDirectOnly || deal.isNonStop
            val hour = deal.departureTime.take(2).toIntOrNull() ?: 12
            val matchesTime = when (uiState.calendarTimeFilter) {
                TimeOfDayFilter.ANY -> true
                TimeOfDayFilter.MORNING -> hour in 5..11
                TimeOfDayFilter.AFTERNOON -> hour in 12..17
                TimeOfDayFilter.NIGHT -> hour in 18..23
            }
            matchesAirline && matchesDirect && matchesTime
        }

        // Apply selected sort option
        val today = LocalDate.now()
        when (uiState.calendarSortOption) {
            CalendarSortOption.CHEAPEST -> baseList.sortedWith(compareBy({ it.baseFareMxn }, { it.departureDate }))
            CalendarSortOption.SOONEST -> baseList.sortedBy { it.departureDate }
            CalendarSortOption.BEST_VALUE -> baseList.sortedBy { deal ->
                // Composite score: Balances low fare with proximity (days away)
                val daysAway = ChronoUnit.DAYS.between(today, deal.departureDate).coerceAtLeast(0)
                deal.baseFareMxn + (daysAway * 12.0)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header with title and description
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Calendario & Plan de Vuelos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Text(
                text = "Compara fechas con tarifas reales desde $199 MXN, selecciona ida y vuelta y obtén los enlaces directos de compra.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        // 2. Trip Type Switcher (Solo Ida vs Viaje Redondo)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF131A2B))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Solo Ida
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isRoundTripMode) CyanNeon else Color.Transparent)
                        .clickable { viewModel.setCalendarTripType(CalendarTripType.ONE_WAY) }
                        .padding(vertical = 10.dp)
                        .testTag("trip_type_one_way"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = if (!isRoundTripMode) Color(0xFF0A0F1D) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Solo Ida",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isRoundTripMode) Color(0xFF0A0F1D) else Color(0xFF94A3B8)
                        )
                    }
                }

                // Viaje Redondo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isRoundTripMode) HotDiscountPink else Color.Transparent)
                        .clickable { viewModel.setCalendarTripType(CalendarTripType.ROUND_TRIP) }
                        .padding(vertical = 10.dp)
                        .testTag("trip_type_round_trip"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapCalls,
                            contentDescription = null,
                            tint = if (isRoundTripMode) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Viaje Redondo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRoundTripMode) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // 3. Route Selector Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF26334D), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Origin
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showOriginPicker = true }
                            .background(Color(0xFF192236))
                            .padding(10.dp)
                            .testTag("calendar_origin_button")
                    ) {
                        Text("Origen (Salida)", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${uiState.calendarOrigin.city} (${uiState.calendarOrigin.code})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.setCalendarRoute(uiState.calendarDestination, uiState.calendarOrigin)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Invertir Ruta",
                            tint = CyanNeon
                        )
                    }

                    // Destination
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showDestPicker = true }
                            .background(Color(0xFF192236))
                            .padding(10.dp)
                            .testTag("calendar_dest_button")
                    ) {
                        Text("Destino (Llegada)", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${uiState.calendarDestination.city} (${uiState.calendarDestination.code})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // 4. Two-Step Selector Tabs for Viaje Redondo
        if (isRoundTripMode) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF11192C))
                        .border(1.dp, Color(0xFF1E2A44), RoundedCornerShape(14.dp))
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isOutboundActive = uiState.calendarSelectionStep == CalendarSelectionStep.OUTBOUND
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isOutboundActive) Color(0xFF1D2A47) else Color.Transparent)
                            .clickable { viewModel.setCalendarSelectionStep(CalendarSelectionStep.OUTBOUND) }
                            .padding(10.dp)
                            .testTag("step_outbound_button")
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = null,
                                    tint = if (isOutboundActive) CyanNeon else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "1. Salida (${uiState.calendarOrigin.code} ➔ ${uiState.calendarDestination.code})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOutboundActive) CyanNeon else Color(0xFF94A3B8)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.selectedOutboundDeal?.let {
                                    "${it.dayAndMonthFormatted} • ${it.airline.displayName} (${FareBreakdown.formatCurrency(it.baseFareMxn)})"
                                } ?: "Elige fecha de ida",
                                fontSize = 11.sp,
                                color = if (uiState.selectedOutboundDeal != null) SuperDealGreen else Color(0xFF64748B),
                                fontWeight = if (uiState.selectedOutboundDeal != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    val isReturnActive = uiState.calendarSelectionStep == CalendarSelectionStep.RETURN
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isReturnActive) Color(0xFF1D2A47) else Color.Transparent)
                            .clickable { viewModel.setCalendarSelectionStep(CalendarSelectionStep.RETURN) }
                            .padding(10.dp)
                            .testTag("step_return_button")
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlightLand,
                                    contentDescription = null,
                                    tint = if (isReturnActive) HotDiscountPink else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2. Regreso (${uiState.calendarDestination.code} ➔ ${uiState.calendarOrigin.code})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isReturnActive) HotDiscountPink else Color(0xFF94A3B8)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.selectedReturnDeal?.let {
                                    "${it.dayAndMonthFormatted} • ${it.airline.displayName} (${FareBreakdown.formatCurrency(it.baseFareMxn)})"
                                } ?: if (uiState.selectedOutboundDeal == null) "Requiere fecha de ida" else "Elige fecha de vuelta",
                                fontSize = 11.sp,
                                color = if (uiState.selectedReturnDeal != null) HotDiscountPink else Color(0xFF64748B),
                                fontWeight = if (uiState.selectedReturnDeal != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // 5. Trip Plan Summary Card (When Outbound or Both are selected)
        if (uiState.selectedOutboundDeal != null) {
            item {
                TripPlanSummaryCard(
                    uiState = uiState,
                    viewModel = viewModel,
                    context = context
                )
            }
        }

        // 6. View Mode Switcher & Filters Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF131A2B))
                    .border(1.dp, Color(0xFF1F2B44), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // View Mode Tabs: Cuadrícula vs Lista
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A2338))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isGridMode = uiState.calendarViewMode == CalendarViewMode.MONTH_GRID
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isGridMode) Color(0xFF253454) else Color.Transparent)
                            .clickable { viewModel.setCalendarViewMode(CalendarViewMode.MONTH_GRID) }
                            .padding(vertical = 8.dp)
                            .testTag("mode_month_grid"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (isGridMode) CyanNeon else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Cuadrícula Calendario",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGridMode) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isGridMode) Color(0xFF253454) else Color.Transparent)
                            .clickable { viewModel.setCalendarViewMode(CalendarViewMode.LIST) }
                            .padding(vertical = 8.dp)
                            .testTag("mode_list"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewAgenda,
                                contentDescription = null,
                                tint = if (!isGridMode) CyanNeon else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Lista de Vuelos",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isGridMode) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // Sorting selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Ordenar por:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        SortChip(
                            label = "Más Barato",
                            selected = uiState.calendarSortOption == CalendarSortOption.CHEAPEST,
                            onClick = { viewModel.setCalendarSortOption(CalendarSortOption.CHEAPEST) }
                        )
                        SortChip(
                            label = "Más Próximo",
                            selected = uiState.calendarSortOption == CalendarSortOption.SOONEST,
                            onClick = { viewModel.setCalendarSortOption(CalendarSortOption.SOONEST) }
                        )
                        SortChip(
                            label = "Recomendado (Balance)",
                            selected = uiState.calendarSortOption == CalendarSortOption.BEST_VALUE,
                            onClick = { viewModel.setCalendarSortOption(CalendarSortOption.BEST_VALUE) }
                        )
                    }
                }

                // Airline filter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Aerolínea:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)

                    FilterPill(
                        label = "Todas",
                        selected = uiState.calendarAirlineFilter == null,
                        onClick = { viewModel.setCalendarAirlineFilter(null) }
                    )
                    FilterPill(
                        label = "Volaris",
                        selected = uiState.calendarAirlineFilter == Airline.VOLARIS,
                        onClick = { viewModel.setCalendarAirlineFilter(Airline.VOLARIS) }
                    )
                    FilterPill(
                        label = "Viva Aerobus",
                        selected = uiState.calendarAirlineFilter == Airline.VIVA_AEROBUS,
                        onClick = { viewModel.setCalendarAirlineFilter(Airline.VIVA_AEROBUS) }
                    )
                    FilterPill(
                        label = "Aeroméxico",
                        selected = uiState.calendarAirlineFilter == Airline.AEROMEXICO,
                        onClick = { viewModel.setCalendarAirlineFilter(Airline.AEROMEXICO) }
                    )
                }

                // Time of day filter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Horario:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)

                    FilterPill(
                        label = "Cualquiera",
                        selected = uiState.calendarTimeFilter == TimeOfDayFilter.ANY,
                        onClick = { viewModel.setCalendarTimeFilter(TimeOfDayFilter.ANY) }
                    )
                    FilterPill(
                        label = "Mañana (05-12h)",
                        selected = uiState.calendarTimeFilter == TimeOfDayFilter.MORNING,
                        onClick = { viewModel.setCalendarTimeFilter(TimeOfDayFilter.MORNING) }
                    )
                    FilterPill(
                        label = "Tarde (12-18h)",
                        selected = uiState.calendarTimeFilter == TimeOfDayFilter.AFTERNOON,
                        onClick = { viewModel.setCalendarTimeFilter(TimeOfDayFilter.AFTERNOON) }
                    )
                    FilterPill(
                        label = "Noche (18-24h)",
                        selected = uiState.calendarTimeFilter == TimeOfDayFilter.NIGHT,
                        onClick = { viewModel.setCalendarTimeFilter(TimeOfDayFilter.NIGHT) }
                    )
                }
            }
        }

        // Price Legend
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141C2E))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = SuperDealGreen, label = "Flash ≤ $249")
                LegendItem(color = CyanNeon, label = "Bajo ≤ $499")
                LegendItem(color = Color(0xFF94A3B8), label = "Regular > $500")
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isReturnStep) Icons.Default.FlightLand else Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = if (isReturnStep) HotDiscountPink else CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isReturnStep) {
                            "Vuelos de Regreso: ${uiState.calendarDestination.code} ➔ ${uiState.calendarOrigin.code}"
                        } else {
                            "Vuelos de Salida: ${uiState.calendarOrigin.code} ➔ ${uiState.calendarDestination.code}"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isReturnStep) HotDiscountPink else CyanNeon
                    )
                }

                if (uiState.selectedOutboundDeal != null || uiState.selectedReturnDeal != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.clearSelectedTrip() }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Reiniciar",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // 7. CONTENT: MONTH GRID or LIST VIEW
        if (uiState.calendarViewMode == CalendarViewMode.MONTH_GRID) {
            // MONTH CALENDAR GRID
            item {
                MonthCalendarGrid(
                    uiState = uiState,
                    viewModel = viewModel,
                    activeMatrix = activeMatrix,
                    isReturnStep = isReturnStep
                )
            }

            // DAY DETAIL SECTION (When a day on the calendar grid is tapped)
            val selectedDate = uiState.calendarSelectedDayDate ?: activeMatrix.keys.minOrNull()
            if (selectedDate != null) {
                item {
                    val dayFlights = remember(selectedDate, isReturnStep) {
                        viewModel.getFlightsForDate(selectedDate, isReturnStep)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF131D31))
                            .border(1.dp, Color(0xFF202E4C), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Vuelos del ${selectedDate.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy", Locale("es", "MX"))).replaceFirstChar { it.uppercase() }}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${dayFlights.size} vuelo(s) operados con tarifa real",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            // Quick Google Flights cross-check button
                            dayFlights.firstOrNull()?.let { sampleDeal ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E2B47))
                                        .clickable {
                                            val url = BookingLinkHelper.getGoogleFlightsUrl(sampleDeal)
                                            BookingLinkHelper.openWebUrl(context, url)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Launch,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Google Flights",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanNeon
                                    )
                                }
                            }
                        }

                        if (dayFlights.isEmpty()) {
                            Text(
                                text = "No hay vuelos programados para esta fecha en la ruta seleccionada.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        } else {
                            dayFlights.forEach { deal ->
                                CalendarFlightDayRow(
                                    deal = deal,
                                    isSelected = if (isReturnStep) uiState.selectedReturnDeal?.id == deal.id else uiState.selectedOutboundDeal?.id == deal.id,
                                    isReturnStep = isReturnStep,
                                    isRoundTripMode = isRoundTripMode,
                                    onSelect = {
                                        if (!isRoundTripMode) {
                                            viewModel.openFlightDetail(deal)
                                        } else {
                                            if (isReturnStep) {
                                                viewModel.selectCalendarReturn(deal)
                                                Toast.makeText(context, "¡Regreso seleccionado!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                viewModel.selectCalendarOutbound(deal)
                                                Toast.makeText(context, "¡Ida seleccionada! Ahora elige tu regreso", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onPromoLocator = { viewModel.openPromoLocator(deal) }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // LIST VIEW (Sorted and filtered)
            if (filteredDeals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isReturnStep) {
                                "No hay vuelos de regreso disponibles con los filtros seleccionados."
                            } else {
                                "No hay fechas disponibles con los filtros aplicados."
                            },
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredDeals, key = { "${if (isReturnStep) "ret_" else "out_"}${it.id}" }) { deal ->
                    val isUltraLow = deal.baseFareMxn <= 249.0
                    val isLow = deal.baseFareMxn <= 499.0
                    val isSelectedOutbound = uiState.selectedOutboundDeal?.id == deal.id
                    val isSelectedReturn = uiState.selectedReturnDeal?.id == deal.id
                    val isSelected = isSelectedOutbound || isSelectedReturn

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                if (!isRoundTripMode) {
                                    viewModel.openFlightDetail(deal)
                                } else {
                                    if (isReturnStep) {
                                        viewModel.selectCalendarReturn(deal)
                                        Toast.makeText(
                                            context,
                                            "¡Regreso seleccionado para el ${deal.dayAndMonthFormatted}!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        viewModel.selectCalendarOutbound(deal)
                                        Toast.makeText(
                                            context,
                                            "¡Ida seleccionada! Ahora elige tu fecha de regreso",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            .background(
                                when {
                                    isSelected -> Color(0xFF1E2B4D)
                                    isUltraLow -> Color(0xFF152A28)
                                    isLow -> Color(0xFF14243B)
                                    else -> Color(0xFF121929)
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = when {
                                    isSelected -> if (isReturnStep) HotDiscountPink else CyanNeon
                                    isUltraLow -> SuperDealGreen
                                    isLow -> CyanNeon
                                    else -> Color(0xFF222F45)
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                            .testTag("calendar_deal_${deal.id}")
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
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isSelected -> if (isReturnStep) HotDiscountPink else CyanNeon
                                                isUltraLow -> SuperDealGreen
                                                else -> CyanNeon
                                            }
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = deal.departureDate.format(DateTimeFormatter.ofPattern("d\nEEE", Locale("es", "MX"))).uppercase(),
                                        color = Color(0xFF0A0F1D),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 13.sp
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = deal.airline.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        deal.promoLabel?.let {
                                            Text(
                                                text = " • $it",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUltraLow) HotDiscountPink else SuperDealGreen
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${deal.departureTime} - ${deal.arrivalTime} • ${deal.flightNumber}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "${deal.stopsSummary} • ${deal.aircraftModel}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (deal.isNonStop) SuperDealGreen else GoldenTag
                                    )
                                    if (isSelected) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isReturnStep) HotDiscountPink else CyanNeon,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "SELECCIONADO",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isReturnStep) HotDiscountPink else CyanNeon
                                            )
                                        }
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = FareBreakdown.formatCurrency(deal.baseFareMxn),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isUltraLow) SuperDealGreen else if (isLow) CyanNeon else Color.White
                                )
                                Text(
                                    text = "+ TUA ${FareBreakdown.formatCurrency(deal.tuaMxn)}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF1E283E))
                                        .clickable { viewModel.openPromoLocator(deal) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("quick_link_${deal.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Launch,
                                        contentDescription = "Ver promo",
                                        tint = CyanNeon,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Ver Promo",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanNeon
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showOriginPicker) {
        AirportSelectorDialog(
            title = "Aeropuerto de Origen",
            isMultiSelect = false,
            selectedAirports = listOf(uiState.calendarOrigin),
            onAirportsSelected = {
                if (it.isNotEmpty()) viewModel.setCalendarRoute(it.first(), uiState.calendarDestination)
            },
            onDismiss = { showOriginPicker = false }
        )
    }

    if (showDestPicker) {
        AirportSelectorDialog(
            title = "Aeropuerto de Destino",
            isMultiSelect = false,
            selectedAirports = listOf(uiState.calendarDestination),
            onAirportsSelected = {
                if (it.isNotEmpty()) viewModel.setCalendarRoute(uiState.calendarOrigin, it.first())
            },
            onDismiss = { showDestPicker = false }
        )
    }
}

/**
 * Interactive Monthly Calendar Grid (7 columns: LUN to DOM)
 */
@Composable
private fun MonthCalendarGrid(
    uiState: CheapTravelUiState,
    viewModel: CheapTravelViewModel,
    activeMatrix: Map<LocalDate, FlightDeal>,
    isReturnStep: Boolean
) {
    val displayedMonth = uiState.calendarDisplayedMonth
    val daysInMonth = displayedMonth.lengthOfMonth()
    val firstDayOfMonth = displayedMonth.atDay(1)
    // Day of week: Monday = 1, Sunday = 7. Empty slots before 1st = dayOfWeek - 1
    val startOffset = firstDayOfMonth.dayOfWeek.value - 1
    val totalCells = startOffset + daysInMonth
    val totalRows = (totalCells + 6) / 7

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121B2F)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF22314F), RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Month Header with Prev/Next buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.previousCalendarMonth() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Mes anterior",
                        tint = CyanNeon
                    )
                }

                Text(
                    text = displayedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "MX"))).uppercase(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                IconButton(
                    onClick = { viewModel.nextCalendarMonth() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Mes siguiente",
                        tint = CyanNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day of Week Header: LUN, MAR, MIÉ, JUE, VIE, SÁB, DOM
            val weekDays = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDays.forEach { dayName ->
                    Text(
                        text = dayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid Rows
            for (row in 0 until totalRows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - startOffset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val cellDate = displayedMonth.atDay(dayNumber)
                            val deal = activeMatrix[cellDate]
                            val isSelectedOutbound = uiState.selectedOutboundDeal?.departureDate == cellDate
                            val isSelectedReturn = uiState.selectedReturnDeal?.departureDate == cellDate
                            val isSelected = isSelectedOutbound || isSelectedReturn
                            val isDaySelected = uiState.calendarSelectedDayDate == cellDate

                            val isUltraLow = deal != null && deal.baseFareMxn <= 249.0
                            val isLow = deal != null && deal.baseFareMxn <= 499.0

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.85f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isSelected -> if (isReturnStep) HotDiscountPink else CyanNeon
                                            isDaySelected -> Color(0xFF1F3155)
                                            isUltraLow -> Color(0xFF132A25)
                                            deal != null -> Color(0xFF182238)
                                            else -> Color(0xFF101524)
                                        }
                                    )
                                    .border(
                                        width = if (isDaySelected || isSelected) 1.5.dp else 0.5.dp,
                                        color = when {
                                            isSelected -> Color.White
                                            isDaySelected -> CyanNeon
                                            isUltraLow -> SuperDealGreen
                                            deal != null -> Color(0xFF283856)
                                            else -> Color.Transparent
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = deal != null) {
                                        viewModel.selectCalendarDay(cellDate)
                                    }
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Day Number
                                    Text(
                                        text = "$dayNumber",
                                        fontSize = 11.sp,
                                        fontWeight = if (deal != null) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> Color(0xFF0A0F1D)
                                            deal != null -> Color.White
                                            else -> Color(0xFF475569)
                                        }
                                    )

                                    // Price Badge
                                    if (deal != null) {
                                        Text(
                                            text = "$${deal.baseFareMxn.toInt()}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = when {
                                                isSelected -> Color(0xFF0A0F1D)
                                                isUltraLow -> SuperDealGreen
                                                isLow -> CyanNeon
                                                else -> Color(0xFFCBD5E1)
                                            },
                                            maxLines = 1
                                        )

                                        // Airline indicator dot
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (deal.airline) {
                                                        Airline.VOLARIS -> Color(0xFFA855F7)
                                                        Airline.VIVA_AEROBUS -> Color(0xFF22C55E)
                                                        Airline.AEROMEXICO -> Color(0xFF3B82F6)
                                                        Airline.MEXICANA -> Color(0xFF0D9488)
                                                    }
                                                )
                                        )
                                    }
                                }
                            }
                        } else {
                            // Empty slot outside current month
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Row representing a flight option within a selected date on the calendar grid
 */
@Composable
private fun CalendarFlightDayRow(
    deal: FlightDeal,
    isSelected: Boolean,
    isReturnStep: Boolean,
    isRoundTripMode: Boolean,
    onSelect: () -> Unit,
    onPromoLocator: () -> Unit
) {
    val isUltraLow = deal.baseFareMxn <= 249.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF1E2F54) else Color(0xFF18233C))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) (if (isReturnStep) HotDiscountPink else CyanNeon) else Color(0xFF263554),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(10.dp)
            .testTag("flight_row_${deal.id}")
    ) {
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
                        text = deal.airline.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "• ${deal.flightNumber}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    if (isUltraLow) {
                        Text(
                            text = "PROMO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = SuperDealGreen
                        )
                    }
                }
                Text(
                    text = "${deal.departureTime} ➔ ${deal.arrivalTime} (${deal.durationFormatted})",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = deal.stopsSummary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deal.isNonStop) SuperDealGreen else GoldenTag
                    )
                    Text(
                        text = "• ${deal.aircraftModel}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = FareBreakdown.formatCurrency(deal.baseFareMxn),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isUltraLow) SuperDealGreen else CyanNeon
                )
                Text(
                    text = "+ TUA ${FareBreakdown.formatCurrency(deal.tuaMxn)}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF22314E))
                        .clickable { onPromoLocator() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Localizar Promo",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon
                    )
                }
            }
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) CyanNeon else Color(0xFF1E283E))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFF0A0F1D) else Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color(0xFF25375A) else Color(0xFF182033))
            .border(
                width = 1.dp,
                color = if (selected) CyanNeon else Color(0xFF24304A),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun TripPlanSummaryCard(
    uiState: CheapTravelUiState,
    viewModel: CheapTravelViewModel,
    context: Context
) {
    val outbound = uiState.selectedOutboundDeal ?: return
    val returnDeal = uiState.selectedReturnDeal
    val isSameAirline = returnDeal != null && outbound.airline == returnDeal.airline

    val totalBase = outbound.baseFareMxn + (returnDeal?.baseFareMxn ?: 0.0)
    val totalTua = outbound.tuaMxn + (returnDeal?.tuaMxn ?: 0.0)
    val grandTotal = totalBase + totalTua

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101A2F)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(CyanNeon, HotDiscountPink)),
                RoundedCornerShape(20.dp)
            )
            .testTag("trip_plan_summary_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuperDealGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (returnDeal != null) "PLAN DE VIAJE REDONDO" else "PLAN DE VUELO DE IDA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanNeon
                    )
                }

                IconButton(
                    onClick = { viewModel.clearSelectedTrip() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reiniciar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Outbound Details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF16233B))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "IDA: ${outbound.origin.code} ➔ ${outbound.destination.code}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanNeon
                            )
                        }
                        Text(
                            text = "${outbound.departureDateFormatted} • ${outbound.departureTime} • ${outbound.airline.displayName} (${outbound.flightNumber})",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "${outbound.stopsSummary} • ${outbound.aircraftModel}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (outbound.isNonStop) SuperDealGreen else GoldenTag
                        )
                    }

                    Text(
                        text = FareBreakdown.formatCurrency(outbound.baseFareMxn),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = SuperDealGreen
                    )
                }
            }

            // Return Details
            if (returnDeal != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1F1C36))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlightLand,
                                    contentDescription = null,
                                    tint = HotDiscountPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "REGRESO: ${returnDeal.origin.code} ➔ ${returnDeal.destination.code}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HotDiscountPink
                                )
                            }
                            Text(
                                text = "${returnDeal.departureDateFormatted} • ${returnDeal.departureTime} • ${returnDeal.airline.displayName} (${returnDeal.flightNumber})",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                            Text(
                                text = "${returnDeal.stopsSummary} • ${returnDeal.aircraftModel}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (returnDeal.isNonStop) SuperDealGreen else GoldenTag
                            )
                        }

                        Text(
                            text = FareBreakdown.formatCurrency(returnDeal.baseFareMxn),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = HotDiscountPink
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = GoldenTag,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Toca cualquier fecha abajo para agregar tu vuelo de regreso en promoción",
                        fontSize = 11.sp,
                        color = GoldenTag,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Base: ${FareBreakdown.formatCurrency(totalBase)}",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "+ TUA (${if (returnDeal != null) "ambos aeropuertos" else outbound.origin.code}): ${FareBreakdown.formatCurrency(totalTua)}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = FareBreakdown.formatCurrency(grandTotal),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SuperDealGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Booking Buttons
            if (returnDeal != null) {
                if (isSameAirline) {
                    // SAME AIRLINE: SINGLE ROUND-TRIP BUTTON
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF183B2B))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuperDealGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "MISMA AEROLÍNEA: Viaje redondo en ${outbound.airline.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuperDealGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val url = BookingLinkHelper.getRoundTripBookingUrl(outbound, returnDeal, uiState.passengers)
                            BookingLinkHelper.openWebUrl(context, url)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reserve_roundtrip_same_airline"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = outbound.airline.brandColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reservar Viaje Redondo en ${outbound.airline.displayName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // SMART MIX: TWO DIFFERENT AIRLINES
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
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "SMART MIX: Combinación más barata de 2 aerolíneas",
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
                        // Book Outbound
                        Button(
                            onClick = {
                                val url = BookingLinkHelper.getDirectBookingUrl(outbound, uiState.passengers)
                                BookingLinkHelper.openWebUrl(context, url)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("reserve_smart_mix_outbound"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = outbound.airline.brandColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "1. Ida ${outbound.airline.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        // Book Return
                        Button(
                            onClick = {
                                val url = BookingLinkHelper.getDirectBookingUrl(returnDeal, uiState.passengers)
                                BookingLinkHelper.openWebUrl(context, url)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("reserve_smart_mix_return"),
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
                            val url1 = BookingLinkHelper.getDirectBookingUrl(outbound, uiState.passengers)
                            val url2 = BookingLinkHelper.getDirectBookingUrl(returnDeal, uiState.passengers)
                            BookingLinkHelper.openBothBookingUrls(context, url1, url2)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("open_both_smart_mix_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanNeon)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Abrir Ambos Enlaces para Reservar", fontSize = 11.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Outbound only button
                Button(
                    onClick = {
                        val url = BookingLinkHelper.getDirectBookingUrl(outbound, uiState.passengers)
                        BookingLinkHelper.openWebUrl(context, url)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("reserve_outbound_only_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = outbound.airline.brandColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reservar Ida en ${outbound.airline.displayName} (${FareBreakdown.formatCurrency(outbound.baseFareMxn)})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: View breakdown calculator + Copy share text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        viewModel.openFlightDetail(outbound, returnDeal)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("calculator_button_from_plan"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF1E283E),
                        contentColor = CyanNeon
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Calculadora & TUA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val breakdown = outbound.calculateRoundTripBreakdown(
                            returnDeal,
                            uiState.passengers,
                            uiState.baggageOption,
                            uiState.seatOption
                        )
                        val summary = BookingLinkHelper.buildTripShareSummary(outbound, returnDeal, uiState.passengers, breakdown)
                        BookingLinkHelper.copyToClipboard(context, "Plan de Vuelo", summary)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("copy_plan_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copiar Plan", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFFCBD5E1))
    }
}
