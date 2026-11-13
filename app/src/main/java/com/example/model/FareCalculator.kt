package com.example.model

enum class BaggagePackage(val label: String, val description: String) {
    ZERO("Zero", "Solo artículo personal bajo asiento"),
    BASICA_10KG("Básica 10kg", "Artículo personal + carry-on 10 kg"),
    PLUS_25KG("Plus 25kg", "Carry-on 10 kg + maleta documentada 25 kg")
}

enum class SeatOption(val label: String, val description: String) {
    ALEATORIO("Aleatorio", "Asignación automática en check-in ($0)"),
    ESTANDAR("Estándar", "Selección de asiento en fila regular"),
    ESPACIO_EXTRA("Espacio Extra", "Mayor espacio para piernas / primeras filas")
}

data class FareBreakdownResult(
    val adultBaseTotalMxn: Int,
    val childBaseTotalMxn: Int,
    val childDiscountSavingsMxn: Int,
    val baggageTotalMxn: Int,
    val seatTotalMxn: Int,
    val outboundTuaTotalMxn: Int,
    val returnTuaTotalMxn: Int,
    val grandTotalMxn: Int,
    val payingPassengersCount: Int,
    val infantCount: Int,
    val aifaSavingsPerPassengerMxn: Int,
    val tuaInsightNote: String
)

object FareCalculator {

    fun calculate(
        outbound: FlightDeal,
        returnFlight: FlightDeal? = null,
        adults: Int,
        children: Int,
        infants: Int,
        baggagePackage: BaggagePackage,
        seatOption: SeatOption
    ): FareBreakdownResult {
        val safeAdults = adults.coerceAtLeast(1)
        val safeChildren = children.coerceAtLeast(0)
        val safeInfants = infants.coerceAtLeast(0)
        val payingPax = safeAdults + safeChildren
        val segments = if (returnFlight != null) 2 else 1

        val baseSumPerAdult = outbound.baseFareMxn + (returnFlight?.baseFareMxn ?: 0)
        val childBasePerChild = ((outbound.baseFareMxn * 0.90).toInt()) +
            ((returnFlight?.baseFareMxn?.times(0.90))?.toInt() ?: 0)

        val adultBaseTotal = baseSumPerAdult * safeAdults
        val childBaseTotal = childBasePerChild * safeChildren
        val childDiscountSavings = (baseSumPerAdult * safeChildren) - childBaseTotal

        val outboundBagPerPax = baggageFeeForAirline(outbound.airline, baggagePackage)
        val returnBagPerPax = returnFlight?.let { baggageFeeForAirline(it.airline, baggagePackage) } ?: 0
        val baggageTotal = (outboundBagPerPax + returnBagPerPax) * payingPax

        val outboundSeatPerPax = seatFeeForAirline(outbound.airline, seatOption)
        val returnSeatPerPax = returnFlight?.let { seatFeeForAirline(it.airline, seatOption) } ?: 0
        val seatTotal = (outboundSeatPerPax + returnSeatPerPax) * payingPax

        // Bebés en brazos están exentos de TUA; adultos y niños pagan TUA por tramo de salida
        val outboundTuaTotal = outbound.originTuaMxn * payingPax
        val returnTuaTotal = (returnFlight?.originTuaMxn ?: 0) * payingPax

        val grandTotal = adultBaseTotal + childBaseTotal + baggageTotal + seatTotal +
            outboundTuaTotal + returnTuaTotal

        // Comparativa informativa AIFA (NLU) vs AICM (MEX)
        val mexAirport = AirportData.findByCode("MEX")
        val nluAirport = AirportData.findByCode("NLU")
        val aifaSavings = if (mexAirport != null && nluAirport != null) {
            if (outbound.isInternational) {
                mexAirport.internationalTuaMxn - nluAirport.internationalTuaMxn
            } else {
                mexAirport.domesticTuaMxn - nluAirport.domesticTuaMxn
            }
        } else 261

        val originAirport = AirportData.findByCode(outbound.originCode)
        val tuaInsightNote = when {
            outbound.originCode == "NLU" ->
                "Saliendo desde AIFA (NLU) ahorras $$aifaSavings MXN de TUA por pasajero frente al AICM (MEX)."
            outbound.originCode == "MEX" ->
                "Tip TUA: El mismo vuelo saliendo desde AIFA (NLU) ahorra $$aifaSavings MXN en TUA por pasajero."
            outbound.originCode == "TQO" ->
                "Tulum (TQO) ofrece una de las TUA más bajas del Caribe mexicano ($${outbound.originTuaMxn} MXN)."
            originAirport != null && !originAirport.isMexican ->
                "Vuelo internacional originando en ${originAirport.city} (${originAirport.code}) con tasas aeroportuarias incluidas."
            else ->
                "TUA oficial de ${originAirport?.city ?: outbound.originCode}: $${outbound.originTuaMxn} MXN por pasajero ($segments tramo${if (segments > 1) "s" else ""}). Bebés en brazos exentos de TUA."
        }

        return FareBreakdownResult(
            adultBaseTotalMxn = adultBaseTotal,
            childBaseTotalMxn = childBaseTotal,
            childDiscountSavingsMxn = childDiscountSavings,
            baggageTotalMxn = baggageTotal,
            seatTotalMxn = seatTotal,
            outboundTuaTotalMxn = outboundTuaTotal,
            returnTuaTotalMxn = returnTuaTotal,
            grandTotalMxn = grandTotal,
            payingPassengersCount = payingPax,
            infantCount = safeInfants,
            aifaSavingsPerPassengerMxn = aifaSavings,
            tuaInsightNote = tuaInsightNote
        )
    }

    fun baggageFeeForAirline(airline: Airline, pkg: BaggagePackage): Int {
        return when (pkg) {
            BaggagePackage.ZERO -> 0
            BaggagePackage.BASICA_10KG -> airline.carryOn10kgMxn
            BaggagePackage.PLUS_25KG -> airline.carryOn10kgMxn + airline.checked25kgMxn
        }
    }

    fun seatFeeForAirline(airline: Airline, option: SeatOption): Int {
        return when (option) {
            SeatOption.ALEATORIO -> 0
            SeatOption.ESTANDAR -> airline.standardSeatMxn
            SeatOption.ESPACIO_EXTRA -> airline.extraLegroomSeatMxn
        }
    }
}
