package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AirportData
import com.example.data.CheapTravelDatabase
import com.example.data.FlightDealsRepository
import com.example.model.Airline
import com.example.model.Airport
import com.example.model.BaggageOption
import com.example.model.FareBreakdown
import com.example.model.FlightAlert
import com.example.model.FlightDeal
import com.example.model.PassengerCount
import com.example.model.SeatOption
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

enum class AppTab(val title: String) {
    RADAR("Radar Flash"),
    SEARCH("Buscador"),
    CALENDAR("Calendario"),
    ALERTS("Alertas Push")
}

enum class CalendarTripType {
    ONE_WAY,
    ROUND_TRIP
}

enum class CalendarSelectionStep {
    OUTBOUND,
    RETURN
}

enum class CalendarViewMode {
    MONTH_GRID,
    LIST
}

enum class CalendarSortOption(val label: String) {
    CHEAPEST("Más Barato"),
    SOONEST("Más Próximo"),
    BEST_VALUE("Recomendado (Balance)")
}

enum class TimeOfDayFilter(val label: String) {
    ANY("Cualquier hora"),
    MORNING("Mañana (05h - 12h)"),
    AFTERNOON("Tarde (12h - 18h)"),
    NIGHT("Noche (18h - 24h)")
}

data class CheapTravelUiState(
    val currentTab: AppTab = AppTab.RADAR,
    val selectedOrigins: List<Airport> = AirportData.defaultOrigins, // Defaults to MEX + NLU
    val selectedDestinations: List<Airport> = emptyList(), // Empty means all / any destination
    val onlyBeaches: Boolean = false,
    val onlyInternational: Boolean = false,
    val selectedAirlineFilter: Airline? = null,
    val maxBaseFareFilter: Double = 1500.0,
    val sortByPrice: Boolean = true,

    // Deals list
    val displayedDeals: List<FlightDeal> = emptyList(),
    val flash199Deals: List<FlightDeal> = emptyList(),

    // Calendar screen
    val calendarOrigin: Airport = AirportData.findByCode("MEX"),
    val calendarDestination: Airport = AirportData.findByCode("CUN"),
    val calendarTripType: CalendarTripType = CalendarTripType.ROUND_TRIP,
    val calendarSelectionStep: CalendarSelectionStep = CalendarSelectionStep.OUTBOUND,
    val selectedOutboundDeal: FlightDeal? = null,
    val selectedReturnDeal: FlightDeal? = null,
    val calendarMatrix: Map<LocalDate, FlightDeal> = emptyMap(),
    val calendarReturnMatrix: Map<LocalDate, FlightDeal> = emptyMap(),
    val calendarViewMode: CalendarViewMode = CalendarViewMode.MONTH_GRID,
    val calendarSortOption: CalendarSortOption = CalendarSortOption.CHEAPEST,
    val calendarDisplayedMonth: YearMonth = YearMonth.of(2026, 11),
    val calendarAirlineFilter: Airline? = null,
    val calendarDirectOnly: Boolean = false,
    val calendarTimeFilter: TimeOfDayFilter = TimeOfDayFilter.ANY,
    val calendarSelectedDayDate: LocalDate? = null,

    // Selected flight for detail & fare breakdown modal
    val selectedFlight: FlightDeal? = null,
    val selectedReturnFlight: FlightDeal? = null,
    val passengers: PassengerCount = PassengerCount(adults = 1, children = 0, infants = 0),
    val baggageOption: BaggageOption = BaggageOption.ZERO,
    val seatOption: SeatOption = SeatOption.RANDOM,
    val currentBreakdown: FareBreakdown? = null,

    // Dialog flags
    val showOriginPicker: Boolean = false,
    val showDestinationPicker: Boolean = false,
    val showCreateAlertDialog: Boolean = false,
    val newAlertPreselectedDeal: FlightDeal? = null,
    val showPromoLocatorDialog: Boolean = false,
    val promoLocatorDeal: FlightDeal? = null,

    val isScanning: Boolean = false
) {
    val selectedDestination: Airport? get() = selectedDestinations.firstOrNull()
    val isRoundTrip: Boolean get() = selectedReturnFlight != null || (calendarTripType == CalendarTripType.ROUND_TRIP && selectedReturnDeal != null)
}

class CheapTravelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FlightDealsRepository()
    private val database = CheapTravelDatabase.getDatabase(application)
    private val alertDao = database.flightAlertDao()
    private val notificationHelper = NotificationHelper(application)
    private val prefs = application.getSharedPreferences("cheap_travel_prefs", Context.MODE_PRIVATE)

    val alerts: StateFlow<List<FlightAlert>> = alertDao.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(CheapTravelUiState())
    val uiState: StateFlow<CheapTravelUiState> = _uiState.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        loadSavedOrigin()
        refreshDeals()
        refreshCalendar()
        seedInitialSampleAlerts()
    }

    private fun seedInitialSampleAlerts() {
        viewModelScope.launch {
            val existing = alertDao.getActiveAlerts()
            if (existing.isEmpty()) {
                alertDao.insertAlert(
                    FlightAlert(
                        originCode = "MEX",
                        originCity = "Ciudad de México (AICM)",
                        destinationCode = "CUN",
                        destinationCity = "Cancún",
                        targetMaxPriceMxn = 750.0,
                        targetMonth = "Próximos 3 meses",
                        airlinePreference = "Todas las mexicanas",
                        bestPriceDetectedMxn = 199.0,
                        lastDealAirline = "Volaris"
                    )
                )
                alertDao.insertAlert(
                    FlightAlert(
                        originCode = "NLU",
                        originCity = "AIFA",
                        destinationCode = "PVR",
                        destinationCity = "Puerto Vallarta",
                        targetMaxPriceMxn = 500.0,
                        targetMonth = "Próximos 2 meses",
                        airlinePreference = "Volaris",
                        bestPriceDetectedMxn = 199.0,
                        lastDealAirline = "Volaris"
                    )
                )
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setOrigins(origins: List<Airport>) {
        _uiState.update { it.copy(selectedOrigins = origins) }
        refreshDeals()
    }

    fun toggleOrigin(airport: Airport) {
        val current = _uiState.value.selectedOrigins.toMutableList()
        if (current.any { it.code == airport.code }) {
            if (current.size > 1) { // Keep at least one
                current.removeAll { it.code == airport.code }
            }
        } else {
            current.add(airport)
        }
        _uiState.update { it.copy(selectedOrigins = current) }
        refreshDeals()
    }

    fun setDestination(airport: Airport?) {
        _uiState.update { it.copy(selectedDestinations = airport?.let { listOf(it) } ?: emptyList()) }
        refreshDeals()
    }

    fun setDestinations(destinations: List<Airport>) {
        _uiState.update { it.copy(selectedDestinations = destinations) }
        refreshDeals()
    }

    fun toggleDestination(airport: Airport) {
        val current = _uiState.value.selectedDestinations.toMutableList()
        if (current.any { it.code == airport.code }) {
            current.removeAll { it.code == airport.code }
        } else {
            current.add(airport)
        }
        _uiState.update { it.copy(selectedDestinations = current) }
        refreshDeals()
    }

    fun clearDestinations() {
        _uiState.update { it.copy(selectedDestinations = emptyList()) }
        refreshDeals()
    }

    fun setBeachFilter(enabled: Boolean) {
        _uiState.update { it.copy(onlyBeaches = enabled, onlyInternational = if (enabled) false else it.onlyInternational) }
        refreshDeals()
    }

    fun setInternationalFilter(enabled: Boolean) {
        _uiState.update { it.copy(onlyInternational = enabled, onlyBeaches = if (enabled) false else it.onlyBeaches) }
        refreshDeals()
    }

    fun setAirlineFilter(airline: Airline?) {
        _uiState.update { it.copy(selectedAirlineFilter = airline) }
        refreshDeals()
    }

    fun setMaxBaseFare(maxFare: Double) {
        _uiState.update { it.copy(maxBaseFareFilter = maxFare) }
        refreshDeals()
    }

    fun refreshDeals() {
        val state = _uiState.value
        val deals = repository.searchDeals(
            origins = state.selectedOrigins,
            destinations = state.selectedDestinations,
            onlyBeaches = state.onlyBeaches,
            onlyInternational = state.onlyInternational,
            airline = state.selectedAirlineFilter,
            maxBaseFare = state.maxBaseFareFilter,
            sortByLowestPrice = state.sortByPrice
        )

        // Specifically highlight $199 MXN and ultra promo deals
        val flashDeals = repository.getAllDeals().filter { it.baseFareMxn <= 249.0 }.take(10)

        _uiState.update {
            it.copy(
                displayedDeals = deals,
                flash199Deals = flashDeals
            )
        }
    }

    fun setCalendarRoute(origin: Airport, destination: Airport) {
        _uiState.update {
            it.copy(
                calendarOrigin = origin,
                calendarDestination = destination,
                selectedOutboundDeal = null,
                selectedReturnDeal = null,
                calendarSelectionStep = CalendarSelectionStep.OUTBOUND
            )
        }
        refreshCalendar()
    }

    fun setCalendarTripType(type: CalendarTripType) {
        _uiState.update { it.copy(calendarTripType = type) }
        refreshCalendar()
    }

    fun setCalendarSelectionStep(step: CalendarSelectionStep) {
        _uiState.update { it.copy(calendarSelectionStep = step) }
    }

    fun selectCalendarOutbound(deal: FlightDeal) {
        _uiState.update {
            it.copy(
                selectedOutboundDeal = deal,
                calendarSelectionStep = if (it.calendarTripType == CalendarTripType.ROUND_TRIP) {
                    CalendarSelectionStep.RETURN
                } else {
                    CalendarSelectionStep.OUTBOUND
                }
            )
        }
        refreshCalendar()
    }

    fun selectCalendarReturn(deal: FlightDeal) {
        _uiState.update { it.copy(selectedReturnDeal = deal) }
    }

    fun clearSelectedTrip() {
        _uiState.update {
            it.copy(
                selectedOutboundDeal = null,
                selectedReturnDeal = null,
                calendarSelectionStep = CalendarSelectionStep.OUTBOUND
            )
        }
    }

    fun startRoundTripFromDeal(deal: FlightDeal) {
        _uiState.update {
            it.copy(
                currentTab = AppTab.CALENDAR,
                calendarOrigin = deal.origin,
                calendarDestination = deal.destination,
                calendarTripType = CalendarTripType.ROUND_TRIP,
                selectedOutboundDeal = deal,
                selectedReturnDeal = null,
                calendarSelectionStep = CalendarSelectionStep.RETURN
            )
        }
        refreshCalendar()
    }

    fun refreshCalendar() {
        val state = _uiState.value
        val matrix = repository.getCalendarMatrix(
            originCode = state.calendarOrigin.code,
            destinationCode = state.calendarDestination.code
        )
        // Return matrix (Destination back to Origin)
        val returnMatrix = repository.getCalendarMatrix(
            originCode = state.calendarDestination.code,
            destinationCode = state.calendarOrigin.code
        )
        _uiState.update {
            it.copy(
                calendarMatrix = matrix,
                calendarReturnMatrix = returnMatrix
            )
        }
    }

    // Detail & Fare Breakdown Modal
    fun openFlightDetail(deal: FlightDeal, returnDeal: FlightDeal? = null) {
        val passengers = _uiState.value.passengers
        val baggage = _uiState.value.baggageOption
        val seat = _uiState.value.seatOption
        val breakdown = deal.calculateRoundTripBreakdown(returnDeal, passengers, baggage, seat)

        _uiState.update {
            it.copy(
                selectedFlight = deal,
                selectedReturnFlight = returnDeal,
                currentBreakdown = breakdown
            )
        }
    }

    fun closeFlightDetail() {
        _uiState.update {
            it.copy(
                selectedFlight = null,
                selectedReturnFlight = null,
                currentBreakdown = null
            )
        }
    }

    fun updatePassengers(adults: Int, children: Int, infants: Int) {
        val safeAdults = adults.coerceIn(1, 9)
        val safeChildren = children.coerceIn(0, 8)
        val safeInfants = infants.coerceIn(0, safeAdults) // 1 infant per adult max
        val newCount = PassengerCount(safeAdults, safeChildren, safeInfants)

        _uiState.update { state ->
            val updatedBreakdown = state.selectedFlight?.calculateRoundTripBreakdown(
                state.selectedReturnFlight,
                newCount,
                state.baggageOption,
                state.seatOption
            )
            state.copy(passengers = newCount, currentBreakdown = updatedBreakdown)
        }
    }

    fun updateBaggageOption(option: BaggageOption) {
        _uiState.update { state ->
            val updatedBreakdown = state.selectedFlight?.calculateRoundTripBreakdown(
                state.selectedReturnFlight,
                state.passengers,
                option,
                state.seatOption
            )
            state.copy(baggageOption = option, currentBreakdown = updatedBreakdown)
        }
    }

    fun updateSeatOption(option: SeatOption) {
        _uiState.update { state ->
            val updatedBreakdown = state.selectedFlight?.calculateRoundTripBreakdown(
                state.selectedReturnFlight,
                state.passengers,
                state.baggageOption,
                option
            )
            state.copy(seatOption = option, currentBreakdown = updatedBreakdown)
        }
    }

    // Alert Dialogs & Management
    fun openCreateAlert(deal: FlightDeal? = null) {
        _uiState.update {
            it.copy(
                showCreateAlertDialog = true,
                newAlertPreselectedDeal = deal
            )
        }
    }

    fun closeCreateAlert() {
        _uiState.update {
            it.copy(
                showCreateAlertDialog = false,
                newAlertPreselectedDeal = null
            )
        }
    }

    fun saveAlert(
        origin: Airport,
        destination: Airport,
        maxPrice: Double,
        airlinePref: String = "Todas las mexicanas",
        targetMonth: String = "Próximos 3 meses"
    ) {
        viewModelScope.launch {
            val alert = FlightAlert(
                originCode = origin.code,
                originCity = origin.city,
                destinationCode = destination.code,
                destinationCity = destination.city,
                targetMaxPriceMxn = maxPrice,
                targetMonth = targetMonth,
                airlinePreference = airlinePref
            )
            alertDao.insertAlert(alert)
            closeCreateAlert()
            _userMessage.emit("¡Alerta creada! Te notificaremos cuando baje de ${FareBreakdown.formatCurrency(maxPrice)}")
        }
    }

    fun toggleAlertEnabled(alert: FlightAlert) {
        viewModelScope.launch {
            alertDao.setAlertEnabled(alert.id, !alert.isEnabled)
        }
    }

    fun deleteAlert(alert: FlightAlert) {
        viewModelScope.launch {
            alertDao.deleteAlert(alert)
            _userMessage.emit("Alerta eliminada")
        }
    }

    fun scanAndTriggerAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            val activeAlerts = alertDao.getActiveAlerts()
            var dealsFound = 0

            for (alert in activeAlerts) {
                val matches = repository.findMatchingDealsForAlert(alert)
                if (matches.isNotEmpty()) {
                    val bestDeal = matches.first()
                    alertDao.updateBestPrice(alert.id, bestDeal.baseFareMxn, bestDeal.airline.displayName)
                    notificationHelper.sendDealNotification(bestDeal, alert.targetMaxPriceMxn)
                    dealsFound++
                }
            }

            _uiState.update { it.copy(isScanning = false) }
            if (dealsFound > 0) {
                _userMessage.emit("¡Se detectaron $dealsFound ofertas y se enviaron notificaciones push!")
            } else {
                _userMessage.emit("Escaneo completado. Precios actuales se mantienen estables.")
            }
        }
    }

    fun sendTestPushNotification() {
        notificationHelper.sendTestPushNotification()
        viewModelScope.launch {
            _userMessage.emit("Alerta push de prueba enviada a la barra de estado")
        }
    }

    fun setShowOriginPicker(show: Boolean) {
        _uiState.update { it.copy(showOriginPicker = show) }
    }

    fun setShowDestinationPicker(show: Boolean) {
        _uiState.update { it.copy(showDestinationPicker = show) }
    }

    fun setSortByPrice(sortByPrice: Boolean) {
        _uiState.update { it.copy(sortByPrice = sortByPrice) }
        refreshDeals()
    }

    fun removeOrigin(airport: Airport) {
        val current = _uiState.value.selectedOrigins.toMutableList()
        if (current.size > 1) {
            current.removeAll { it.code == airport.code }
            _uiState.update { it.copy(selectedOrigins = current) }
            refreshDeals()
        } else {
            viewModelScope.launch {
                _userMessage.emit("Debes mantener al menos un aeropuerto de salida")
            }
        }
    }

    fun removeDestination(airport: Airport) {
        val current = _uiState.value.selectedDestinations.toMutableList()
        current.removeAll { it.code == airport.code }
        _uiState.update { it.copy(selectedDestinations = current) }
        refreshDeals()
    }

    fun detectNearestAirport(lat: Double, lon: Double) {
        val nearest = AirportData.findNearestAirport(lat, lon)
        val distance = nearest.distanceKmTo(lat, lon).toInt()
        saveUserPreferredOrigin(nearest.code)
        _uiState.update {
            it.copy(
                selectedOrigins = listOf(nearest),
                calendarOrigin = nearest
            )
        }
        refreshDeals()
        refreshCalendar()
        viewModelScope.launch {
            _userMessage.emit("Aeropuerto más cercano: ${nearest.city} (${nearest.code}) a ~$distance km")
        }
    }

    fun setCalendarViewMode(mode: CalendarViewMode) {
        _uiState.update { it.copy(calendarViewMode = mode) }
    }

    fun setCalendarSortOption(option: CalendarSortOption) {
        _uiState.update { it.copy(calendarSortOption = option) }
    }

    fun setCalendarDisplayedMonth(yearMonth: YearMonth) {
        _uiState.update { it.copy(calendarDisplayedMonth = yearMonth) }
    }

    fun previousCalendarMonth() {
        _uiState.update { it.copy(calendarDisplayedMonth = it.calendarDisplayedMonth.minusMonths(1)) }
    }

    fun nextCalendarMonth() {
        _uiState.update { it.copy(calendarDisplayedMonth = it.calendarDisplayedMonth.plusMonths(1)) }
    }

    fun setCalendarAirlineFilter(airline: Airline?) {
        _uiState.update { it.copy(calendarAirlineFilter = airline) }
    }

    fun setCalendarDirectOnly(directOnly: Boolean) {
        _uiState.update { it.copy(calendarDirectOnly = directOnly) }
    }

    fun setCalendarTimeFilter(filter: TimeOfDayFilter) {
        _uiState.update { it.copy(calendarTimeFilter = filter) }
    }

    fun selectCalendarDay(date: LocalDate?) {
        _uiState.update { it.copy(calendarSelectedDayDate = date) }
    }

    fun getFlightsForDate(date: LocalDate, isReturn: Boolean): List<FlightDeal> {
        val originCode = if (isReturn) _uiState.value.calendarDestination.code else _uiState.value.calendarOrigin.code
        val destCode = if (isReturn) _uiState.value.calendarOrigin.code else _uiState.value.calendarDestination.code
        return repository.getFlightsForRouteAndDate(originCode, destCode, date)
    }

    fun openPromoLocator(deal: FlightDeal) {
        _uiState.update {
            it.copy(
                showPromoLocatorDialog = true,
                promoLocatorDeal = deal
            )
        }
    }

    fun closePromoLocator() {
        _uiState.update {
            it.copy(
                showPromoLocatorDialog = false,
                promoLocatorDeal = null
            )
        }
    }

    private fun loadSavedOrigin() {
        val savedCode = prefs.getString("preferred_origin_code", null)
        if (savedCode != null) {
            val airport = AirportData.findByCode(savedCode)
            if (airport != AirportData.allAirports.first()) {
                _uiState.update {
                    it.copy(
                        selectedOrigins = listOf(airport),
                        calendarOrigin = airport
                    )
                }
            }
        }
    }

    private fun saveUserPreferredOrigin(code: String) {
        prefs.edit().putString("preferred_origin_code", code).apply()
    }
}
