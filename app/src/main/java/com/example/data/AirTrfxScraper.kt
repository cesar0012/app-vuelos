package com.example.data

import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.TravelDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.GZIPInputStream
import kotlin.math.ceil

data class ScrapedFare(
    val airline: Airline,
    val originCode: String,
    val destinationCode: String,
    val departureDate: TravelDate,
    val baseFareMxn: Int,
    val lastSeenLabel: String? = null,
    val sourceUrl: String? = null
)

object AirTrfxCitySlugs {
    val volarisSlugs: Map<String, String> = mapOf(
        "NLU" to "felipe-%C3%A1ngeles-cdmx",
        "MEX" to "mexico-df",
        "AGU" to "aguascalientes",
        "GDL" to "guadalajara",
        "MTY" to "monterrey",
        "TIJ" to "tijuana",
        "CUN" to "cancun",
        "PVR" to "puerto-vallarta",
        "SJD" to "los-cabos",
        "TQO" to "tulum",
        "MID" to "merida",
        "MZT" to "mazatlan",
        "OAX" to "oaxaca-mx",
        "HUX" to "huatulco",
        "LAP" to "la-paz",
        "CZM" to "cozumel",
        "TGZ" to "tuxtla-gutierrez",
        "BJX" to "leon",
        "QRO" to "queretaro",
        "ZIH" to "ixtapa-zihuatanejo",
        "LAX" to "los-angeles",
        "ORD" to "chicago",
        "MIA" to "miami",
        "IAH" to "houston",
        "LAS" to "las-vegas",
        "MCO" to "orlando",
        "DFW" to "dallas",
        "SJO" to "san-jose-cr",
        "GUA" to "ciudad-de-guatemala"
    )

    val vivaSlugs: Map<String, String> = mapOf(
        "NLU" to "felipe-angeles-cdmx",
        "MEX" to "ciudad-de-mexico",
        "AGU" to "aguascalientes",
        "GDL" to "guadalajara",
        "MTY" to "monterrey",
        "TIJ" to "tijuana",
        "CUN" to "cancun",
        "PVR" to "puerto-vallarta",
        "SJD" to "los-cabos",
        "TQO" to "tulum",
        "MID" to "merida",
        "MZT" to "mazatlan",
        "OAX" to "oaxaca",
        "HUX" to "huatulco",
        "LAP" to "la-paz",
        "CZM" to "cozumel",
        "TGZ" to "tuxtla-gutierrez",
        "BJX" to "leon",
        "QRO" to "queretaro",
        "ZIH" to "ixtapa-zihuatanejo",
        "LAX" to "los-angeles",
        "ORD" to "chicago",
        "MIA" to "miami",
        "IAH" to "houston",
        "LAS" to "las-vegas",
        "MCO" to "orlando",
        "DFW" to "dallas",
        "JFK" to "nueva-york-jfk",
        "BOG" to "bogota"
    )

    fun volarisOriginUrl(originCode: String): String? {
        val slug = volarisSlugs[originCode.uppercase()] ?: return null
        return "https://www.volaris.com/es-mx/vuelos-desde-$slug"
    }

    fun volarisRouteUrl(originCode: String, destCode: String): String? {
        val oSlug = volarisSlugs[originCode.uppercase()] ?: return null
        val dSlug = volarisSlugs[destCode.uppercase()] ?: return null
        return "https://www.volaris.com/es-mx/vuelos-desde-$oSlug-a-$dSlug"
    }

    fun vivaOriginUrl(originCode: String): String? {
        val slug = vivaSlugs[originCode.uppercase()] ?: return null
        return "https://www.vivaaerobus.com/es-mx/vuelos-desde-$slug"
    }

    fun vivaRouteUrl(originCode: String, destCode: String): String? {
        val oSlug = vivaSlugs[originCode.uppercase()] ?: return null
        val dSlug = vivaSlugs[destCode.uppercase()] ?: return null
        return "https://www.vivaaerobus.com/es-mx/vuelos-desde-$oSlug-a-$dSlug"
    }
}

object AirTrfxParser {
    private val NEXT_DATA_REGEX = Regex(
        """<script\s+id="__NEXT_DATA__"\s+type="application/json"[^>]*>(.*?)</script>""",
        setOf(RegexOption.DOT_MATCHES_ALL)
    )

    fun extractNextDataJson(html: String): String? {
        return NEXT_DATA_REGEX.find(html)?.groupValues?.getOrNull(1)
    }

    fun parseFaresFromHtml(
        html: String,
        defaultAirline: Airline,
        sourceUrl: String? = null
    ): List<ScrapedFare> {
        val jsonText = extractNextDataJson(html) ?: return emptyList()
        return parseFaresFromNextDataJson(jsonText, defaultAirline, sourceUrl)
    }

    fun parseFaresFromNextDataJson(
        jsonText: String,
        defaultAirline: Airline,
        sourceUrl: String? = null
    ): List<ScrapedFare> {
        val collected = LinkedHashMap<String, ScrapedFare>()
        try {
            val root = JSONObject(jsonText)
            val apolloData = root
                .optJSONObject("props")
                ?.optJSONObject("pageProps")
                ?.optJSONObject("apolloState")
                ?.optJSONObject("data")
                ?: return emptyList()

            val keys = apolloData.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val obj = apolloData.optJSONObject(key) ?: continue

                when {
                    key.startsWith("StandardFareModule") -> {
                        val faresArr = obj.optJSONArray("fares") ?: continue
                        parseStandardFaresArray(faresArr, defaultAirline, sourceUrl, collected)
                    }

                    key.contains("fare-daily-histogram", ignoreCase = true) -> {
                        val initState = obj
                            .optJSONObject("cmpConfig")
                            ?.optJSONObject("settings")
                            ?.optJSONObject("initialState")
                            ?: continue
                        initState.optJSONArray("dailyHistogram")?.let {
                            parseHistogramArray(it, defaultAirline, sourceUrl, collected)
                        }
                        initState.optJSONArray("monthlyHistogram")?.let {
                            parseHistogramArray(it, defaultAirline, sourceUrl, collected)
                        }
                    }

                    key.startsWith("DpaHeadline") -> {
                        val lowest = obj
                            .optJSONObject("metaData")
                            ?.optJSONObject("headline")
                            ?.optJSONObject("lowestFare")
                        if (lowest != null) {
                            parseSingleFareObject(lowest, defaultAirline, sourceUrl, collected)
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Gracefully return whatever fares were parsed before malformed section
        }
        return collected.values.toList()
    }

    private fun parseStandardFaresArray(
        arr: JSONArray,
        defaultAirline: Airline,
        sourceUrl: String?,
        out: MutableMap<String, ScrapedFare>
    ) {
        for (i in 0 until arr.length()) {
            val item = arr.optJSONObject(i) ?: continue
            parseSingleFareObject(item, defaultAirline, sourceUrl, out)
        }
    }

    private fun parseSingleFareObject(
        item: JSONObject,
        defaultAirline: Airline,
        sourceUrl: String?,
        out: MutableMap<String, ScrapedFare>
    ) {
        val flightType = item.optString("flightType", "")
        if (flightType.isNotEmpty() && !flightType.equals("ONE_WAY", ignoreCase = true)) return

        val currency = item.optString("currencyCode", "MXN")
        if (currency.isNotEmpty() && !currency.equals("MXN", ignoreCase = true)) return

        val origin = normalizeCode(item.optString("originAirportCode", "")) ?: return
        val dest = normalizeCode(item.optString("destinationAirportCode", "")) ?: return
        val depDate = TravelDate.parseIso(item.optString("departureDate", "")) ?: return

        val rawPrice = item.optDouble("totalPrice", Double.NaN)
        if (rawPrice.isNaN() || rawPrice <= 0.0) return
        val priceMxn = ceil(rawPrice).toInt()

        val airline = resolveAirline(item.optJSONObject("airline")?.optString("iataCode"), defaultAirline)
        val lastSeen = parseLastSeen(item.optJSONObject("lastSeen"))
        putIfCheaper(
            out,
            ScrapedFare(
                airline = airline,
                originCode = origin,
                destinationCode = dest,
                departureDate = depDate,
                baseFareMxn = priceMxn,
                lastSeenLabel = lastSeen,
                sourceUrl = sourceUrl
            )
        )
    }

    private fun parseHistogramArray(
        arr: JSONArray,
        defaultAirline: Airline,
        sourceUrl: String?,
        out: MutableMap<String, ScrapedFare>
    ) {
        for (i in 0 until arr.length()) {
            val item = arr.optJSONObject(i) ?: continue
            val journeyType = item.optString("journeyType", "")
            if (journeyType.isNotEmpty() && !journeyType.equals("ONE_WAY", ignoreCase = true)) continue

            val priceSpec = item.optJSONObject("priceSpecification") ?: continue
            val currency = priceSpec.optString("currencyCode", "MXN")
            if (currency.isNotEmpty() && !currency.equals("MXN", ignoreCase = true)) continue

            val rawPrice = priceSpec.optDouble("totalPrice", Double.NaN)
            if (rawPrice.isNaN() || rawPrice <= 0.0) continue
            val priceMxn = ceil(rawPrice).toInt()

            val outbound = item.optJSONObject("outboundFlight") ?: continue
            val origin = normalizeCode(outbound.optString("departureAirportIataCode", "")) ?: continue
            val dest = normalizeCode(outbound.optString("arrivalAirportIataCode", "")) ?: continue
            val depDate = TravelDate.parseIso(item.optString("departureDate", "")) ?: continue

            val airline = resolveAirline(item.optJSONObject("airline")?.optString("iataCode"), defaultAirline)
            val lastSeen = parseLastSeen(item.optJSONObject("lastSeen"))

            putIfCheaper(
                out,
                ScrapedFare(
                    airline = airline,
                    originCode = origin,
                    destinationCode = dest,
                    departureDate = depDate,
                    baseFareMxn = priceMxn,
                    lastSeenLabel = lastSeen,
                    sourceUrl = sourceUrl
                )
            )
        }
    }

    private fun parseLastSeen(lastSeenObj: JSONObject?): String? {
        if (lastSeenObj == null) return null
        val value = lastSeenObj.optInt("value", -1)
        val unit = lastSeenObj.optString("unit", "")
        if (value <= 0 || unit.isBlank()) return null
        val unitEs = when (unit.lowercase()) {
            "minute" -> if (value == 1) "hace 1 min" else "hace $value min"
            "hour" -> if (value == 1) "hace 1 h" else "hace $value h"
            "day" -> if (value == 1) "hace 1 día" else "hace $value días"
            else -> null
        }
        return unitEs
    }

    private fun resolveAirline(iata: String?, fallback: Airline): Airline {
        return when (iata?.uppercase()) {
            "Y4" -> Airline.VOLARIS
            "VB" -> Airline.VIVA_AEROBUS
            "AM" -> Airline.AEROMEXICO
            "MX" -> Airline.MEXICANA
            else -> fallback
        }
    }

    private fun normalizeCode(raw: String): String? {
        val clean = raw.trim().uppercase()
        if (clean.length != 3) return null
        // Map Chicago Midway (MDW) -> ORD for UI airport matching when Volaris lists Chicago Midway
        val mapped = if (clean == "MDW") "ORD" else clean
        return if (AirportData.findByCode(mapped) != null) mapped else null
    }

    private fun putIfCheaper(out: MutableMap<String, ScrapedFare>, fare: ScrapedFare) {
        val key = "${fare.airline.iataCode}_${fare.originCode}_${fare.destinationCode}_${fare.departureDate.isoString}"
        val existing = out[key]
        if (existing == null || fare.baseFareMxn < existing.baseFareMxn) {
            out[key] = fare
        }
    }
}

class LiveAirlineScraper {

    private data class CacheEntry(
        val fetchedAtMs: Long,
        val fares: List<ScrapedFare>
    )

    private val pageCache = ConcurrentHashMap<String, CacheEntry>()

    suspend fun fetchGlobalPromos(forceRefresh: Boolean = false): List<ScrapedFare> = coroutineScope {
        val tasks = listOf(
            async { fetchPage(VOLARIS_OFFERS_URL, Airline.VOLARIS, forceRefresh) },
            async { fetchPage(VIVA_MAIN_FLIGHTS_URL, Airline.VIVA_AEROBUS, forceRefresh) }
        )
        tasks.awaitAll().flatten()
    }

    suspend fun fetchOriginDeals(
        originCode: String,
        forceRefresh: Boolean = false
    ): List<ScrapedFare> = coroutineScope {
        val o = originCode.uppercase()
        val requests = mutableListOf<Pair<String, Airline>>()

        AirTrfxCitySlugs.volarisOriginUrl(o)?.let { requests.add(it to Airline.VOLARIS) }
        AirTrfxCitySlugs.vivaOriginUrl(o)?.let { requests.add(it to Airline.VIVA_AEROBUS) }

        // Also fetch top Volaris 30-day route histograms for key beach/hub routes from this origin
        val topDests = when (o) {
            "AGU" -> listOf("PVR", "CUN", "TIJ")
            "NLU" -> listOf("CUN", "PVR", "GDL")
            "MEX" -> listOf("CUN", "PVR", "GDL")
            "GDL" -> listOf("PVR", "CUN", "MTY")
            "MTY" -> listOf("CUN", "PVR", "GDL")
            else -> listOf("CUN", "PVR", "MEX")
        }.filter { it != o }

        topDests.forEach { d ->
            AirTrfxCitySlugs.volarisRouteUrl(o, d)?.let { requests.add(it to Airline.VOLARIS) }
        }

        requests.distinctBy { it.first }
            .map { (url, airline) -> async { fetchPage(url, airline, forceRefresh) } }
            .awaitAll()
            .flatten()
    }

    suspend fun fetchRouteHistogram(
        originCode: String,
        destinationCode: String,
        forceRefresh: Boolean = false
    ): List<ScrapedFare> = coroutineScope {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        val requests = buildList {
            AirTrfxCitySlugs.volarisRouteUrl(o, d)?.let { add(it to Airline.VOLARIS) }
            AirTrfxCitySlugs.vivaRouteUrl(o, d)?.let { add(it to Airline.VIVA_AEROBUS) }
        }
        requests
            .map { (url, airline) -> async { fetchPage(url, airline, forceRefresh) } }
            .awaitAll()
            .flatten()
    }

    private suspend fun fetchPage(
        url: String,
        airline: Airline,
        forceRefresh: Boolean
    ): List<ScrapedFare> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (!forceRefresh) {
            val cached = pageCache[url]
            if (cached != null && (now - cached.fetchedAtMs) < CACHE_TTL_MS) {
                return@withContext cached.fares
            }
        }

        val html = downloadHtml(url) ?: return@withContext emptyList()
        val parsed = AirTrfxParser.parseFaresFromHtml(html, airline, sourceUrl = url)
        if (parsed.isNotEmpty()) {
            pageCache[url] = CacheEntry(now, parsed)
        }
        parsed
    }

    private fun downloadHtml(urlString: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8_000
                readTimeout = 10_000
                instanceFollowRedirects = true
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
                )
                setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.8")
                setRequestProperty("Accept-Encoding", "gzip")
            }
            if (conn.responseCode !in 200..299) return null
            val rawStream = conn.inputStream
            val stream = if ("gzip".equals(conn.contentEncoding, ignoreCase = true)) {
                GZIPInputStream(rawStream)
            } else {
                rawStream
            }
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    companion object {
        const val VOLARIS_OFFERS_URL = "https://www.volaris.com/es-mx/vuelos-en-oferta"
        const val VIVA_MAIN_FLIGHTS_URL = "https://www.vivaaerobus.com/es-mx/vuelos"
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }
}
