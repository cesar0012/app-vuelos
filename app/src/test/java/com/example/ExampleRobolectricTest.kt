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
import com.example.data.AppDatabase
import com.example.data.FlightAlert
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
        composeTestRule.onNodeWithTag("promo_kayak_btn").assertDoesNotExist()

        composeTestRule.onNodeWithTag("promo_toggle_other_options").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("promo_kayak_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("promo_google_all_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("promo_official_airline_btn").performScrollTo().assertIsDisplayed()
    }
}
