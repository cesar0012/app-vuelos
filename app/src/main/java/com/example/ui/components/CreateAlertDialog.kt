package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.AirportData
import com.example.model.Airport
import com.example.model.FareBreakdown
import com.example.model.FlightDeal
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.HotDiscountPink

@Composable
fun CreateAlertDialog(
    initialDeal: FlightDeal? = null,
    onSaveAlert: (origin: Airport, destination: Airport, maxPrice: Double, airlinePref: String, targetMonth: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var origin by remember {
        mutableStateOf(initialDeal?.origin ?: AirportData.findByCode("MEX"))
    }
    var destination by remember {
        mutableStateOf(initialDeal?.destination ?: AirportData.findByCode("CUN"))
    }
    var maxPrice by remember {
        mutableFloatStateOf(initialDeal?.previewTotalMxn?.toFloat() ?: 799f)
    }
    var airlinePreference by remember {
        mutableStateOf(initialDeal?.airline?.displayName ?: "Todas las mexicanas")
    }
    var targetMonth by remember {
        mutableStateOf("Próximos 3 meses")
    }

    var showOriginPicker by remember { mutableStateOf(false) }
    var showDestPicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF2E3E5A), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Crear Alerta Push",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_create_alert_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Te avisaremos de inmediato mediante una notificación push cuando un vuelo baje de tu presupuesto configurado.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Route selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Origin box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF162035))
                            .border(1.dp, Color(0xFF2B3A55), RoundedCornerShape(12.dp))
                            .clickable { showOriginPicker = true }
                            .padding(12.dp)
                            .testTag("alert_origin_picker")
                    ) {
                        Column {
                            Text("Origen", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "${origin.city} (${origin.code})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }

                    // Dest box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF162035))
                            .border(1.dp, Color(0xFF2B3A55), RoundedCornerShape(12.dp))
                            .clickable { showDestPicker = true }
                            .padding(12.dp)
                            .testTag("alert_dest_picker")
                    ) {
                        Column {
                            Text("Destino", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "${destination.city} (${destination.code})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Max price threshold slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Precio máximo deseado:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = FareBreakdown.formatCurrency(maxPrice.toDouble()),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = HotDiscountPink
                    )
                }

                Slider(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    valueRange = 199f..2500f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = HotDiscountPink,
                        activeTrackColor = HotDiscountPink,
                        inactiveTrackColor = Color(0xFF22304A)
                    ),
                    modifier = Modifier.testTag("alert_price_slider")
                )

                Text(
                    text = "Incluye vuelos desde promociones de $199 MXN de tarifa base hasta el límite seleccionado.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Airline preference
                Text(
                    text = "Aerolínea preferida:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))

                val airlines = listOf("Todas las mexicanas", "Volaris", "Viva Aerobus", "Aeroméxico", "Mexicana")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    airlines.take(3).forEach { airline ->
                        val isSelected = airlinePreference == airline
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { airlinePreference = airline }
                                .background(if (isSelected) CyanNeon else Color(0xFF162035))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = airline,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF0A0F1D) else Color(0xFFCBD5E1),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                Button(
                    onClick = {
                        onSaveAlert(origin, destination, maxPrice.toDouble(), airlinePreference, targetMonth)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_alert_confirm_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = Color(0xFF0A0F1D)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Activar Alerta de Precio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }

    if (showOriginPicker) {
        AirportSelectorDialog(
            title = "Selecciona Aeropuerto de Salida",
            isMultiSelect = false,
            selectedAirports = listOf(origin),
            onAirportsSelected = {
                if (it.isNotEmpty()) origin = it.first()
            },
            onDismiss = { showOriginPicker = false }
        )
    }

    if (showDestPicker) {
        AirportSelectorDialog(
            title = "Selecciona Aeropuerto de Destino",
            isMultiSelect = false,
            selectedAirports = listOf(destination),
            onAirportsSelected = {
                if (it.isNotEmpty()) destination = it.first()
            },
            onDismiss = { showDestPicker = false }
        )
    }
}
