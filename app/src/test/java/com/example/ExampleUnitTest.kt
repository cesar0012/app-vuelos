package com.example

import com.example.data.AirportData
import com.example.data.FlightDealsRepository
import com.example.model.Airline
import com.example.model.BaggageOption
import com.example.model.FlightAlert
import com.example.model.FlightDeal
import com.example.model.PassengerCount
import com.example.model.SeatOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun testVolaris199PromoWithTua() {
        val aicm = AirportData.findByCode("MEX")
        val cancun = AirportData.findByCode("CUN")

        val deal = FlightDeal(
            id = "test_deal",
            airline = Airline.VOLARIS,
            flightNumber = "Y4 1001",
            origin = aicm,
            destination = cancun,
            departureDate = LocalDate.now().plusDays(10),
            departureTime = "08:00",
            arrivalTime = "10:30",
            durationMinutes = 150,
            baseFareMxn = 199.0
        )

        val breakdown = deal.calculateBreakdown(
            passengerCount = PassengerCount(adults = 1, children = 0, infants = 0),
            baggageOption = BaggageOption.ZERO,
            seatOption = SeatOption.RANDOM
        )

        assertEquals(199.0, breakdown.totalBaseFare, 0.01)
        assertEquals(570.0, breakdown.totalTua, 0.01) // AICM national TUA
        assertEquals(769.0, breakdown.grandTotalMxn, 0.01)
    }

    @Test
    fun testAifaTuaSavingsVersusAicm() {
        val aicm = AirportData.findByCode("MEX")
        val aifa = AirportData.findByCode("NLU")

        assertTrue("AIFA TUA ($309) should be significantly lower than AICM ($570)",
            aifa.nationalTuaMxn < aicm.nationalTuaMxn
        )
        assertEquals(261.0, aicm.nationalTuaMxn - aifa.nationalTuaMxn, 0.01)
    }

    @Test
    fun testMultiPassengerCalculationWithBaggageAndSeats() {
        val aifa = AirportData.findByCode("NLU")
        val puertoVallarta = AirportData.findByCode("PVR")

        val deal = FlightDeal(
            id = "test_family",
            airline = Airline.VOLARIS,
            flightNumber = "Y4 2002",
            origin = aifa,
            destination = puertoVallarta,
            departureDate = LocalDate.now().plusDays(15),
            departureTime = "10:00",
            arrivalTime = "11:30",
            durationMinutes = 90,
            baseFareMxn = 249.0
        )

        // 2 Adults, 1 Child, 1 Infant (lap infant: no TUA)
        val passengers = PassengerCount(adults = 2, children = 1, infants = 1)
        assertEquals(3, passengers.totalPayingPassengers)
        assertEquals(4, passengers.totalTravelers)

        val breakdown = deal.calculateBreakdown(
            passengerCount = passengers,
            baggageOption = BaggageOption.BASIC, // +$289 carry on per passenger
            seatOption = SeatOption.STANDARD // +$129 standard seat per passenger
        )

        // Base fare: 2 adults * $249 + 1 child * ($249 * 0.9 = $224.10) = $722.10
        assertEquals(722.10, breakdown.totalBaseFare, 0.1)

        // TUA: 3 paying passengers * $309 = $927
        assertEquals(3 * 309.0, breakdown.totalTua, 0.01)

        // Luggage: 3 * $289 = $867
        assertEquals(3 * 289.0, breakdown.totalBaggage, 0.01)

        // Seats: 3 * $129 = $387
        assertEquals(3 * 129.0, breakdown.totalSeats, 0.01)

        // Grand Total: 722.10 + 927 + 867 + 387 = 2903.10
        assertEquals(2903.10, breakdown.grandTotalMxn, 0.1)
    }

    @Test
    fun testFlightDealsRepositoryGenerates199PromoDeals() {
        val repo = FlightDealsRepository()
        val allDeals = repo.getAllDeals()
        assertTrue("Repository should have generated deals", allDeals.isNotEmpty())

        val ultraCheapDeals = allDeals.filter { it.baseFareMxn <= 199.0 }
        assertTrue("Repository must include famous Volaris/Viva $199 MXN deals", ultraCheapDeals.isNotEmpty())
    }

    @Test
    fun testAlertMatchingDetectsCheapDeals() {
        val repo = FlightDealsRepository()
        val alert = FlightAlert(
            originCode = "MEX",
            originCity = "CDMX",
            destinationCode = "CUN",
            destinationCity = "Cancún",
            targetMaxPriceMxn = 800.0 // MEX $199 base + $570 TUA = $769, so it matches!
        )

        val matches = repo.findMatchingDealsForAlert(alert)
        assertTrue("Should detect matching flight deals below $800 MXN budget", matches.isNotEmpty())
    }

    @Test
    fun testAguascalientesAirportExists() {
        val agu = AirportData.findByCode("AGU")
        assertNotNull(agu)
        assertEquals("AGU", agu.code)
        assertEquals("Aguascalientes", agu.city)
        assertEquals(528.0, agu.nationalTuaMxn, 0.01)

        val repo = FlightDealsRepository()
        val aguDeals = repo.getAllDeals().filter { it.origin.code == "AGU" || it.destination.code == "AGU" }
        assertTrue("Should generate flight deals for Aguascalientes", aguDeals.isNotEmpty())
    }

    @Test
    fun testMultiDestinationSearch() {
        val repo = FlightDealsRepository()
        val cancun = AirportData.findByCode("CUN")
        val tulum = AirportData.findByCode("TQO")
        val vallarta = AirportData.findByCode("PVR")

        val beachResults = repo.searchDeals(
            origins = listOf(AirportData.findByCode("NLU")),
            destinations = listOf(cancun, tulum, vallarta)
        )
        assertTrue("Multi-destination search should return matching deals", beachResults.isNotEmpty())
        assertTrue("All returned deals must match one of the selected destinations",
            beachResults.all { it.destination.code in listOf("CUN", "TQO", "PVR") }
        )
    }

    @Test
    fun testNewArrivalDestinationsExist() {
        val tulum = AirportData.findByCode("TQO")
        val mazatlan = AirportData.findByCode("MZT")
        val laPaz = AirportData.findByCode("LAP")
        val cozumel = AirportData.findByCode("CZM")

        assertEquals("Tulum / Riviera Maya", tulum.city)
        assertTrue(tulum.isBeachDestination)
        assertEquals("Mazatlán", mazatlan.city)
        assertTrue(mazatlan.isBeachDestination)
        assertEquals("La Paz", laPaz.city)
        assertEquals("Cozumel", cozumel.city)
    }

    @Test
    fun testAguascalientesToPuertoVallartaNov26Deal() {
        val repo = FlightDealsRepository()
        val agu = AirportData.findByCode("AGU")
        val pvr = AirportData.findByCode("PVR")

        val deals = repo.searchDeals(
            origins = listOf(agu),
            destinations = listOf(pvr)
        )
        assertTrue("Should have deals from Aguascalientes to Puerto Vallarta", deals.isNotEmpty())

        val nov26Date = LocalDate.of(2026, 11, 26)
        val nov26Deal = deals.firstOrNull { it.departureDate == nov26Date }
        assertNotNull("Must find flight on November 26, 2026 from AGU to PVR", nov26Deal)
        assertEquals(199.0, nov26Deal!!.baseFareMxn, 0.01)
        assertEquals(Airline.VOLARIS, nov26Deal.airline)
    }

    @Test
    fun testBookingLinksGeneration() {
        val agu = AirportData.findByCode("AGU")
        val pvr = AirportData.findByCode("PVR")
        val deal = FlightDeal(
            id = "test_volaris_link",
            airline = Airline.VOLARIS,
            flightNumber = "Y4 3402",
            origin = agu,
            destination = pvr,
            departureDate = LocalDate.of(2026, 11, 26),
            departureTime = "08:15",
            arrivalTime = "09:25",
            durationMinutes = 70,
            baseFareMxn = 199.0
        )

        val passengers = PassengerCount(adults = 2, children = 1, infants = 0)
        val link = com.example.util.BookingLinkHelper.getDirectBookingUrl(deal, passengers)
        val promoUrl = com.example.util.BookingLinkHelper.getOfficialPromoPortalUrl(deal.airline)

        assertTrue("Direct live URL must contain Google Flights domain", link.contains("google.com/travel/flights"))
        assertTrue("URL must contain origin AGU", link.contains("AGU"))
        assertTrue("URL must contain destination PVR", link.contains("PVR"))
        assertTrue("URL must contain departure date", link.contains("2026-11-26"))
        assertTrue("Official promo URL must contain airline domain", promoUrl.contains("volaris.com"))
    }

    @Test
    fun testRoundTripBookingUrlAndSmartMix() {
        val agu = AirportData.findByCode("AGU")
        val pvr = AirportData.findByCode("PVR")

        val outbound = FlightDeal(
            id = "out",
            airline = Airline.VOLARIS,
            flightNumber = "Y4 3402",
            origin = agu,
            destination = pvr,
            departureDate = LocalDate.of(2026, 11, 26),
            departureTime = "08:15",
            arrivalTime = "09:25",
            durationMinutes = 70,
            baseFareMxn = 199.0
        )

        val returnVolaris = FlightDeal(
            id = "ret_volaris",
            airline = Airline.VOLARIS,
            flightNumber = "Y4 3403",
            origin = pvr,
            destination = agu,
            departureDate = LocalDate.of(2026, 11, 28),
            departureTime = "18:15",
            arrivalTime = "19:25",
            durationMinutes = 70,
            baseFareMxn = 199.0
        )

        // Same airline round trip
        val roundTripUrl = com.example.util.BookingLinkHelper.getRoundTripBookingUrl(outbound, returnVolaris)
        assertTrue("Round-trip URL must be on Google Flights", roundTripUrl.contains("google.com/travel/flights"))
        assertTrue("Round-trip URL must include return date", roundTripUrl.contains("2026-11-28"))
        assertTrue("Round-trip URL must include origin AGU", roundTripUrl.contains("AGU"))
        assertTrue("Round-trip URL must include destination PVR", roundTripUrl.contains("PVR"))

        // Round trip breakdown calculation
        val breakdown = outbound.calculateRoundTripBreakdown(
            returnDeal = returnVolaris,
            passengerCount = PassengerCount(adults = 1),
            baggageOption = BaggageOption.ZERO,
            seatOption = SeatOption.RANDOM
        )

        assertEquals(398.0, breakdown.totalBaseFare, 0.01) // 199 + 199
        assertEquals(528.0 + 470.0, breakdown.totalTua, 0.01) // AGU TUA ($528) + PVR TUA ($470)
        assertEquals(1396.0, breakdown.grandTotalMxn, 0.01)
        assertTrue(breakdown.isRoundTrip)
    }

    @Test
    fun testAguascalientesPuertoVallartaReturnFlightsExist() {
        val repo = FlightDealsRepository()
        val pvr = AirportData.findByCode("PVR")
        val agu = AirportData.findByCode("AGU")

        val returnDeals = repo.searchDeals(
            origins = listOf(pvr),
            destinations = listOf(agu)
        )

        assertTrue("Must have return deals from PVR to AGU", returnDeals.isNotEmpty())

        val nov28Return = returnDeals.firstOrNull { it.departureDate == LocalDate.of(2026, 11, 28) }
        assertNotNull("Return flight on Nov 28 from PVR to AGU must exist", nov28Return)
        assertEquals(199.0, nov28Return!!.baseFareMxn, 0.01)
        assertEquals(Airline.VOLARIS, nov28Return.airline)

        val nov29Return = returnDeals.firstOrNull { it.departureDate == LocalDate.of(2026, 11, 29) }
        assertNotNull("Return flight on Nov 29 from PVR to AGU must exist", nov29Return)
        assertEquals(229.0, nov29Return!!.baseFareMxn, 0.01)
        assertEquals(Airline.VIVA_AEROBUS, nov29Return.airline)
    }
}
