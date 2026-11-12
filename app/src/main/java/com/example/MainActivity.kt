package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Airport
import com.example.ui.AppTab
import com.example.ui.CheapTravelViewModel
import com.example.ui.components.AirportSelectorDialog
import com.example.ui.components.CreateAlertDialog
import com.example.ui.components.FareBreakdownSheet
import com.example.ui.components.PromoLocatorDialog
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.CalendarPriceScreen
import com.example.ui.screens.DealsRadarScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.CheapTravelTheme
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.HotDiscountPink
import com.example.ui.theme.SuperDealGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheapTravelTheme {
                CheapTravelApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheapTravelApp(
    viewModel: CheapTravelViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Location detection helper
    val triggerLocationDetection: () -> Unit = {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            var bestLocation: Location? = null
            try {
                val providers = listOf(
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER,
                    LocationManager.PASSIVE_PROVIDER
                )
                for (p in providers) {
                    val loc = locationManager?.getLastKnownLocation(p)
                    if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                        bestLocation = loc
                    }
                }
            } catch (_: SecurityException) {}

            if (bestLocation != null) {
                viewModel.detectNearestAirport(bestLocation.latitude, bestLocation.longitude)
            } else {
                viewModel.detectNearestAirport(21.88, -102.29) // Default to Aguascalientes coords
            }
        }
    }

    // Request Location permissions
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            triggerLocationDetection()
        }
    }

    // Request POST_NOTIFICATIONS permission for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.sendTestPushNotification()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Auto-detect location on start if permission already granted, or ask permission once
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            triggerLocationDetection()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Collect snackbar messages
    LaunchedEffect(viewModel) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle back button navigation
    BackHandler(enabled = uiState.currentTab != AppTab.RADAR || uiState.selectedFlight != null) {
        if (uiState.selectedFlight != null) {
            viewModel.closeFlightDetail()
        } else {
            viewModel.selectTab(AppTab.RADAR)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF080D1A),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                selectedOrigins = uiState.selectedOrigins,
                selectedDestinations = uiState.selectedDestinations,
                onOriginClick = { viewModel.setShowOriginPicker(true) },
                onDestinationClick = { viewModel.setShowDestinationPicker(true) },
                onRemoveOrigin = { viewModel.removeOrigin(it) },
                onRemoveDestination = { viewModel.removeDestination(it) },
                onNotificationClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.selectTab(AppTab.ALERTS)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Tab 1: Radar Flash
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.RADAR,
                    onClick = { viewModel.selectTab(AppTab.RADAR) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Radar Flash",
                            tint = if (uiState.currentTab == AppTab.RADAR) HotDiscountPink else Color(0xFF94A3B8)
                        )
                    },
                    label = {
                        Text(
                            text = "Ofertas $199",
                            fontSize = 11.sp,
                            fontWeight = if (uiState.currentTab == AppTab.RADAR) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = HotDiscountPink.copy(alpha = 0.2f),
                        selectedTextColor = HotDiscountPink,
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_radar")
                )

                // Tab 2: Buscador
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.SEARCH,
                    onClick = { viewModel.selectTab(AppTab.SEARCH) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                            contentDescription = "Buscador",
                            tint = if (uiState.currentTab == AppTab.SEARCH) CyanNeon else Color(0xFF94A3B8)
                        )
                    },
                    label = {
                        Text(
                            text = "Buscador",
                            fontSize = 11.sp,
                            fontWeight = if (uiState.currentTab == AppTab.SEARCH) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = CyanNeon.copy(alpha = 0.2f),
                        selectedTextColor = CyanNeon,
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_search")
                )

                // Tab 3: Calendario
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.CALENDAR,
                    onClick = { viewModel.selectTab(AppTab.CALENDAR) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppTab.CALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Calendario",
                            tint = if (uiState.currentTab == AppTab.CALENDAR) SuperDealGreen else Color(0xFF94A3B8)
                        )
                    },
                    label = {
                        Text(
                            text = "Calendario",
                            fontSize = 11.sp,
                            fontWeight = if (uiState.currentTab == AppTab.CALENDAR) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = SuperDealGreen.copy(alpha = 0.2f),
                        selectedTextColor = SuperDealGreen,
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_calendar")
                )

                // Tab 4: Alertas Push
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.ALERTS,
                    onClick = { viewModel.selectTab(AppTab.ALERTS) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (alerts.any { it.isEnabled }) {
                                    Badge(
                                        containerColor = HotDiscountPink,
                                        contentColor = Color.White
                                    ) {
                                        Text("${alerts.count { it.isEnabled }}", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.currentTab == AppTab.ALERTS) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                                contentDescription = "Alertas",
                                tint = if (uiState.currentTab == AppTab.ALERTS) CyanNeon else Color(0xFF94A3B8)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "Alertas",
                            fontSize = 11.sp,
                            fontWeight = if (uiState.currentTab == AppTab.ALERTS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = CyanNeon.copy(alpha = 0.2f),
                        selectedTextColor = CyanNeon,
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_alerts")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.RADAR -> DealsRadarScreen(uiState = uiState, viewModel = viewModel)
                AppTab.SEARCH -> SearchScreen(uiState = uiState, viewModel = viewModel)
                AppTab.CALENDAR -> CalendarPriceScreen(uiState = uiState, viewModel = viewModel)
                AppTab.ALERTS -> AlertsScreen(alerts = alerts, uiState = uiState, viewModel = viewModel)
            }
        }
    }

    // Fare breakdown sheet modal
    uiState.selectedFlight?.let { flight ->
        uiState.currentBreakdown?.let { breakdown ->
            FareBreakdownSheet(
                deal = flight,
                returnDeal = uiState.selectedReturnFlight,
                passengers = uiState.passengers,
                baggageOption = uiState.baggageOption,
                seatOption = uiState.seatOption,
                breakdown = breakdown,
                onPassengerChange = { adults, children, infants ->
                    viewModel.updatePassengers(adults, children, infants)
                },
                onBaggageChange = { option -> viewModel.updateBaggageOption(option) },
                onSeatChange = { option -> viewModel.updateSeatOption(option) },
                onCreateAlert = { deal -> viewModel.openCreateAlert(deal) },
                onDismiss = { viewModel.closeFlightDetail() }
            )
        }
    }

    // Origin Selector Dialog (Multi-select)
    if (uiState.showOriginPicker) {
        AirportSelectorDialog(
            title = "Aeropuertos de Salida",
            isMultiSelect = true,
            selectedAirports = uiState.selectedOrigins,
            onDetectGpsLocation = triggerLocationDetection,
            onAirportsSelected = { airports ->
                if (airports.isNotEmpty()) {
                    viewModel.setOrigins(airports)
                }
                viewModel.setShowOriginPicker(false)
            },
            onDismiss = { viewModel.setShowOriginPicker(false) }
        )
    }

    // Destination Selector Dialog (Supports multi-select or any destination)
    if (uiState.showDestinationPicker) {
        AirportSelectorDialog(
            title = "Aeropuertos de Destino de Llegada",
            isMultiSelect = true,
            selectedAirports = uiState.selectedDestinations,
            allowAnyDestination = true,
            onAirportsSelected = { airports ->
                viewModel.setDestinations(airports)
                viewModel.setShowDestinationPicker(false)
            },
            onDismiss = { viewModel.setShowDestinationPicker(false) }
        )
    }

    // Create Alert Dialog
    if (uiState.showCreateAlertDialog) {
        CreateAlertDialog(
            initialDeal = uiState.newAlertPreselectedDeal,
            onSaveAlert = { origin, destination, maxPrice, airlinePref, targetMonth ->
                viewModel.saveAlert(origin, destination, maxPrice, airlinePref, targetMonth)
            },
            onDismiss = { viewModel.closeCreateAlert() }
        )
    }

    // Promo Locator Dialog (Google Flights verification & Airline promo booking)
    if (uiState.showPromoLocatorDialog && uiState.promoLocatorDeal != null) {
        PromoLocatorDialog(
            deal = uiState.promoLocatorDeal!!,
            passengers = uiState.passengers,
            onDismiss = { viewModel.closePromoLocator() }
        )
    }
}

@Composable
private fun TopAppBar(
    selectedOrigins: List<Airport>,
    selectedDestinations: List<Airport>,
    onOriginClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onRemoveOrigin: (Airport) -> Unit,
    onRemoveDestination: (Airport) -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Airplane Icon badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(HotDiscountPink, CyanNeon)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = null,
                        tint = Color(0xFF0A0F1D),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Cheap Travel",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(HotDiscountPink)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "PROMO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = "Volaris • Viva • Aeroméxico • TUA",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Quick Origin and Destination Switcher Pills
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                        .clickable(onClick = onOriginClick)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("top_origin_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = selectedOrigins.joinToString("+") { it.code },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                        .clickable(onClick = onDestinationClick)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("top_destination_pill")
                ) {
                    val destText = if (selectedDestinations.isEmpty()) "Todos" else selectedDestinations.joinToString("+") { it.code }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightLand,
                            contentDescription = null,
                            tint = HotDiscountPink,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = destText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HotDiscountPink
                        )
                    }
                }

                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .testTag("top_alert_icon")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alertas",
                        tint = HotDiscountPink,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        // Active removable filter chips with '✕' to quickly remove without opening the dialog
        if (selectedOrigins.isNotEmpty() || selectedDestinations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(selectedOrigins, key = { "top_org_${it.code}" }) { airport ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF16233B))
                            .border(1.dp, CyanNeon.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .clickable { onRemoveOrigin(airport) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("top_remove_origin_${airport.code}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${airport.city} (${airport.code})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Quitar ${airport.code}",
                                tint = HotDiscountPink,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                items(selectedDestinations, key = { "top_dst_${it.code}" }) { airport ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF26182C))
                            .border(1.dp, HotDiscountPink.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .clickable { onRemoveDestination(airport) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("top_remove_dest_${airport.code}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlightLand,
                                contentDescription = null,
                                tint = HotDiscountPink,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${airport.city} (${airport.code})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Quitar ${airport.code}",
                                tint = HotDiscountPink,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
