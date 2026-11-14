package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.AirTrfxCitySlugs
import com.example.model.Airline
import java.net.URLEncoder

/**
 * Generates verified HTTP-200 official airline URLs and secondary comparison links
 * (Kayak Mexico & Google Flights).
 */
object BookingLinkHelper {

    /**
     * Direct official airline URL.
     * - Volaris: verified route page (vuelos-desde-{origin}-a-{dest}) showing exact base-fare
     *   calendar + flight search widget, or volaris.com/es-mx/ fallback.
     * - Viva Aerobus: vivaaerobus.com/es-mx (normal booking homepage to avoid 404 on connecting routes).
     * - Aeroméxico / Mexicana: verified HTTP 200 official search/offers pages.
     */
    fun officialAirlineBookingUrl(
        airline: Airline,
        originCode: String,
        destinationCode: String
    ): String {
        return when (airline) {
            Airline.VOLARIS -> {
                AirTrfxCitySlugs.volarisRouteUrl(originCode, destinationCode)
                    ?: airline.officialPromoUrl
            }
            Airline.VIVA_AEROBUS -> airline.officialPromoUrl
            Airline.AEROMEXICO -> airline.officialPromoUrl
            Airline.MEXICANA -> airline.officialPromoUrl
        }
    }

    /**
     * Secondary one-way Google Flights comparison link (includes airport TUA in total).
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
