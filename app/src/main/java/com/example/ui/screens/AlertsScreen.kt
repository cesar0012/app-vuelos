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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.FlightAlert
import com.example.model.AirportData
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.GreenTint
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PinkTint
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun AlertsScreen(
    alerts: List<FlightAlert>,
    matchCounts: Map<Int, Int>,
    lastScanSummary: String?,
    defaultOriginCode: String,
    onCreateAlert: (originCode: String, destCode: String?, maxPriceMxn: Int, nonStopOnly: Boolean) -> Unit,
    onToggleAlert: (id: Int, isActive: Boolean) -> Unit,
    onDeleteAlert: (id: Int) -> Unit,
    onScanOffers: () -> Unit,
    onTestPushNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOrigin by remember(defaultOriginCode) { mutableStateOf(defaultOriginCode) }
    var selectedDestination by remember { mutableStateOf<String?>("PVR") }
    var maxPrice by remember { mutableFloatStateOf(499f) }
    var nonStopOnly by remember { mutableStateOf(false) }

    var showOriginMenu by remember { mutableStateOf(false) }
    var showDestMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNightBg)
            .testTag("alerts_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Create Alert Form Card
        item(key = "create_alert_card") {
            Surface(
                color = CardElevated,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = null, tint = NeonCyan)
                        Text(
                            text = "Nueva Alerta de Precio (Room)",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Guarda rutas objetivo y recibe notificaciones push locales cuando bajen de precio.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = DeepNightBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showOriginMenu = true }
                                    .testTag("alert_origin_selector")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Origen", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    Text(selectedOrigin, style = MaterialTheme.typography.titleMedium, color = NeonCyan)
                                }
                            }
                            DropdownMenu(
                                expanded = showOriginMenu,
                                onDismissRequest = { showOriginMenu = false },
                                modifier = Modifier.background(SurfaceNavy)
                            ) {
                                AirportData.mexicanAirports.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = TextPrimary) },
                                        onClick = {
                                            selectedOrigin = ap.code
                                            showOriginMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = DeepNightBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDestMenu = true }
                                    .testTag("alert_dest_selector")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Destino", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    Text(
                                        text = selectedDestination ?: "Cualquiera",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = NeonPink
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = showDestMenu,
                                onDismissRequest = { showDestMenu = false },
                                modifier = Modifier.background(SurfaceNavy)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Cualquier destino", color = NeonPink, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        selectedDestination = null
                                        showDestMenu = false
                                    }
                                )
                                AirportData.airports.filter { it.code != selectedOrigin }.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = TextPrimary) },
                                        onClick = {
                                            selectedDestination = ap.code
                                            showDestMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tarifa base máxima", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                        Text(
                            text = "$${maxPrice.roundToInt()} MXN",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = maxPrice,
                        onValueChange = { maxPrice = it },
                        valueRange = 199f..4000f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGreen,
                            activeTrackColor = NeonGreen,
                            inactiveTrackColor = CardBorder
                        ),
                        modifier = Modifier.testTag("alert_max_price_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Solo vuelos directos", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Switch(
                            checked = nonStopOnly,
                            onCheckedChange = { nonStopOnly = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DeepNightBg,
                                checkedTrackColor = NeonCyan
                            ),
                            modifier = Modifier.testTag("alert_nonstop_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onCreateAlert(selectedOrigin, selectedDestination, maxPrice.roundToInt(), nonStopOnly)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepNightBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("create_alert_save_btn")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Alerta", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Scan & Test Push Controls
        item(key = "alert_actions_row") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onScanOffers,
                    colors = ButtonDefaults.buttonColors(containerColor = PinkTint, contentColor = NeonPink),
                    border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("scan_alerts_btn")
                ) {
                    Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Escanear Ofertas", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onTestPushNotification,
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("test_push_notification_btn")
                ) {
                    Icon(Icons.Default.NotificationAdd, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Probar Push", color = NeonGreen, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (!lastScanSummary.isNullOrBlank()) {
            item(key = "scan_summary_banner") {
                Surface(
                    color = GreenTint,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = lastScanSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        item(key = "alerts_list_hdr") {
            Text(
                text = "Alertas Guardadas (${alerts.size})",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }

        if (alerts.isEmpty()) {
            item(key = "alerts_empty") {
                EmptyDealsBox("Aún no tienes alertas guardadas. Crea tu primera alerta arriba para cazar tarifas de $199 MXN.")
            }
        } else {
            items(alerts, key = { it.id }) { alert ->
                val matches = matchCounts[alert.id] ?: 0
                AlertItemCard(
                    alert = alert,
                    matchCount = matches,
                    onToggle = { active -> onToggleAlert(alert.id, active) },
                    onDelete = { onDeleteAlert(alert.id) }
                )
            }
        }
    }
}

@Composable
private fun AlertItemCard(
    alert: FlightAlert,
    matchCount: Int,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val originCity = AirportData.findByCode(alert.originCode)?.city ?: alert.originCode
    val destLabel = alert.destinationCode?.let { code ->
        val city = AirportData.findByCode(code)?.city ?: code
        "$code ($city)"
    } ?: "Cualquier destino"

    Surface(
        color = CardElevated,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (alert.isActive) NeonCyan.copy(alpha = 0.5f) else CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alert_card_${alert.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${alert.originCode} ($originCity) -> $destLabel",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Máx $${alert.maxPriceMxn} MXN base",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonGreen
                    )
                    if (alert.nonStopOnly) {
                        Text(
                            text = "· Directos",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = if (matchCount > 0) CyanTint else DeepNightBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$matchCount vuelos coincidentes",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (matchCount > 0) NeonCyan else TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = alert.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeepNightBg,
                        checkedTrackColor = NeonCyan
                    ),
                    modifier = Modifier.testTag("alert_toggle_${alert.id}")
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("alert_delete_${alert.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar alerta", tint = NeonPink)
                }
            }
        }
    }
}
