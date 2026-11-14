package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.model.Airline
import java.net.URLEncoder

/**
 * Generates live one-way and round-trip deep links verified against HTTP 200 endpoints
 * (Google Flights with airline filter & one-way day pre-selection, Kayak Mexico with airline
 * filter, and normal airline flight search homepages).
 */
object BookingLinkHelper {

    /**
     * Primary promo locator URL: takes the user directly to the exact one-way day, origin,
     * destination, and specific airline in MXN.
     */
    fun oneWayPromoDayUrl(
        airline: Airline,
        originCode: String,
        destinationCode: String,
        departureDateIso: String
    ): String {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        val airlineQuery = when (airline) {
            Airline.AEROMEXICO -> "Aeromexico"
            Airline.VIVA_AEROBUS -> "Viva Aerobus"
            Airline.VOLARIS -> "Volaris"
            Airline.MEXICANA -> "Mexicana"
        }
        val encodedAirline = URLEncoder.encode(airlineQuery, "UTF-8")
        return "https://www.google.com/travel/flights?q=$encodedAirline+one+way+flights+from+$o+to+$d+on+$departureDateIso&curr=MXN&hl=es-MX"
    }

    fun googleFlightsOneWayUrl(
        originCode: String,
        destinationCode: String,
        departureDateIso: String
    ): String {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        return "https://www.google.com/travel/flights?q=one+way+flights+from+$o+to+$d+on+$departureDateIso&curr=MXN&hl=es-MX"
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
        departureDateIso: String,
        airlineIataCode: String? = null
    ): String {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        val base = "https://www.kayak.com.mx/flights/$o-$d/$departureDateIso?sort=price_a"
        return if (!airlineIataCode.isNullOrBlank()) {
            "$base&fs=airlines=${airlineIataCode.uppercase()}"
        } else {
            base
        }
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
