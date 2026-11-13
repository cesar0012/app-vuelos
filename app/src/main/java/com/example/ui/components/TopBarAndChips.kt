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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Airport
import com.example.model.AirportCategory
import com.example.model.AirportData
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CardHighest
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.GreenTint
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PinkTint
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AirportDialogMode

@Composable
fun CheapTravelTopHeader(
    selectedOrigins: List<String>,
    selectedDestinations: List<String>,
    gpsBanner: String?,
    onOpenOriginsDialog: () -> Unit,
    onOpenDestinationsDialog: () -> Unit,
    onRemoveOrigin: (String) -> Unit,
    onRemoveDestination: (String) -> Unit,
    onDetectGps: () -> Unit
) {
    Surface(
        color = SurfaceNavy,
        tonalElevation = 4.dp,
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(NeonCyan, NeonPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar Cheap Travel",
                            tint = DeepNightBg,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cheap Travel",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "Radar de Vuelos MX + TUA Real",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonCyan,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenOriginsDialog,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CyanTint,
                            contentColor = NeonCyan
                        ),
                        modifier = Modifier
                            .heightIn(min = 40.dp)
                            .testTag("top_origins_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = "Seleccionar orígenes",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedOrigins.isEmpty()) "Origen" else "Orig (${selectedOrigins.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenDestinationsDialog,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = PinkTint,
                            contentColor = NeonPink
                        ),
                        modifier = Modifier
                            .heightIn(min = 40.dp)
                            .testTag("top_destinations_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightLand,
                            contentDescription = "Seleccionar destinos",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedDestinations.isEmpty()) "Dest" else "Dest (${selectedDestinations.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
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
                            tint = NeonGreen
                        )
                    }
                }
            }

            if (!gpsBanner.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = GreenTint,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.35f)),
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
                            tint = NeonGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = gpsBanner,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Always-visible removable active chips row
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
                            accentColor = NeonCyan,
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
                            accentColor = NeonCyan,
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
                            label = "Destino: Cualquiera",
                            accentColor = NeonPink,
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
                            accentColor = NeonPink,
                            showClose = true,
                            onClick = onOpenDestinationsDialog,
                            onRemove = { onRemoveDestination(code) },
                            testTag = "chip_remove_dest_$code"
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = CardBorder, thickness = 1.dp)
    }
}

@Composable
private fun RemovableSummaryChip(
    label: String,
    accentColor: androidx.compose.ui.graphics.Color,
    showClose: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    testTag: String
) {
    Surface(
        color = accentColor.copy(alpha = 0.14f),
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
                color = TextPrimary,
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

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember {
        mutableStateOf(if (mode == AirportDialogMode.ORIGINS) AirportCategory.MEXICO else AirportCategory.ALL)
    }

    val filteredAirports = remember(searchQuery, selectedCategory, mode) {
        AirportData.airports.filter { airport ->
            val matchesCategory = when (selectedCategory) {
                AirportCategory.ALL -> true
                AirportCategory.MEXICO -> airport.isMexican
                AirportCategory.BEACH -> airport.isBeach
                AirportCategory.INTERNATIONAL -> !airport.isMexican
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
            color = SurfaceNavy,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CardBorder),
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
                                "Aeropuertos de Salida"
                            } else {
                                "Aeropuertos de Destino"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = if (mode == AirportDialogMode.ORIGINS) {
                                "Selecciona uno o varios orígenes en México o EE.UU."
                            } else {
                                "Elige destinos específicos o deja vacío para Cualquier destino"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
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
                            tint = TextSecondary
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
                            containerColor = GreenTint,
                            contentColor = NeonGreen
                        ),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
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
                        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 44.dp)
                            .testTag("dialog_clear_destinations_button")
                    ) {
                        Text(
                            text = "Cambiar a Cualquier destino",
                            color = NeonPink
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por ciudad o código IATA (ej. AGU, NLU, CUN)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = NeonCyan
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
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
                                selectedContainerColor = CyanTint,
                                selectedLabelColor = NeonCyan,
                                containerColor = CardElevated,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = active,
                                selectedBorderColor = NeonCyan,
                                borderColor = CardBorder
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
                            accentColor = if (mode == AirportDialogMode.ORIGINS) NeonCyan else NeonPink,
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
                        Text("Listo", color = NeonCyan, fontWeight = FontWeight.Bold)
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
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) accentColor.copy(alpha = 0.14f) else CardElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) accentColor else CardBorder
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
                color = CardHighest,
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
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = airport.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (airport.isMexican) {
                        val tuaColor = if (airport.domesticTuaMxn <= 400) NeonGreen else TextMuted
                        Text(
                            text = "TUA Nac: $${airport.domesticTuaMxn} MXN",
                            style = MaterialTheme.typography.labelSmall,
                            color = tuaColor
                        )
                    } else {
                        Text(
                            text = "Tasa Intl: $${airport.internationalTuaMxn} MXN",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    if (!airport.highlightTag.isNullOrBlank()) {
                        Text(
                            text = airport.highlightTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor else CardHighest),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seleccionado",
                        tint = DeepNightBg,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
