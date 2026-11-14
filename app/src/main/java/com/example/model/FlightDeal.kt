package com.example.model

enum class TimePeriod(val label: String) {
    MANANA("Mañana 05-12h"),
    TARDE("Tarde 12-18h"),
    NOCHE("Noche 18-24h")
}

data class FlightDeal(
    val id: String,
    val airline: Airline,
    val flightNumber: String,
    val originCode: String,
    val destinationCode: String,
    val departureDate: TravelDate,
    val departureTime: String,
    val arrivalTime: String,
    val departureHour: Int,
    val durationMinutes: Int,
    val aircraftModel: String,
    val isNonStop: Boolean,
    val stopsCount: Int,
    val layoverAirportCode: String? = null,
    val layoverDurationMinutes: Int? = null,
    val baseFareMxn: Int,
    val originTuaMxn: Int,
    val isInternational: Boolean,
    val promoSourceDescription: String,
    val seatsRemaining: Int = 4,
    val isLiveVerified: Boolean = false,
    val liveSourceLabel: String? = null
) {
    val totalWithTuaMxn: Int
        get() = baseFareMxn + originTuaMxn

    val formattedDuration: String
        get() {
            val h = durationMinutes / 60
            val m = durationMinutes % 60
            return if (h > 0) "${h}h ${m}m" else "${m} min"
        }

    val formattedLayoverDuration: String?
        get() = layoverDurationMinutes?.let { mins ->
            val h = mins / 60
            val m = mins % 60
            if (h > 0) "${h}h ${m}m" else "${m} min"
        }

    val stopsLabel: String
        get() = if (isNonStop || stopsCount == 0) {
            "Directo"
        } else {
            val layoverAirport = layoverAirportCode ?: "Conexión"
            val layoverTime = formattedLayoverDuration ?: ""
            if (layoverTime.isNotEmpty()) {
                "$stopsCount escala en $layoverAirport · $layoverTime"
            } else {
                "$stopsCount escala en $layoverAirport"
            }
        }

    val timePeriod: TimePeriod
        get() = when {
            departureHour < 12 -> TimePeriod.MANANA
            departureHour < 18 -> TimePeriod.TARDE
            else -> TimePeriod.NOCHE
        }
}
