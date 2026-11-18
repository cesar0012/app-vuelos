package com.example.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class AirportCategory(val label: String) {
    ALL("Todos MX"),
    BEACH("Playas"),
    HUBS("Hubs Principales"),
    REGIONAL("Regionales")
}

data class Airport(
    val code: String,
    val name: String,
    val city: String,
    val stateOrCountry: String,
    val latitude: Double,
    val longitude: Double,
    val domesticTuaMxn: Int,
    val internationalTuaMxn: Int = domesticTuaMxn,
    val isMexican: Boolean = true,
    val isBeach: Boolean = false,
    val isMajorHub: Boolean = false,
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

    fun tuaForFlight(isInternationalRoute: Boolean = false): Int {
        return if (isInternationalRoute) internationalTuaMxn else domesticTuaMxn
    }
}

object AirportData {
    // Catálogo 100% nacional de aeropuertos mexicanos con TUAs domésticas actualizadas (MXN)
    val airports: List<Airport> = listOf(
        // Hubs principales de México
        Airport(
            code = "NLU",
            name = "Aeropuerto Internacional Felipe Ángeles (AIFA)",
            city = "CDMX / AIFA",
            stateOrCountry = "Estado de México",
            latitude = 19.7575,
            longitude = -99.0156,
            domesticTuaMxn = 309,
            isMajorHub = true,
            highlightTag = "TUA Ultra Baja (-52% vs AICM)"
        ),
        Airport(
            code = "MEX",
            name = "Aeropuerto Internacional Benito Juárez (AICM)",
            city = "Ciudad de México",
            stateOrCountry = "CDMX",
            latitude = 19.4363,
            longitude = -99.0721,
            domesticTuaMxn = 653,
            isMajorHub = true,
            highlightTag = "Mayor conectividad"
        ),
        Airport(
            code = "TLC",
            name = "Aeropuerto Internacional de Toluca",
            city = "Toluca",
            stateOrCountry = "Estado de México",
            latitude = 19.3371,
            longitude = -99.5660,
            domesticTuaMxn = 452,
            isMajorHub = true,
            highlightTag = "Alternativa CDMX TUA media"
        ),
        Airport(
            code = "GDL",
            name = "Aeropuerto Internacional Miguel Hidalgo y Costilla",
            city = "Guadalajara",
            stateOrCountry = "Jalisco",
            latitude = 20.5218,
            longitude = -103.3110,
            domesticTuaMxn = 665,
            isMajorHub = true
        ),
        Airport(
            code = "MTY",
            name = "Aeropuerto Internacional Mariano Escobedo",
            city = "Monterrey",
            stateOrCountry = "Nuevo León",
            latitude = 25.7785,
            longitude = -100.1070,
            domesticTuaMxn = 746,
            isMajorHub = true,
            highlightTag = "Hub Viva y Aerus"
        ),
        Airport(
            code = "TIJ",
            name = "Aeropuerto Internacional Abelardo L. Rodríguez",
            city = "Tijuana",
            stateOrCountry = "Baja California",
            latitude = 32.5411,
            longitude = -116.9700,
            domesticTuaMxn = 619,
            isMajorHub = true,
            highlightTag = "Hub Norte Volaris"
        ),
        Airport(
            code = "CUN",
            name = "Aeropuerto Internacional de Cancún",
            city = "Cancún",
            stateOrCountry = "Quintana Roo",
            latitude = 21.0365,
            longitude = -86.8771,
            domesticTuaMxn = 488,
            isBeach = true,
            isMajorHub = true,
            highlightTag = "Caribe Top"
        ),

        // Destinos Turísticos de Playa en México
        Airport(
            code = "PVR",
            name = "Aeropuerto Internacional Gustavo Díaz Ordaz",
            city = "Puerto Vallarta",
            stateOrCountry = "Jalisco",
            latitude = 20.6801,
            longitude = -105.2540,
            domesticTuaMxn = 530,
            isBeach = true,
            highlightTag = "Pacífico Top"
        ),
        Airport(
            code = "SJD",
            name = "Aeropuerto Internacional de Los Cabos",
            city = "Los Cabos",
            stateOrCountry = "Baja California Sur",
            latitude = 23.1518,
            longitude = -109.7210,
            domesticTuaMxn = 486,
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
            isBeach = true,
            highlightTag = "TUA Baja Caribe"
        ),
        Airport(
            code = "MZT",
            name = "Aeropuerto Internacional Rafael Buelna",
            city = "Mazatlán",
            stateOrCountry = "Sinaloa",
            latitude = 23.1614,
            longitude = -106.2660,
            domesticTuaMxn = 605,
            isBeach = true
        ),
        Airport(
            code = "HUX",
            name = "Aeropuerto Internacional Bahías de Huatulco",
            city = "Huatulco",
            stateOrCountry = "Oaxaca",
            latitude = 15.7753,
            longitude = -96.2626,
            domesticTuaMxn = 580,
            isBeach = true
        ),
        Airport(
            code = "PXM",
            name = "Aeropuerto Internacional de Puerto Escondido",
            city = "Puerto Escondido",
            stateOrCountry = "Oaxaca",
            latitude = 15.8769,
            longitude = -97.0891,
            domesticTuaMxn = 490,
            isBeach = true,
            highlightTag = "Surf y Playa Oaxaca"
        ),
        Airport(
            code = "ACA",
            name = "Aeropuerto Internacional General Juan N. Álvarez",
            city = "Acapulco",
            stateOrCountry = "Guerrero",
            latitude = 16.7571,
            longitude = -99.7540,
            domesticTuaMxn = 640,
            isBeach = true
        ),
        Airport(
            code = "ZIH",
            name = "Aeropuerto Internacional de Ixtapa-Zihuatanejo",
            city = "Zihuatanejo",
            stateOrCountry = "Guerrero",
            latitude = 17.6016,
            longitude = -101.4610,
            domesticTuaMxn = 610,
            isBeach = true
        ),
        Airport(
            code = "LAP",
            name = "Aeropuerto Internacional Manuel Márquez de León",
            city = "La Paz",
            stateOrCountry = "Baja California Sur",
            latitude = 24.0727,
            longitude = -110.3620,
            domesticTuaMxn = 540,
            isBeach = true
        ),
        Airport(
            code = "CZM",
            name = "Aeropuerto Internacional de Cozumel",
            city = "Cozumel",
            stateOrCountry = "Quintana Roo",
            latitude = 20.5224,
            longitude = -86.9256,
            domesticTuaMxn = 470,
            isBeach = true
        ),
        Airport(
            code = "ZLO",
            name = "Aeropuerto Internacional Playa de Oro",
            city = "Manzanillo",
            stateOrCountry = "Colima",
            latitude = 19.1448,
            longitude = -104.5586,
            domesticTuaMxn = 560,
            isBeach = true
        ),
        Airport(
            code = "LTO",
            name = "Aeropuerto Internacional de Loreto",
            city = "Loreto",
            stateOrCountry = "Baja California Sur",
            latitude = 25.9892,
            longitude = -111.3483,
            domesticTuaMxn = 490,
            isBeach = true
        ),
        Airport(
            code = "CTM",
            name = "Aeropuerto Internacional de Chetumal",
            city = "Chetumal / Bacalar",
            stateOrCountry = "Quintana Roo",
            latitude = 18.5047,
            longitude = -88.3268,
            domesticTuaMxn = 420,
            isBeach = true,
            highlightTag = "Puerta a Bacalar"
        ),
        Airport(
            code = "VER",
            name = "Aeropuerto Internacional Heriberto Jara",
            city = "Veracruz",
            stateOrCountry = "Veracruz",
            latitude = 19.1459,
            longitude = -96.1873,
            domesticTuaMxn = 560,
            isBeach = true
        ),

        // Ciudades Culturales, Bajío y Regionales de México
        Airport(
            code = "AGU",
            name = "Aeropuerto Internacional Lic. Jesús Terán Peredo",
            city = "Aguascalientes",
            stateOrCountry = "Aguascalientes",
            latitude = 21.7056,
            longitude = -102.3180,
            domesticTuaMxn = 476,
            highlightTag = "Hub Bajío"
        ),
        Airport(
            code = "BJX",
            name = "Aeropuerto Internacional del Bajío",
            city = "León / Bajío",
            stateOrCountry = "Guanajuato",
            latitude = 20.9935,
            longitude = -101.4810,
            domesticTuaMxn = 532
        ),
        Airport(
            code = "QRO",
            name = "Aeropuerto Intercontinental de Querétaro",
            city = "Querétaro",
            stateOrCountry = "Querétaro",
            latitude = 20.6173,
            longitude = -100.1860,
            domesticTuaMxn = 425,
            highlightTag = "Hub TAR · TUA Baja"
        ),
        Airport(
            code = "MID",
            name = "Aeropuerto Internacional Manuel Crescencio Rejón",
            city = "Mérida",
            stateOrCountry = "Yucatán",
            latitude = 20.9370,
            longitude = -89.6577,
            domesticTuaMxn = 610
        ),
        Airport(
            code = "OAX",
            name = "Aeropuerto Internacional Xoxocotlán",
            city = "Oaxaca",
            stateOrCountry = "Oaxaca",
            latitude = 16.9999,
            longitude = -96.7266,
            domesticTuaMxn = 580
        ),
        Airport(
            code = "TGZ",
            name = "Aeropuerto Internacional Ángel Albino Corzo",
            city = "Tuxtla Gutiérrez",
            stateOrCountry = "Chiapas",
            latitude = 16.5618,
            longitude = -93.0261,
            domesticTuaMxn = 548
        ),
        Airport(
            code = "PBC",
            name = "Aeropuerto Internacional Hermanos Serdán",
            city = "Puebla",
            stateOrCountry = "Puebla",
            latitude = 19.1581,
            longitude = -98.3714,
            domesticTuaMxn = 460
        ),
        Airport(
            code = "MLM",
            name = "Aeropuerto Internacional General Francisco Mujica",
            city = "Morelia",
            stateOrCountry = "Michoacán",
            latitude = 19.8499,
            longitude = -101.0255,
            domesticTuaMxn = 540
        ),
        Airport(
            code = "SLP",
            name = "Aeropuerto Internacional Ponciano Arriaga",
            city = "San Luis Potosí",
            stateOrCountry = "San Luis Potosí",
            latitude = 22.2543,
            longitude = -100.9308,
            domesticTuaMxn = 595
        ),
        Airport(
            code = "HMO",
            name = "Aeropuerto Internacional General Ignacio Pesqueira",
            city = "Hermosillo",
            stateOrCountry = "Sonora",
            latitude = 29.0959,
            longitude = -111.0479,
            domesticTuaMxn = 560
        ),
        Airport(
            code = "CUL",
            name = "Aeropuerto Internacional Federal de Culiacán",
            city = "Culiacán",
            stateOrCountry = "Sinaloa",
            latitude = 24.7645,
            longitude = -107.4747,
            domesticTuaMxn = 645
        ),
        Airport(
            code = "CUU",
            name = "Aeropuerto Internacional General Roberto Fierro",
            city = "Chihuahua",
            stateOrCountry = "Chihuahua",
            latitude = 28.7029,
            longitude = -105.9646,
            domesticTuaMxn = 630
        ),
        Airport(
            code = "CJS",
            name = "Aeropuerto Internacional Abraham González",
            city = "Ciudad Juárez",
            stateOrCountry = "Chihuahua",
            latitude = 31.6361,
            longitude = -106.4287,
            domesticTuaMxn = 625
        ),
        Airport(
            code = "VSA",
            name = "Aeropuerto Internacional Carlos Rovirosa Pérez",
            city = "Villahermosa",
            stateOrCountry = "Tabasco",
            latitude = 17.9970,
            longitude = -92.8174,
            domesticTuaMxn = 580
        ),
        Airport(
            code = "TAM",
            name = "Aeropuerto Internacional General Francisco Javier Mina",
            city = "Tampico",
            stateOrCountry = "Tamaulipas",
            latitude = 22.2964,
            longitude = -97.8659,
            domesticTuaMxn = 610
        ),
        Airport(
            code = "TRC",
            name = "Aeropuerto Internacional Francisco Sarabia",
            city = "Torreón",
            stateOrCountry = "Coahuila",
            latitude = 25.5683,
            longitude = -103.4106,
            domesticTuaMxn = 635
        ),
        Airport(
            code = "LMM",
            name = "Aeropuerto Internacional Federal del Valle del Fuerte",
            city = "Los Mochis",
            stateOrCountry = "Sinaloa",
            latitude = 25.6852,
            longitude = -109.0808,
            domesticTuaMxn = 570
        ),
        Airport(
            code = "DGO",
            name = "Aeropuerto Internacional Guadalupe Victoria",
            city = "Durango",
            stateOrCountry = "Durango",
            latitude = 24.1242,
            longitude = -104.5280,
            domesticTuaMxn = 595
        ),
        Airport(
            code = "ZCL",
            name = "Aeropuerto Internacional General Leobardo C. Ruiz",
            city = "Zacatecas",
            stateOrCountry = "Zacatecas",
            latitude = 22.8971,
            longitude = -102.6869,
            domesticTuaMxn = 615
        )
    )

    private val byCode: Map<String, Airport> = airports.associateBy { it.code }

    val mexicanAirports: List<Airport> = airports

    fun findByCode(code: String): Airport? = byCode[code.uppercase()]

    fun isDomesticMexicanAirport(code: String): Boolean = byCode.containsKey(code.uppercase())

    fun nearestMexicanAirport(latitude: Double, longitude: Double): Airport {
        return mexicanAirports.minByOrNull { it.distanceKmTo(latitude, longitude) }
            ?: findByCode("MEX")!!
    }
}
