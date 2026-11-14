package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AirTrfxParser
import com.example.data.AppDatabase
import com.example.data.FlightAlert
import com.example.data.FlightDealsRepository
import com.example.model.Airline
import com.example.model.TravelDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `app name matches Cheap Travel`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Cheap Travel", appName)
    }

    @Test
    fun `room database persists and toggles flight alerts`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.flightAlertDao()

        val id = dao.insertAlert(
            FlightAlert(
                originCode = "AGU",
                destinationCode = "PVR",
                maxPriceMxn = 250,
                nonStopOnly = true
            )
        ).toInt()

        var list = dao.getAllAlerts().first()
        assertEquals(1, list.size)
        assertEquals("AGU", list.first().originCode)
        assertEquals(true, list.first().isActive)

        dao.setAlertActive(id, false)
        list = dao.getAllAlerts().first()
        assertEquals(false, list.first().isActive)

        dao.deleteAlertById(id)
        list = dao.getAllAlerts().first()
        assertEquals(0, list.size)
        db.close()
    }

    @Test
    fun `navigation across all 4 tabs and calendar month grid renders cleanly`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Cheap Travel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("active_airport_chips_row").assertIsDisplayed()

        // Navigate to Buscador tab
        composeTestRule.onNodeWithTag("nav_tab_search").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Buscador Flexible Multi-Ruta").assertIsDisplayed()

        // Navigate to Calendario tab
        composeTestRule.onNodeWithTag("nav_tab_calendar").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("calendar_swap_route_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("calendar_swap_route_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("calendar_screen_list")
            .performScrollToNode(hasTestTag("calendar_month_grid"))
        composeTestRule.onNodeWithTag("calendar_month_grid").assertIsDisplayed()

        // Navigate to Alertas tab
        composeTestRule.onNodeWithTag("nav_tab_alerts").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("alert_origin_selector").assertIsDisplayed()
        composeTestRule.onNodeWithTag("create_alert_save_btn").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `promo locator dialog shows primary one-way link and expands secondary options on click`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("deals_radar_list")
            .performScrollToIndex(3)
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("Ver Promo"))[0]
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("promo_primary_day_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("promo_search_instructions").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("promo_kayak_btn").assertDoesNotExist()

        composeTestRule.onNodeWithTag("promo_toggle_other_options").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("promo_kayak_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("promo_google_all_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("promo_official_airline_btn").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `airTrfxParser parses live Volaris and Viva __NEXT_DATA__ and updates repository exact prices`() {
        val sampleHtml = """
            <html><head><title>Test</title></head><body>
            <script id="__NEXT_DATA__" type="application/json">{
              "props": {
                "pageProps": {
                  "apolloState": {
                    "data": {
                      "StandardFareModule:1": {
                        "fares": [
                          {
                            "originAirportCode": "AGU",
                            "destinationAirportCode": "PVR",
                            "departureDate": "2026-11-17",
                            "totalPrice": 212,
                            "currencyCode": "MXN",
                            "flightType": "ONE_WAY",
                            "airline": { "iataCode": "Y4" },
                            "lastSeen": { "value": 2, "unit": "hour" }
                          },
                          {
                            "originAirportCode": "NLU",
                            "destinationAirportCode": "CUN",
                            "departureDate": "2026-11-18",
                            "totalPrice": 256.55,
                            "currencyCode": "MXN",
                            "flightType": "ONE_WAY",
                            "airline": { "iataCode": "VB" },
                            "lastSeen": { "value": 15, "unit": "minute" }
                          }
                        ]
                      },
                      "Component:fare-daily-histogram": {
                        "cmpConfig": {
                          "settings": {
                            "initialState": {
                              "dailyHistogram": [
                                {
                                  "journeyType": "ONE_WAY",
                                  "departureDate": "2026-11-26",
                                  "priceSpecification": { "totalPrice": 186, "currencyCode": "MXN" },
                                  "outboundFlight": { "departureAirportIataCode": "AGU", "arrivalAirportIataCode": "PVR" },
                                  "airline": { "iataCode": "Y4" }
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }</script></body></html>
        """.trimIndent()

        val fares = AirTrfxParser.parseFaresFromHtml(sampleHtml, Airline.VOLARIS, "https://www.volaris.com/es-mx/vuelos-desde-aguascalientes-a-puerto-vallarta")
        assertEquals(3, fares.size)

        val aguPvrNov17 = fares.first { it.originCode == "AGU" && it.departureDate.isoString == "2026-11-17" }
        assertEquals(Airline.VOLARIS, aguPvrNov17.airline)
        assertEquals(212, aguPvrNov17.baseFareMxn)
        assertEquals("hace 2 h", aguPvrNov17.lastSeenLabel)

        val aguPvrNov26 = fares.first { it.originCode == "AGU" && it.departureDate.isoString == "2026-11-26" }
        assertEquals(186, aguPvrNov26.baseFareMxn)

        val nluCunViva = fares.first { it.originCode == "NLU" && it.destinationCode == "CUN" }
        assertEquals(Airline.VIVA_AEROBUS, nluCunViva.airline)
        assertEquals(257, nluCunViva.baseFareMxn) // ceil(256.55) == 257

        val repo = FlightDealsRepository(baseDate = TravelDate(2026, 11, 12))
        repo.applyLiveFares(fares)
        assertEquals(3, repo.liveFareCount())

        val aguPvrOn17 = repo.getDealsForRoute("AGU", "PVR")
            .filter { it.airline == Airline.VOLARIS && it.departureDate.isoString == "2026-11-17" }
        assertTrue(aguPvrOn17.isNotEmpty())
        val primaryLive = aguPvrOn17.first { it.isLiveVerified }
        assertEquals(212, primaryLive.baseFareMxn)
        assertTrue(aguPvrOn17.filter { !it.isLiveVerified }.all { it.baseFareMxn >= 212 + 120 })
        assertFalse(aguPvrOn17.any { !it.isLiveVerified && it.baseFareMxn < 212 })
    }
}
