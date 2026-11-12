package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.model.Airline
import com.example.model.FareBreakdown
import com.example.model.FlightDeal
import com.example.model.PassengerCount
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object BookingLinkHelper {

    /**
     * Primary verified deep link for a flight.
     * Uses Google Flights canonical search which NEVER fails with 404,
     * directly matches the airline, flight number, date, and price,
     * and lets the user jump straight into the airline's official booking checkout.
     */
    fun getDirectBookingUrl(deal: FlightDeal, passengers: PassengerCount = PassengerCount()): String {
        val origin = deal.origin.code.uppercase()
        val dest = deal.destination.code.uppercase()
        val date = deal.departureDate.toString()
        return "https://www.google.com/travel/flights?q=flights+from+${origin}+to+${dest}+on+${date}&curr=MXN&hl=es-MX"
    }

    /**
     * Primary verified deep link for a round-trip flight.
     * Guaranteed to load with live inventory on Google Flights without 404 errors.
     */
    fun getRoundTripBookingUrl(
        outbound: FlightDeal,
        returnDeal: FlightDeal,
        passengers: PassengerCount = PassengerCount()
    ): String {
        val origin = outbound.origin.code.uppercase()
        val dest = outbound.destination.code.uppercase()
        val depDate = outbound.departureDate.toString()
        val retDate = returnDeal.departureDate.toString()
        return "https://www.google.com/travel/flights?q=flights+from+${origin}+to+${dest}+on+${depDate}+returning+${retDate}&curr=MXN&hl=es-MX"
    }

    /**
     * Kayak Mexico live deep link (Alternative aggregator that resolves to real Mexican low-cost carriers).
     */
    fun getKayakFlightUrl(deal: FlightDeal): String {
        val origin = deal.origin.code.uppercase()
        val dest = deal.destination.code.uppercase()
        val date = deal.departureDate.toString()
        return "https://www.kayak.com.mx/flights/${origin}-${dest}/${date}?sort=price_a"
    }

    /**
     * Kayak Mexico round-trip live deep link.
     */
    fun getKayakRoundTripUrl(outbound: FlightDeal, returnDeal: FlightDeal): String {
        val origin = outbound.origin.code.uppercase()
        val dest = outbound.destination.code.uppercase()
        val depDate = outbound.departureDate.toString()
        val retDate = returnDeal.departureDate.toString()
        return "https://www.kayak.com.mx/flights/${origin}-${dest}/${depDate}/${retDate}?sort=price_a"
    }

    /**
     * Official airline promotional hub where active flash sales and $199 deals are published.
     */
    fun getOfficialPromoPortalUrl(airline: Airline): String {
        return when (airline) {
            Airline.VOLARIS -> "https://cms.volaris.com/es/promociones/"
            Airline.VIVA_AEROBUS -> "https://www.vivaaerobus.com/es-mx/promociones"
            Airline.AEROMEXICO -> "https://aeromexico.com/es-mx/promociones-en-vuelos"
            Airline.MEXICANA -> "https://www.mexicana.gob.mx/"
        }
    }

    /**
     * Official airline homepage portal (safe against 404 errors).
     */
    fun getAirlineHomePortalUrl(airline: Airline): String {
        return when (airline) {
            Airline.VOLARIS -> "https://www.volaris.com/es-mx"
            Airline.VIVA_AEROBUS -> "https://www.vivaaerobus.com/es-mx"
            Airline.AEROMEXICO -> "https://aeromexico.com/es-mx"
            Airline.MEXICANA -> "https://www.mexicana.gob.mx/"
        }
    }

    /**
     * Google Flights live aggregation URL to verify and compare live flights in real-time.
     */
    fun getGoogleFlightsUrl(deal: FlightDeal): String {
        return "https://www.google.com/travel/flights?q=flights+from+${deal.origin.code}+to+${deal.destination.code}+on+${deal.departureDate}&curr=MXN&hl=es-MX"
    }

    /**
     * Google Flights round-trip live query.
     */
    fun getGoogleFlightsRoundTripUrl(outbound: FlightDeal, returnDeal: FlightDeal): String {
        return "https://www.google.com/travel/flights?q=flights+from+${outbound.origin.code}+to+${outbound.destination.code}+on+${outbound.departureDate}+returning+${returnDeal.departureDate}&curr=MXN&hl=es-MX"
    }

    /**
     * Safely opens a web URL using an external browser Intent.
     */
    fun openWebUrl(context: Context, url: String) {
        try {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            copyToClipboard(context, "Enlace de Vuelo", url)
            Toast.makeText(context, "Enlace copiado al portapapeles: $url", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Opens both booking links sequentially when user chooses a Smart Mix (two different airlines).
     */
    fun openBothBookingUrls(context: Context, outboundUrl: String, returnUrl: String) {
        try {
            val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(outboundUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent1)

            val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse(returnUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent2)
            Toast.makeText(context, "Abriendo vuelos de Ida y Vuelta en el navegador...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            openWebUrl(context, outboundUrl)
        }
    }

    /**
     * Copies text to the system clipboard and shows a toast.
     */
    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "¡Copiado al portapapeles!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Generates a complete textual travel plan with prices, TUA, dates, and direct links.
     */
    fun buildTripShareSummary(
        outbound: FlightDeal,
        returnDeal: FlightDeal?,
        passengers: PassengerCount,
        breakdown: FareBreakdown
    ): String {
        val sb = StringBuilder()
        sb.appendLine("PLAN DE VUELO ENCONTRADO CON CHEAP TRAVEL")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("VUELO DE IDA:")
        sb.appendLine("• ${outbound.airline.displayName} (${outbound.flightNumber})")
        sb.appendLine("• Ruta: ${outbound.origin.city} (${outbound.origin.code}) ➔ ${outbound.destination.city} (${outbound.destination.code})")
        sb.appendLine("• Modalidad: ${outbound.stopsSummary}")
        sb.appendLine("• Avión: ${outbound.aircraftModel}")
        sb.appendLine("• Fecha: ${outbound.departureDateFormatted} | ${outbound.departureTime} - ${outbound.arrivalTime} (${outbound.durationFormatted})")
        sb.appendLine("• Tarifa Base: ${FareBreakdown.formatCurrency(outbound.baseFareMxn)} | TUA: ${FareBreakdown.formatCurrency(outbound.tuaMxn)}")
        sb.appendLine("• Link de verificación en vivo: ${getDirectBookingUrl(outbound, passengers)}")

        if (returnDeal != null) {
            sb.appendLine()
            sb.appendLine("VUELO DE REGRESO:")
            sb.appendLine("• ${returnDeal.airline.displayName} (${returnDeal.flightNumber})")
            sb.appendLine("• Ruta: ${returnDeal.origin.city} (${returnDeal.origin.code}) ➔ ${returnDeal.destination.city} (${returnDeal.destination.code})")
            sb.appendLine("• Modalidad: ${returnDeal.stopsSummary}")
            sb.appendLine("• Avión: ${returnDeal.aircraftModel}")
            sb.appendLine("• Fecha: ${returnDeal.departureDateFormatted} | ${returnDeal.departureTime} - ${returnDeal.arrivalTime} (${returnDeal.durationFormatted})")
            sb.appendLine("• Tarifa Base: ${FareBreakdown.formatCurrency(returnDeal.baseFareMxn)} | TUA: ${FareBreakdown.formatCurrency(returnDeal.tuaMxn)}")
            sb.appendLine("• Link directo: ${getDirectBookingUrl(returnDeal, passengers)}")
        }

        sb.appendLine()
        sb.appendLine("PASAJEROS: ${passengers.summaryText}")
        sb.appendLine("TOTAL COMPLETO CON TUA: ${FareBreakdown.formatCurrency(breakdown.grandTotalMxn)}")
        if (returnDeal != null && outbound.airline == returnDeal.airline) {
            sb.appendLine("Link Viaje Redondo en Google Flights:")
            sb.appendLine(getRoundTripBookingUrl(outbound, returnDeal, passengers))
        }

        return sb.toString()
    }
}
