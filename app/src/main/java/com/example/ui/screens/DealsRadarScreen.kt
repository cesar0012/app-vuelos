package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.FlightDeal
import com.example.model.TravelDate
import com.example.ui.components.FlightDealCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PinkTint
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.BookingLinkHelper
import com.example.viewmodel.DealQuickFilter
import com.example.viewmodel.DealsRadarUiState
import com.example.viewmodel.SortOrder
import kotlin.math.roundToInt

@Composable
fun DealsRadarScreen(
    state: DealsRadarUiState,
    onQuickFilterSelected: (DealQuickFilter) -> Unit,
    onMaxBaseFareChange: (Float) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onOpenCalculator: (FlightDeal) -> Unit,
    onOpenPromoLocator: (FlightDeal) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lowestDealFare = state.deals.minOfOrNull { it.baseFareMxn }
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
            .background(DeepNightBg)
            .testTag("deals_radar_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "hero_banner") {
            Surface(
                color = CardElevated,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(NeonCyan, NeonPink))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = NeonPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "RADAR DE OFERTAS FLASH",
                                style = MaterialTheme.typography.labelLarge,
                                color = NeonPink
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lowestDealFare != null) {
                                "Vuelos desde $$lowestDealFare MXN base"
                            } else {
                                "Explora tarifas promocionales"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = "${state.deals.size} tarifas activas en los próximos 90 días con TUA oficial calculada.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

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
                            selectedContainerColor = PinkTint,
                            selectedLabelColor = NeonPink,
                            containerColor = CardElevated,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = NeonPink,
                            borderColor = CardBorder
                        ),
                        modifier = Modifier.testTag("deals_quick_filter_${filter.name.lowercase()}")
                    )
                }
            }
        }

        item(key = "slider_and_sort") {
            Surface(
                color = CardElevated,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tarifa base máxima",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "Hasta $${state.maxBaseFareMxn.roundToInt()} MXN",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = state.maxBaseFareMxn,
                        onValueChange = onMaxBaseFareChange,
                        valueRange = 199f..6500f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = CardBorder
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
                                    activeContainerColor = CyanTint,
                                    activeContentColor = NeonCyan,
                                    inactiveContainerColor = DeepNightBg,
                                    inactiveContentColor = TextSecondary
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
                    message = "No hay ofertas bajo $${state.maxBaseFareMxn.roundToInt()} MXN con los filtros actuales. Sube el presupuesto o cambia los aeropuertos arriba."
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
                        isHeadToHeadCompetition = compKey in state.headToHeadRouteDateKeys,
                        onOpenCalculator = { onOpenCalculator(deal) },
                        onVerifyGoogleFlights = {
                            BookingLinkHelper.openExternalUrl(
                                context,
                                BookingLinkHelper.googleFlightsOneWayUrl(
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
            items(state.deals, key = { "price_deal_${it.id}" }) { deal ->
                val compKey = "${deal.originCode}-${deal.destinationCode}-${deal.departureDate.isoString}"
                FlightDealCard(
                    deal = deal,
                    isHeadToHeadCompetition = compKey in state.headToHeadRouteDateKeys,
                    onOpenCalculator = { onOpenCalculator(deal) },
                    onVerifyGoogleFlights = {
                        BookingLinkHelper.openExternalUrl(
                            context,
                            BookingLinkHelper.googleFlightsOneWayUrl(
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
}

@Composable
fun DateGroupHeader(
    date: TravelDate,
    count: Int,
    hasCompetition: Boolean
) {
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
                tint = NeonCyan,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = date.fullDayLabelEs,
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan
            )
        }
        Text(
            text = if (hasCompetition) "$count vuelos · Cabeza a cabeza" else "$count vuelos",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
fun EmptyDealsBox(message: String) {
    Surface(
        color = CardElevated,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
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
                    .background(CyanTint, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Flight, contentDescription = null, tint = NeonCyan)
            }
            Text(
                text = "Sin coincidencias exactas",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
