package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.FlightDeal
import com.example.model.TimePeriod
import com.example.model.TravelDate
import com.example.ui.components.FlightDealCard
import com.example.ui.components.TripPlanSummaryCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CardHighest
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PinkTint
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.BookingLinkHelper
import com.example.viewmodel.CalendarDayCell
import com.example.viewmodel.CalendarSortOption
import com.example.viewmodel.CalendarUiState
import com.example.viewmodel.CalendarViewMode
import com.example.viewmodel.RoundTripStep
import com.example.viewmodel.TripMode

@Composable
fun CalendarPriceScreen(
    state: CalendarUiState,
    onTripModeChange: (TripMode) -> Unit,
    onStepChange: (RoundTripStep) -> Unit,
    onRouteChange: (String, String) -> Unit,
    onSwapRoute: () -> Unit,
    onViewModeChange: (CalendarViewMode) -> Unit,
    onSortOptionChange: (CalendarSortOption) -> Unit,
    onAirlineFilterChange: (Airline?) -> Unit,
    onDirectOnlyChange: (Boolean) -> Unit,
    onTimePeriodChange: (TimePeriod?) -> Unit,
    onNavigateMonth: (Int) -> Unit,
    onDateClicked: (TravelDate) -> Unit,
    onSelectFlightForPlan: (FlightDeal) -> Unit,
    onOpenCalculator: (FlightDeal, FlightDeal?) -> Unit,
    onOpenPromoLocator: (FlightDeal) -> Unit,
    onClearPlan: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOriginDropdown by remember { mutableStateOf(false) }
    var showDestDropdown by remember { mutableStateOf(false) }

    val originAirports = remember { AirportData.mexicanAirports }
    val destAirports = remember(state.availableRoutesDestinations) {
        val reachable = state.availableRoutesDestinations.mapNotNull { AirportData.findByCode(it) }
        if (reachable.isNotEmpty()) reachable else AirportData.airports.filter { it.code != state.originCode }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNightBg)
            .testTag("calendar_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Mode + Route Selector Card
        item(key = "calendar_header_controls") {
            Surface(
                color = CardElevated,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Solo Ida vs Viaje Redondo
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        TripMode.entries.forEachIndexed { idx, mode ->
                            SegmentedButton(
                                selected = state.tripMode == mode,
                                onClick = { onTripModeChange(mode) },
                                shape = SegmentedButtonDefaults.itemShape(idx, TripMode.entries.size),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = CyanTint,
                                    activeContentColor = NeonCyan,
                                    inactiveContainerColor = DeepNightBg,
                                    inactiveContentColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("calendar_mode_${mode.name.lowercase()}")
                            ) {
                                Text(mode.label, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Guided 2-step indicator for Round Trip
                    if (state.tripMode == TripMode.ROUND_TRIP) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StepPill(
                                label = "1. Salida" + (state.selectedOutboundDate?.let { " (${it.shortLabelEs})" } ?: ""),
                                isActive = state.roundTripStep == RoundTripStep.OUTBOUND,
                                accentColor = NeonCyan,
                                onClick = { onStepChange(RoundTripStep.OUTBOUND) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("step_outbound_pill")
                            )
                            StepPill(
                                label = "2. Regreso" + (state.selectedReturnDate?.let { " (${it.shortLabelEs})" } ?: ""),
                                isActive = state.roundTripStep == RoundTripStep.RETURN,
                                accentColor = NeonPink,
                                onClick = { onStepChange(RoundTripStep.RETURN) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("step_return_pill")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Route selector (Origin <-> Destination with SwapHoriz)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            RouteAirportButton(
                                label = "Origen",
                                code = state.originCode,
                                city = AirportData.findByCode(state.originCode)?.city ?: state.originCode,
                                accentColor = NeonCyan,
                                onClick = { showOriginDropdown = true },
                                modifier = Modifier.testTag("calendar_origin_picker")
                            )
                            DropdownMenu(
                                expanded = showOriginDropdown,
                                onDismissRequest = { showOriginDropdown = false },
                                modifier = Modifier.background(SurfaceNavy)
                            ) {
                                originAirports.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = TextPrimary) },
                                        onClick = {
                                            showOriginDropdown = false
                                            onRouteChange(ap.code, state.destinationCode)
                                        }
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onSwapRoute,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(CircleShape)
                                .background(CardHighest)
                                .testTag("calendar_swap_route_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Invertir ruta",
                                tint = NeonCyan
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            RouteAirportButton(
                                label = "Destino",
                                code = state.destinationCode,
                                city = AirportData.findByCode(state.destinationCode)?.city ?: state.destinationCode,
                                accentColor = NeonPink,
                                onClick = { showDestDropdown = true },
                                modifier = Modifier.testTag("calendar_dest_picker")
                            )
                            DropdownMenu(
                                expanded = showDestDropdown,
                                onDismissRequest = { showDestDropdown = false },
                                modifier = Modifier.background(SurfaceNavy)
                            ) {
                                destAirports.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = TextPrimary) },
                                        onClick = {
                                            showDestDropdown = false
                                            onRouteChange(state.originCode, ap.code)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // View Toggle: MONTH_GRID vs LIST
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        CalendarViewMode.entries.forEachIndexed { idx, mode ->
                            SegmentedButton(
                                selected = state.viewMode == mode,
                                onClick = { onViewModeChange(mode) },
                                shape = SegmentedButtonDefaults.itemShape(idx, CalendarViewMode.entries.size),
                                icon = {
                                    Icon(
                                        imageVector = if (mode == CalendarViewMode.MONTH_GRID) Icons.Default.CalendarMonth else Icons.AutoMirrored.Filled.List,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = PinkTint,
                                    activeContentColor = NeonPink,
                                    inactiveContainerColor = DeepNightBg,
                                    inactiveContentColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("calendar_view_${mode.name.lowercase()}")
                            ) {
                                Text(mode.label)
                            }
                        }
                    }
                }
            }
        }

        // 2. Smart Sort & Filters Row
        item(key = "calendar_filters") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CalendarSortOption.entries) { opt ->
                        val active = state.sortOption == opt
                        FilterChip(
                            selected = active,
                            onClick = { onSortOptionChange(opt) },
                            label = { Text(opt.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanTint,
                                selectedLabelColor = NeonCyan,
                                containerColor = CardElevated,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("cal_sort_${opt.name.lowercase()}")
                        )
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item(key = "cal_direct") {
                        FilterChip(
                            selected = state.directOnly,
                            onClick = { onDirectOnlyChange(!state.directOnly) },
                            label = { Text("Solo Directos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen.copy(alpha = 0.2f),
                                selectedLabelColor = NeonGreen,
                                containerColor = CardElevated,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("cal_filter_direct_only")
                        )
                    }
                    items(Airline.entries) { airline ->
                        val active = state.airlineFilter == airline
                        FilterChip(
                            selected = active,
                            onClick = { onAirlineFilterChange(if (active) null else airline) },
                            label = { Text(airline.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = airline.brandColor.copy(alpha = 0.24f),
                                selectedLabelColor = TextPrimary,
                                containerColor = CardElevated,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("cal_filter_airline_${airline.iataCode.lowercase()}")
                        )
                    }
                    items(TimePeriod.entries) { period ->
                        val active = state.timePeriodFilter == period
                        FilterChip(
                            selected = active,
                            onClick = { onTimePeriodChange(if (active) null else period) },
                            label = { Text(period.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinkTint,
                                selectedLabelColor = NeonPink,
                                containerColor = CardElevated,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("cal_filter_time_${period.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // 3. Calendar Grid OR Chronological List
        if (state.viewMode == CalendarViewMode.MONTH_GRID) {
            item(key = "month_grid_card") {
                MonthGridCard(
                    state = state,
                    onPrevMonth = { onNavigateMonth(-1) },
                    onNextMonth = { onNavigateMonth(1) },
                    onDayClicked = onDateClicked
                )
            }

            // Day details list below the calendar
            val activeDate = if (state.tripMode == TripMode.ROUND_TRIP && state.roundTripStep == RoundTripStep.RETURN) {
                state.selectedReturnDate
            } else {
                state.selectedOutboundDate
            }

            if (activeDate != null) {
                item(key = "day_details_hdr") {
                    val legText = if (state.tripMode == TripMode.ROUND_TRIP && state.roundTripStep == RoundTripStep.RETURN) {
                        "Vuelos de Regreso · ${state.destinationCode} -> ${state.originCode} (${activeDate.fullDayLabelEs})"
                    } else {
                        "Vuelos de Salida · ${state.originCode} -> ${state.destinationCode} (${activeDate.fullDayLabelEs})"
                    }
                    Text(
                        text = legText,
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan
                    )
                }

                if (state.activeDayFlights.isEmpty()) {
                    item(key = "day_no_flights") {
                        EmptyDealsBox("No hay vuelos para este día con los filtros seleccionados. Prueba otro día o quita filtros.")
                    }
                } else {
                    items(state.activeDayFlights, key = { "cal_day_flight_${it.id}" }) { deal ->
                        val isSelected = deal.id == state.selectedOutboundFlight?.id ||
                            deal.id == state.selectedReturnFlight?.id
                        val isReturnLeg = state.tripMode == TripMode.ROUND_TRIP &&
                            state.roundTripStep == RoundTripStep.RETURN
                        FlightDealCard(
                            deal = deal,
                            isSelectedInPlan = isSelected,
                            selectButtonLabel = if (isSelected) {
                                "Elegido"
                            } else if (isReturnLeg) {
                                "Elegir Vuelta"
                            } else {
                                "Elegir Ida"
                            },
                            onSelectForPlan = { onSelectFlightForPlan(deal) },
                            onOpenCalculator = { onOpenCalculator(deal, null) },
                            onVerifyGoogleFlights = {
                                BookingLinkHelper.openExternalUrl(
                                    context,
                                    BookingLinkHelper.oneWayPromoDayUrl(
                                        deal.airline,
                                        deal.originCode,
                                        deal.destinationCode,
                                        deal.departureDate.isoString
                                    )
                                )
                            },
                            onOpenPromoLocator = { onOpenPromoLocator(deal) }
                        )
                    }
                }
            } else {
                item(key = "tap_day_hint") {
                    Surface(
                        color = CyanTint,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Toca cualquier fecha con precio en el calendario para ver horarios, escalas y elegir tu vuelo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        } else {
            // LIST Mode
            item(key = "list_mode_hdr") {
                val legTitle = if (state.tripMode == TripMode.ROUND_TRIP && state.roundTripStep == RoundTripStep.RETURN) {
                    "Lista de Regreso: ${state.destinationCode} -> ${state.originCode} (${state.chronologicalFlights.size})"
                } else {
                    "Lista de Salida: ${state.originCode} -> ${state.destinationCode} (${state.chronologicalFlights.size})"
                }
                Text(
                    text = legTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
            }
            if (state.chronologicalFlights.isEmpty()) {
                item(key = "list_empty") {
                    EmptyDealsBox("No hay vuelos disponibles con los filtros elegidos.")
                }
            } else {
                items(state.chronologicalFlights, key = { "cal_list_${it.id}" }) { deal ->
                    val isSelected = deal.id == state.selectedOutboundFlight?.id ||
                        deal.id == state.selectedReturnFlight?.id
                    val isReturnLeg = state.tripMode == TripMode.ROUND_TRIP &&
                        state.roundTripStep == RoundTripStep.RETURN
                    FlightDealCard(
                        deal = deal,
                        isSelectedInPlan = isSelected,
                        selectButtonLabel = if (isSelected) {
                            "Elegido"
                        } else if (isReturnLeg) {
                            "Elegir Vuelta"
                        } else {
                            "Elegir Ida"
                        },
                        onSelectForPlan = { onSelectFlightForPlan(deal) },
                        onOpenCalculator = { onOpenCalculator(deal, null) },
                        onVerifyGoogleFlights = {
                            BookingLinkHelper.openExternalUrl(
                                context,
                                BookingLinkHelper.oneWayPromoDayUrl(
                                    deal.airline,
                                    deal.originCode,
                                    deal.destinationCode,
                                    deal.departureDate.isoString
                                )
                            )
                        },
                        onOpenPromoLocator = { onOpenPromoLocator(deal) }
                    )
                }
            }
        }

        // 4. Trip Plan Summary Card (when outbound selected)
        if (state.selectedOutboundFlight != null) {
            item(key = "trip_plan_summary") {
                TripPlanSummaryCard(
                    tripMode = state.tripMode,
                    outbound = state.selectedOutboundFlight,
                    returnFlight = state.selectedReturnFlight,
                    onOpenCalculator = onOpenCalculator,
                    onClearPlan = onClearPlan,
                    onPlanCopied = onShowMessage
                )
            }
        }
    }
}

@Composable
private fun StepPill(
    label: String,
    isActive: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isActive) accentColor.copy(alpha = 0.18f) else DeepNightBg,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isActive) accentColor else CardBorder),
        modifier = modifier
            .heightIn(min = 40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) accentColor else TextSecondary,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RouteAirportButton(
    label: String,
    code: String,
    city: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DeepNightBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(text = code, style = MaterialTheme.typography.titleLarge, color = accentColor)
            Text(text = city, style = MaterialTheme.typography.bodySmall, color = TextPrimary, maxLines = 1)
        }
    }
}

@Composable
private fun MonthGridCard(
    state: CalendarUiState,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClicked: (TravelDate) -> Unit
) {
    val weekHeaders = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")

    Surface(
        color = CardElevated,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_month_grid")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrevMonth,
                    enabled = state.canGoPrevMonth,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("calendar_prev_month_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Mes anterior",
                        tint = if (state.canGoPrevMonth) NeonCyan else TextMuted
                    )
                }

                Text(
                    text = state.displayedMonth.monthYearLabelEs,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.testTag("calendar_month_title")
                )

                IconButton(
                    onClick = onNextMonth,
                    enabled = state.canGoNextMonth,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("calendar_next_month_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mes siguiente",
                        tint = if (state.canGoNextMonth) NeonCyan else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Weekday Headers
            Row(modifier = Modifier.fillMaxWidth()) {
                weekHeaders.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 7-column grid rows
            val rows = state.monthCells.chunked(7)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                rows.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        week.forEach { cell ->
                            CalendarDayCellBox(
                                cell = cell,
                                isOutboundSelected = cell.date == state.selectedOutboundDate,
                                isReturnSelected = cell.date == state.selectedReturnDate,
                                isInRange = state.selectedOutboundDate != null &&
                                    state.selectedReturnDate != null &&
                                    cell.date > state.selectedOutboundDate &&
                                    cell.date < state.selectedReturnDate,
                                onClick = {
                                    if (cell.isInCurrentMonth && cell.isInCatalogRange && cell.lowestBaseFareMxn != null) {
                                        onDayClicked(cell.date)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Airline dot legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Airline.entries.forEach { airline ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(airline.brandColor)
                        )
                        Text(
                            text = airline.iataCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCellBox(
    cell: CalendarDayCell,
    isOutboundSelected: Boolean,
    isReturnSelected: Boolean,
    isInRange: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = cell.isInCurrentMonth && cell.isInCatalogRange && cell.lowestBaseFareMxn != null
    val bgColor = when {
        !cell.isInCurrentMonth -> Color.Transparent
        isOutboundSelected -> CyanTint
        isReturnSelected -> PinkTint
        isInRange -> Color(0x1600E5FF)
        enabled -> DeepNightBg
        else -> Color(0xFF0A1020)
    }
    val borderColor = when {
        !cell.isInCurrentMonth -> Color.Transparent
        isOutboundSelected -> NeonCyan
        isReturnSelected -> NeonPink
        enabled -> CardBorder
        else -> Color(0xFF141C2E)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = if (cell.isInCurrentMonth) BorderStroke(if (isOutboundSelected || isReturnSelected) 1.5.dp else 1.dp, borderColor) else null,
        modifier = modifier
            .height(54.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(if (cell.isInCurrentMonth) "cal_day_${cell.date.isoString}" else "cal_empty_${cell.date.isoString}")
    ) {
        if (cell.isInCurrentMonth) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cell.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = when {
                        isOutboundSelected -> NeonCyan
                        isReturnSelected -> NeonPink
                        enabled -> TextPrimary
                        else -> TextMuted
                    },
                    fontWeight = if (isOutboundSelected || isReturnSelected) FontWeight.Bold else FontWeight.Medium
                )

                if (cell.lowestBaseFareMxn != null) {
                    val priceColor = when {
                        cell.lowestBaseFareMxn <= 249 -> NeonGreen
                        cell.lowestBaseFareMxn <= 499 -> NeonCyan
                        else -> TextSecondary
                    }
                    Text(
                        text = "$${cell.lowestBaseFareMxn}",
                        fontSize = 9.sp,
                        lineHeight = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = priceColor,
                        maxLines = 1
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        cell.airlinesOperating.take(3).forEach { airline ->
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(airline.brandColor)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "-",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
