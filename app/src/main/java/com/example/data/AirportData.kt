package com.example.data

import com.example.model.Airport

object AirportData {
    val mexicanAirports = listOf(
        Airport(
            code = "MEX",
            name = "Aeropuerto Internacional Benito Juárez (AICM)",
            city = "Ciudad de México",
            state = "CDMX",
            nationalTuaMxn = 570.0,
            internationalTuaMxn = 1080.0,
            latitude = 19.4363,
            longitude = -99.0721
        ),
        Airport(
            code = "NLU",
            name = "Aeropuerto Internacional Felipe Ángeles (AIFA)",
            city = "Ciudad de México / EdoMex",
            state = "EdoMex",
            nationalTuaMxn = 309.0, // Famoso por su TUA ultra baja
            internationalTuaMxn = 580.0,
            latitude = 19.7439,
            longitude = -99.0156
        ),
        Airport(
            code = "CUN",
            name = "Aeropuerto Internacional de Cancún",
            city = "Cancún / Riviera Maya",
            state = "Quintana Roo",
            isBeachDestination = true,
            nationalTuaMxn = 340.0,
            internationalTuaMxn = 890.0,
            latitude = 21.0365,
            longitude = -86.8771
        ),
        Airport(
            code = "GDL",
            name = "Aeropuerto Internacional de Guadalajara",
            city = "Guadalajara",
            state = "Jalisco",
            nationalTuaMxn = 535.0,
            internationalTuaMxn = 1020.0,
            latitude = 20.5218,
            longitude = -103.3111
        ),
        Airport(
            code = "MTY",
            name = "Aeropuerto Internacional de Monterrey",
            city = "Monterrey",
            state = "Nuevo León",
            nationalTuaMxn = 706.0,
            internationalTuaMxn = 1280.0,
            latitude = 25.7785,
            longitude = -100.1069
        ),
        Airport(
            code = "TIJ",
            name = "Aeropuerto Internacional de Tijuana (CBX)",
            city = "Tijuana",
            state = "Baja California",
            nationalTuaMxn = 520.0,
            internationalTuaMxn = 950.0,
            latitude = 32.5411,
            longitude = -116.9702
        ),
        Airport(
            code = "PVR",
            name = "Aeropuerto Internacional Lic. Gustavo Díaz Ordaz",
            city = "Puerto Vallarta / Riviera Nayarit",
            state = "Jalisco",
            isBeachDestination = true,
            nationalTuaMxn = 470.0,
            internationalTuaMxn = 910.0,
            latitude = 20.6801,
            longitude = -105.2542
        ),
        Airport(
            code = "SJD",
            name = "Aeropuerto Internacional de Los Cabos",
            city = "San José del Cabo / Cabo San Lucas",
            state = "Baja California Sur",
            isBeachDestination = true,
            nationalTuaMxn = 430.0,
            internationalTuaMxn = 880.0,
            latitude = 23.1518,
            longitude = -109.7214
        ),
        Airport(
            code = "MID",
            name = "Aeropuerto Internacional Manuel Crescencio Rejón",
            city = "Mérida",
            state = "Yucatán",
            nationalTuaMxn = 650.0,
            internationalTuaMxn = 1100.0,
            latitude = 20.9370,
            longitude = -89.6577
        ),
        Airport(
            code = "OAX",
            name = "Aeropuerto Internacional de Oaxaca",
            city = "Oaxaca",
            state = "Oaxaca",
            nationalTuaMxn = 590.0,
            internationalTuaMxn = 990.0,
            latitude = 17.0000,
            longitude = -96.7266
        ),
        Airport(
            code = "HUX",
            name = "Aeropuerto Internacional de Bahías de Huatulco",
            city = "Huatulco",
            state = "Oaxaca",
            isBeachDestination = true,
            nationalTuaMxn = 580.0,
            internationalTuaMxn = 980.0,
            latitude = 15.7753,
            longitude = -96.2626
        ),
        Airport(
            code = "TGZ",
            name = "Aeropuerto Internacional Ángel Albino Corzo",
            city = "Tuxtla Gutiérrez",
            state = "Chiapas",
            nationalTuaMxn = 520.0,
            internationalTuaMxn = 940.0,
            latitude = 16.5644,
            longitude = -93.0258
        ),
        Airport(
            code = "BJX",
            name = "Aeropuerto Internacional del Bajío",
            city = "León / Bajío",
            state = "Guanajuato",
            nationalTuaMxn = 518.0,
            internationalTuaMxn = 960.0,
            latitude = 20.9935,
            longitude = -101.4809
        ),
        Airport(
            code = "AGU",
            name = "Aeropuerto Internacional Lic. Jesús Terán Peredo",
            city = "Aguascalientes",
            state = "Aguascalientes",
            nationalTuaMxn = 528.0,
            internationalTuaMxn = 980.0,
            latitude = 21.7056,
            longitude = -102.2878
        ),
        Airport(
            code = "ACA",
            name = "Aeropuerto Internacional General Juan N. Álvarez",
            city = "Acapulco",
            state = "Guerrero",
            isBeachDestination = true,
            nationalTuaMxn = 450.0,
            internationalTuaMxn = 890.0,
            latitude = 16.7571,
            longitude = -99.7540
        ),
        Airport(
            code = "ZIH",
            name = "Aeropuerto Internacional de Ixtapa-Zihuatanejo",
            city = "Ixtapa Zihuatanejo",
            state = "Guerrero",
            isBeachDestination = true,
            nationalTuaMxn = 460.0,
            internationalTuaMxn = 900.0,
            latitude = 17.6015,
            longitude = -101.4606
        ),
        Airport(
            code = "CUU",
            name = "Aeropuerto Internacional General Roberto Fierro",
            city = "Chihuahua",
            state = "Chihuahua",
            nationalTuaMxn = 540.0,
            internationalTuaMxn = 980.0,
            latitude = 28.7029,
            longitude = -105.9646
        ),
        Airport(
            code = "HMO",
            name = "Aeropuerto Internacional General Ignacio Pesqueira",
            city = "Hermosillo",
            state = "Sonora",
            nationalTuaMxn = 510.0,
            internationalTuaMxn = 950.0,
            latitude = 29.0959,
            longitude = -111.0479
        ),
        Airport(
            code = "VER",
            name = "Aeropuerto Internacional General Heriberto Jara",
            city = "Veracruz",
            state = "Veracruz",
            nationalTuaMxn = 490.0,
            internationalTuaMxn = 910.0,
            latitude = 19.1459,
            longitude = -96.1873
        ),
        Airport(
            code = "TQO",
            name = "Aeropuerto Internacional Felipe Carrillo Puerto (Tulum)",
            city = "Tulum / Riviera Maya",
            state = "Quintana Roo",
            isBeachDestination = true,
            nationalTuaMxn = 345.0,
            internationalTuaMxn = 880.0,
            latitude = 20.1583,
            longitude = -87.6667
        ),
        Airport(
            code = "MZT",
            name = "Aeropuerto Internacional General Rafael Buelna",
            city = "Mazatlán",
            state = "Sinaloa",
            isBeachDestination = true,
            nationalTuaMxn = 460.0,
            internationalTuaMxn = 890.0,
            latitude = 23.1614,
            longitude = -106.2661
        ),
        Airport(
            code = "LAP",
            name = "Aeropuerto Internacional Manuel Márquez de León",
            city = "La Paz",
            state = "Baja California Sur",
            isBeachDestination = true,
            nationalTuaMxn = 450.0,
            internationalTuaMxn = 870.0,
            latitude = 24.0727,
            longitude = -110.3624
        ),
        Airport(
            code = "CZM",
            name = "Aeropuerto Internacional de Cozumel",
            city = "Cozumel",
            state = "Quintana Roo",
            isBeachDestination = true,
            nationalTuaMxn = 420.0,
            internationalTuaMxn = 860.0,
            latitude = 20.5224,
            longitude = -86.9256
        ),
        Airport(
            code = "QRO",
            name = "Aeropuerto Intercontinental de Querétaro",
            city = "Querétaro",
            state = "Querétaro",
            nationalTuaMxn = 480.0,
            internationalTuaMxn = 920.0,
            latitude = 20.6173,
            longitude = -100.1864
        ),
        Airport(
            code = "SLP",
            name = "Aeropuerto Internacional Ponciano Arriaga",
            city = "San Luis Potosí",
            state = "San Luis Potosí",
            nationalTuaMxn = 490.0,
            internationalTuaMxn = 930.0,
            latitude = 22.2543,
            longitude = -100.9308
        ),
        Airport(
            code = "PBC",
            name = "Aeropuerto Internacional Hermanos Serdán",
            city = "Puebla",
            state = "Puebla",
            nationalTuaMxn = 370.0,
            internationalTuaMxn = 780.0,
            latitude = 19.1581,
            longitude = -98.3714
        ),
        Airport(
            code = "MLM",
            name = "Aeropuerto Internacional General Francisco J. Múgica",
            city = "Morelia",
            state = "Michoacán",
            nationalTuaMxn = 485.0,
            internationalTuaMxn = 910.0,
            latitude = 19.8500,
            longitude = -101.0256
        ),
        Airport(
            code = "CUL",
            name = "Aeropuerto Internacional Federal de Culiacán",
            city = "Culiacán",
            state = "Sinaloa",
            nationalTuaMxn = 510.0,
            internationalTuaMxn = 950.0,
            latitude = 24.7644,
            longitude = -107.4747
        ),
        Airport(
            code = "MXL",
            name = "Aeropuerto Internacional General Rodolfo Sánchez Taboada",
            city = "Mexicali",
            state = "Baja California",
            nationalTuaMxn = 495.0,
            internationalTuaMxn = 920.0,
            latitude = 32.6311,
            longitude = -115.2414
        )
    )

    val internationalAirports = listOf(
        Airport(
            code = "LAX",
            name = "Los Angeles International Airport",
            city = "Los Ángeles",
            state = "California",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 750.0
        ),
        Airport(
            code = "MIA",
            name = "Miami International Airport",
            city = "Miami",
            state = "Florida",
            country = "Estados Unidos",
            isInternational = true,
            isBeachDestination = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 780.0
        ),
        Airport(
            code = "ORD",
            name = "O'Hare International Airport",
            city = "Chicago",
            state = "Illinois",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 820.0
        ),
        Airport(
            code = "IAH",
            name = "George Bush Intercontinental Airport",
            city = "Houston",
            state = "Texas",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 760.0
        ),
        Airport(
            code = "JFK",
            name = "John F. Kennedy International Airport",
            city = "Nueva York",
            state = "New York",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 850.0
        ),
        Airport(
            code = "LAS",
            name = "Harry Reid International Airport",
            city = "Las Vegas",
            state = "Nevada",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 740.0
        ),
        Airport(
            code = "MCO",
            name = "Orlando International Airport",
            city = "Orlando",
            state = "Florida",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 770.0
        ),
        Airport(
            code = "SAT",
            name = "San Antonio International Airport",
            city = "San Antonio",
            state = "Texas",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 720.0
        ),
        Airport(
            code = "BOG",
            name = "Aeropuerto Internacional El Dorado",
            city = "Bogotá",
            state = "Cundinamarca",
            country = "Colombia",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 790.0
        ),
        Airport(
            code = "GUA",
            name = "Aeropuerto Internacional La Aurora",
            city = "Ciudad de Guatemala",
            state = "Guatemala",
            country = "Guatemala",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 680.0
        ),
        Airport(
            code = "SJO",
            name = "Aeropuerto Internacional Juan Santamaría",
            city = "San José",
            state = "Alajuela",
            country = "Costa Rica",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 710.0
        ),
        Airport(
            code = "MAD",
            name = "Aeropuerto Adolfo Suárez Madrid-Barajas",
            city = "Madrid",
            state = "Madrid",
            country = "España",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 1200.0
        ),
        Airport(
            code = "DFW",
            name = "Dallas/Fort Worth International Airport",
            city = "Dallas / Fort Worth",
            state = "Texas",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 760.0
        ),
        Airport(
            code = "SFO",
            name = "San Francisco International Airport",
            city = "San Francisco",
            state = "California",
            country = "Estados Unidos",
            isInternational = true,
            nationalTuaMxn = 0.0,
            internationalTuaMxn = 790.0
        )
    )

    val allAirports: List<Airport> = mexicanAirports + internationalAirports

    fun findByCode(code: String): Airport {
        return allAirports.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: mexicanAirports.first()
    }

    /**
     * Finds the closest airport to the user's geographic coordinates.
     */
    fun findNearestAirport(userLat: Double, userLon: Double): Airport {
        return mexicanAirports.minByOrNull { it.distanceKmTo(userLat, userLon) }
            ?: findByCode("AGU")
    }

    val defaultOrigins: List<Airport> = listOf(
        findByCode("MEX"),
        findByCode("NLU")
    )
}
