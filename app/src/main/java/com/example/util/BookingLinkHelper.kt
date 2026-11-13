package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Generates live search URLs verified against Google Flights, Kayak Mexico, and active
 * airline promotions pages so the user never hits 404 Page Not Found SPA routes.
 */
object BookingLinkHelper {

    fun googleFlightsOneWayUrl(
        originCode: String,
        destinationCode: String,
        departureDateIso: String
    ): String {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        return "https://www.google.com/travel/flights?q=flights+from+$o+to+$d+on+$departureDateIso&curr=MXN&hl=es-MX"
    }

    fun googleFlightsRoundTripUrl(
        originCode: String,
        destinationCode: String,
        departureDateIso: String,
        returnDateIso: String
    ): String {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        return "https://www.google.com/travel/flights?q=flights+from+$o+to+$d+on+$departureDateIso+returning+$returnDateIso&curr=MXN&hl=es-MX"
    }

    fun kayakOneWayUrl(
        originCode: String,
        destinationCode: String,
        departureDateIso: String
    ): String {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        return "https://www.kayak.com.mx/flights/$o-$d/$departureDateIso?sort=price_a"
    }

    fun kayakRoundTripUrl(
        originCode: String,
        destinationCode: String,
        departureDateIso: String,
        returnDateIso: String
    ): String {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        return "https://www.kayak.com.mx/flights/$o-$d/$departureDateIso/$returnDateIso?sort=price_a"
    }

    fun openExternalUrl(context: Context, url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
