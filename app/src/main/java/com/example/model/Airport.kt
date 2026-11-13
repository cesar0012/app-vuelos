package com.example.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class AirportCategory(val label: String) {
    ALL("Todos"),
    MEXICO("México"),
    BEACH("Playas"),
    INTERNATIONAL("Internacional")
}

data class Airport(
    val code: String,
    val name: String,
    val city: String,
    val stateOrCountry: String,
    val latitude: Double,
    val longitude: Double,
    val domesticTuaMxn: Int,
    val internationalTuaMxn: Int,
    val isMexican: Boolean,
    val isBeach: Boolean,
    val highlightTag: String? = null
) {
    /**
     * Calculates great-circle distance in kilometers using the Haversine formula.
     */
    fun distanceKmTo(lat: Double, lon: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat - latitude)
        val dLon = Math.toRadians(lon - longitude)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(latitude)) * cos(Math.toRadians(lat)) *
            sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    fun tuaForFlight(isInternationalRoute: Boolean): Int {
        return if (isInternationalRoute) internationalTuaMxn else domesticTuaMxn
    }
}

object AirportData {
    val airports: List<Airport> = listOf(
        // Aeropuertos Mexicanos Principales y Regionales
        Airport(
            code = "NLU",
            name = "Aeropuerto Internacional Felipe Ángeles (AIFA)",
            city = "CDMX / AIFA",
            stateOrCountry = "Estado de México",
            latitude = 19.7575,
            longitude = -99.0156,
            domesticTuaMxn = 309,
            internationalTuaMxn = 615,
            isMexican = true,
            isBeach = false,
            highlightTag = "TUA Ultra Baja (-46% vs AICM)"
        ),
        Airport(
            code = "MEX",
            name = "Aeropuerto Internacional Benito Juárez (AICM)",
            city = "Ciudad de México",
            stateOrCountry = "CDMX",
            latitude = 19.4363,
            longitude = -99.0721,
            domesticTuaMxn = 570,
            internationalTuaMxn = 1095,
            isMexican = true,
            isBeach = false,
            highlightTag = "Mayor conectividad"
        ),
        Airport(
            code = "AGU",
            name = "Aeropuerto Internacional Lic. Jesús Terán Peredo",
            city = "Aguascalientes",
            stateOrCountry = "Aguascalientes",
            latitude = 21.7056,
            longitude = -102.3180,
            domesticTuaMxn = 528,
            internationalTuaMxn = 995,
            isMexican = true,
            isBeach = false,
            highlightTag = "Hub Bajío $199"
        ),
        Airport(
            code = "GDL",
            name = "Aeropuerto Internacional Miguel Hidalgo y Costilla",
            city = "Guadalajara",
            stateOrCountry = "Jalisco",
            latitude = 20.5218,
            longitude = -103.3110,
            domesticTuaMxn = 535,
            internationalTuaMxn = 1045,
            isMexican = true,
            isBeach = false
        ),
        Airport(
            code = "MTY",
            name = "Aeropuerto Internacional Mariano Escobedo",
            city = "Monterrey",
            stateOrCountry = "Nuevo León",
            latitude = 25.7785,
            longitude = -100.1070,
            domesticTuaMxn = 578,
            internationalTuaMxn = 1120,
            isMexican = true,
            isBeach = false
        ),
        Airport(
            code = "TIJ",
            name = "Aeropuerto Internacional Abelardo L. Rodríguez",
            city = "Tijuana",
            stateOrCountry = "Baja California",
            latitude = 32.5411,
            longitude = -116.9700,
            domesticTuaMxn = 516,
            internationalTuaMxn = 980,
            isMexican = true,
            isBeach = false,
            highlightTag = "Cruce CBX San Diego"
        ),
        Airport(
            code = "CUN",
            name = "Aeropuerto Internacional de Cancún",
            city = "Cancún",
            stateOrCountry = "Quintana Roo",
            latitude = 21.0365,
            longitude = -86.8771,
            domesticTuaMxn = 488,
            internationalTuaMxn = 990,
            isMexican = true,
            isBeach = true,
            highlightTag = "Caribe Top"
        ),
        Airport(
            code = "PVR",
            name = "Aeropuerto Internacional Gustavo Díaz Ordaz",
            city = "Puerto Vallarta",
            stateOrCountry = "Jalisco",
            latitude = 20.6801,
            longitude = -105.2540,
            domesticTuaMxn = 470,
            internationalTuaMxn = 960,
            isMexican = true,
            isBeach = true,
            highlightTag = "TUA Accesible"
        ),
        Airport(
            code = "SJD",
            name = "Aeropuerto Internacional de Los Cabos",
            city = "Los Cabos",
            stateOrCountry = "Baja California Sur",
            latitude = 23.1518,
            longitude = -109.7210,
            domesticTuaMxn = 436,
            internationalTuaMxn = 940,
            isMexican = true,
            isBeach = true
        ),
        Airport(
            code = "TQO",
            name = "Aeropuerto Internacional Felipe Carrillo Puerto",
            city = "Tulum",
            stateOrCountry = "Quintana Roo",
            latitude = 20.1419,
            longitude = -87.6744,
            domesticTuaMxn = 395,
            internationalTuaMxn = 860,
            isMexican = true,
            isBeach = true,
            highlightTag = "TUA Baja Caribe"
        ),
        Airport(
            code = "MID",
            name = "Aeropuerto Internacional Manuel Crescencio Rejón",
            city = "Mérida",
            stateOrCountry = "Yucatán",
            latitude = 20.9370,
            longitude = -89.6577,
            domesticTuaMxn = 585,
            internationalTuaMxn = 1080,
            isMexican = true,
            isBeach = false
        ),
        Airport(
            code = "MZT",
            name = "Aeropuerto Internacional Rafael Buelna",
            city = "Mazatlán",
            stateOrCountry = "Sinaloa",
            latitude = 23.1614,
            longitude = -106.2660,
            domesticTuaMxn = 542,
            internationalTuaMxn = 1020,
            isMexican = true,
            isBeach = true
        ),
        Airport(
            code = "OAX",
            name = "Aeropuerto Internacional Xoxocotlán",
            city = "Oaxaca",
            stateOrCountry = "Oaxaca",
            latitude = 16.9999,
            longitude = -96.7266,
            domesticTuaMxn = 560,
            internationalTuaMxn = 1040,
            isMexican = true,
            isBeach = false
        ),
        Airport(
            code = "HUX",
            name = "Aeropuerto Internacional Bahías de Huatulco",
            city = "Huatulco",
            stateOrCountry = "Oaxaca",
            latitude = 15.7753,
            longitude = -96.2626,
            domesticTuaMxn = 568,
            internationalTuaMxn = 1050,
            isMexican = true,
            isBeach = true
        ),
        Airport(
            code = "LAP",
            name = "Aeropuerto Internacional Manuel Márquez de León",
            city = "La Paz",
            stateOrCountry = "Baja California Sur",
            latitude = 24.0727,
            longitude = -110.3620,
            domesticTuaMxn = 495,
            internationalTuaMxn = 930,
            isMexican = true,
            isBeach = true
        ),
        Airport(
            code = "CZM",
            name = "Aeropuerto Internacional de Cozumel",
            city = "Cozumel",
            stateOrCountry = "Quintana Roo",
            latitude = 20.5224,
            longitude = -86.9256,
            domesticTuaMxn = 442,
            internationalTuaMxn = 910,
            isMexican = true,
            isBeach = true
        ),
        Airport(
            code = "TGZ",
            name = "Aeropuerto Internacional Ángel Albino Corzo",
            city = "Tuxtla Gutiérrez",
            stateOrCountry = "Chiapas",
            latitude = 16.5618,
            longitude = -93.0261,
            domesticTuaMxn = 548,
            internationalTuaMxn = 990,
            isMexican = true,
            isBeach = false
        ),
        Airport(
            code = "BJX",
            name = "Aeropuerto Internacional del Bajío",
            city = "León / Bajío",
            stateOrCountry = "Guanajuato",
            latitude = 20.9935,
            longitude = -101.4810,
            domesticTuaMxn = 532,
            internationalTuaMxn = 1010,
            isMexican = true,
            isBeach = false
        ),
        Airport(
            code = "QRO",
            name = "Aeropuerto Intercontinental de Querétaro",
            city = "Querétaro",
            stateOrCountry = "Querétaro",
            latitude = 20.6173,
            longitude = -100.1860,
            domesticTuaMxn = 425,
            internationalTuaMxn = 890,
            isMexican = true,
            isBeach = false,
            highlightTag = "TUA Baja Centro"
        ),
        Airport(
            code = "ZIH",
            name = "Aeropuerto Internacional de Ixtapa-Zihuatanejo",
            city = "Zihuatanejo",
            stateOrCountry = "Guerrero",
            latitude = 17.6016,
            longitude = -101.4610,
            domesticTuaMxn = 518,
            internationalTuaMxn = 980,
            isMexican = true,
            isBeach = true
        ),

        // Destinos Internacionales
        Airport(
            code = "LAX",
            name = "Los Angeles International Airport",
            city = "Los Ángeles",
            stateOrCountry = "Estados Unidos",
            latitude = 33.9416,
            longitude = -118.4085,
            domesticTuaMxn = 0,
            internationalTuaMxn = 780,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "ORD",
            name = "O'Hare International Airport",
            city = "Chicago",
            stateOrCountry = "Estados Unidos",
            latitude = 41.9742,
            longitude = -87.9073,
            domesticTuaMxn = 0,
            internationalTuaMxn = 790,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "MIA",
            name = "Miami International Airport",
            city = "Miami",
            stateOrCountry = "Estados Unidos",
            latitude = 25.7959,
            longitude = -80.2870,
            domesticTuaMxn = 0,
            internationalTuaMxn = 770,
            isMexican = false,
            isBeach = true
        ),
        Airport(
            code = "IAH",
            name = "George Bush Intercontinental",
            city = "Houston",
            stateOrCountry = "Estados Unidos",
            latitude = 29.9902,
            longitude = -95.3368,
            domesticTuaMxn = 0,
            internationalTuaMxn = 740,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "JFK",
            name = "John F. Kennedy International",
            city = "Nueva York",
            stateOrCountry = "Estados Unidos",
            latitude = 40.6413,
            longitude = -73.7781,
            domesticTuaMxn = 0,
            internationalTuaMxn = 820,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "LAS",
            name = "Harry Reid International Airport",
            city = "Las Vegas",
            stateOrCountry = "Estados Unidos",
            latitude = 36.0840,
            longitude = -115.1537,
            domesticTuaMxn = 0,
            internationalTuaMxn = 750,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "MCO",
            name = "Orlando International Airport",
            city = "Orlando",
            stateOrCountry = "Estados Unidos",
            latitude = 28.4312,
            longitude = -81.3081,
            domesticTuaMxn = 0,
            internationalTuaMxn = 760,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "DFW",
            name = "Dallas/Fort Worth International",
            city = "Dallas",
            stateOrCountry = "Estados Unidos",
            latitude = 32.8998,
            longitude = -97.0403,
            domesticTuaMxn = 0,
            internationalTuaMxn = 740,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "SFO",
            name = "San Francisco International",
            city = "San Francisco",
            stateOrCountry = "Estados Unidos",
            latitude = 37.6213,
            longitude = -122.3790,
            domesticTuaMxn = 0,
            internationalTuaMxn = 790,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "BOG",
            name = "Aeropuerto Internacional El Dorado",
            city = "Bogotá",
            stateOrCountry = "Colombia",
            latitude = 4.7016,
            longitude = -74.1469,
            domesticTuaMxn = 0,
            internationalTuaMxn = 690,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "MAD",
            name = "Adolfo Suárez Madrid-Barajas",
            city = "Madrid",
            stateOrCountry = "España",
            latitude = 40.4983,
            longitude = -3.5676,
            domesticTuaMxn = 0,
            internationalTuaMxn = 880,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "GUA",
            name = "Aeropuerto Internacional La Aurora",
            city = "Ciudad de Guatemala",
            stateOrCountry = "Guatemala",
            latitude = 14.5833,
            longitude = -90.5275,
            domesticTuaMxn = 0,
            internationalTuaMxn = 610,
            isMexican = false,
            isBeach = false
        ),
        Airport(
            code = "SJO",
            name = "Aeropuerto Internacional Juan Santamaría",
            city = "San José",
            stateOrCountry = "Costa Rica",
            latitude = 9.9939,
            longitude = -84.2088,
            domesticTuaMxn = 0,
            internationalTuaMxn = 630,
            isMexican = false,
            isBeach = false
        )
    )

    private val byCode: Map<String, Airport> = airports.associateBy { it.code }

    val mexicanAirports: List<Airport> = airports.filter { it.isMexican }

    fun findByCode(code: String): Airport? = byCode[code.uppercase()]

    fun nearestMexicanAirport(latitude: Double, longitude: Double): Airport {
        return mexicanAirports.minByOrNull { it.distanceKmTo(latitude, longitude) }
            ?: findByCode("MEX")!!
    }
}
