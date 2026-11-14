package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Airline
import com.example.model.FlightDeal
import com.example.ui.components.FlightDealCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.GreenTint
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.BookingLinkHelper
import com.example.viewmodel.SearchUiState
import com.example.viewmodel.SortOrder
import kotlin.math.roundToInt

@Composable
fun SearchScreen(
    state: SearchUiState,
    onMaxTotalChange: (Float) -> Unit,
    onDirectOnlyChange: (Boolean) -> Unit,
    onAirlineFilterChange: (Airline?) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onOpenCalculator: (FlightDeal) -> Unit,
    onOpenPromoLocator: (FlightDeal) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val groupedByDate = remember(state.results, state.sortOrder) {
        if (state.sortOrder == SortOrder.DATE) {
            state.results.groupBy { it.departureDate }
        } else {
            emptyMap()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNightBg)
            .testTag("search_results_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "search_controls") {
            Surface(
                color = CardElevated,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Buscador Flexible Multi-Ruta",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Filtra combinaciones por presupuesto total con TUA incluido usando los chips superiores.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Presupuesto máx. (Base + TUA)",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "$${state.maxTotalMxn.roundToInt()} MXN",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = state.maxTotalMxn,
                        onValueChange = onMaxTotalChange,
                        valueRange = 500f..12000f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPink,
                            activeTrackColor = NeonPink,
                            inactiveTrackColor = CardBorder
                        ),
                        modifier = Modifier.testTag("search_budget_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Solo vuelos directos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        Switch(
                            checked = state.directOnly,
                            onCheckedChange = onDirectOnlyChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DeepNightBg,
                                checkedTrackColor = NeonGreen
                            ),
                            modifier = Modifier.testTag("search_direct_only_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item(key = "airline_all") {
                            val selected = state.airlineFilter == null
                            FilterChip(
                                selected = selected,
                                onClick = { onAirlineFilterChange(null) },
                                label = { Text("Todas") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanTint,
                                    selectedLabelColor = NeonCyan,
                                    containerColor = DeepNightBg,
                                    labelColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("search_airline_all")
                            )
                        }
                        items(Airline.entries) { airline ->
                            val selected = state.airlineFilter == airline
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    onAirlineFilterChange(if (selected) null else airline)
                                },
                                label = { Text(airline.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = airline.brandColor.copy(alpha = 0.22f),
                                    selectedLabelColor = TextPrimary,
                                    containerColor = DeepNightBg,
                                    labelColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("search_airline_${airline.iataCode.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SortOrder.entries.forEachIndexed { idx, order ->
                            SegmentedButton(
                                selected = state.sortOrder == order,
                                onClick = { onSortOrderChange(order) },
                                shape = SegmentedButtonDefaults.itemShape(idx, SortOrder.entries.size),
                                icon = {
                                    Icon(
                                        imageVector = if (order == SortOrder.PRICE) Icons.Default.AttachMoney else Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = GreenTint,
                                    activeContentColor = NeonGreen,
                                    inactiveContainerColor = DeepNightBg,
                                    inactiveContentColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("search_sort_${order.name.lowercase()}")
                            ) {
                                Text("Ordenar: ${order.label}")
                            }
                        }
                    }
                }
            }
        }

        item(key = "results_count") {
            Text(
                text = "${state.results.size} resultados encontrados",
                style = MaterialTheme.typography.labelLarge,
                color = NeonCyan
            )
        }

        if (state.results.isEmpty()) {
            item(key = "empty_search") {
                EmptyDealsBox(
                    message = "No encontramos vuelos por debajo de $${state.maxTotalMxn.roundToInt()} MXN con TUA. Amplía el presupuesto o desactiva filtros."
                )
            }
        } else if (state.sortOrder == SortOrder.DATE) {
            groupedByDate.forEach { (date, dayDeals) ->
                item(key = "search_date_${date.isoString}") {
                    val hasComp = dayDeals.any {
                        "${it.originCode}-${it.destinationCode}-${it.departureDate.isoString}" in state.headToHeadRouteDateKeys
                    }
                    DateGroupHeader(date = date, count = dayDeals.size, hasCompetition = hasComp)
                }
                items(dayDeals, key = { "s_date_${it.id}" }) { deal ->
                    val compKey = "${deal.originCode}-${deal.destinationCode}-${deal.departureDate.isoString}"
                    FlightDealCard(
                        deal = deal,
                        isHeadToHeadCompetition = compKey in state.headToHeadRouteDateKeys,
                        onOpenCalculator = { onOpenCalculator(deal) },
                        onVerifyGoogleFlights = {
                            BookingLinkHelper.openExternalUrl(
                                context,
                                BookingLinkHelper.officialAirlineBookingUrl(
                                    deal.airline,
                                    deal.originCode,
                                    deal.destinationCode
                                )
                            )
                        },
                        onOpenPromoLocator = { onOpenPromoLocator(deal) }
                    )
                }
            }
        } else {
            items(state.results, key = { "s_price_${it.id}" }) { deal ->
                val compKey = "${deal.originCode}-${deal.destinationCode}-${deal.departureDate.isoString}"
                FlightDealCard(
                    deal = deal,
                    isHeadToHeadCompetition = compKey in state.headToHeadRouteDateKeys,
                    onOpenCalculator = { onOpenCalculator(deal) },
                    onVerifyGoogleFlights = {
                        BookingLinkHelper.openExternalUrl(
                            context,
                            BookingLinkHelper.officialAirlineBookingUrl(
                                deal.airline,
                                deal.originCode,
                                deal.destinationCode
                            )
                        )
                    },
                    onOpenPromoLocator = { onOpenPromoLocator(deal) }
                )
            }
        }
    }
}
