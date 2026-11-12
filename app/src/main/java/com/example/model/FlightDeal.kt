package com.example.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class FlightDeal(
    val id: String,
    val airline: Airline,
    val flightNumber: String,
    val origin: Airport,
    val destination: Airport,
    val departureDate: LocalDate,
    val departureTime: String,
    val arrivalTime: String,
    val durationMinutes: Int,
    val isNonStop: Boolean = true,
    val stopsCount: Int = 0,
    val layoverAirportCode: String? = null,
    val layoverDurationMinutes: Int? = null,
    val aircraftModel: String = "Airbus A320neo",
    val baseFareMxn: Double,
    val promoLabel: String? = null,
    val seatsAvailableAtPromo: Int = 4,
    val returnDate: LocalDate? = null,
    val promoSourceDescription: String = "Tarifa Promocional Detectada"
) {
    val durationFormatted: String
        get() {
            val hours = durationMinutes / 60
            val minutes = durationMinutes % 60
            return "${hours}h ${minutes}m"
        }

    val stopsSummary: String
        get() = when {
            isNonStop || stopsCount == 0 -> "Directo (Sin escalas)"
            layoverAirportCode != null -> "1 escala en $layoverAirportCode (${layoverDurationMinutes ?: 60} min)"
            else -> "$stopsCount escala"
        }

    val tuaMxn: Double
        get() = if (destination.isInternational) origin.internationalTuaMxn else origin.nationalTuaMxn

    // Default quick preview for 1 adult, Zero baggage, Random seat
    val previewTotalMxn: Double
        get() = baseFareMxn + tuaMxn

    val departureDateFormatted: String
        get() = departureDate.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale("es", "MX")))

    val dayAndMonthFormatted: String
        get() = departureDate.format(DateTimeFormatter.ofPattern("d MMM", Locale("es", "MX")))

    fun calculateBreakdown(
        passengerCount: PassengerCount,
        baggageOption: BaggageOption,
        seatOption: SeatOption
    ): FareBreakdown {
        val baggageFee = when (baggageOption) {
            BaggageOption.ZERO -> 0.0
            BaggageOption.BASIC -> airline.defaultCarryOnFeeMxn
            BaggageOption.PLUS -> airline.defaultCarryOnFeeMxn + airline.defaultCheckedBagFeeMxn
        }

        val seatFee = when (seatOption) {
            SeatOption.RANDOM -> 0.0
            SeatOption.STANDARD -> airline.defaultStandardSeatFeeMxn
            SeatOption.EXTRA_LEGROOM -> airline.defaultExtraLegroomFeeMxn
        }

        return FareBreakdown(
            baseFarePerAdult = baseFareMxn,
            baseFarePerChild = baseFareMxn * 0.9, // 10% discount on base fare for children
            tuaPerPassenger = tuaMxn,
            baggageFeePerPassenger = baggageFee,
            seatFeePerPassenger = seatFee,
            passengerCount = passengerCount
        )
    }

    fun calculateRoundTripBreakdown(
        returnDeal: FlightDeal?,
        passengerCount: PassengerCount,
        baggageOption: BaggageOption,
        seatOption: SeatOption
    ): FareBreakdown {
        if (returnDeal == null) {
            return calculateBreakdown(passengerCount, baggageOption, seatOption)
        }

        val baggageFee = when (baggageOption) {
            BaggageOption.ZERO -> 0.0
            BaggageOption.BASIC -> airline.defaultCarryOnFeeMxn
            BaggageOption.PLUS -> airline.defaultCarryOnFeeMxn + airline.defaultCheckedBagFeeMxn
        }

        val seatFee = when (seatOption) {
            SeatOption.RANDOM -> 0.0
            SeatOption.STANDARD -> airline.defaultStandardSeatFeeMxn
            SeatOption.EXTRA_LEGROOM -> airline.defaultExtraLegroomFeeMxn
        }

        return FareBreakdown(
            baseFarePerAdult = baseFareMxn,
            baseFarePerChild = baseFareMxn * 0.9,
            tuaPerPassenger = tuaMxn,
            baggageFeePerPassenger = baggageFee,
            seatFeePerPassenger = seatFee,
            passengerCount = passengerCount,
            returnBaseFarePerAdult = returnDeal.baseFareMxn,
            returnBaseFarePerChild = returnDeal.baseFareMxn * 0.9,
            returnTuaPerPassenger = returnDeal.tuaMxn,
            isRoundTrip = true
        )
    }
}
