package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FlightAlert
import com.example.data.FlightAlertRepository
import com.example.data.FlightDealsRepository
import com.example.data.LiveAirlineScraper
import com.example.data.OriginPreferences
import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.FlightDeal
import com.example.model.TimePeriod
import com.example.model.TravelDate
import com.example.util.LocationDetector
import com.example.util.NotificationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MainTab(val label: String) {
    DEALS("Ofertas"),
    SEARCH("Buscador"),
    CALENDAR("Calendario"),
    ALERTS("Alertas")
}

enum class SortOrder(val label: String) {
    PRICE("Precio"),
    DATE("Fecha")
}

enum class DealQuickFilter(val label: String) {
    ALL("Todas"),
    LIVE_VERIFIED("En Vivo"),
    BEACHES("Playas"),
    DOMESTIC("Nacionales"),
    INTERNATIONAL("Internacionales"),
    VOLARIS_199("Solo Volaris"),
    VIVA("Solo Viva Aerobus")
}

enum class CalendarViewMode(val label: String) {
    MONTH_GRID("Calendario"),
    LIST("Lista de Vuelos")
}

enum class CalendarSortOption(val label: String) {
    CHEAPEST("Más Barato"),
    SOONEST("Más Próximo"),
    RECOMMENDED("Recomendado (Balance)")
}

enum class TripMode(val label: String) {
    ONE_WAY("Solo Ida"),
    ROUND_TRIP("Viaje Redondo")
}

enum class RoundTripStep(val label: String) {
    OUTBOUND("1. Salida"),
    RETURN("2. Regreso")
}

enum class AirportDialogMode {
    ORIGINS,
    DESTINATIONS
}

data class LiveSyncState(
    val isSyncing: Boolean = false,
    val liveFareCount: Int = 0,
    val statusMessage: String = "Listo para consultar precios en vivo",
    val lastUpdatedTimeLabel: String? = null
)

data class CalendarDayCell(
    val date: TravelDate,
    val isInCurrentMonth: Boolean,
    val isInCatalogRange: Boolean,
    val lowestBaseFareMxn: Int?,
    val airlinesOperating: List<Airline>,
    val dealsCount: Int,
    val hasLiveFare: Boolean = false
)

data class DealsRadarUiState(
    val deals: List<FlightDeal> = emptyList(),
    val headToHeadRouteDateKeys: Set<String> = emptySet(),
    val quickFilter: DealQuickFilter = DealQuickFilter.ALL,
    val maxBaseFareMxn: Float = 2000f,
    val sortOrder: SortOrder = SortOrder.PRICE
)

data class SearchUiState(
    val results: List<FlightDeal> = emptyList(),
    val headToHeadRouteDateKeys: Set<String> = emptySet(),
    val maxTotalMxn: Float = 5500f,
    val directOnly: Boolean = false,
    val airlineFilter: Airline? = null,
    val sortOrder: SortOrder = SortOrder.PRICE
)

data class CalendarUiState(
    val tripMode: TripMode = TripMode.ROUND_TRIP,
    val roundTripStep: RoundTripStep = RoundTripStep.OUTBOUND,
    val originCode: String = "AGU",
    val destinationCode: String = "PVR",
    val viewMode: CalendarViewMode = CalendarViewMode.MONTH_GRID,
    val sortOption: CalendarSortOption = CalendarSortOption.RECOMMENDED,
    val airlineFilter: Airline? = null,
    val directOnly: Boolean = false,
    val timePeriodFilter: TimePeriod? = null,
    val displayedMonth: TravelDate = TravelDate.today().firstOfMonth(),
    val canGoPrevMonth: Boolean = false,
    val canGoNextMonth: Boolean = true,
    val monthCells: List<CalendarDayCell> = emptyList(),
    val selectedOutboundDate: TravelDate? = null,
    val selectedReturnDate: TravelDate? = null,
    val activeDayFlights: List<FlightDeal> = emptyList(),
    val chronologicalFlights: List<FlightDeal> = emptyList(),
    val selectedOutboundFlight: FlightDeal? = null,
    val selectedReturnFlight: FlightDeal? = null,
    val availableRoutesDestinations: List<String> = emptyList(),
    val liveRouteFaresCount: Int = 0
)

class CheapTravelViewModel(application: Application) : AndroidViewModel(application) {

    private val originPreferences = OriginPreferences(application)
    private val dealsRepository = FlightDealsRepository()
    private val liveScraper = LiveAirlineScraper()
    private val alertRepository = FlightAlertRepository(
        AppDatabase.getInstance(application).flightAlertDao()
    )

    private val today: TravelDate = TravelDate.today()
    private val maxCatalogDate: TravelDate = today.plusDays(120)

    private val _catalogVersion = MutableStateFlow(0)
    private val _liveSyncState = MutableStateFlow(LiveSyncState())
    val liveSyncState: StateFlow<LiveSyncState> = _liveSyncState.asStateFlow()

    private val _selectedTab = MutableStateFlow(MainTab.DEALS)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    private val _selectedOrigins = MutableStateFlow(originPreferences.getSelectedOrigins())
    val selectedOrigins: StateFlow<List<String>> = _selectedOrigins.asStateFlow()

    private val _selectedDestinations = MutableStateFlow(originPreferences.getSelectedDestinations())
    val selectedDestinations: StateFlow<List<String>> = _selectedDestinations.asStateFlow()

    private val _detectedGpsBanner = MutableStateFlow<String?>(null)
    val detectedGpsBanner: StateFlow<String?> = _detectedGpsBanner.asStateFlow()

    private val _airportDialogMode = MutableStateFlow<AirportDialogMode?>(null)
    val airportDialogMode: StateFlow<AirportDialogMode?> = _airportDialogMode.asStateFlow()

    private val _calculatorFlights = MutableStateFlow<Pair<FlightDeal, FlightDeal?>?>(null)
    val calculatorFlights: StateFlow<Pair<FlightDeal, FlightDeal?>?> = _calculatorFlights.asStateFlow()

    private val _promoLocatorDeal = MutableStateFlow<FlightDeal?>(null)
    val promoLocatorDeal: StateFlow<FlightDeal?> = _promoLocatorDeal.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Tab 1: Deals Radar filters
    private val _dealQuickFilter = MutableStateFlow(DealQuickFilter.ALL)
    private val _dealMaxBaseFare = MutableStateFlow(2000f)
    private val _dealSortOrder = MutableStateFlow(SortOrder.PRICE)

    val dealsRadarState: StateFlow<DealsRadarUiState> = combine(
        _selectedOrigins,
        _selectedDestinations,
        _dealQuickFilter,
        _dealMaxBaseFare,
        _dealSortOrder,
        _catalogVersion
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val origins = values[0] as List<String>
        @Suppress("UNCHECKED_CAST")
        val dests = values[1] as List<String>
        val quickFilter = values[2] as DealQuickFilter
        val maxBase = values[3] as Float
        val sortOrder = values[4] as SortOrder

        val raw = dealsRepository.getDealsForOriginsAndDestinations(origins, dests)
        val filtered = raw.filter { deal ->
            val matchesMax = deal.baseFareMxn <= maxBase.toInt()
            val destAirport = AirportData.findByCode(deal.destinationCode)
            val matchesQuick = when (quickFilter) {
                DealQuickFilter.ALL -> true
                DealQuickFilter.LIVE_VERIFIED -> deal.isLiveVerified
                DealQuickFilter.BEACHES -> destAirport?.isBeach == true
                DealQuickFilter.DOMESTIC -> !deal.isInternational
                DealQuickFilter.INTERNATIONAL -> deal.isInternational
                DealQuickFilter.VOLARIS_199 -> deal.airline == Airline.VOLARIS
                DealQuickFilter.VIVA -> deal.airline == Airline.VIVA_AEROBUS
            }
            matchesMax && matchesQuick
        }
        val sorted = when (sortOrder) {
            SortOrder.PRICE -> filtered.sortedWith(
                compareByDescending<FlightDeal> { it.isLiveVerified }
                    .thenBy { it.baseFareMxn }
                    .thenBy { it.totalWithTuaMxn }
                    .thenBy { it.departureDate }
            ).let { list ->
                // Sort strictly by price when ALL/other filters, prioritizing live verified ties
                filtered.sortedWith(
                    compareBy<FlightDeal> { it.baseFareMxn }
                        .thenByDescending { it.isLiveVerified }
                        .thenBy { it.totalWithTuaMxn }
                        .thenBy { it.departureDate }
                )
            }
            SortOrder.DATE -> filtered.sortedWith(
                compareBy<FlightDeal> { it.departureDate }
                    .thenByDescending { it.isLiveVerified }
                    .thenBy { it.baseFareMxn }
                    .thenBy { it.departureTime }
            )
        }
        DealsRadarUiState(
            deals = sorted.take(140),
            headToHeadRouteDateKeys = computeHeadToHeadKeys(sorted),
            quickFilter = quickFilter,
            maxBaseFareMxn = maxBase,
            sortOrder = sortOrder
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DealsRadarUiState())

    // Tab 2: Search filters
    private val _searchMaxTotal = MutableStateFlow(5500f)
    private val _searchDirectOnly = MutableStateFlow(false)
    private val _searchAirline = MutableStateFlow<Airline?>(null)
    private val _searchSortOrder = MutableStateFlow(SortOrder.PRICE)

    val searchState: StateFlow<SearchUiState> = combine(
        _selectedOrigins,
        _selectedDestinations,
        _searchMaxTotal,
        _searchDirectOnly,
        _searchAirline,
        _searchSortOrder,
        _catalogVersion
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val origins = values[0] as List<String>
        @Suppress("UNCHECKED_CAST")
        val dests = values[1] as List<String>
        val maxTotal = values[2] as Float
        val directOnly = values[3] as Boolean
        val airline = values[4] as Airline?
        val sortOrder = values[5] as SortOrder

        val raw = dealsRepository.getDealsForOriginsAndDestinations(origins, dests)
        val filtered = raw.filter { deal ->
            deal.totalWithTuaMxn <= maxTotal.toInt() &&
                (!directOnly || deal.isNonStop) &&
                (airline == null || deal.airline == airline)
        }
        val sorted = when (sortOrder) {
            SortOrder.PRICE -> filtered.sortedWith(
                compareBy<FlightDeal> { it.totalWithTuaMxn }
                    .thenByDescending { it.isLiveVerified }
                    .thenBy { it.departureDate }
            )
            SortOrder.DATE -> filtered.sortedWith(
                compareBy<FlightDeal> { it.departureDate }
                    .thenBy { it.totalWithTuaMxn }
                    .thenByDescending { it.isLiveVerified }
            )
        }
        SearchUiState(
            results = sorted.take(150),
            headToHeadRouteDateKeys = computeHeadToHeadKeys(sorted),
            maxTotalMxn = maxTotal,
            directOnly = directOnly,
            airlineFilter = airline,
            sortOrder = sortOrder
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    // Tab 3: Calendar & Trip Plan
    private val _calendarConfig = MutableStateFlow(
        CalendarConfig(
            originCode = originPreferences.getSelectedOrigins().firstOrNull() ?: "AGU",
            destinationCode = defaultDestinationFor(originPreferences.getSelectedOrigins().firstOrNull() ?: "AGU")
        )
    )

    private data class CalendarConfig(
        val tripMode: TripMode = TripMode.ROUND_TRIP,
        val roundTripStep: RoundTripStep = RoundTripStep.OUTBOUND,
        val originCode: String = "AGU",
        val destinationCode: String = "PVR",
        val viewMode: CalendarViewMode = CalendarViewMode.MONTH_GRID,
        val sortOption: CalendarSortOption = CalendarSortOption.RECOMMENDED,
        val airlineFilter: Airline? = null,
        val directOnly: Boolean = false,
        val timePeriodFilter: TimePeriod? = null,
        val monthOffset: Int = 0,
        val selectedOutboundDate: TravelDate? = null,
        val selectedReturnDate: TravelDate? = null,
        val selectedOutboundFlight: FlightDeal? = null,
        val selectedReturnFlight: FlightDeal? = null
    )

    val calendarState: StateFlow<CalendarUiState> = combine(
        _calendarConfig,
        _selectedOrigins,
        _catalogVersion
    ) { cfg, _, _ ->
        buildCalendarState(cfg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), buildCalendarState(_calendarConfig.value))

    // Tab 4: Alerts (Room)
    val alerts: StateFlow<List<FlightAlert>> = alertRepository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _alertMatchCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val alertMatchCounts: StateFlow<Map<Int, Int>> = _alertMatchCounts.asStateFlow()

    private val _lastScanSummary = MutableStateFlow<String?>(null)
    val lastScanSummary: StateFlow<String?> = _lastScanSummary.asStateFlow()

    init {
        NotificationHelper.ensureChannel(application)
        viewModelScope.launch {
            alerts.collect { list ->
                recomputeAlertMatches(list)
            }
        }
        viewModelScope.launch {
            _catalogVersion.collect {
                recomputeAlertMatches(alerts.value)
            }
        }
        refreshLiveDeals(forceRefresh = false)
    }

    fun refreshLiveDeals(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            _liveSyncState.update {
                it.copy(
                    isSyncing = true,
                    statusMessage = "Consultando precios reales en Volaris.com y VivaAerobus.com..."
                )
            }
            try {
                val origins = _selectedOrigins.value.ifEmpty { listOf("AGU", "MEX", "NLU") }.take(3)
                val calCfg = _calendarConfig.value

                val scrapedAll = coroutineScope {
                    val globalJob = async { liveScraper.fetchGlobalPromos(forceRefresh) }
                    val originJobs = origins.map { origin ->
                        async { liveScraper.fetchOriginDeals(origin, forceRefresh) }
                    }
                    val calOutJob = async {
                        liveScraper.fetchRouteHistogram(calCfg.originCode, calCfg.destinationCode, forceRefresh)
                    }
                    val calRetJob = async {
                        liveScraper.fetchRouteHistogram(calCfg.destinationCode, calCfg.originCode, forceRefresh)
                    }
                    (listOf(globalJob, calOutJob, calRetJob) + originJobs).awaitAll().flatten()
                }

                val totalLive = dealsRepository.applyLiveFares(scrapedAll)
                _catalogVersion.update { it + 1 }

                val timeLabel = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                _liveSyncState.value = if (totalLive > 0) {
                    LiveSyncState(
                        isSyncing = false,
                        liveFareCount = totalLive,
                        statusMessage = "En vivo: $totalLive tarifas reales de Volaris.com y VivaAerobus.com",
                        lastUpdatedTimeLabel = timeLabel
                    )
                } else {
                    LiveSyncState(
                        isSyncing = false,
                        liveFareCount = 0,
                        statusMessage = "Modo referencial offline (sin respuesta de red)",
                        lastUpdatedTimeLabel = timeLabel
                    )
                }
            } catch (_: Exception) {
                _liveSyncState.update {
                    it.copy(
                        isSyncing = false,
                        statusMessage = "Usando tarifas referenciales (sin conexión en vivo)"
                    )
                }
            }
        }
    }

    private fun fetchRouteLiveInBackground(originCode: String, destCode: String) {
        viewModelScope.launch {
            _liveSyncState.update {
                it.copy(
                    isSyncing = true,
                    statusMessage = "Cargando calendario real $originCode <-> $destCode..."
                )
            }
            try {
                val fares = coroutineScope {
                    val outJob = async { liveScraper.fetchRouteHistogram(originCode, destCode, false) }
                    val retJob = async { liveScraper.fetchRouteHistogram(destCode, originCode, false) }
                    (outJob.await() + retJob.await())
                }
                val totalLive = dealsRepository.applyLiveFares(fares)
                _catalogVersion.update { it + 1 }
                val timeLabel = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                _liveSyncState.value = LiveSyncState(
                    isSyncing = false,
                    liveFareCount = totalLive,
                    statusMessage = if (totalLive > 0) {
                        "En vivo: $totalLive tarifas reales (Volaris.com y VivaAerobus.com)"
                    } else {
                        "Modo referencial offline"
                    },
                    lastUpdatedTimeLabel = timeLabel
                )
            } catch (_: Exception) {
                _liveSyncState.update { it.copy(isSyncing = false) }
            }
        }
    }

    private fun defaultDestinationFor(origin: String): String {
        val reachable = dealsRepository.getReachableDestinations(origin)
        return when {
            origin == "AGU" && "PVR" in reachable -> "PVR"
            "CUN" in reachable -> "CUN"
            "PVR" in reachable -> "PVR"
            reachable.isNotEmpty() -> reachable.first()
            else -> "CUN"
        }
    }

    private fun computeHeadToHeadKeys(deals: List<FlightDeal>): Set<String> {
        val map = HashMap<String, MutableSet<Airline>>()
        for (deal in deals) {
            val key = "${deal.originCode}-${deal.destinationCode}-${deal.departureDate.isoString}"
            map.getOrPut(key) { HashSet() }.add(deal.airline)
        }
        return map.filterValues { it.size >= 2 }.keys
    }

    private fun buildCalendarState(cfg: CalendarConfig): CalendarUiState {
        val displayedMonth = today.firstOfMonth().plusMonths(cfg.monthOffset)
        val isSelectingReturn = cfg.tripMode == TripMode.ROUND_TRIP && cfg.roundTripStep == RoundTripStep.RETURN

        val activeOrigin = if (isSelectingReturn) cfg.destinationCode else cfg.originCode
        val activeDest = if (isSelectingReturn) cfg.originCode else cfg.destinationCode

        val routeDealsRaw = dealsRepository.getDealsForRoute(activeOrigin, activeDest)
        val filteredRouteDeals = routeDealsRaw.filter { deal ->
            (cfg.airlineFilter == null || deal.airline == cfg.airlineFilter) &&
                (!cfg.directOnly || deal.isNonStop) &&
                (cfg.timePeriodFilter == null || deal.timePeriod == cfg.timePeriodFilter)
        }

        val dealsByDate = filteredRouteDeals.groupBy { it.departureDate }
        val minAllowedDate = if (isSelectingReturn && cfg.selectedOutboundDate != null) {
            cfg.selectedOutboundDate
        } else {
            today
        }

        // Build 7-column Monday-first month grid (42 cells = 6 weeks or 35 cells = 5 weeks)
        val firstOfMonth = displayedMonth.firstOfMonth()
        val leadingEmptyDays = firstOfMonth.dayOfWeekMondayFirst - 1 // 0..6
        val daysInMonth = displayedMonth.daysInMonth()
        val totalSlots = if (leadingEmptyDays + daysInMonth <= 35) 35 else 42
        val gridStartDate = firstOfMonth.plusDays(-leadingEmptyDays)

        val cells = (0 until totalSlots).map { offset ->
            val cellDate = gridStartDate.plusDays(offset)
            val inMonth = cellDate.month == displayedMonth.month && cellDate.year == displayedMonth.year
            val inRange = cellDate >= minAllowedDate && cellDate <= maxCatalogDate
            val dayDeals = if (inRange) dealsByDate[cellDate].orEmpty() else emptyList()
            val lowestBase = dayDeals.minOfOrNull { it.baseFareMxn }
            val airlines = dayDeals.map { it.airline }.distinct()
            CalendarDayCell(
                date = cellDate,
                isInCurrentMonth = inMonth,
                isInCatalogRange = inRange,
                lowestBaseFareMxn = lowestBase,
                airlinesOperating = airlines,
                dealsCount = dayDeals.size,
                hasLiveFare = dayDeals.any { it.isLiveVerified }
            )
        }

        val activeSelectedDate = if (isSelectingReturn) cfg.selectedReturnDate else cfg.selectedOutboundDate
        val activeDayFlights = if (activeSelectedDate != null) {
            sortCalendarFlights(dealsByDate[activeSelectedDate].orEmpty(), cfg.sortOption)
        } else {
            emptyList()
        }

        val chronologicalPool = filteredRouteDeals.filter { it.departureDate >= minAllowedDate }
        val sortedListFlights = sortCalendarFlights(chronologicalPool, cfg.sortOption).take(120)

        val minMonthKey = today.firstOfMonth().yearMonthKey
        val maxMonthKey = maxCatalogDate.firstOfMonth().yearMonthKey
        val liveRouteCount = routeDealsRaw.count { it.isLiveVerified }

        return CalendarUiState(
            tripMode = cfg.tripMode,
            roundTripStep = cfg.roundTripStep,
            originCode = cfg.originCode,
            destinationCode = cfg.destinationCode,
            viewMode = cfg.viewMode,
            sortOption = cfg.sortOption,
            airlineFilter = cfg.airlineFilter,
            directOnly = cfg.directOnly,
            timePeriodFilter = cfg.timePeriodFilter,
            displayedMonth = displayedMonth,
            canGoPrevMonth = displayedMonth.yearMonthKey > minMonthKey,
            canGoNextMonth = displayedMonth.yearMonthKey < maxMonthKey,
            monthCells = cells,
            selectedOutboundDate = cfg.selectedOutboundDate,
            selectedReturnDate = cfg.selectedReturnDate,
            activeDayFlights = activeDayFlights,
            chronologicalFlights = sortedListFlights,
            selectedOutboundFlight = cfg.selectedOutboundFlight,
            selectedReturnFlight = cfg.selectedReturnFlight,
            availableRoutesDestinations = dealsRepository.getReachableDestinations(cfg.originCode),
            liveRouteFaresCount = liveRouteCount
        )
    }

    private fun sortCalendarFlights(
        deals: List<FlightDeal>,
        option: CalendarSortOption
    ): List<FlightDeal> {
        return when (option) {
            CalendarSortOption.CHEAPEST -> deals.sortedWith(
                compareBy<FlightDeal> { it.baseFareMxn }
                    .thenByDescending { it.isLiveVerified }
                    .thenBy { it.totalWithTuaMxn }
                    .thenBy { it.departureDate }
            )
            CalendarSortOption.SOONEST -> deals.sortedWith(
                compareBy<FlightDeal> { it.departureDate }
                    .thenBy { it.departureTime }
                    .thenBy { it.baseFareMxn }
            )
            CalendarSortOption.RECOMMENDED -> deals.sortedWith(
                compareBy<FlightDeal> { deal ->
                    val daysOut = TravelDate.daysBetween(today, deal.departureDate).coerceAtLeast(0)
                    val layoverPenalty = if (deal.isNonStop) 0 else 180
                    deal.baseFareMxn + (daysOut * 14) + layoverPenalty
                }.thenByDescending { it.isLiveVerified }
                    .thenBy { it.totalWithTuaMxn }
            )
        }
    }

    // Navigation & Modals
    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
        if (tab == MainTab.CALENDAR) {
            val cfg = _calendarConfig.value
            fetchRouteLiveInBackground(cfg.originCode, cfg.destinationCode)
        }
    }

    fun openAirportDialog(mode: AirportDialogMode) {
        _airportDialogMode.value = mode
    }

    fun closeAirportDialog() {
        _airportDialogMode.value = null
    }

    fun openCalculator(outbound: FlightDeal, returnFlight: FlightDeal? = null) {
        _calculatorFlights.value = outbound to returnFlight
    }

    fun closeCalculator() {
        _calculatorFlights.value = null
    }

    fun openPromoLocator(deal: FlightDeal) {
        _promoLocatorDeal.value = deal
    }

    fun closePromoLocator() {
        _promoLocatorDeal.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    // Multi-Airport Origin & Destination Actions
    fun toggleOriginSelection(code: String) {
        val upper = code.uppercase()
        val current = _selectedOrigins.value.toMutableList()
        val added = if (upper in current) {
            current.remove(upper)
            false
        } else {
            current.add(upper)
            true
        }
        _selectedOrigins.value = current
        originPreferences.saveSelectedOrigins(current)
        if (current.isNotEmpty() && _calendarConfig.value.originCode !in current) {
            setCalendarRoute(current.first(), _calendarConfig.value.destinationCode)
        }
        if (added) {
            viewModelScope.launch {
                val fares = liveScraper.fetchOriginDeals(upper, forceRefresh = false)
                if (fares.isNotEmpty()) {
                    dealsRepository.applyLiveFares(fares)
                    _catalogVersion.update { it + 1 }
                }
            }
        }
    }

    fun removeOriginChip(code: String) {
        val current = _selectedOrigins.value.filterNot { it.equals(code, ignoreCase = true) }
        _selectedOrigins.value = current
        originPreferences.saveSelectedOrigins(current)
    }

    fun toggleDestinationSelection(code: String) {
        val upper = code.uppercase()
        val current = _selectedDestinations.value.toMutableList()
        if (upper in current) {
            current.remove(upper)
        } else {
            current.add(upper)
        }
        _selectedDestinations.value = current
        originPreferences.saveSelectedDestinations(current)
    }

    fun removeDestinationChip(code: String) {
        val current = _selectedDestinations.value.filterNot { it.equals(code, ignoreCase = true) }
        _selectedDestinations.value = current
        originPreferences.saveSelectedDestinations(current)
    }

    fun clearAllDestinations() {
        _selectedDestinations.value = emptyList()
        originPreferences.saveSelectedDestinations(emptyList())
    }

    fun selectSingleOriginFromGps(code: String, city: String, distanceKm: Int) {
        val upper = code.uppercase()
        val updated = listOf(upper)
        _selectedOrigins.value = updated
        originPreferences.saveSelectedOrigins(updated)
        originPreferences.saveDetectedAirportCode(upper)
        val newDest = defaultDestinationFor(upper)
        setCalendarRoute(upper, newDest)
        _detectedGpsBanner.value = "Aeropuerto cercano detectado: $city ($upper) a $distanceKm km"
        _snackbarMessage.value = "Origen ajustado por GPS a $city ($upper)"
        refreshLiveDeals(forceRefresh = false)
    }

    fun attemptFirstLaunchGpsIfNeeded(context: Context) {
        if (originPreferences.hasAttemptedFirstLaunchGps()) return
        originPreferences.markFirstLaunchGpsAttempted()
        val detected = LocationDetector.detectNearestMexicanAirport(context)
        if (detected != null) {
            selectSingleOriginFromGps(
                code = detected.airport.code,
                city = detected.airport.city,
                distanceKm = detected.distanceKm
            )
        }
    }

    fun detectNearestAirportManually(context: Context): Boolean {
        originPreferences.markFirstLaunchGpsAttempted()
        val detected = LocationDetector.detectNearestMexicanAirport(context)
        return if (detected != null) {
            selectSingleOriginFromGps(
                code = detected.airport.code,
                city = detected.airport.city,
                distanceKm = detected.distanceKm
            )
            true
        } else {
            _snackbarMessage.value = "No se pudo obtener ubicación GPS reciente. Selecciona manualmente."
            false
        }
    }

    // Tab 1 Actions
    fun setDealQuickFilter(filter: DealQuickFilter) {
        _dealQuickFilter.value = filter
    }

    fun setDealMaxBaseFare(value: Float) {
        _dealMaxBaseFare.value = value
    }

    fun setDealSortOrder(order: SortOrder) {
        _dealSortOrder.value = order
    }

    // Tab 2 Actions
    fun setSearchMaxTotal(value: Float) {
        _searchMaxTotal.value = value
    }

    fun setSearchDirectOnly(directOnly: Boolean) {
        _searchDirectOnly.value = directOnly
    }

    fun setSearchAirlineFilter(airline: Airline?) {
        _searchAirline.value = airline
    }

    fun setSearchSortOrder(order: SortOrder) {
        _searchSortOrder.value = order
    }

    // Tab 3 Calendar Actions
    fun setTripMode(mode: TripMode) {
        _calendarConfig.update { cfg ->
            cfg.copy(
                tripMode = mode,
                roundTripStep = RoundTripStep.OUTBOUND,
                selectedReturnDate = if (mode == TripMode.ONE_WAY) null else cfg.selectedReturnDate,
                selectedReturnFlight = if (mode == TripMode.ONE_WAY) null else cfg.selectedReturnFlight
            )
        }
    }

    fun setRoundTripStep(step: RoundTripStep) {
        _calendarConfig.update { cfg ->
            if (step == RoundTripStep.RETURN && cfg.selectedOutboundDate == null) {
                _snackbarMessage.value = "Elige primero la fecha de Salida"
                cfg
            } else {
                cfg.copy(roundTripStep = step)
            }
        }
    }

    fun setCalendarRoute(originCode: String, destinationCode: String) {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        val safeDest = if (o == d) defaultDestinationFor(o) else d
        _calendarConfig.update { cfg ->
            cfg.copy(
                originCode = o,
                destinationCode = safeDest,
                roundTripStep = RoundTripStep.OUTBOUND,
                selectedOutboundDate = null,
                selectedReturnDate = null,
                selectedOutboundFlight = null,
                selectedReturnFlight = null
            )
        }
        fetchRouteLiveInBackground(o, safeDest)
    }

    fun swapCalendarRoute() {
        var newOrigin = "PVR"
        var newDest = "AGU"
        _calendarConfig.update { cfg ->
            newOrigin = cfg.destinationCode
            newDest = cfg.originCode
            cfg.copy(
                originCode = newOrigin,
                destinationCode = newDest,
                roundTripStep = RoundTripStep.OUTBOUND,
                selectedOutboundDate = null,
                selectedReturnDate = null,
                selectedOutboundFlight = null,
                selectedReturnFlight = null
            )
        }
        fetchRouteLiveInBackground(newOrigin, newDest)
    }

    fun setCalendarViewMode(mode: CalendarViewMode) {
        _calendarConfig.update { it.copy(viewMode = mode) }
    }

    fun setCalendarSortOption(option: CalendarSortOption) {
        _calendarConfig.update { it.copy(sortOption = option) }
    }

    fun setCalendarAirlineFilter(airline: Airline?) {
        _calendarConfig.update { it.copy(airlineFilter = airline) }
    }

    fun setCalendarDirectOnly(directOnly: Boolean) {
        _calendarConfig.update { it.copy(directOnly = directOnly) }
    }

    fun setCalendarTimeFilter(period: TimePeriod?) {
        _calendarConfig.update { it.copy(timePeriodFilter = period) }
    }

    fun navigateCalendarMonth(delta: Int) {
        _calendarConfig.update { cfg ->
            val next = (cfg.monthOffset + delta).coerceIn(0, 4)
            cfg.copy(monthOffset = next)
        }
    }

    fun onCalendarDateClicked(date: TravelDate) {
        _calendarConfig.update { cfg ->
            val filteredForStep = { o: String, d: String ->
                dealsRepository.getDealsForRoute(o, d).filter { deal ->
                    deal.departureDate == date &&
                        (cfg.airlineFilter == null || deal.airline == cfg.airlineFilter) &&
                        (!cfg.directOnly || deal.isNonStop) &&
                        (cfg.timePeriodFilter == null || deal.timePeriod == cfg.timePeriodFilter)
                }
            }

            if (cfg.tripMode == TripMode.ONE_WAY) {
                val sorted = sortCalendarFlights(filteredForStep(cfg.originCode, cfg.destinationCode), cfg.sortOption)
                cfg.copy(
                    selectedOutboundDate = date,
                    selectedOutboundFlight = sorted.firstOrNull()
                )
            } else {
                if (cfg.roundTripStep == RoundTripStep.OUTBOUND ||
                    (cfg.selectedOutboundDate != null && date < cfg.selectedOutboundDate)
                ) {
                    val sortedOut = sortCalendarFlights(filteredForStep(cfg.originCode, cfg.destinationCode), cfg.sortOption)
                    val keepReturn = cfg.selectedReturnDate != null && cfg.selectedReturnDate >= date
                    cfg.copy(
                        selectedOutboundDate = date,
                        selectedOutboundFlight = sortedOut.firstOrNull(),
                        selectedReturnDate = if (keepReturn) cfg.selectedReturnDate else null,
                        selectedReturnFlight = if (keepReturn) cfg.selectedReturnFlight else null,
                        roundTripStep = RoundTripStep.RETURN
                    )
                } else {
                    val sortedRet = sortCalendarFlights(filteredForStep(cfg.destinationCode, cfg.originCode), cfg.sortOption)
                    cfg.copy(
                        selectedReturnDate = date,
                        selectedReturnFlight = sortedRet.firstOrNull()
                    )
                }
            }
        }
    }

    fun selectCalendarFlight(deal: FlightDeal) {
        _calendarConfig.update { cfg ->
            val isReturnStep = cfg.tripMode == TripMode.ROUND_TRIP &&
                (cfg.roundTripStep == RoundTripStep.RETURN || deal.originCode == cfg.destinationCode)
            if (isReturnStep) {
                cfg.copy(
                    selectedReturnDate = deal.departureDate,
                    selectedReturnFlight = deal
                )
            } else {
                cfg.copy(
                    selectedOutboundDate = deal.departureDate,
                    selectedOutboundFlight = deal,
                    roundTripStep = if (cfg.tripMode == TripMode.ROUND_TRIP) RoundTripStep.RETURN else cfg.roundTripStep
                )
            }
        }
    }

    fun clearTripPlan() {
        _calendarConfig.update { cfg ->
            cfg.copy(
                roundTripStep = RoundTripStep.OUTBOUND,
                selectedOutboundDate = null,
                selectedReturnDate = null,
                selectedOutboundFlight = null,
                selectedReturnFlight = null
            )
        }
    }

    // Tab 4: Room Alerts
    fun createAlert(
        originCode: String,
        destinationCode: String?,
        maxPriceMxn: Int,
        nonStopOnly: Boolean
    ) {
        viewModelScope.launch {
            alertRepository.insert(
                FlightAlert(
                    originCode = originCode.uppercase(),
                    destinationCode = destinationCode?.takeIf { it.isNotBlank() }?.uppercase(),
                    maxPriceMxn = maxPriceMxn,
                    nonStopOnly = nonStopOnly,
                    isActive = true
                )
            )
            _snackbarMessage.value = "Alerta guardada para ${originCode.uppercase()} -> ${destinationCode ?: "Cualquier destino"}"
        }
    }

    fun toggleAlert(id: Int, active: Boolean) {
        viewModelScope.launch {
            alertRepository.setActive(id, active)
        }
    }

    fun deleteAlert(id: Int) {
        viewModelScope.launch {
            alertRepository.deleteById(id)
            _snackbarMessage.value = "Alerta eliminada"
        }
    }

    private fun findMatchingDealsForAlert(alert: FlightAlert): List<FlightDeal> {
        return dealsRepository.catalog.filter { deal ->
            deal.originCode == alert.originCode &&
                (alert.destinationCode == null || deal.destinationCode == alert.destinationCode) &&
                deal.baseFareMxn <= alert.maxPriceMxn &&
                (!alert.nonStopOnly || deal.isNonStop)
        }.sortedBy { it.baseFareMxn }
    }

    private fun recomputeAlertMatches(list: List<FlightAlert>) {
        val counts = list.associate { alert ->
            alert.id to findMatchingDealsForAlert(alert).size
        }
        _alertMatchCounts.value = counts
    }

    fun scanAlertsAndNotify(context: Context) {
        val active = alerts.value.filter { it.isActive }
        recomputeAlertMatches(alerts.value)

        if (active.isEmpty()) {
            _lastScanSummary.value = "No tienes alertas activas. Crea una arriba para comenzar a cazar."
            return
        }

        val allMatches = active.flatMap { findMatchingDealsForAlert(it) }
            .distinctBy { it.id }
            .sortedBy { it.baseFareMxn }

        if (allMatches.isEmpty()) {
            _lastScanSummary.value = "Escaneo completo: sin vuelos por debajo de tus presupuestos configurados."
            return
        }

        val best = allMatches.first()
        val summary = "${allMatches.size} vuelos encontrados. Mejor oferta: ${best.airline.displayName} ${best.flightNumber} ${best.originCode}-${best.destinationCode} desde $${best.baseFareMxn} MXN (+ $${best.originTuaMxn} TUA)."
        _lastScanSummary.value = summary

        NotificationHelper.sendDealNotification(
            context = context,
            title = "Oferta detectada: ${best.originCode} a ${best.destinationCode} desde $${best.baseFareMxn} MXN",
            message = "${best.airline.displayName} (${best.flightNumber}) el ${best.departureDate.shortLabelEs} · Total con TUA $${best.totalWithTuaMxn} MXN",
            notificationId = 2001
        )
    }

    fun sendTestPushNotification(context: Context): Boolean {
        val samplePool = dealsRadarState.value.deals.ifEmpty { dealsRepository.catalog }
        val best = samplePool.minByOrNull { it.baseFareMxn }
        val title = if (best != null) {
            "Alerta Cheap Travel: ${best.originCode} - ${best.destinationCode} $${best.baseFareMxn} MXN"
        } else {
            "Alerta Cheap Travel: Vuelos desde $199 MXN"
        }
        val body = if (best != null) {
            "${best.airline.displayName} ${best.flightNumber} el ${best.departureDate.shortLabelEs}. TUA $${best.originTuaMxn} MXN."
        } else {
            "Notificación de prueba de alta prioridad activa."
        }
        val sent = NotificationHelper.sendDealNotification(context, title, body, notificationId = 1099)
        _snackbarMessage.value = if (sent) {
            "Notificación push local enviada"
        } else {
            "Activa el permiso de notificaciones para recibir alertas"
        }
        return sent
    }
}
