package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FareBreakdown
import com.example.model.FlightAlert
import com.example.ui.CheapTravelUiState
import com.example.ui.CheapTravelViewModel
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen

@Composable
fun AlertsScreen(
    alerts: List<FlightAlert>,
    uiState: CheapTravelUiState,
    viewModel: CheapTravelViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = HotDiscountPink,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Alertas de Precio en Segundo Plano",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Text(
                text = "Recibe notificaciones automáticas en tu teléfono en cuanto detectemos tarifas que bajen de tu presupuesto configurado.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        // Action Buttons: Create Alert & Test Push
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.openCreateAlert() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("create_new_alert_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = Color(0xFF0A0F1D)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vigilar Nueva Ruta",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.scanAndTriggerAlerts() },
                        enabled = !uiState.isScanning,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("scan_now_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = CyanNeon
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = CyanNeon
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isScanning) "Escaneando..." else "Escanear Ahora",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.sendTestPushNotification() },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("test_push_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = SuperDealGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Probar Notificación",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuperDealGreen
                        )
                    }
                }
            }
        }

        // Active Alerts Section Title
        item {
            Text(
                text = "Tus Rutas Monitoreadas (${alerts.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Alerts List
        if (alerts.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No tienes alertas activas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Crea tu primera alerta para que Cheap Travel te avise de ofertas de $199 MXN antes de que se agoten.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(alerts, key = { it.id }) { alert ->
                AlertCard(
                    alert = alert,
                    onToggle = { viewModel.toggleAlertEnabled(alert) },
                    onDelete = { viewModel.deleteAlert(alert) }
                )
            }
        }
    }
}

@Composable
private fun AlertCard(
    alert: FlightAlert,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141D30)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (alert.isEnabled) Color(0xFF2E3E5C) else Color(0xFF1B2438),
                RoundedCornerShape(18.dp)
            )
            .testTag("alert_card_${alert.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Route and Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${alert.originCode} ➔ ${alert.destinationCode}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (alert.isEnabled) Color.White else Color(0xFF64748B)
                        )
                        if (alert.isEnabled) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SuperDealGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Vigilando",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuperDealGreen
                                )
                            }
                        }
                    }
                    Text(
                        text = "${alert.originCity} a ${alert.destinationCity}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Switch(
                    checked = alert.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanNeon,
                        checkedTrackColor = Color(0xFF004D54)
                    ),
                    modifier = Modifier.testTag("toggle_alert_${alert.id}")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Criteria Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Límite deseado: ${FareBreakdown.formatCurrency(alert.targetMaxPriceMxn)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HotDiscountPink
                    )
                    Text(
                        text = "Aerolíneas: ${alert.airlinePreference} • ${alert.targetMonth}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_alert_${alert.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar alerta",
                        tint = Color(0xFFEF4444)
                    )
                }
            }

            // Best price detected badge
            alert.bestPriceDetectedMxn?.let { bestPrice ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F2624))
                        .border(1.dp, SuperDealGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = SuperDealGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Mejor oferta detectada:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuperDealGreen
                            )
                        }
                        Text(
                            text = "${FareBreakdown.formatCurrency(bestPrice)} (${alert.lastDealAirline ?: "Promo"})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
