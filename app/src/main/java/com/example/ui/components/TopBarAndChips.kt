package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Airport
import com.example.model.AirportCategory
import com.example.model.AirportData
import com.example.model.PriceDisplayOptions
import com.example.model.PricingBaggageTier
import com.example.ui.theme.AppTheme
import com.example.viewmodel.AirportDialogMode

@Composable
fun CheapTravelTopHeader(
    selectedOrigins: List<String>,
    selectedDestinations: List<String>,
    gpsBanner: String?,
    isDarkMode: Boolean = false,
    priceOptions: PriceDisplayOptions = PriceDisplayOptions(),
    onToggleTheme: () -> Unit = {},
    onToggleIncludeTua: () -> Unit = {},
    onSelectBaggageTier: (PricingBaggageTier) -> Unit = {},
    onOpenOriginsDialog: () -> Unit,
    onOpenDestinationsDialog: () -> Unit,
    onRemoveOrigin: (String) -> Unit,
    onRemoveDestination: (String) -> Unit,
    onDetectGps: () -> Unit
) {
    val colors = AppTheme.colors

    Surface(
        color = colors.surfaceNavy,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(colors.neonCyan, colors.neonPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar Cheap Travel",
                            tint = colors.onAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cheap Travel",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Vuelos Nacionales MX · TUA y Equipaje Real",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.neonCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = if (isDarkMode) "Cambiar a modo claro" else "Cambiar a modo oscuro",
                            tint = if (isDarkMode) colors.neonGold else colors.textPrimary
                        )
                    }

                    IconButton(
                        onClick = onDetectGps,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("top_gps_detect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Detectar aeropuerto cercano por GPS",
                            tint = colors.neonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenOriginsDialog,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colors.cyanTint,
                        contentColor = colors.neonCyan
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp)
                        .testTag("top_origins_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = "Seleccionar orígenes",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedOrigins.isEmpty()) "Origen: Todos MX" else "Orígenes (${selectedOrigins.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = onOpenDestinationsDialog,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colors.pinkTint,
                        contentColor = colors.neonPink
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp)
                        .testTag("top_destinations_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlightLand,
                        contentDescription = "Seleccionar destinos",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedDestinations.isEmpty()) "Destino: Todos MX" else "Destinos (${selectedDestinations.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            if (!gpsBanner.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = colors.greenTint,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = colors.neonGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = gpsBanner,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.neonGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Always-visible removable active airport chips row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_airport_chips_row")
            ) {
                if (selectedOrigins.isEmpty()) {
                    item(key = "all_origins") {
                        RemovableSummaryChip(
                            label = "Origen: Todos MX",
                            accentColor = colors.neonCyan,
                            showClose = false,
                            onClick = onOpenOriginsDialog,
                            onRemove = {},
                            testTag = "chip_origin_all"
                        )
                    }
                } else {
                    items(selectedOrigins, key = { "orig_$it" }) { code ->
                        val city = AirportData.findByCode(code)?.city ?: code
                        RemovableSummaryChip(
                            label = "Sale: $code ($city)",
                            accentColor = colors.neonCyan,
                            showClose = true,
                            onClick = onOpenOriginsDialog,
                            onRemove = { onRemoveOrigin(code) },
                            testTag = "chip_remove_origin_$code"
                        )
                    }
                }

                if (selectedDestinations.isEmpty()) {
                    item(key = "any_dest") {
                        RemovableSummaryChip(
                            label = "Destino: Todo México",
                            accentColor = colors.neonPink,
                            showClose = false,
                            onClick = onOpenDestinationsDialog,
                            onRemove = {},
                            testTag = "chip_dest_any"
                        )
                    }
                } else {
                    items(selectedDestinations, key = { "dest_$it" }) { code ->
                        val city = AirportData.findByCode(code)?.city ?: code
                        RemovableSummaryChip(
                            label = "Va a: $code ($city)",
                            accentColor = colors.neonPink,
                            showClose = true,
                            onClick = onOpenDestinationsDialog,
                            onRemove = { onRemoveDestination(code) },
                            testTag = "chip_remove_dest_$code"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Global Pricing Calculation Filter Bar: Con/Sin TUA + Baggage Tier (Básica 10kg default)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_pricing_filters_row")
            ) {
                item(key = "tua_toggle") {
                    FilterChip(
                        selected = priceOptions.includeTua,
                        onClick = onToggleIncludeTua,
                        leadingIcon = {
                            Icon(
                                imageVector = if (priceOptions.includeTua) Icons.Default.Check else Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (priceOptions.includeTua) "Con TUA" else "Sin TUA",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.greenTint,
                            selectedLabelColor = colors.neonGreen,
                            selectedLeadingIconColor = colors.neonGreen,
                            containerColor = colors.cardElevated,
                            labelColor = colors.textSecondary,
                            iconColor = colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = priceOptions.includeTua,
                            selectedBorderColor = colors.neonGreen,
                            borderColor = colors.cardBorder
                        ),
                        modifier = Modifier.testTag("price_filter_tua_toggle")
                    )
                }

                items(PricingBaggageTier.entries, key = { it.name }) { tier ->
                    val selected = priceOptions.baggageTier == tier
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectBaggageTier(tier) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Luggage,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tier.chipLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.cyanTint,
                            selectedLabelColor = colors.neonCyan,
                            selectedLeadingIconColor = colors.neonCyan,
                            containerColor = colors.cardElevated,
                            labelColor = colors.textSecondary,
                            iconColor = colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = colors.neonCyan,
                            borderColor = colors.cardBorder
                        ),
                        modifier = Modifier.testTag("price_filter_tier_${tier.name.lowercase()}")
                    )
                }
            }
        }
        HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
    }
}

@Composable
private fun RemovableSummaryChip(
    label: String,
    accentColor: Color,
    showClose: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    testTag: String
) {
    val colors = AppTheme.colors
    Surface(
        color = accentColor.copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .heightIn(min = 34.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 10.dp,
                end = if (showClose) 4.dp else 10.dp,
                top = 4.dp,
                bottom = 4.dp
            )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textPrimary,
                maxLines = 1
            )
            if (showClose) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onRemove)
                        .testTag(testTag),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quitar $label",
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AirportSelectorDialog(
    mode: AirportDialogMode,
    selectedCodes: List<String>,
    onToggleAirport: (String) -> Unit,
    onClearAll: () -> Unit,
    onDetectGps: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val colors = AppTheme.colors

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember {
        mutableStateOf(if (mode == AirportDialogMode.DESTINATIONS) AirportCategory.BEACH else AirportCategory.ALL)
    }

    val filteredAirports = remember(searchQuery, selectedCategory) {
        AirportData.airports.filter { airport ->
            val matchesCategory = when (selectedCategory) {
                AirportCategory.ALL -> true
                AirportCategory.BEACH -> airport.isBeach
                AirportCategory.HUBS -> airport.isMajorHub
                AirportCategory.REGIONAL -> !airport.isMajorHub && !airport.isBeach
            }
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                airport.code.lowercase().contains(q) ||
                airport.city.lowercase().contains(q) ||
                airport.name.lowercase().contains(q) ||
                airport.stateOrCountry.lowercase().contains(q)
            matchesCategory && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = colors.surfaceNavy,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 640.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (mode == AirportDialogMode.ORIGINS) {
                                "Aeropuertos de Salida (México)"
                            } else {
                                "Destinos en México"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Vuelos 100% nacionales · ${AirportData.airports.size} aeropuertos de México",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("airport_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar selector",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (mode == AirportDialogMode.ORIGINS) {
                    Button(
                        onClick = {
                            onDetectGps()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.greenTint,
                            contentColor = colors.neonGreen
                        ),
                        border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("dialog_detect_gps_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Detectar más cercano por GPS",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                } else if (selectedCodes.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onClearAll,
                        border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 44.dp)
                            .testTag("dialog_clear_destinations_button")
                    ) {
                        Text(
                            text = "Ver todos los destinos de México",
                            color = colors.neonPink
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar ciudad o IATA (ej. AGU, NLU, CUN, QRO)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colors.neonCyan
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = colors.textSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.neonCyan,
                        unfocusedBorderColor = colors.cardBorder,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("airport_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AirportCategory.entries) { cat ->
                        val active = selectedCategory == cat
                        FilterChip(
                            selected = active,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.cyanTint,
                                selectedLabelColor = colors.neonCyan,
                                containerColor = colors.cardElevated,
                                labelColor = colors.textSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = active,
                                selectedBorderColor = colors.neonCyan,
                                borderColor = colors.cardBorder
                            ),
                            modifier = Modifier.testTag("airport_category_${cat.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = 360.dp)
                ) {
                    items(filteredAirports, key = { it.code }) { airport ->
                        val isSelected = airport.code in selectedCodes
                        AirportOptionRow(
                            airport = airport,
                            isSelected = isSelected,
                            accentColor = if (mode == AirportDialogMode.ORIGINS) colors.neonCyan else colors.neonPink,
                            onClick = { onToggleAirport(airport.code) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text("Listo", color = colors.neonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AirportOptionRow(
    airport: Airport,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Surface(
        color = if (isSelected) accentColor.copy(alpha = 0.12f) else colors.cardElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) accentColor else colors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("airport_option_${airport.code}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = colors.cardHighest,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = airport.code,
                    style = MaterialTheme.typography.titleSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${airport.city} · ${airport.stateOrCountry}",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = airport.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tuaColor = if (airport.domesticTuaMxn <= 450) colors.neonGreen else colors.textMuted
                    Text(
                        text = "TUA MX: $${airport.domesticTuaMxn} MXN",
                        style = MaterialTheme.typography.labelSmall,
                        color = tuaColor
                    )
                    if (!airport.highlightTag.isNullOrBlank()) {
                        Text(
                            text = airport.highlightTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.neonGreen
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor else colors.cardHighest),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seleccionado",
                        tint = colors.onAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
