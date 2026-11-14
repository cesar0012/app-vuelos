package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AirportSelectorDialog
import com.example.ui.components.CheapTravelTopHeader
import com.example.ui.components.FareBreakdownSheet
import com.example.ui.components.PromoLocatorDialog
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.CalendarPriceScreen
import com.example.ui.screens.DealsRadarScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.CardElevated
import com.example.ui.theme.CyanTint
import com.example.ui.theme.DeepNightBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.LocationDetector
import com.example.util.NotificationHelper
import com.example.viewmodel.AirportDialogMode
import com.example.viewmodel.CheapTravelViewModel
import com.example.viewmodel.MainTab

@Composable
fun CheapTravelApp(
    viewModel: CheapTravelViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedOrigins by viewModel.selectedOrigins.collectAsStateWithLifecycle()
    val selectedDestinations by viewModel.selectedDestinations.collectAsStateWithLifecycle()
    val gpsBanner by viewModel.detectedGpsBanner.collectAsStateWithLifecycle()
    val airportDialogMode by viewModel.airportDialogMode.collectAsStateWithLifecycle()
    val calculatorFlights by viewModel.calculatorFlights.collectAsStateWithLifecycle()
    val promoLocatorDeal by viewModel.promoLocatorDeal.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val dealsState by viewModel.dealsRadarState.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val calendarState by viewModel.calendarState.collectAsStateWithLifecycle()
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    val alertMatchCounts by viewModel.alertMatchCounts.collectAsStateWithLifecycle()
    val lastScanSummary by viewModel.lastScanSummary.collectAsStateWithLifecycle()
    val liveSyncState by viewModel.liveSyncState.collectAsStateWithLifecycle()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.detectNearestAirportManually(context)
        } else {
            viewModel.showMessage("Permiso de ubicación denegado. Puedes elegir tu aeropuerto manualmente.")
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.sendTestPushNotification(context)
        } else {
            viewModel.showMessage("Permiso de notificaciones denegado.")
        }
    }

    // Attempt first-launch automatic GPS airport detection if permission already exists
    LaunchedEffect(Unit) {
        if (LocationDetector.hasLocationPermission(context)) {
            viewModel.attemptFirstLaunchGpsIfNeeded(context)
        }
    }

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearSnackbar()
    }

    if (selectedTab != MainTab.DEALS &&
        airportDialogMode == null &&
        calculatorFlights == null &&
        promoLocatorDeal == null
    ) {
        BackHandler {
            viewModel.selectTab(MainTab.DEALS)
        }
    }

    val requestOrDetectLocation = {
        if (LocationDetector.hasLocationPermission(context)) {
            viewModel.detectNearestAirportManually(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    Scaffold(
        containerColor = DeepNightBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CheapTravelTopHeader(
                selectedOrigins = selectedOrigins,
                selectedDestinations = selectedDestinations,
                gpsBanner = gpsBanner,
                onOpenOriginsDialog = { viewModel.openAirportDialog(AirportDialogMode.ORIGINS) },
                onOpenDestinationsDialog = { viewModel.openAirportDialog(AirportDialogMode.DESTINATIONS) },
                onRemoveOrigin = viewModel::removeOriginChip,
                onRemoveDestination = viewModel::removeDestinationChip,
                onDetectGps = { requestOrDetectLocation() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceNavy,
                contentColor = TextPrimary
            ) {
                MainTab.entries.forEach { tab ->
                    val selected = selectedTab == tab
                    val icon = when (tab) {
                        MainTab.DEALS -> if (selected) Icons.Filled.Bolt else Icons.Outlined.Bolt
                        MainTab.SEARCH -> if (selected) Icons.Filled.Search else Icons.Outlined.Search
                        MainTab.CALENDAR -> if (selected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth
                        MainTab.ALERTS -> if (selected) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications
                    }
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = { Icon(imageVector = icon, contentDescription = tab.label) },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = CyanTint,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.DEALS -> DealsRadarScreen(
                    state = dealsState,
                    liveSyncState = liveSyncState,
                    onRefreshLive = { viewModel.refreshLiveDeals(forceRefresh = true) },
                    onQuickFilterSelected = viewModel::setDealQuickFilter,
                    onMaxBaseFareChange = viewModel::setDealMaxBaseFare,
                    onSortOrderChange = viewModel::setDealSortOrder,
                    onOpenCalculator = { deal -> viewModel.openCalculator(deal, null) },
                    onOpenPromoLocator = viewModel::openPromoLocator
                )

                MainTab.SEARCH -> SearchScreen(
                    state = searchState,
                    onMaxTotalChange = viewModel::setSearchMaxTotal,
                    onDirectOnlyChange = viewModel::setSearchDirectOnly,
                    onAirlineFilterChange = viewModel::setSearchAirlineFilter,
                    onSortOrderChange = viewModel::setSearchSortOrder,
                    onOpenCalculator = { deal -> viewModel.openCalculator(deal, null) },
                    onOpenPromoLocator = viewModel::openPromoLocator
                )

                MainTab.CALENDAR -> CalendarPriceScreen(
                    state = calendarState,
                    liveSyncState = liveSyncState,
                    onRefreshLive = { viewModel.refreshLiveDeals(forceRefresh = true) },
                    onTripModeChange = viewModel::setTripMode,
                    onStepChange = viewModel::setRoundTripStep,
                    onRouteChange = viewModel::setCalendarRoute,
                    onSwapRoute = viewModel::swapCalendarRoute,
                    onViewModeChange = viewModel::setCalendarViewMode,
                    onSortOptionChange = viewModel::setCalendarSortOption,
                    onAirlineFilterChange = viewModel::setCalendarAirlineFilter,
                    onDirectOnlyChange = viewModel::setCalendarDirectOnly,
                    onTimePeriodChange = viewModel::setCalendarTimeFilter,
                    onNavigateMonth = viewModel::navigateCalendarMonth,
                    onDateClicked = viewModel::onCalendarDateClicked,
                    onSelectFlightForPlan = viewModel::selectCalendarFlight,
                    onOpenCalculator = viewModel::openCalculator,
                    onOpenPromoLocator = viewModel::openPromoLocator,
                    onClearPlan = viewModel::clearTripPlan,
                    onShowMessage = viewModel::showMessage
                )

                MainTab.ALERTS -> AlertsScreen(
                    alerts = alerts,
                    matchCounts = alertMatchCounts,
                    lastScanSummary = lastScanSummary,
                    defaultOriginCode = selectedOrigins.firstOrNull() ?: "AGU",
                    onCreateAlert = viewModel::createAlert,
                    onToggleAlert = viewModel::toggleAlert,
                    onDeleteAlert = viewModel::deleteAlert,
                    onScanOffers = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            !NotificationHelper.hasNotificationPermission(context)
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        viewModel.scanAlertsAndNotify(context)
                    },
                    onTestPushNotification = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            !NotificationHelper.hasNotificationPermission(context)
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.sendTestPushNotification(context)
                        }
                    }
                )
            }
        }
    }

    airportDialogMode?.let { mode ->
        AirportSelectorDialog(
            mode = mode,
            selectedCodes = if (mode == AirportDialogMode.ORIGINS) selectedOrigins else selectedDestinations,
            onToggleAirport = { code ->
                if (mode == AirportDialogMode.ORIGINS) {
                    viewModel.toggleOriginSelection(code)
                } else {
                    viewModel.toggleDestinationSelection(code)
                }
            },
            onClearAll = {
                if (mode == AirportDialogMode.DESTINATIONS) {
                    viewModel.clearAllDestinations()
                }
            },
            onDetectGps = { requestOrDetectLocation() },
            onDismiss = viewModel::closeAirportDialog
        )
    }

    calculatorFlights?.let { (outbound, returnFlight) ->
        FareBreakdownSheet(
            outbound = outbound,
            returnFlight = returnFlight,
            onDismiss = viewModel::closeCalculator
        )
    }

    promoLocatorDeal?.let { deal ->
        PromoLocatorDialog(
            deal = deal,
            onDismiss = viewModel::closePromoLocator
        )
    }
}
