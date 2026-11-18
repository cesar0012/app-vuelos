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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.FlightDeal
import com.example.model.PriceDisplayOptions
import com.example.model.TimePeriod
import com.example.model.TravelDate
import com.example.ui.components.FlightDealCard
import com.example.ui.components.TripPlanSummaryCard
import com.example.ui.theme.AppTheme
import com.example.util.BookingLinkHelper
import com.example.util.PendingRedirectRequest
import com.example.viewmodel.CalendarDayCell
import com.example.viewmodel.CalendarSortOption
import com.example.viewmodel.CalendarUiState
import com.example.viewmodel.CalendarViewMode
import com.example.viewmodel.LiveSyncState
import com.example.viewmodel.RoundTripStep
import com.example.viewmodel.TripMode

@Composable
fun CalendarPriceScreen(
    state: CalendarUiState,
    liveSyncState: LiveSyncState = LiveSyncState(),
    onRefreshLive: () -> Unit = {},
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
    onRequestExternalRedirect: (PendingRedirectRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
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
            .background(colors.deepNightBg)
            .testTag("calendar_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Mode + Route Selector Card
        item(key = "calendar_header_controls") {
            Surface(
                color = colors.cardElevated,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        TripMode.entries.forEachIndexed { idx, mode ->
                            SegmentedButton(
                                selected = state.tripMode == mode,
                                onClick = { onTripModeChange(mode) },
                                shape = SegmentedButtonDefaults.itemShape(idx, TripMode.entries.size),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = colors.cyanTint,
                                    activeContentColor = colors.neonCyan,
                                    inactiveContainerColor = colors.cardHighest,
                                    inactiveContentColor = colors.textSecondary
                                ),
                                modifier = Modifier.testTag("calendar_mode_${mode.name.lowercase()}")
                            ) {
                                Text(mode.label, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    if (state.tripMode == TripMode.ROUND_TRIP) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StepPill(
                                label = "1. Salida" + (state.selectedOutboundDate?.let { " (${it.shortLabelEs})" } ?: ""),
                                isActive = state.roundTripStep == RoundTripStep.OUTBOUND,
                                accentColor = colors.neonCyan,
                                onClick = { onStepChange(RoundTripStep.OUTBOUND) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("step_outbound_pill")
                            )
                            StepPill(
                                label = "2. Regreso" + (state.selectedReturnDate?.let { " (${it.shortLabelEs})" } ?: ""),
                                isActive = state.roundTripStep == RoundTripStep.RETURN,
                                accentColor = colors.neonPink,
                                onClick = { onStepChange(RoundTripStep.RETURN) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("step_return_pill")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            RouteAirportButton(
                                label = "Origen MX",
                                code = state.originCode,
                                city = AirportData.findByCode(state.originCode)?.city ?: state.originCode,
                                accentColor = colors.neonCyan,
                                onClick = { showOriginDropdown = true },
                                modifier = Modifier.testTag("calendar_origin_picker")
                            )
                            DropdownMenu(
                                expanded = showOriginDropdown,
                                onDismissRequest = { showOriginDropdown = false },
                                modifier = Modifier.background(colors.surfaceNavy)
                            ) {
                                originAirports.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = colors.textPrimary) },
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
                                .background(colors.cardHighest)
                                .testTag("calendar_swap_route_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Invertir ruta",
                                tint = colors.neonCyan
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            RouteAirportButton(
                                label = "Destino MX",
                                code = state.destinationCode,
                                city = AirportData.findByCode(state.destinationCode)?.city ?: state.destinationCode,
                                accentColor = colors.neonPink,
                                onClick = { showDestDropdown = true },
                                modifier = Modifier.testTag("calendar_dest_picker")
                            )
                            DropdownMenu(
                                expanded = showDestDropdown,
                                onDismissRequest = { showDestDropdown = false },
                                modifier = Modifier.background(colors.surfaceNavy)
                            ) {
                                destAirports.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = colors.textPrimary) },
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
                                    activeContainerColor = colors.pinkTint,
                                    activeContentColor = colors.neonPink,
                                    inactiveContainerColor = colors.cardHighest,
                                    inactiveContentColor = colors.textSecondary
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
                                selectedContainerColor = colors.cyanTint,
                                selectedLabelColor = colors.neonCyan,
                                containerColor = colors.cardElevated,
                                labelColor = colors.textSecondary
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
                                selectedContainerColor = colors.greenTint,
                                selectedLabelColor = colors.neonGreen,
                                containerColor = colors.cardElevated,
                                labelColor = colors.textSecondary
                            ),
                            modifier = Modifier.testTag("cal_filter_direct_only")
                        )
                    }
                    items(Airline.entries) { airline ->
                        val active = state.airlineFilter == airline
                        FilterChip(
                            selected = active,
                            onClick = { onAirlineFilterChange(if (active) null else airline) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(airline.brandColor)
                                )
                            },
                            label = { Text(airline.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = airline.brandColor.copy(alpha = if (colors.isDark) 0.24f else 0.14f),
                                selectedLabelColor = colors.textPrimary,
                                containerColor = colors.cardElevated,
                                labelColor = colors.textSecondary
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
                                selectedContainerColor = colors.pinkTint,
                                selectedLabelColor = colors.neonPink,
                                containerColor = colors.cardElevated,
                                labelColor = colors.textSecondary
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
                    liveSyncState = liveSyncState,
                    onRefreshLive = onRefreshLive,
                    onPrevMonth = { onNavigateMonth(-1) },
                    onNextMonth = { onNavigateMonth(1) },
                    onDayClicked = onDateClicked
                )
            }

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
                        color = colors.neonCyan
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
                            priceOptions = state.priceOptions,
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
                                val url = BookingLinkHelper.officialAirlineBookingUrl(
                                    airline = deal.airline,
                                    originCode = deal.originCode,
                                    destinationCode = deal.destinationCode,
                                    liveBookingUrl = deal.liveBookingUrl
                                )
                                onRequestExternalRedirect(
                                    BookingLinkHelper.createRedirectRequest(deal, url, priceOptions = state.priceOptions)
                                )
                            },
                            onOpenPromoLocator = { onOpenPromoLocator(deal) }
                        )
                    }
                }
            } else {
                item(key = "tap_day_hint") {
                    Surface(
                        color = colors.cyanTint,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Precios en el calendario mostrados en modo ${state.priceOptions.shortSummaryLabel}. Toca una fecha para ver horarios y elegir tu vuelo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.neonCyan,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        } else {
            item(key = "list_mode_hdr") {
                val legTitle = if (state.tripMode == TripMode.ROUND_TRIP && state.roundTripStep == RoundTripStep.RETURN) {
                    "Lista de Regreso: ${state.destinationCode} -> ${state.originCode} (${state.chronologicalFlights.size})"
                } else {
                    "Lista de Salida: ${state.originCode} -> ${state.destinationCode} (${state.chronologicalFlights.size})"
                }
                Text(
                    text = legTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.neonCyan
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
                        priceOptions = state.priceOptions,
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
                            val url = BookingLinkHelper.officialAirlineBookingUrl(
                                airline = deal.airline,
                                originCode = deal.originCode,
                                destinationCode = deal.destinationCode,
                                liveBookingUrl = deal.liveBookingUrl
                            )
                            onRequestExternalRedirect(
                                BookingLinkHelper.createRedirectRequest(deal, url, priceOptions = state.priceOptions)
                            )
                        },
                        onOpenPromoLocator = { onOpenPromoLocator(deal) }
                    )
                }
            }
        }

        if (state.selectedOutboundFlight != null) {
            item(key = "trip_plan_summary") {
                TripPlanSummaryCard(
                    tripMode = state.tripMode,
                    outbound = state.selectedOutboundFlight,
                    returnFlight = state.selectedReturnFlight,
                    priceOptions = state.priceOptions,
                    onOpenCalculator = onOpenCalculator,
                    onClearPlan = onClearPlan,
                    onPlanCopied = onShowMessage,
                    onRequestExternalRedirect = onRequestExternalRedirect
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
    val colors = AppTheme.colors
    Surface(
        color = if (isActive) accentColor.copy(alpha = 0.14f) else colors.cardHighest,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isActive) accentColor else colors.cardBorder),
        modifier = modifier
            .heightIn(min = 40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) accentColor else colors.textSecondary,
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
    val colors = AppTheme.colors
    Surface(
        color = colors.cardHighest,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
            Text(text = code, style = MaterialTheme.typography.titleLarge, color = accentColor)
            Text(text = city, style = MaterialTheme.typography.bodySmall, color = colors.textPrimary, maxLines = 1)
        }
    }
}

@Composable
private fun MonthGridCard(
    state: CalendarUiState,
    liveSyncState: LiveSyncState,
    onRefreshLive: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClicked: (TravelDate) -> Unit
) {
    val colors = AppTheme.colors
    val weekHeaders = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")

    Surface(
        color = colors.cardElevated,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_month_grid")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                        tint = if (state.canGoPrevMonth) colors.neonCyan else colors.textMuted
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.displayedMonth.monthYearLabelEs,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.testTag("calendar_month_title")
                    )
                    Text(
                        text = state.priceOptions.shortSummaryLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.neonCyan
                    )
                }

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
                        tint = if (state.canGoNextMonth) colors.neonCyan else colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                weekHeaders.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

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
                                priceOptions = state.priceOptions,
                                isOutboundSelected = cell.date == state.selectedOutboundDate,
                                isReturnSelected = cell.date == state.selectedReturnDate,
                                isInRange = state.selectedOutboundDate != null &&
                                    state.selectedReturnDate != null &&
                                    cell.date > state.selectedOutboundDate &&
                                    cell.date < state.selectedReturnDate,
                                onClick = {
                                    if (cell.isInCurrentMonth && cell.isInCatalogRange && cell.lowestEffectiveFareMxn != null) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    items(Airline.entries, key = { it.iataCode }) { airline ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
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
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(8.dp))

                Surface(
                    color = if (state.liveRouteFaresCount > 0) colors.greenTint else colors.cyanTint,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clickable(enabled = !liveSyncState.isSyncing, onClick = onRefreshLive)
                        .testTag("cal_refresh_live_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        if (liveSyncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(11.dp),
                                strokeWidth = 1.5.dp,
                                color = colors.neonCyan
                            )
                        } else {
                            Icon(
                                imageVector = if (state.liveRouteFaresCount > 0) Icons.Default.Wifi else Icons.Default.Refresh,
                                contentDescription = "Actualizar fechas en vivo",
                                tint = if (state.liveRouteFaresCount > 0) colors.neonGreen else colors.neonCyan,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = when {
                                liveSyncState.isSyncing -> "Consultando..."
                                state.liveRouteFaresCount > 0 -> "${state.liveRouteFaresCount} en vivo"
                                else -> "En vivo"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.liveRouteFaresCount > 0) colors.neonGreen else colors.neonCyan,
                            fontWeight = FontWeight.SemiBold
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
    priceOptions: PriceDisplayOptions,
    isOutboundSelected: Boolean,
    isReturnSelected: Boolean,
    isInRange: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val shownFare = cell.lowestEffectiveFareMxn
    val enabled = cell.isInCurrentMonth && cell.isInCatalogRange && shownFare != null
    val bgColor = when {
        !cell.isInCurrentMonth -> Color.Transparent
        isOutboundSelected -> colors.cyanTint
        isReturnSelected -> colors.pinkTint
        isInRange -> colors.neonCyan.copy(alpha = 0.08f)
        enabled -> colors.cardHighest
        else -> colors.disabledCellBg
    }
    val borderColor = when {
        !cell.isInCurrentMonth -> Color.Transparent
        isOutboundSelected -> colors.neonCyan
        isReturnSelected -> colors.neonPink
        cell.hasLiveFare && enabled -> colors.neonGreen.copy(alpha = 0.65f)
        enabled -> colors.cardBorder
        else -> colors.disabledCellBorder
    }

    val lowThreshold = if (priceOptions.includeTua) 1100 else 350
    val midThreshold = if (priceOptions.includeTua) 1500 else 650

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
                        isOutboundSelected -> colors.neonCyan
                        isReturnSelected -> colors.neonPink
                        enabled -> colors.textPrimary
                        else -> colors.textMuted
                    },
                    fontWeight = if (isOutboundSelected || isReturnSelected) FontWeight.Bold else FontWeight.Medium
                )

                if (shownFare != null) {
                    val priceColor = when {
                        shownFare <= lowThreshold -> colors.neonGreen
                        shownFare <= midThreshold -> colors.neonCyan
                        else -> colors.textSecondary
                    }
                    Text(
                        text = "$$shownFare",
                        fontSize = 8.sp,
                        lineHeight = 9.sp,
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
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}
