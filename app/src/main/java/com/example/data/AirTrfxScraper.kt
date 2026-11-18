package com.example.data

import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.TravelDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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
    val tuaMxn: Int? = null,
    val lastSeenLabel: String? = null,
    val sourceUrl: String? = null
)

/**
 * Records the date range inspected in a live dailyHistogram for an (airline, origin, destination).
 * Days inside this window that do NOT have a ScrapedFare (because AirTrfx returned totalPrice == 0)
 * are confirmed unavailable/without promo so the repository can suppress phantom catalog estimates.
 */
data class RouteHistogramWindow(
    val airline: Airline,
    val originCode: String,
    val destinationCode: String,
    val minDate: TravelDate,
    val maxDate: TravelDate
)

data class ScrapedPageResult(
    val fares: List<ScrapedFare> = emptyList(),
    val histogramWindows: List<RouteHistogramWindow> = emptyList()
)

object AirTrfxCitySlugs {
    // 100% aeropuertos nacionales de México
    val volarisSlugs: Map<String, String> = mapOf(
        "NLU" to "felipe-%C3%A1ngeles-cdmx",
        "MEX" to "mexico-df",
        "TLC" to "toluca",
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
        "PXM" to "puerto-escondido",
        "ACA" to "acapulco",
        "LAP" to "la-paz",
        "CZM" to "cozumel",
        "ZLO" to "manzanillo",
        "LTO" to "loreto",
        "CTM" to "chetumal",
        "VER" to "veracruz",
        "TGZ" to "tuxtla-gutierrez",
        "BJX" to "leon",
        "QRO" to "queretaro",
        "ZIH" to "ixtapa-zihuatanejo",
        "PBC" to "puebla",
        "MLM" to "morelia",
        "SLP" to "san-luis-potosi",
        "HMO" to "hermosillo",
        "CUL" to "culiacan",
        "CUU" to "chihuahua",
        "CJS" to "ciudad-juarez",
        "VSA" to "villahermosa",
        "TAM" to "tampico",
        "TRC" to "torreon",
        "LMM" to "los-mochis",
        "DGO" to "durango",
        "ZCL" to "zacatecas"
    )

    val vivaSlugs: Map<String, String> = mapOf(
        "NLU" to "felipe-angeles-cdmx",
        "MEX" to "ciudad-de-mexico",
        "TLC" to "toluca",
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
        "PXM" to "puerto-escondido",
        "ACA" to "acapulco",
        "LAP" to "la-paz",
        "CZM" to "cozumel",
        "ZLO" to "manzanillo",
        "LTO" to "loreto",
        "CTM" to "chetumal",
        "VER" to "veracruz",
        "TGZ" to "tuxtla-gutierrez",
        "BJX" to "leon",
        "QRO" to "queretaro",
        "ZIH" to "ixtapa-zihuatanejo",
        "PBC" to "puebla",
        "MLM" to "morelia",
        "SLP" to "san-luis-potosi",
        "HMO" to "hermosillo",
        "CUL" to "culiacan",
        "CUU" to "chihuahua",
        "CJS" to "ciudad-juarez",
        "VSA" to "villahermosa",
        "TAM" to "tampico",
        "TRC" to "torreon",
        "LMM" to "los-mochis",
        "DGO" to "durango",
        "ZCL" to "zacatecas"
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
    fun extractNextDataJson(html: String): String? {
        val markerIdx = html.indexOf("id=\"__NEXT_DATA__\"")
        if (markerIdx < 0) return null
        val tagEnd = html.indexOf('>', startIndex = markerIdx)
        if (tagEnd < 0) return null
        val closeIdx = html.indexOf("</script>", startIndex = tagEnd + 1, ignoreCase = true)
        if (closeIdx < 0 || closeIdx <= tagEnd + 1) return null
        return html.substring(tagEnd + 1, closeIdx)
    }

    fun parseFaresFromHtml(
        html: String,
        defaultAirline: Airline,
        sourceUrl: String? = null
    ): List<ScrapedFare> = parsePageResultFromHtml(html, defaultAirline, sourceUrl).fares

    fun parsePageResultFromHtml(
        html: String,
        defaultAirline: Airline,
        sourceUrl: String? = null
    ): ScrapedPageResult {
        val jsonText = extractNextDataJson(html) ?: return ScrapedPageResult()
        return parsePageResultFromNextDataJson(jsonText, defaultAirline, sourceUrl)
    }

    fun parseFaresFromNextDataJson(
        jsonText: String,
        defaultAirline: Airline,
        sourceUrl: String? = null
    ): List<ScrapedFare> = parsePageResultFromNextDataJson(jsonText, defaultAirline, sourceUrl).fares

    fun parsePageResultFromNextDataJson(
        jsonText: String,
        defaultAirline: Airline,
        sourceUrl: String? = null
    ): ScrapedPageResult {
        val collected = LinkedHashMap<String, ScrapedFare>()
        val histogramDateRanges = LinkedHashMap<String, Pair<TravelDate, TravelDate>>()
        val histogramMeta = HashMap<String, Triple<Airline, String, String>>()
        try {
            val root = JSONObject(jsonText)
            val apolloData = root
                .optJSONObject("props")
                ?.optJSONObject("pageProps")
                ?.optJSONObject("apolloState")
                ?.optJSONObject("data")
                ?: return ScrapedPageResult()

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
                            parseHistogramArray(
                                arr = it,
                                defaultAirline = defaultAirline,
                                sourceUrl = sourceUrl,
                                out = collected,
                                recordWindow = true,
                                histogramDateRanges = histogramDateRanges,
                                histogramMeta = histogramMeta
                            )
                        }
                        initState.optJSONArray("monthlyHistogram")?.let {
                            parseHistogramArray(
                                arr = it,
                                defaultAirline = defaultAirline,
                                sourceUrl = sourceUrl,
                                out = collected,
                                recordWindow = false,
                                histogramDateRanges = histogramDateRanges,
                                histogramMeta = histogramMeta
                            )
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

        // Propagate route-level TUA (e.g. Viva Aerobus additionalFeesAmount / formattedAdditionalFeesAmount)
        val routeTuaMap = HashMap<String, Int>()
        val originTuaMap = HashMap<String, Int>()
        for (fare in collected.values) {
            if (fare.tuaMxn != null) {
                val rKey = "${fare.airline.iataCode}_${fare.originCode}_${fare.destinationCode}"
                routeTuaMap.putIfAbsent(rKey, fare.tuaMxn)
                originTuaMap.putIfAbsent("${fare.airline.iataCode}_${fare.originCode}", fare.tuaMxn)
            }
        }
        val finalFares = collected.values.map { fare ->
            if (fare.tuaMxn != null) {
                fare
            } else {
                val rKey = "${fare.airline.iataCode}_${fare.originCode}_${fare.destinationCode}"
                val resolved = routeTuaMap[rKey] ?: originTuaMap["${fare.airline.iataCode}_${fare.originCode}"]
                if (resolved != null) fare.copy(tuaMxn = resolved) else fare
            }
        }

        val windows = histogramDateRanges.mapNotNull { (routeKey, range) ->
            val meta = histogramMeta[routeKey] ?: return@mapNotNull null
            RouteHistogramWindow(
                airline = meta.first,
                originCode = meta.second,
                destinationCode = meta.third,
                minDate = range.first,
                maxDate = range.second
            )
        }

        return ScrapedPageResult(fares = finalFares, histogramWindows = windows)
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
        if (flightType.equals("INTERNATIONAL", ignoreCase = true)) return
        if (flightType.isNotEmpty() &&
            !flightType.equals("ONE_WAY", ignoreCase = true) &&
            !flightType.equals("DOMESTIC", ignoreCase = true)
        ) return

        val journeyType = item.optString("journeyType", "")
        if (journeyType.isNotEmpty() && !journeyType.equals("ONE_WAY", ignoreCase = true)) return

        val currency = item.optString("currencyCode", "MXN")
        if (currency.isNotEmpty() && !currency.equals("MXN", ignoreCase = true)) return

        val origin = normalizeDomesticCode(item.optString("originAirportCode", "")) ?: return
        val dest = normalizeDomesticCode(item.optString("destinationAirportCode", "")) ?: return
        if (origin == dest) return
        val depDate = TravelDate.parseIso(item.optString("departureDate", "")) ?: return

        val rawPrice = item.optDouble("totalPrice", Double.NaN)
        if (rawPrice.isNaN() || rawPrice <= 0.0) return
        val priceMxn = ceil(rawPrice).toInt()

        val feesMxn = extractFeeMxn(item)
        val airline = resolveAirline(item.optJSONObject("airline")?.optString("iataCode"), defaultAirline)
        val lastSeen = parseLastSeen(item.optJSONObject("lastSeen") ?: item.optJSONObject("priceLastSeen"))

        putIfCheaper(
            out,
            ScrapedFare(
                airline = airline,
                originCode = origin,
                destinationCode = dest,
                departureDate = depDate,
                baseFareMxn = priceMxn,
                tuaMxn = feesMxn,
                lastSeenLabel = lastSeen,
                sourceUrl = sourceUrl
            )
        )
    }

    private fun parseHistogramArray(
        arr: JSONArray,
        defaultAirline: Airline,
        sourceUrl: String?,
        out: MutableMap<String, ScrapedFare>,
        recordWindow: Boolean,
        histogramDateRanges: MutableMap<String, Pair<TravelDate, TravelDate>>,
        histogramMeta: MutableMap<String, Triple<Airline, String, String>>
    ) {
        for (i in 0 until arr.length()) {
            val item = arr.optJSONObject(i) ?: continue
            val journeyType = item.optString("journeyType", "")
            if (journeyType.isNotEmpty() && !journeyType.equals("ONE_WAY", ignoreCase = true)) continue

            val outbound = item.optJSONObject("outboundFlight") ?: continue
            val origin = normalizeDomesticCode(outbound.optString("departureAirportIataCode", "")) ?: continue
            val dest = normalizeDomesticCode(outbound.optString("arrivalAirportIataCode", "")) ?: continue
            if (origin == dest) continue
            val depDate = TravelDate.parseIso(item.optString("departureDate", "")) ?: continue
            val airline = resolveAirline(item.optJSONObject("airline")?.optString("iataCode"), defaultAirline)

            if (recordWindow) {
                val routeKey = "${airline.iataCode}_${origin}_${dest}"
                histogramMeta[routeKey] = Triple(airline, origin, dest)
                val existing = histogramDateRanges[routeKey]
                histogramDateRanges[routeKey] = if (existing == null) {
                    depDate to depDate
                } else {
                    minOf(existing.first, depDate) to maxOf(existing.second, depDate)
                }
            }

            val priceSpec = item.optJSONObject("priceSpecification") ?: continue
            val currency = priceSpec.optString("currencyCode", "MXN")
            if (currency.isNotEmpty() && !currency.equals("MXN", ignoreCase = true)) continue

            val rawPrice = priceSpec.optDouble("totalPrice", Double.NaN)
            if (rawPrice.isNaN() || rawPrice <= 0.0) continue
            val priceMxn = ceil(rawPrice).toInt()

            val feesMxn = extractFeeMxn(priceSpec) ?: extractFeeMxn(item)
            val lastSeen = parseLastSeen(item.optJSONObject("lastSeen") ?: item.optJSONObject("priceLastSeen"))

            putIfCheaper(
                out,
                ScrapedFare(
                    airline = airline,
                    originCode = origin,
                    destinationCode = dest,
                    departureDate = depDate,
                    baseFareMxn = priceMxn,
                    tuaMxn = feesMxn,
                    lastSeenLabel = lastSeen,
                    sourceUrl = sourceUrl
                )
            )
        }
    }

    internal fun extractFeeMxn(obj: JSONObject): Int? {
        val rawFees = obj.optDouble("additionalFeesAmount", Double.NaN)
        if (!rawFees.isNaN() && rawFees > 0.0) {
            return ceil(rawFees).toInt()
        }
        val formatted = obj.optString("formattedAdditionalFeesAmount", "")
        return parseFormattedFeeMxn(formatted)
    }

    internal fun parseFormattedFeeMxn(raw: String?): Int? {
        if (raw.isNullOrBlank()) return null
        // Examples from live Viva Aerobus JSON: "653MXN", "1,272MXN", "$653.40 MXN"
        val cleaned = raw.replace(",", "").trim()
        val match = Regex("""(\d+(?:\.\d+)?)""").find(cleaned) ?: return null
        val value = match.groupValues[1].toDoubleOrNull() ?: return null
        return if (value > 0.0) ceil(value).toInt() else null
    }

    private fun parseLastSeen(lastSeenObj: JSONObject?): String? {
        if (lastSeenObj == null) return null
        val rawInt = lastSeenObj.optInt("value", -1)
        val value = if (rawInt > 0) {
            rawInt
        } else {
            lastSeenObj.optString("value", "").trim().toIntOrNull() ?: -1
        }
        val unit = lastSeenObj.optString("unit", "").trim().lowercase()
        if (value <= 0 || unit.isBlank()) return null
        return when {
            unit.startsWith("min") -> if (value == 1) "hace 1 min" else "hace $value min"
            unit.startsWith("hour") || unit.startsWith("hora") -> if (value == 1) "hace 1 h" else "hace $value h"
            unit.startsWith("day") || unit.startsWith("d") -> if (value == 1) "hace 1 día" else "hace $value días"
            else -> null
        }
    }

    private fun resolveAirline(iata: String?, fallback: Airline): Airline {
        return when (iata?.uppercase()) {
            "Y4" -> Airline.VOLARIS
            "VB" -> Airline.VIVA_AEROBUS
            "AM" -> Airline.AEROMEXICO
            "MX" -> Airline.MEXICANA
            "YQ" -> Airline.TAR
            "ZV" -> Airline.AERUS
            "UJ" -> Airline.MAGNICHARTERS
            else -> fallback
        }
    }

    private fun normalizeDomesticCode(raw: String): String? {
        val clean = raw.trim().uppercase()
        if (clean.length != 3) return null
        return if (AirportData.isDomesticMexicanAirport(clean)) clean else null
    }

    private fun putIfCheaper(out: MutableMap<String, ScrapedFare>, fare: ScrapedFare) {
        val key = "${fare.airline.iataCode}_${fare.originCode}_${fare.destinationCode}_${fare.departureDate.isoString}"
        val existing = out[key]
        if (existing == null || fare.baseFareMxn < existing.baseFareMxn) {
            out[key] = if (fare.tuaMxn == null && existing?.tuaMxn != null) {
                fare.copy(tuaMxn = existing.tuaMxn)
            } else {
                fare
            }
        } else if (fare.baseFareMxn == existing.baseFareMxn && existing.tuaMxn == null && fare.tuaMxn != null) {
            out[key] = fare
        }
    }
}

class LiveAirlineScraper {

    private data class CacheEntry(
        val fetchedAtMs: Long,
        val result: ScrapedPageResult
    )

    private val pageCache = ConcurrentHashMap<String, CacheEntry>()
    private val fetchSemaphore = Semaphore(4)

    suspend fun fetchGlobalPromos(forceRefresh: Boolean = false): List<ScrapedFare> =
        fetchGlobalPromosResult(forceRefresh).fares

    suspend fun fetchGlobalPromosResult(forceRefresh: Boolean = false): ScrapedPageResult = coroutineScope {
        val tasks = listOf(
            async { fetchPage(VOLARIS_OFFERS_URL, Airline.VOLARIS, forceRefresh) },
            async { fetchPage(VIVA_MAIN_FLIGHTS_URL, Airline.VIVA_AEROBUS, forceRefresh) }
        )
        mergePageResults(tasks.awaitAll())
    }

    suspend fun fetchOriginDeals(
        originCode: String,
        forceRefresh: Boolean = false
    ): List<ScrapedFare> = fetchOriginDealsResult(originCode, forceRefresh).fares

    suspend fun fetchOriginDealsResult(
        originCode: String,
        forceRefresh: Boolean = false
    ): ScrapedPageResult = coroutineScope {
        val o = originCode.uppercase()
        if (!AirportData.isDomesticMexicanAirport(o)) return@coroutineScope ScrapedPageResult()

        val requests = mutableListOf<Pair<String, Airline>>()
        AirTrfxCitySlugs.volarisOriginUrl(o)?.let { requests.add(it to Airline.VOLARIS) }
        AirTrfxCitySlugs.vivaOriginUrl(o)?.let { requests.add(it to Airline.VIVA_AEROBUS) }

        val topDests = when (o) {
            "AGU" -> listOf("PVR", "CUN", "TIJ", "MTY", "MEX")
            "NLU" -> listOf("CUN", "PVR", "SJD", "TQO", "MTY", "GDL")
            "MEX" -> listOf("CUN", "PVR", "SJD", "HUX", "MTY", "GDL")
            "GDL" -> listOf("PVR", "CUN", "SJD", "MTY", "TIJ")
            "MTY" -> listOf("CUN", "PVR", "SJD", "MZT", "MEX")
            else -> listOf("CUN", "PVR", "SJD", "MEX", "MTY")
        }.filter { it != o }

        topDests.forEach { d ->
            AirTrfxCitySlugs.volarisRouteUrl(o, d)?.let { requests.add(it to Airline.VOLARIS) }
            AirTrfxCitySlugs.vivaRouteUrl(o, d)?.let { requests.add(it to Airline.VIVA_AEROBUS) }
        }

        val results = requests.distinctBy { it.first }
            .map { (url, airline) -> async { fetchPage(url, airline, forceRefresh) } }
            .awaitAll()
        mergePageResults(results)
    }

    suspend fun fetchRouteHistogram(
        originCode: String,
        destinationCode: String,
        forceRefresh: Boolean = false
    ): List<ScrapedFare> = fetchRouteHistogramResult(originCode, destinationCode, forceRefresh).fares

    suspend fun fetchRouteHistogramResult(
        originCode: String,
        destinationCode: String,
        forceRefresh: Boolean = false
    ): ScrapedPageResult = coroutineScope {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        if (!AirportData.isDomesticMexicanAirport(o) || !AirportData.isDomesticMexicanAirport(d)) {
            return@coroutineScope ScrapedPageResult()
        }
        val requests = buildList {
            AirTrfxCitySlugs.volarisRouteUrl(o, d)?.let { add(it to Airline.VOLARIS) }
            AirTrfxCitySlugs.vivaRouteUrl(o, d)?.let { add(it to Airline.VIVA_AEROBUS) }
        }
        val results = requests
            .map { (url, airline) -> async { fetchPage(url, airline, forceRefresh) } }
            .awaitAll()
        mergePageResults(results)
    }

    private fun mergePageResults(results: List<ScrapedPageResult>): ScrapedPageResult {
        val allFares = results.flatMap { it.fares }
        val allWindows = results.flatMap { it.histogramWindows }
        return ScrapedPageResult(fares = allFares, histogramWindows = allWindows)
    }

    private suspend fun fetchPage(
        url: String,
        airline: Airline,
        forceRefresh: Boolean
    ): ScrapedPageResult = fetchSemaphore.withPermit {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            if (!forceRefresh) {
                val cached = pageCache[url]
                if (cached != null && (now - cached.fetchedAtMs) < CACHE_TTL_MS) {
                    return@withContext cached.result
                }
            }

            val jsonText = downloadNextDataJson(url) ?: return@withContext ScrapedPageResult()
            val parsed = AirTrfxParser.parsePageResultFromNextDataJson(jsonText, airline, sourceUrl = url)
            if (parsed.fares.isNotEmpty() || parsed.histogramWindows.isNotEmpty()) {
                pageCache[url] = CacheEntry(now, parsed)
            }
            parsed
        }
    }

    private fun downloadNextDataJson(urlString: String): String? {
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
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8), 16 * 1024).use { reader ->
                extractNextDataFromReader(reader)
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun extractNextDataFromReader(reader: BufferedReader): String? {
        val marker = "id=\"__NEXT_DATA__\""
        val closeTag = "</script>"
        val rolling = StringBuilder(4096)
        val jsonBuffer = StringBuilder(64 * 1024)
        val charBuf = CharArray(8192)
        var state = 0 // 0: seeking marker, 1: seeking '>', 2: capturing json
        var totalCharsRead = 0

        while (totalCharsRead < MAX_HTML_SCAN_CHARS) {
            val read = reader.read(charBuf)
            if (read <= 0) break
            totalCharsRead += read

            if (state < 2) {
                rolling.append(charBuf, 0, read)
                if (state == 0) {
                    val mIdx = rolling.indexOf(marker)
                    if (mIdx >= 0) {
                        rolling.delete(0, mIdx + marker.length)
                        state = 1
                    } else if (rolling.length > marker.length) {
                        rolling.delete(0, rolling.length - marker.length)
                    }
                }
                if (state == 1) {
                    val gtIdx = rolling.indexOf(">")
                    if (gtIdx >= 0) {
                        val remainder = rolling.substring(gtIdx + 1)
                        rolling.setLength(0)
                        jsonBuffer.append(remainder)
                        state = 2
                    } else if (rolling.length > 512) {
                        return null
                    }
                }
            } else {
                jsonBuffer.append(charBuf, 0, read)
            }

            if (state == 2) {
                val searchStart = (jsonBuffer.length - read - closeTag.length).coerceAtLeast(0)
                val endIdx = jsonBuffer.indexOf(closeTag, startIndex = searchStart, ignoreCase = true)
                if (endIdx >= 0) {
                    return jsonBuffer.substring(0, endIdx)
                }
                if (jsonBuffer.length > MAX_NEXT_DATA_CHARS) {
                    return null
                }
            }
        }
        return null
    }

    companion object {
        const val VOLARIS_OFFERS_URL = "https://www.volaris.com/es-mx/vuelos-en-oferta"
        const val VIVA_MAIN_FLIGHTS_URL = "https://www.vivaaerobus.com/es-mx/vuelos"
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
        private const val MAX_HTML_SCAN_CHARS = 6 * 1024 * 1024
        private const val MAX_NEXT_DATA_CHARS = 3 * 1024 * 1024
    }
}
