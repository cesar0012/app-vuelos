package com.example

import com.example.data.FlightDealsRepository
import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.BaggagePackage
import com.example.model.FareCalculator
import com.example.model.SeatOption
import com.example.model.TravelDate
import com.example.util.BookingLinkHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `haversine detects nearest mexican airport accurately`() {
        // Coordinates near Aguascalientes city center
        val nearAgu = AirportData.nearestMexicanAirport(21.8853, -102.2916)
        assertEquals("AGU", nearAgu.code)

        // Coordinates near AIFA (Tecámac / Zumpango)
        val nearNlu = AirportData.nearestMexicanAirport(19.75, -99.01)
        assertEquals("NLU", nearNlu.code)

        // Coordinates near Cancún Hotel Zone
        val nearCun = AirportData.nearestMexicanAirport(21.13, -86.75)
        assertEquals("CUN", nearCun.code)
    }

    @Test
    fun `repository covers 90 days and includes AGU to PVR 199 non-stop and layovers`() {
        val fixedBase = TravelDate(2026, 11, 12)
        val repo = FlightDealsRepository(baseDate = fixedBase)
        val aguPvr = repo.getDealsForRoute("AGU", "PVR")

        assertTrue("Expected at least 100 AGU->PVR deals over 90 days", aguPvr.size >= 100)

        val volarisDirect = aguPvr.filter { it.airline == Airline.VOLARIS && it.isNonStop }
        assertTrue(volarisDirect.isNotEmpty())
        assertTrue(volarisDirect.any { it.baseFareMxn == 199 })
        assertEquals(58, volarisDirect.first().durationMinutes)

        val vivaWithStop = aguPvr.filter { it.airline == Airline.VIVA_AEROBUS }
        assertTrue(vivaWithStop.isNotEmpty())
        assertFalse(vivaWithStop.first().isNonStop)
        assertEquals("MTY", vivaWithStop.first().layoverAirportCode)
        assertEquals(85, vivaWithStop.first().layoverDurationMinutes)
    }

    @Test
    fun `fare calculator applies 10 percent child discount and exempts infants from TUA`() {
        val repo = FlightDealsRepository(baseDate = TravelDate(2026, 11, 12))
        val deal = repo.getDealsForRoute("NLU", "CUN").first { it.baseFareMxn == 199 }
        assertEquals(309, deal.originTuaMxn) // AIFA official TUA

        val result = FareCalculator.calculate(
            outbound = deal,
            returnFlight = null,
            adults = 1,
            children = 1,
            infants = 1,
            baggagePackage = BaggagePackage.ZERO,
            seatOption = SeatOption.ALEATORIO
        )

        // 1 adult ($199) + 1 child (90% of 199 = 179) + TUA for 2 paying pax (309 * 2 = 618), infant exempt!
        assertEquals(199, result.adultBaseTotalMxn)
        assertEquals(179, result.childBaseTotalMxn)
        assertEquals(618, result.outboundTuaTotalMxn)
        assertEquals(199 + 179 + 618, result.grandTotalMxn)
        assertTrue(result.tuaInsightNote.contains("AIFA"))
    }

    @Test
    fun `booking link helper generates valid 404-free Google Flights and Kayak URLs`() {
        val promoDay = BookingLinkHelper.oneWayPromoDayUrl(Airline.VOLARIS, "AGU", "PVR", "2026-11-20")
        assertEquals(
            "https://www.google.com/travel/flights?q=Volaris+one+way+flights+from+AGU+to+PVR+on+2026-11-20&curr=MXN&hl=es-MX",
            promoDay
        )

        val oneWay = BookingLinkHelper.googleFlightsOneWayUrl("AGU", "PVR", "2026-11-20")
        assertEquals(
            "https://www.google.com/travel/flights?q=one+way+flights+from+AGU+to+PVR+on+2026-11-20&curr=MXN&hl=es-MX",
            oneWay
        )

        val roundTrip = BookingLinkHelper.googleFlightsRoundTripUrl("NLU", "CUN", "2026-11-20", "2026-11-25")
        assertEquals(
            "https://www.google.com/travel/flights?q=flights+from+NLU+to+CUN+on+2026-11-20+returning+2026-11-25&curr=MXN&hl=es-MX",
            roundTrip
        )

        val kayak = BookingLinkHelper.kayakOneWayUrl("MEX", "CUN", "2026-11-20", "Y4")
        assertEquals("https://www.kayak.com.mx/flights/MEX-CUN/2026-11-20?sort=price_a&fs=airlines=Y4", kayak)

        assertEquals("https://www.volaris.com/es-mx/", Airline.VOLARIS.officialPromoUrl)
        assertEquals("https://www.vivaaerobus.com/es-mx", Airline.VIVA_AEROBUS.officialPromoUrl)
        assertEquals("https://www.aeromexico.com/es-mx/vuelos-baratos", Airline.AEROMEXICO.officialPromoUrl)
        assertEquals("https://mexicana.gob.mx/", Airline.MEXICANA.officialPromoUrl)

        Airline.entries.forEach { airline ->
            assertNotNull(airline.officialPromoUrl)
            assertTrue(airline.officialPromoUrl.startsWith("https://"))
            assertFalse(airline.officialPromoUrl.contains("cms.volaris.com"))
        }
    }
}
