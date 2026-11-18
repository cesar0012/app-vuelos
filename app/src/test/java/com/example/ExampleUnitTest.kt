package com.example

import com.example.data.FlightDealsRepository
import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.BaggagePackage
import com.example.model.FareCalculator
import com.example.model.PriceDisplayOptions
import com.example.model.PricingBaggageTier
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
    fun `catalog is 100 percent Mexican domestic with 40 airports and 7 active airlines`() {
        assertEquals(40, AirportData.airports.size)
        assertTrue(AirportData.airports.all { it.isMexican })
        assertFalse(AirportData.isDomesticMexicanAirport("LAX"))
        assertFalse(AirportData.isDomesticMexicanAirport("JFK"))
        assertFalse(AirportData.isDomesticMexicanAirport("MAD"))
        assertTrue(AirportData.isDomesticMexicanAirport("AGU"))
        assertTrue(AirportData.isDomesticMexicanAirport("NLU"))
        assertTrue(AirportData.isDomesticMexicanAirport("QRO"))

        assertEquals(7, Airline.entries.size)
        val repo = FlightDealsRepository(baseDate = TravelDate(2026, 11, 12))
        assertTrue(repo.catalog.all { !it.isInternational })
        assertTrue(repo.catalog.all { AirportData.isDomesticMexicanAirport(it.originCode) && AirportData.isDomesticMexicanAirport(it.destinationCode) })

        val airlinesInCatalog = repo.catalog.map { it.airline }.toSet()
        Airline.entries.forEach { airline ->
            assertTrue("Expected domestic routes for ${airline.displayName}", airline in airlinesInCatalog)
        }
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
    fun `price display options accurately compute Con TUA vs Sin TUA and Zero vs Basica 10kg`() {
        val repo = FlightDealsRepository(baseDate = TravelDate(2026, 11, 12))
        val vivaDeal = repo.getDealsForRoute("MEX", "CUN").first { it.airline == Airline.VIVA_AEROBUS }
        val tarDeal = repo.getDealsForRoute("QRO", "PVR").first { it.airline == Airline.TAR }

        // Viva Aerobus: Tarifa Zero does NOT include 10kg carry-on (+380 MXN for Light 10kg)
        val sinTuaZero = PriceDisplayOptions(includeTua = false, baggageTier = PricingBaggageTier.ZERO)
        val conTuaZero = PriceDisplayOptions(includeTua = true, baggageTier = PricingBaggageTier.ZERO)
        val conTuaBasica = PriceDisplayOptions(includeTua = true, baggageTier = PricingBaggageTier.BASICA_10KG)
        val conTuaPlus = PriceDisplayOptions(includeTua = true, baggageTier = PricingBaggageTier.PLUS_25KG)

        assertEquals(vivaDeal.baseFareMxn, vivaDeal.effectivePriceMxn(sinTuaZero))
        assertEquals(vivaDeal.baseFareMxn + vivaDeal.originTuaMxn, vivaDeal.effectivePriceMxn(conTuaZero))
        assertEquals(
            vivaDeal.baseFareMxn + Airline.VIVA_AEROBUS.carryOn10kgMxn + vivaDeal.originTuaMxn,
            vivaDeal.effectivePriceMxn(conTuaBasica)
        )
        assertEquals(
            vivaDeal.baseFareMxn + Airline.VIVA_AEROBUS.carryOn10kgMxn + Airline.VIVA_AEROBUS.checked25kgMxn + vivaDeal.originTuaMxn,
            vivaDeal.effectivePriceMxn(conTuaPlus)
        )

        // TAR Aerolíneas: Base already includes 10kg carry-on ($0 extra for BASICA_10KG)
        assertEquals(0, tarDeal.baggageAddonMxn(PricingBaggageTier.BASICA_10KG))
        assertEquals(tarDeal.baseFareMxn + tarDeal.originTuaMxn, tarDeal.effectivePriceMxn(conTuaBasica))
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
    fun `booking link helper generates valid 404-free Google Flights and Kayak URLs for all 7 airlines`() {
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
        assertEquals("https://www.tarmexico.com/", Airline.TAR.officialPromoUrl)
        assertEquals("https://www.flyaerus.com/", Airline.AERUS.officialPromoUrl)
        assertEquals("https://www.magnicharters.com/", Airline.MAGNICHARTERS.officialPromoUrl)

        assertEquals(
            "https://www.volaris.com/es-mx/vuelos-desde-aguascalientes-a-puerto-vallarta",
            BookingLinkHelper.officialAirlineBookingUrl(Airline.VOLARIS, "AGU", "PVR")
        )
        assertEquals(
            "https://www.volaris.com/es-mx/vuelos-desde-felipe-%C3%A1ngeles-cdmx-a-cancun",
            BookingLinkHelper.officialAirlineBookingUrl(Airline.VOLARIS, "NLU", "CUN")
        )
        assertEquals(
            "https://www.vivaaerobus.com/es-mx",
            BookingLinkHelper.officialAirlineBookingUrl(Airline.VIVA_AEROBUS, "AGU", "PVR")
        )

        Airline.entries.forEach { airline ->
            assertNotNull(airline.officialPromoUrl)
            assertTrue(airline.officialPromoUrl.startsWith("https://"))
            assertFalse(airline.officialPromoUrl.contains("cms.volaris.com"))
            assertTrue(BookingLinkHelper.officialAirlineBookingUrl(airline, "MEX", "CUN").startsWith("https://"))
        }
    }
}
