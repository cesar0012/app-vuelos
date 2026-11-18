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
    val isInternational: Boolean = false,
    val promoSourceDescription: String,
    val seatsRemaining: Int = 4,
    val isLiveVerified: Boolean = false,
    val liveSourceLabel: String? = null,
    val liveBookingUrl: String? = null
) {
    val totalWithTuaMxn: Int
        get() = baseFareMxn + originTuaMxn

    fun baggageAddonMxn(tier: PricingBaggageTier): Int =
        FareCalculator.baggageFeeForAirline(airline, tier.baggagePackage)

    fun effectivePriceMxn(options: PriceDisplayOptions): Int {
        val baggageAddon = baggageAddonMxn(options.baggageTier)
        val tua = if (options.includeTua) originTuaMxn else 0
        return baseFareMxn + baggageAddon + tua
    }

    fun fareBreakdownSummary(options: PriceDisplayOptions): String {
        val bagAddon = baggageAddonMxn(options.baggageTier)
        val parts = mutableListOf<String>()
        val baseLabel = if (airline.includesCarryOnInBase) "Base (inc. 10kg)" else "Zero"
        parts.add("$baseLabel $$baseFareMxn")
        if (bagAddon > 0) {
            val bagLabel = when (options.baggageTier) {
                PricingBaggageTier.BASICA_10KG -> if (airline == Airline.VIVA_AEROBUS) "Light 10kg" else "Carry-on 10kg"
                PricingBaggageTier.PLUS_25KG -> "Equipaje 25kg"
                PricingBaggageTier.ZERO -> ""
            }
            if (bagLabel.isNotEmpty()) parts.add("+$bagLabel $$bagAddon")
        }
        if (options.includeTua) {
            parts.add("+TUA $$originTuaMxn")
        } else {
            parts.add("(+$originTuaMxn TUA aparte)")
        }
        return parts.joinToString(" ")
    }

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
