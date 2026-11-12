package com.example.model

import java.text.NumberFormat
import java.util.Locale

data class PassengerCount(
    val adults: Int = 1,
    val children: Int = 0,
    val infants: Int = 0 // Infants under 2 do not pay TUA on most domestic carriers
) {
    val totalPayingPassengers: Int get() = adults + children
    val totalTravelers: Int get() = adults + children + infants

    val summaryText: String
        get() {
            val parts = mutableListOf<String>()
            parts.add("$adults ${if (adults == 1) "Adulto" else "Adultos"}")
            if (children > 0) {
                parts.add("$children ${if (children == 1) "Niño" else "Niños"}")
            }
            if (infants > 0) {
                parts.add("$infants ${if (infants == 1) "Bebé" else "Bebés"}")
            }
            return parts.joinToString(", ")
        }
}

enum class BaggageOption(
    val title: String,
    val description: String,
    val iconName: String
) {
    ZERO(
        title = "Zero / Solo Objeto Personal",
        description = "Mochila o bolso que quepa debajo del asiento delantero (45x35x20 cm)",
        iconName = "backpack"
    ),
    BASIC(
        title = "Básica / Maleta de Mano 10kg",
        description = "Objeto personal + maleta de mano para el compartimento superior (10kg)",
        iconName = "carry_on"
    ),
    PLUS(
        title = "Plus / Documentada 25kg",
        description = "Objeto personal + maleta de mano 10kg + maleta documentada de hasta 25kg",
        iconName = "luggage"
    )
}

enum class SeatOption(
    val title: String,
    val description: String
) {
    RANDOM(
        title = "Asignación Aleatoria",
        description = "Gratis al hacer Web Check-in 24-48h antes del vuelo"
    ),
    STANDARD(
        title = "Asiento Estándar",
        description = "Elige ventanilla o pasillo en filas regulares"
    ),
    EXTRA_LEGROOM(
        title = "Espacio Extra / Primera Fila",
        description = "Mayor comodidad para las piernas y abordaje preferente"
    )
}

data class FareBreakdown(
    val baseFarePerAdult: Double,
    val baseFarePerChild: Double,
    val tuaPerPassenger: Double,
    val baggageFeePerPassenger: Double,
    val seatFeePerPassenger: Double,
    val passengerCount: PassengerCount,
    val returnBaseFarePerAdult: Double = 0.0,
    val returnBaseFarePerChild: Double = 0.0,
    val returnTuaPerPassenger: Double = 0.0,
    val isRoundTrip: Boolean = false
) {
    val totalAdultBaseFare: Double get() = (baseFarePerAdult + returnBaseFarePerAdult) * passengerCount.adults
    val totalChildBaseFare: Double get() = (baseFarePerChild + returnBaseFarePerChild) * passengerCount.children
    val totalBaseFare: Double get() = totalAdultBaseFare + totalChildBaseFare

    // TUA applies to adults and children (not lap infants)
    val totalTua: Double get() = (tuaPerPassenger + returnTuaPerPassenger) * passengerCount.totalPayingPassengers

    // For round trip, baggage and seats apply to both legs
    val legMultiplier: Int get() = if (isRoundTrip) 2 else 1
    val totalBaggage: Double get() = baggageFeePerPassenger * passengerCount.totalPayingPassengers * legMultiplier
    val totalSeats: Double get() = seatFeePerPassenger * passengerCount.totalPayingPassengers * legMultiplier

    val grandTotalMxn: Double get() = totalBaseFare + totalTua + totalBaggage + totalSeats

    val perPersonAverageMxn: Double
        get() = if (passengerCount.totalTravelers > 0) {
            grandTotalMxn / passengerCount.totalTravelers
        } else grandTotalMxn

    companion object {
        private val mxnFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }

        fun formatCurrency(amount: Double): String = mxnFormat.format(amount)
    }
}
