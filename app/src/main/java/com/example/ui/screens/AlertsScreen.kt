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
import com.example.ui.theme.AppTheme
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
    val colors = AppTheme.colors
    var selectedOrigin by remember(defaultOriginCode) { mutableStateOf(defaultOriginCode) }
    var selectedDestination by remember { mutableStateOf<String?>("PVR") }
    var maxPrice by remember { mutableFloatStateOf(499f) }
    var nonStopOnly by remember { mutableStateOf(false) }

    var showOriginMenu by remember { mutableStateOf(false) }
    var showDestMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.deepNightBg)
            .testTag("alerts_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Create Alert Form Card
        item(key = "create_alert_card") {
            Surface(
                color = colors.cardElevated,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = null, tint = colors.neonCyan)
                        Text(
                            text = "Nueva Alerta de Precio (Room)",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "Guarda rutas nacionales en México y recibe notificaciones push locales cuando bajen de precio.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = colors.cardHighest,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, colors.neonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showOriginMenu = true }
                                    .testTag("alert_origin_selector")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Origen", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                                    Text(selectedOrigin, style = MaterialTheme.typography.titleMedium, color = colors.neonCyan)
                                }
                            }
                            DropdownMenu(
                                expanded = showOriginMenu,
                                onDismissRequest = { showOriginMenu = false },
                                modifier = Modifier.background(colors.surfaceNavy)
                            ) {
                                AirportData.mexicanAirports.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = colors.textPrimary) },
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
                                color = colors.cardHighest,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDestMenu = true }
                                    .testTag("alert_dest_selector")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Destino", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                                    Text(
                                        text = selectedDestination ?: "Cualquiera",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.neonPink
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = showDestMenu,
                                onDismissRequest = { showDestMenu = false },
                                modifier = Modifier.background(colors.surfaceNavy)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Cualquier destino", color = colors.neonPink, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        selectedDestination = null
                                        showDestMenu = false
                                    }
                                )
                                AirportData.airports.filter { it.code != selectedOrigin }.forEach { ap ->
                                    DropdownMenuItem(
                                        text = { Text("${ap.code} · ${ap.city}", color = colors.textPrimary) },
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
                        Text("Tarifa base máxima", style = MaterialTheme.typography.labelLarge, color = colors.textPrimary)
                        Text(
                            text = "$${maxPrice.roundToInt()} MXN",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.neonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = maxPrice,
                        onValueChange = { maxPrice = it },
                        valueRange = 199f..4000f,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.neonGreen,
                            activeTrackColor = colors.neonGreen,
                            inactiveTrackColor = colors.cardBorder
                        ),
                        modifier = Modifier.testTag("alert_max_price_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Solo vuelos directos", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        Switch(
                            checked = nonStopOnly,
                            onCheckedChange = { nonStopOnly = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.onAccent,
                                checkedTrackColor = colors.neonCyan
                            ),
                            modifier = Modifier.testTag("alert_nonstop_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onCreateAlert(selectedOrigin, selectedDestination, maxPrice.roundToInt(), nonStopOnly)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.neonCyan, contentColor = colors.onAccent),
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
                    colors = ButtonDefaults.buttonColors(containerColor = colors.pinkTint, contentColor = colors.neonPink),
                    border = BorderStroke(1.dp, colors.neonPink.copy(alpha = 0.6f)),
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
                    border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("test_push_notification_btn")
                ) {
                    Icon(Icons.Default.NotificationAdd, contentDescription = null, tint = colors.neonGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Probar Push", color = colors.neonGreen, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (!lastScanSummary.isNullOrBlank()) {
            item(key = "scan_summary_banner") {
                Surface(
                    color = colors.greenTint,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.neonGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = lastScanSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        item(key = "alerts_list_hdr") {
            Text(
                text = "Alertas Guardadas (${alerts.size})",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
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
    val colors = AppTheme.colors
    val originCity = AirportData.findByCode(alert.originCode)?.city ?: alert.originCode
    val destLabel = alert.destinationCode?.let { code ->
        val city = AirportData.findByCode(code)?.city ?: code
        "$code ($city)"
    } ?: "Cualquier destino"

    Surface(
        color = colors.cardElevated,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (alert.isActive) colors.neonCyan.copy(alpha = 0.5f) else colors.cardBorder),
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
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Máx $${alert.maxPriceMxn} MXN base",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.neonGreen
                    )
                    if (alert.nonStopOnly) {
                        Text(
                            text = "· Directos",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.neonCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = if (matchCount > 0) colors.cyanTint else colors.cardHighest,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$matchCount vuelos coincidentes",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (matchCount > 0) colors.neonCyan else colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = alert.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.onAccent,
                        checkedTrackColor = colors.neonCyan
                    ),
                    modifier = Modifier.testTag("alert_toggle_${alert.id}")
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("alert_delete_${alert.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar alerta", tint = colors.neonPink)
                }
            }
        }
    }
}
