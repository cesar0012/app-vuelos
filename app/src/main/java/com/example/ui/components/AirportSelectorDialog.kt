package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AirportData
import com.example.model.Airport
import com.example.model.FareBreakdown
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen

@Composable
fun AirportSelectorDialog(
    title: String,
    isMultiSelect: Boolean,
    selectedAirports: List<Airport>,
    onAirportsSelected: (List<Airport>) -> Unit,
    onDismiss: () -> Unit,
    allowAnyDestination: Boolean = false,
    onDetectGpsLocation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentSelection by remember { mutableStateOf(selectedAirports.toSet()) }

    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            AirportData.allAirports
        } else {
            AirportData.allAirports.filter {
                it.city.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true) ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.country.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF26354D), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1728))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxHeight()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isMultiSelect) "Puedes marcar varios aeropuertos de salida" else "Selecciona el destino deseado",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_airport_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por ciudad o código IATA (ej. MEX, Cancún)...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = CyanNeon
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_airport_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = Color(0xFF2E3E5A),
                        focusedContainerColor = Color(0xFF162035),
                        unfocusedContainerColor = Color(0xFF131B2C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // GPS detect button if provided
                if (onDetectGpsLocation != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF13223B))
                            .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable {
                                onDetectGpsLocation()
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("gps_detect_airport_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Detectar mi aeropuerto más cercano automáticamente",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanNeon
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Selected airports removable chips (Requested: tachita para quitar rápido)
                if (currentSelection.isNotEmpty()) {
                    Column(modifier = Modifier.padding(bottom = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Seleccionados actualmente (Toca para quitar):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                            if (allowAnyDestination && currentSelection.isNotEmpty()) {
                                Text(
                                    text = "Quitar todos",
                                    fontSize = 11.sp,
                                    color = HotDiscountPink,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { currentSelection = emptySet() }
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(currentSelection.toList(), key = { "top_sel_${it.code}" }) { airport ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFF1E2D48))
                                        .border(1.dp, CyanNeon, RoundedCornerShape(20.dp))
                                        .clickable {
                                            val next = currentSelection.toMutableSet()
                                            if (allowAnyDestination || next.size > 1) {
                                                next.removeIf { it.code == airport.code }
                                            }
                                            currentSelection = next
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("dialog_chip_remove_${airport.code}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${airport.city} (${airport.code})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Quitar ${airport.code}",
                                            tint = HotDiscountPink,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Optional "Cualquier Destino" chip and category presets when choosing arrival destinations
                if (allowAnyDestination) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isMultiSelect) {
                                        currentSelection = emptySet()
                                    } else {
                                        onAirportsSelected(emptyList())
                                        onDismiss()
                                    }
                                }
                                .background(if (currentSelection.isEmpty()) Color(0xFF1E2D4A) else Color(0xFF131C2E))
                                .border(
                                    1.dp,
                                    if (currentSelection.isEmpty()) CyanNeon else Color(0xFF26354D),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .testTag("airport_any_destination")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Cualquier destino (Ver todas las ofertas)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Encuentra vuelos de $199 a cualquier playa o ciudad nacional",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick category preset buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF162035))
                                    .clickable {
                                        val beaches = AirportData.allAirports.filter { it.isBeachDestination }
                                        currentSelection = beaches.toSet()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BeachAccess,
                                        contentDescription = null,
                                        tint = SuperDealGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text("Playas", fontSize = 11.sp, color = SuperDealGreen, fontWeight = FontWeight.Bold)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF162035))
                                    .clickable {
                                        val mx = AirportData.mexicanAirports
                                        currentSelection = mx.toSet()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text("Nacionales", fontSize = 11.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF162035))
                                    .clickable {
                                        val intl = AirportData.internationalAirports
                                        currentSelection = intl.toSet()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD166),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text("Internacional", fontSize = 11.sp, color = Color(0xFFFFD166), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Airport list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredList, key = { it.code }) { airport ->
                        val isSelected = currentSelection.any { it.code == airport.code }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isMultiSelect) {
                                        val next = currentSelection.toMutableSet()
                                        if (isSelected) {
                                            if (allowAnyDestination || next.size > 1) {
                                                next.removeIf { it.code == airport.code }
                                            }
                                        } else {
                                            next.add(airport)
                                        }
                                        currentSelection = next
                                    } else {
                                        onAirportsSelected(listOf(airport))
                                        onDismiss()
                                    }
                                }
                                .background(if (isSelected) Color(0xFF1D2C47) else Color(0xFF121929))
                                .border(
                                    1.dp,
                                    if (isSelected) CyanNeon else Color(0xFF222F44),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .testTag("airport_item_${airport.code}")
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
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (airport.code == "NLU") SuperDealGreen.copy(alpha = 0.2f)
                                                else Color(0xFF1E293B)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = airport.code,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = if (airport.code == "NLU") SuperDealGreen else Color.White
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "${airport.city} • ${airport.country}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${airport.name} • TUA: ${FareBreakdown.formatCurrency(airport.nationalTuaMxn)}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Seleccionado",
                                        tint = CyanNeon,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom button for multi-select
                if (isMultiSelect) {
                    Button(
                        onClick = {
                            onAirportsSelected(currentSelection.toList())
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_airports_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanNeon,
                            contentColor = Color(0xFF0A0F1D)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Confirmar ${currentSelection.size} Aeropuertos",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
