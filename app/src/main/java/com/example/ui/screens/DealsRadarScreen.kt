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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Airline
import com.example.model.FlightDeal
import com.example.model.TravelDate
import com.example.ui.components.FlightDealCard
import com.example.ui.theme.AppTheme
import com.example.util.BookingLinkHelper
import com.example.util.PendingRedirectRequest
import com.example.viewmodel.DealQuickFilter
import com.example.viewmodel.DealsRadarUiState
import com.example.viewmodel.LiveSyncState
import com.example.viewmodel.SortOrder
import kotlin.math.roundToInt

@Composable
fun DealsRadarScreen(
    state: DealsRadarUiState,
    liveSyncState: LiveSyncState = LiveSyncState(),
    onRefreshLive: () -> Unit = {},
    onQuickFilterSelected: (DealQuickFilter) -> Unit,
    onAirlineFilterSelected: (Airline?) -> Unit = {},
    onMaxBaseFareChange: (Float) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onOpenCalculator: (FlightDeal) -> Unit,
    onOpenPromoLocator: (FlightDeal) -> Unit,
    onRequestExternalRedirect: (PendingRedirectRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val lowestEffectiveFare = state.deals.minOfOrNull { it.effectivePriceMxn(state.priceOptions) }
    val groupedByDate = remember(state.deals, state.sortOrder) {
        if (state.sortOrder == SortOrder.DATE) {
            state.deals.groupBy { it.departureDate }
        } else {
            emptyMap()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.deepNightBg)
            .testTag("deals_radar_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "hero_banner") {
            Surface(
                color = colors.cardElevated,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(colors.neonCyan, colors.neonPink))
                ),
                shadowElevation = if (colors.isDark) 0.dp else 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = colors.neonPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "RADAR NACIONAL MÉXICO",
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.neonPink
                            )
                        }

                        Surface(
                            color = if (liveSyncState.liveFareCount > 0) colors.greenTint else colors.cyanTint,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.dp,
                                if (liveSyncState.liveFareCount > 0) colors.neonGreen.copy(alpha = 0.6f) else colors.neonCyan.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .heightIn(min = 36.dp)
                                .clickable(enabled = !liveSyncState.isSyncing, onClick = onRefreshLive)
                                .testTag("refresh_live_fares_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                if (liveSyncState.isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(13.dp),
                                        strokeWidth = 2.dp,
                                        color = colors.neonCyan
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (liveSyncState.liveFareCount > 0) Icons.Default.Wifi else Icons.Default.Refresh,
                                        contentDescription = "Actualizar precios en vivo",
                                        tint = if (liveSyncState.liveFareCount > 0) colors.neonGreen else colors.neonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = when {
                                        liveSyncState.isSyncing -> "Sincronizando..."
                                        liveSyncState.liveFareCount > 0 -> "${liveSyncState.liveFareCount} en vivo"
                                        else -> "Sincronizar"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (liveSyncState.liveFareCount > 0) colors.neonGreen else colors.neonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lowestEffectiveFare != null) {
                            "Desde $$lowestEffectiveFare MXN (${state.priceOptions.shortSummaryLabel})"
                        } else {
                            "Explora vuelos nacionales en México"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${state.deals.size} vuelos · ${liveSyncState.statusMessage}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = colors.cyanTint,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = colors.neonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Volaris y Viva publican Tarifa Zero (sin maleta ni TUA). El precio mostrado ya aplica tus filtros de arriba.",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Quick category filters
        item(key = "quick_filters") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(DealQuickFilter.entries) { filter ->
                    val selected = state.quickFilter == filter
                    FilterChip(
                        selected = selected,
                        onClick = { onQuickFilterSelected(filter) },
                        label = { Text(filter.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.pinkTint,
                            selectedLabelColor = colors.neonPink,
                            containerColor = colors.cardElevated,
                            labelColor = colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = colors.neonPink,
                            borderColor = colors.cardBorder
                        ),
                        modifier = Modifier.testTag("deals_quick_filter_${filter.name.lowercase()}")
                    )
                }
            }
        }

        // Dedicated 7 Mexican Airlines filter bar
        item(key = "airline_filters") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("deals_airline_filters_row")
            ) {
                item(key = "deals_airline_all") {
                    val selected = state.airlineFilter == null
                    FilterChip(
                        selected = selected,
                        onClick = { onAirlineFilterSelected(null) },
                        label = { Text("Todas las aerolíneas (7)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.cyanTint,
                            selectedLabelColor = colors.neonCyan,
                            containerColor = colors.cardElevated,
                            labelColor = colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = colors.neonCyan,
                            borderColor = colors.cardBorder
                        ),
                        modifier = Modifier.testTag("deals_airline_all")
                    )
                }
                items(Airline.entries, key = { it.iataCode }) { airline ->
                    val selected = state.airlineFilter == airline
                    FilterChip(
                        selected = selected,
                        onClick = {
                            onAirlineFilterSelected(if (selected) null else airline)
                        },
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
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = airline.brandColor,
                            borderColor = colors.cardBorder
                        ),
                        modifier = Modifier.testTag("deals_airline_${airline.iataCode.lowercase()}")
                    )
                }
            }
        }

        item(key = "slider_and_sort") {
            Surface(
                color = colors.cardElevated,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Máx. (${state.priceOptions.shortSummaryLabel})",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Hasta $${state.maxBaseFareMxn.roundToInt()} MXN",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.neonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = state.maxBaseFareMxn.coerceIn(300f, 8500f),
                        onValueChange = onMaxBaseFareChange,
                        valueRange = 300f..8500f,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.neonCyan,
                            activeTrackColor = colors.neonCyan,
                            inactiveTrackColor = colors.cardBorder
                        ),
                        modifier = Modifier.testTag("deals_max_fare_slider")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SortOrder.entries.forEachIndexed { idx, order ->
                            SegmentedButton(
                                selected = state.sortOrder == order,
                                onClick = { onSortOrderChange(order) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = idx,
                                    count = SortOrder.entries.size
                                ),
                                icon = {
                                    Icon(
                                        imageVector = if (order == SortOrder.PRICE) Icons.Default.AttachMoney else Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = colors.cyanTint,
                                    activeContentColor = colors.neonCyan,
                                    inactiveContainerColor = colors.cardHighest,
                                    inactiveContentColor = colors.textSecondary
                                ),
                                modifier = Modifier.testTag("deals_sort_${order.name.lowercase()}")
                            ) {
                                Text("Ordenar: ${order.label}")
                            }
                        }
                    }
                }
            }
        }

        if (state.deals.isEmpty()) {
            item(key = "empty_deals") {
                EmptyDealsBox(
                    message = "No hay vuelos nacionales bajo $${state.maxBaseFareMxn.roundToInt()} MXN (${state.priceOptions.shortSummaryLabel}) con los filtros actuales. Sube el presupuesto o cambia aerolínea/aeropuertos."
                )
            }
        } else if (state.sortOrder == SortOrder.DATE) {
            groupedByDate.forEach { (date: TravelDate, dayDeals: List<FlightDeal>) ->
                item(key = "date_hdr_${date.isoString}") {
                    val hasCompetition = dayDeals.any {
                        "${it.originCode}-${it.destinationCode}-${it.departureDate.isoString}" in state.headToHeadRouteDateKeys
                    }
                    DateGroupHeader(date = date, count = dayDeals.size, hasCompetition = hasCompetition)
                }
                items(dayDeals, key = { "date_deal_${it.id}" }) { deal ->
                    val compKey = "${deal.originCode}-${deal.destinationCode}-${deal.departureDate.isoString}"
                    FlightDealCard(
                        deal = deal,
                        priceOptions = state.priceOptions,
                        isHeadToHeadCompetition = compKey in state.headToHeadRouteDateKeys,
                        onOpenCalculator = { onOpenCalculator(deal) },
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
            items(state.deals, key = { "price_deal_${it.id}" }) { deal ->
                val compKey = "${deal.originCode}-${deal.destinationCode}-${deal.departureDate.isoString}"
                FlightDealCard(
                    deal = deal,
                    priceOptions = state.priceOptions,
                    isHeadToHeadCompetition = compKey in state.headToHeadRouteDateKeys,
                    onOpenCalculator = { onOpenCalculator(deal) },
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
}

@Composable
fun DateGroupHeader(
    date: TravelDate,
    count: Int,
    hasCompetition: Boolean
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = colors.neonCyan,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = date.fullDayLabelEs,
                style = MaterialTheme.typography.titleSmall,
                color = colors.neonCyan
            )
        }
        Text(
            text = if (hasCompetition) "$count vuelos · Cabeza a cabeza" else "$count vuelos",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
    }
}

@Composable
fun EmptyDealsBox(message: String) {
    val colors = AppTheme.colors
    Surface(
        color = colors.cardElevated,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors.cyanTint, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Flight, contentDescription = null, tint = colors.neonCyan)
            }
            Text(
                text = "Sin coincidencias exactas",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}
