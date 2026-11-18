package com.example.data

import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.FlightDeal
import com.example.model.TravelDate
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.roundToInt

private data class RouteTemplate(
    val airline: Airline,
    val outFlightNum: String,
    val retFlightNum: String,
    val originCode: String,
    val destCode: String,
    val outDepTime: String,
    val outArrTime: String,
    val retDepTime: String,
    val retArrTime: String,
    val durationMin: Int,
    val aircraft: String,
    val isNonStop: Boolean,
    val stopsCount: Int = 0,
    val layoverCode: String? = null,
    val layoverMin: Int? = null,
    val minBaseFareMxn: Int,
    val typicalBaseFareMxn: Int,
    val promoSource: String,
    val operatingStride: Int = 1 // 1 = diario, 2 = cada 2 días, etc.
)

class FlightDealsRepository(
    private val baseDate: TravelDate = TravelDate.today()
) {
    // Catálogo 100% vuelos nacionales dentro de México (7 aerolíneas mexicanas activas)
    private val templates: List<RouteTemplate> = listOf(
        // ==================== AGU (Aguascalientes) ====================
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 3180",
            retFlightNum = "Y4 3181",
            originCode = "AGU",
            destCode = "PVR",
            outDepTime = "07:15",
            outArrTime = "08:13",
            retDepTime = "09:05",
            retArrTime = "10:03",
            durationMin = 58,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 469,
            promoSource = "Referencial · Tarifa Zero Volaris Bajío-Pacífico"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 3184",
            retFlightNum = "Y4 3185",
            originCode = "AGU",
            destCode = "PVR",
            outDepTime = "18:40",
            outArrTime = "19:38",
            retDepTime = "20:25",
            retArrTime = "21:23",
            durationMin = 58,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 259,
            typicalBaseFareMxn = 549,
            promoSource = "Referencial · Tarifa Zero Volaris Nocturno",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 4120",
            retFlightNum = "VB 4121",
            originCode = "AGU",
            destCode = "PVR",
            outDepTime = "13:10",
            outArrTime = "16:20",
            retDepTime = "14:40",
            retArrTime = "17:50",
            durationMin = 190,
            aircraft = "Airbus A320neo",
            isNonStop = false,
            stopsCount = 1,
            layoverCode = "MTY",
            layoverMin = 85,
            minBaseFareMxn = 289,
            typicalBaseFareMxn = 599,
            promoSource = "Referencial · Tarifa Zero Viva Conexión MTY"
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 2631",
            retFlightNum = "AM 2632",
            originCode = "AGU",
            destCode = "PVR",
            outDepTime = "10:25",
            outArrTime = "14:05",
            retDepTime = "15:10",
            retArrTime = "18:50",
            durationMin = 220,
            aircraft = "Embraer 190",
            isNonStop = false,
            stopsCount = 1,
            layoverCode = "MEX",
            layoverMin = 95,
            minBaseFareMxn = 699,
            typicalBaseFareMxn = 1190,
            promoSource = "Referencial · Básica Aeroméxico (inc. 10kg)",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 3210",
            retFlightNum = "Y4 3211",
            originCode = "AGU",
            destCode = "CUN",
            outDepTime = "08:20",
            outArrTime = "11:45",
            retDepTime = "12:35",
            retArrTime = "14:15",
            durationMin = 145,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 249,
            typicalBaseFareMxn = 649,
            promoSource = "Referencial · Tarifa Zero Volaris Caribe"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 4208",
            retFlightNum = "VB 4209",
            originCode = "AGU",
            destCode = "CUN",
            outDepTime = "14:15",
            outArrTime = "18:55",
            retDepTime = "16:10",
            retArrTime = "20:50",
            durationMin = 220,
            aircraft = "Airbus A320",
            isNonStop = false,
            stopsCount = 1,
            layoverCode = "MTY",
            layoverMin = 70,
            minBaseFareMxn = 299,
            typicalBaseFareMxn = 699,
            promoSource = "Referencial · Tarifa Zero Viva Escala MTY"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 3050",
            retFlightNum = "Y4 3051",
            originCode = "AGU",
            destCode = "TIJ",
            outDepTime = "21:10",
            outArrTime = "22:30",
            retDepTime = "06:15",
            retArrTime = "11:10",
            durationMin = 200,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 299,
            typicalBaseFareMxn = 749,
            promoSource = "Referencial · Tarifa Zero Volaris Tijuana"
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 2623",
            retFlightNum = "AM 2624",
            originCode = "AGU",
            destCode = "MEX",
            outDepTime = "06:40",
            outArrTime = "07:55",
            retDepTime = "19:30",
            retArrTime = "20:45",
            durationMin = 75,
            aircraft = "Embraer 190",
            isNonStop = true,
            minBaseFareMxn = 529,
            typicalBaseFareMxn = 949,
            promoSource = "Referencial · Básica Aeroméxico Puente Bajío"
        ),
        RouteTemplate(
            airline = Airline.TAR,
            outFlightNum = "YQ 412",
            retFlightNum = "YQ 413",
            originCode = "AGU",
            destCode = "MTY",
            outDepTime = "09:10",
            outArrTime = "10:25",
            retDepTime = "16:40",
            retArrTime = "17:55",
            durationMin = 75,
            aircraft = "Embraer ERJ-145",
            isNonStop = true,
            minBaseFareMxn = 549,
            typicalBaseFareMxn = 929,
            promoSource = "Referencial · TAR Aerolíneas Regional (inc. 10kg)",
            operatingStride = 2
        ),

        // ==================== NLU (AIFA — TUA más baja de CDMX $309 MXN) ====================
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 9210",
            retFlightNum = "VB 9211",
            originCode = "NLU",
            destCode = "CUN",
            outDepTime = "06:30",
            outArrTime = "09:45",
            retDepTime = "10:35",
            retArrTime = "11:55",
            durationMin = 135,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 519,
            promoSource = "Referencial · Tarifa Zero Viva AIFA"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 1284",
            retFlightNum = "Y4 1285",
            originCode = "NLU",
            destCode = "CUN",
            outDepTime = "13:15",
            outArrTime = "16:30",
            retDepTime = "17:20",
            retArrTime = "18:45",
            durationMin = 135,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 529,
            promoSource = "Referencial · Tarifa Zero Volaris AIFA"
        ),
        RouteTemplate(
            airline = Airline.MEXICANA,
            outFlightNum = "MX 1402",
            retFlightNum = "MX 1403",
            originCode = "NLU",
            destCode = "TQO",
            outDepTime = "09:10",
            outArrTime = "12:20",
            retDepTime = "13:15",
            retArrTime = "14:30",
            durationMin = 130,
            aircraft = "Boeing 737-800",
            isNonStop = true,
            minBaseFareMxn = 289,
            typicalBaseFareMxn = 619,
            promoSource = "Referencial · Mexicana (inc. 10kg) AIFA-Tulum"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 9350",
            retFlightNum = "VB 9351",
            originCode = "NLU",
            destCode = "TQO",
            outDepTime = "15:20",
            outArrTime = "18:35",
            retDepTime = "19:20",
            retArrTime = "20:40",
            durationMin = 135,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 239,
            typicalBaseFareMxn = 569,
            promoSource = "Referencial · Tarifa Zero Viva Caribe Sur"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 9140",
            retFlightNum = "VB 9141",
            originCode = "NLU",
            destCode = "PVR",
            outDepTime = "08:05",
            outArrTime = "09:40",
            retDepTime = "10:25",
            retArrTime = "12:00",
            durationMin = 95,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 209,
            typicalBaseFareMxn = 489,
            promoSource = "Referencial · Tarifa Zero Viva Pacífico"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 1240",
            retFlightNum = "Y4 1241",
            originCode = "NLU",
            destCode = "PVR",
            outDepTime = "16:50",
            outArrTime = "18:25",
            retDepTime = "19:10",
            retArrTime = "20:45",
            durationMin = 95,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 499,
            promoSource = "Referencial · Tarifa Zero Volaris Directo"
        ),
        RouteTemplate(
            airline = Airline.MEXICANA,
            outFlightNum = "MX 1520",
            retFlightNum = "MX 1521",
            originCode = "NLU",
            destCode = "SJD",
            outDepTime = "11:10",
            outArrTime = "12:35",
            retDepTime = "13:20",
            retArrTime = "16:35",
            durationMin = 145,
            aircraft = "Boeing 737-800",
            isNonStop = true,
            minBaseFareMxn = 329,
            typicalBaseFareMxn = 719,
            promoSource = "Referencial · Mexicana (inc. 10kg) Los Cabos"
        ),
        RouteTemplate(
            airline = Airline.MEXICANA,
            outFlightNum = "MX 1480",
            retFlightNum = "MX 1481",
            originCode = "NLU",
            destCode = "PVR",
            outDepTime = "07:40",
            outArrTime = "09:15",
            retDepTime = "10:05",
            retArrTime = "11:40",
            durationMin = 95,
            aircraft = "Boeing 737-800",
            isNonStop = true,
            minBaseFareMxn = 289,
            typicalBaseFareMxn = 619,
            promoSource = "Referencial · Mexicana (inc. 10kg) Puerto Vallarta",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.MEXICANA,
            outFlightNum = "MX 1492",
            retFlightNum = "MX 1493",
            originCode = "NLU",
            destCode = "CTM",
            outDepTime = "10:20",
            outArrTime = "13:30",
            retDepTime = "14:15",
            retArrTime = "15:25",
            durationMin = 130,
            aircraft = "Boeing 737-800",
            isNonStop = true,
            minBaseFareMxn = 299,
            typicalBaseFareMxn = 649,
            promoSource = "Referencial · Mexicana (inc. 10kg) Bacalar/Chetumal",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.MEXICANA,
            outFlightNum = "MX 1468",
            retFlightNum = "MX 1469",
            originCode = "NLU",
            destCode = "ZIH",
            outDepTime = "12:00",
            outArrTime = "13:10",
            retDepTime = "13:55",
            retArrTime = "15:05",
            durationMin = 70,
            aircraft = "Boeing 737-800",
            isNonStop = true,
            minBaseFareMxn = 269,
            typicalBaseFareMxn = 569,
            promoSource = "Referencial · Mexicana (inc. 10kg) Ixtapa",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 9180",
            retFlightNum = "VB 9181",
            originCode = "NLU",
            destCode = "TIJ",
            outDepTime = "19:45",
            outArrTime = "21:35",
            retDepTime = "22:30",
            retArrTime = "04:05",
            durationMin = 230,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 299,
            typicalBaseFareMxn = 719,
            promoSource = "Referencial · Tarifa Zero Viva Tijuana"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 9110",
            retFlightNum = "VB 9111",
            originCode = "NLU",
            destCode = "OAX",
            outDepTime = "07:45",
            outArrTime = "08:55",
            retDepTime = "09:40",
            retArrTime = "10:50",
            durationMin = 70,
            aircraft = "Airbus A320",
            isNonStop = true,
            minBaseFareMxn = 209,
            typicalBaseFareMxn = 459,
            promoSource = "Referencial · Tarifa Zero Viva Oaxaca"
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 882",
            retFlightNum = "AM 883",
            originCode = "NLU",
            destCode = "MID",
            outDepTime = "10:15",
            outArrTime = "12:15",
            retDepTime = "13:05",
            retArrTime = "15:10",
            durationMin = 120,
            aircraft = "Boeing 737 MAX 9",
            isNonStop = true,
            minBaseFareMxn = 489,
            typicalBaseFareMxn = 919,
            promoSource = "Referencial · Básica Aeroméxico (inc. 10kg)"
        ),
        RouteTemplate(
            airline = Airline.MEXICANA,
            outFlightNum = "MX 1450",
            retFlightNum = "MX 1451",
            originCode = "NLU",
            destCode = "MZT",
            outDepTime = "12:30",
            outArrTime = "13:15",
            retDepTime = "14:00",
            retArrTime = "16:40",
            durationMin = 105,
            aircraft = "Boeing 737-800",
            isNonStop = true,
            minBaseFareMxn = 279,
            typicalBaseFareMxn = 599,
            promoSource = "Referencial · Mexicana (inc. 10kg) Mazatlán",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.AERUS,
            outFlightNum = "ZV 204",
            retFlightNum = "ZV 205",
            originCode = "NLU",
            destCode = "SLP",
            outDepTime = "08:30",
            outArrTime = "09:45",
            retDepTime = "10:20",
            retArrTime = "11:35",
            durationMin = 75,
            aircraft = "Cessna Grand Caravan EX",
            isNonStop = true,
            minBaseFareMxn = 599,
            typicalBaseFareMxn = 990,
            promoSource = "Referencial · Aerus Light Regional",
            operatingStride = 2
        ),

        // ==================== MEX (CDMX AICM) ====================
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 710",
            retFlightNum = "Y4 711",
            originCode = "MEX",
            destCode = "CUN",
            outDepTime = "06:10",
            outArrTime = "09:30",
            retDepTime = "10:25",
            retArrTime = "11:55",
            durationMin = 140,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 569,
            promoSource = "Referencial · Tarifa Zero Volaris AICM"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 1140",
            retFlightNum = "VB 1141",
            originCode = "MEX",
            destCode = "CUN",
            outDepTime = "14:05",
            outArrTime = "17:25",
            retDepTime = "18:15",
            retArrTime = "19:45",
            durationMin = 140,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 249,
            typicalBaseFareMxn = 599,
            promoSource = "Referencial · Tarifa Zero Viva AICM T1"
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 514",
            retFlightNum = "AM 515",
            originCode = "MEX",
            destCode = "CUN",
            outDepTime = "18:30",
            outArrTime = "21:55",
            retDepTime = "08:00",
            retArrTime = "09:35",
            durationMin = 145,
            aircraft = "Boeing 737 MAX 9",
            isNonStop = true,
            minBaseFareMxn = 699,
            typicalBaseFareMxn = 1249,
            promoSource = "Referencial · Básica Aeroméxico (inc. 10kg)"
        ),
        RouteTemplate(
            airline = Airline.MAGNICHARTERS,
            outFlightNum = "UJ 770",
            retFlightNum = "UJ 771",
            originCode = "MEX",
            destCode = "CUN",
            outDepTime = "09:30",
            outArrTime = "12:55",
            retDepTime = "13:50",
            retArrTime = "15:25",
            durationMin = 145,
            aircraft = "Boeing 737-300",
            isNonStop = true,
            minBaseFareMxn = 649,
            typicalBaseFareMxn = 1149,
            promoSource = "Referencial · Magnicharters Turístico (inc. 10kg)",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 740",
            retFlightNum = "Y4 741",
            originCode = "MEX",
            destCode = "PVR",
            outDepTime = "09:15",
            outArrTime = "10:50",
            retDepTime = "11:40",
            retArrTime = "13:15",
            durationMin = 95,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 209,
            typicalBaseFareMxn = 519,
            promoSource = "Referencial · Tarifa Zero Volaris Pacífico"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 1220",
            retFlightNum = "VB 1221",
            originCode = "MEX",
            destCode = "PVR",
            outDepTime = "16:10",
            outArrTime = "17:45",
            retDepTime = "18:30",
            retArrTime = "20:05",
            durationMin = 95,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 239,
            typicalBaseFareMxn = 549,
            promoSource = "Referencial · Tarifa Zero Viva Pacífico"
        ),
        RouteTemplate(
            airline = Airline.MAGNICHARTERS,
            outFlightNum = "UJ 774",
            retFlightNum = "UJ 775",
            originCode = "MEX",
            destCode = "PVR",
            outDepTime = "11:20",
            outArrTime = "12:55",
            retDepTime = "13:45",
            retArrTime = "15:20",
            durationMin = 95,
            aircraft = "Boeing 737-300",
            isNonStop = true,
            minBaseFareMxn = 589,
            typicalBaseFareMxn = 1049,
            promoSource = "Referencial · Magnicharters (inc. 10kg) Vallarta",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 762",
            retFlightNum = "Y4 763",
            originCode = "MEX",
            destCode = "SJD",
            outDepTime = "11:30",
            outArrTime = "12:55",
            retDepTime = "13:45",
            retArrTime = "17:00",
            durationMin = 145,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 259,
            typicalBaseFareMxn = 669,
            promoSource = "Referencial · Tarifa Zero Volaris Los Cabos"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 1304",
            retFlightNum = "VB 1305",
            originCode = "MEX",
            destCode = "HUX",
            outDepTime = "10:00",
            outArrTime = "11:15",
            retDepTime = "12:00",
            retArrTime = "13:15",
            durationMin = 75,
            aircraft = "Airbus A320",
            isNonStop = true,
            minBaseFareMxn = 249,
            typicalBaseFareMxn = 569,
            promoSource = "Referencial · Tarifa Zero Viva Huatulco"
        ),
        RouteTemplate(
            airline = Airline.MAGNICHARTERS,
            outFlightNum = "UJ 782",
            retFlightNum = "UJ 783",
            originCode = "MEX",
            destCode = "HUX",
            outDepTime = "08:50",
            outArrTime = "10:05",
            retDepTime = "10:55",
            retArrTime = "12:10",
            durationMin = 75,
            aircraft = "Boeing 737-300",
            isNonStop = true,
            minBaseFareMxn = 549,
            typicalBaseFareMxn = 989,
            promoSource = "Referencial · Magnicharters (inc. 10kg) Huatulco",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 1316",
            retFlightNum = "VB 1317",
            originCode = "MEX",
            destCode = "PXM",
            outDepTime = "08:35",
            outArrTime = "09:50",
            retDepTime = "10:35",
            retArrTime = "11:50",
            durationMin = 75,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 239,
            typicalBaseFareMxn = 549,
            promoSource = "Referencial · Tarifa Zero Viva Puerto Escondido"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 790",
            retFlightNum = "Y4 791",
            originCode = "MEX",
            destCode = "TQO",
            outDepTime = "12:15",
            outArrTime = "15:25",
            retDepTime = "16:10",
            retArrTime = "17:25",
            durationMin = 130,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 269,
            typicalBaseFareMxn = 619,
            promoSource = "Referencial · Tarifa Zero Volaris Tulum"
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 560",
            retFlightNum = "AM 561",
            originCode = "MEX",
            destCode = "ACA",
            outDepTime = "08:00",
            outArrTime = "09:05",
            retDepTime = "09:50",
            retArrTime = "10:55",
            durationMin = 65,
            aircraft = "Embraer 190",
            isNonStop = true,
            minBaseFareMxn = 549,
            typicalBaseFareMxn = 990,
            promoSource = "Referencial · Básica Aeroméxico Acapulco",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.MAGNICHARTERS,
            outFlightNum = "UJ 788",
            retFlightNum = "UJ 789",
            originCode = "MEX",
            destCode = "ZIH",
            outDepTime = "10:15",
            outArrTime = "11:20",
            retDepTime = "12:10",
            retArrTime = "13:15",
            durationMin = 65,
            aircraft = "Boeing 737-300",
            isNonStop = true,
            minBaseFareMxn = 539,
            typicalBaseFareMxn = 949,
            promoSource = "Referencial · Magnicharters (inc. 10kg) Ixtapa",
            operatingStride = 2
        ),

        // ==================== GDL (Guadalajara Hub) ====================
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 210",
            retFlightNum = "Y4 211",
            originCode = "GDL",
            destCode = "PVR",
            outDepTime = "08:15",
            outArrTime = "09:05",
            retDepTime = "09:50",
            retArrTime = "10:40",
            durationMin = 50,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 429,
            promoSource = "Referencial · Tarifa Zero Volaris GDL-PVR"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 3120",
            retFlightNum = "VB 3121",
            originCode = "GDL",
            destCode = "CUN",
            outDepTime = "07:30",
            outArrTime = "11:15",
            retDepTime = "12:05",
            retArrTime = "14:00",
            durationMin = 165,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 239,
            typicalBaseFareMxn = 619,
            promoSource = "Referencial · Tarifa Zero Viva GDL-Caribe"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 234",
            retFlightNum = "Y4 235",
            originCode = "GDL",
            destCode = "CUN",
            outDepTime = "15:10",
            outArrTime = "18:55",
            retDepTime = "19:45",
            retArrTime = "21:40",
            durationMin = 165,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 599,
            promoSource = "Referencial · Tarifa Zero Volaris GDL-Cancún"
        ),
        RouteTemplate(
            airline = Airline.MAGNICHARTERS,
            outFlightNum = "UJ 640",
            retFlightNum = "UJ 641",
            originCode = "GDL",
            destCode = "CUN",
            outDepTime = "08:40",
            outArrTime = "12:30",
            retDepTime = "13:25",
            retArrTime = "15:20",
            durationMin = 170,
            aircraft = "Boeing 737-300",
            isNonStop = true,
            minBaseFareMxn = 679,
            typicalBaseFareMxn = 1190,
            promoSource = "Referencial · Magnicharters (inc. 10kg) GDL-Cancún",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 3204",
            retFlightNum = "VB 3205",
            originCode = "GDL",
            destCode = "TIJ",
            outDepTime = "20:15",
            outArrTime = "21:35",
            retDepTime = "06:30",
            retArrTime = "11:30",
            durationMin = 200,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 249,
            typicalBaseFareMxn = 619,
            promoSource = "Referencial · Tarifa Zero Viva GDL-Tijuana"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 248",
            retFlightNum = "Y4 249",
            originCode = "GDL",
            destCode = "SJD",
            outDepTime = "10:10",
            outArrTime = "10:40",
            retDepTime = "11:25",
            retArrTime = "13:55",
            durationMin = 90,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 529,
            promoSource = "Referencial · Tarifa Zero Volaris GDL-Cabos"
        ),
        RouteTemplate(
            airline = Airline.TAR,
            outFlightNum = "YQ 520",
            retFlightNum = "YQ 521",
            originCode = "GDL",
            destCode = "LAP",
            outDepTime = "11:15",
            outArrTime = "12:00",
            retDepTime = "12:40",
            retArrTime = "15:25",
            durationMin = 105,
            aircraft = "Embraer ERJ-145",
            isNonStop = true,
            minBaseFareMxn = 579,
            typicalBaseFareMxn = 990,
            promoSource = "Referencial · TAR Aerolíneas (inc. 10kg) La Paz",
            operatingStride = 2
        ),

        // ==================== MTY (Monterrey Hub) ====================
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 4010",
            retFlightNum = "VB 4011",
            originCode = "MTY",
            destCode = "CUN",
            outDepTime = "07:05",
            outArrTime = "10:25",
            retDepTime = "11:15",
            retArrTime = "12:50",
            durationMin = 140,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 569,
            promoSource = "Referencial · Tarifa Zero Viva Hub Monterrey"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 410",
            retFlightNum = "Y4 411",
            originCode = "MTY",
            destCode = "CUN",
            outDepTime = "14:20",
            outArrTime = "17:40",
            retDepTime = "18:30",
            retArrTime = "20:05",
            durationMin = 140,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 579,
            promoSource = "Referencial · Tarifa Zero Volaris Regio-Caribe"
        ),
        RouteTemplate(
            airline = Airline.MAGNICHARTERS,
            outFlightNum = "UJ 510",
            retFlightNum = "UJ 511",
            originCode = "MTY",
            destCode = "CUN",
            outDepTime = "08:15",
            outArrTime = "11:40",
            retDepTime = "12:35",
            retArrTime = "14:10",
            durationMin = 145,
            aircraft = "Boeing 737-300",
            isNonStop = true,
            minBaseFareMxn = 629,
            typicalBaseFareMxn = 1090,
            promoSource = "Referencial · Magnicharters (inc. 10kg) MTY-Cancún",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 4050",
            retFlightNum = "VB 4051",
            originCode = "MTY",
            destCode = "PVR",
            outDepTime = "09:40",
            outArrTime = "11:25",
            retDepTime = "12:15",
            retArrTime = "13:55",
            durationMin = 105,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 209,
            typicalBaseFareMxn = 499,
            promoSource = "Referencial · Tarifa Zero Viva MTY-Pacífico"
        ),
        RouteTemplate(
            airline = Airline.MAGNICHARTERS,
            outFlightNum = "UJ 522",
            retFlightNum = "UJ 523",
            originCode = "MTY",
            destCode = "MZT",
            outDepTime = "10:30",
            outArrTime = "10:55",
            retDepTime = "11:45",
            retArrTime = "14:10",
            durationMin = 85,
            aircraft = "Boeing 737-300",
            isNonStop = true,
            minBaseFareMxn = 519,
            typicalBaseFareMxn = 899,
            promoSource = "Referencial · Magnicharters (inc. 10kg) Mazatlán",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 4072",
            retFlightNum = "VB 4073",
            originCode = "MTY",
            destCode = "SJD",
            outDepTime = "11:50",
            outArrTime = "12:55",
            retDepTime = "13:40",
            retArrTime = "16:45",
            durationMin = 125,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 249,
            typicalBaseFareMxn = 599,
            promoSource = "Referencial · Tarifa Zero Viva MTY-Cabos"
        ),
        RouteTemplate(
            airline = Airline.AERUS,
            outFlightNum = "ZV 102",
            retFlightNum = "ZV 103",
            originCode = "MTY",
            destCode = "TAM",
            outDepTime = "07:20",
            outArrTime = "08:30",
            retDepTime = "09:10",
            retArrTime = "10:20",
            durationMin = 70,
            aircraft = "Cessna Grand Caravan EX",
            isNonStop = true,
            minBaseFareMxn = 499,
            typicalBaseFareMxn = 879,
            promoSource = "Referencial · Aerus Light MTY-Tampico"
        ),
        RouteTemplate(
            airline = Airline.AERUS,
            outFlightNum = "ZV 118",
            retFlightNum = "ZV 119",
            originCode = "MTY",
            destCode = "VER",
            outDepTime = "11:00",
            outArrTime = "12:55",
            retDepTime = "13:40",
            retArrTime = "15:35",
            durationMin = 115,
            aircraft = "Cessna Grand Caravan EX",
            isNonStop = true,
            minBaseFareMxn = 649,
            typicalBaseFareMxn = 1090,
            promoSource = "Referencial · Aerus Light Golfo",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.TAR,
            outFlightNum = "YQ 440",
            retFlightNum = "YQ 441",
            originCode = "MTY",
            destCode = "TRC",
            outDepTime = "08:10",
            outArrTime = "09:10",
            retDepTime = "18:00",
            retArrTime = "19:00",
            durationMin = 60,
            aircraft = "Embraer ERJ-145",
            isNonStop = true,
            minBaseFareMxn = 499,
            typicalBaseFareMxn = 859,
            promoSource = "Referencial · TAR Aerolíneas (inc. 10kg) Torreón",
            operatingStride = 2
        ),

        // ==================== QRO, BJX, TIJ, TLC, PBC, CUN, VER, MZT (Regionales y Playas) ====================
        RouteTemplate(
            airline = Airline.TAR,
            outFlightNum = "YQ 310",
            retFlightNum = "YQ 311",
            originCode = "QRO",
            destCode = "PVR",
            outDepTime = "09:20",
            outArrTime = "10:35",
            retDepTime = "11:15",
            retArrTime = "12:30",
            durationMin = 75,
            aircraft = "Embraer ERJ-145",
            isNonStop = true,
            minBaseFareMxn = 529,
            typicalBaseFareMxn = 949,
            promoSource = "Referencial · TAR Hub Querétaro (inc. 10kg)"
        ),
        RouteTemplate(
            airline = Airline.TAR,
            outFlightNum = "YQ 324",
            retFlightNum = "YQ 325",
            originCode = "QRO",
            destCode = "MZT",
            outDepTime = "13:10",
            outArrTime = "13:40",
            retDepTime = "14:20",
            retArrTime = "16:50",
            durationMin = 90,
            aircraft = "Embraer ERJ-145",
            isNonStop = true,
            minBaseFareMxn = 549,
            typicalBaseFareMxn = 979,
            promoSource = "Referencial · TAR Querétaro-Mazatlán (inc. 10kg)",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.TAR,
            outFlightNum = "YQ 336",
            retFlightNum = "YQ 337",
            originCode = "QRO",
            destCode = "ZIH",
            outDepTime = "10:40",
            outArrTime = "11:45",
            retDepTime = "12:25",
            retArrTime = "13:30",
            durationMin = 65,
            aircraft = "Embraer ERJ-145",
            isNonStop = true,
            minBaseFareMxn = 519,
            typicalBaseFareMxn = 899,
            promoSource = "Referencial · TAR Querétaro-Ixtapa (inc. 10kg)",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 5120",
            retFlightNum = "VB 5121",
            originCode = "QRO",
            destCode = "CUN",
            outDepTime = "10:40",
            outArrTime = "14:00",
            retDepTime = "14:45",
            retArrTime = "16:15",
            durationMin = 140,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 229,
            typicalBaseFareMxn = 579,
            promoSource = "Referencial · Tarifa Zero Viva Querétaro-Cancún"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 510",
            retFlightNum = "Y4 511",
            originCode = "BJX",
            destCode = "PVR",
            outDepTime = "07:50",
            outArrTime = "08:50",
            retDepTime = "09:35",
            retArrTime = "10:35",
            durationMin = 60,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 479,
            promoSource = "Referencial · Tarifa Zero Volaris Bajío-Pacífico"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 810",
            retFlightNum = "Y4 811",
            originCode = "TIJ",
            destCode = "SJD",
            outDepTime = "08:00",
            outArrTime = "11:05",
            retDepTime = "11:55",
            retArrTime = "13:10",
            durationMin = 125,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 209,
            typicalBaseFareMxn = 519,
            promoSource = "Referencial · Tarifa Zero Volaris Baja Directo"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 5310",
            retFlightNum = "VB 5311",
            originCode = "TLC",
            destCode = "CUN",
            outDepTime = "07:10",
            outArrTime = "10:30",
            retDepTime = "11:20",
            retArrTime = "12:50",
            durationMin = 140,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 209,
            typicalBaseFareMxn = 519,
            promoSource = "Referencial · Tarifa Zero Viva Toluca-Cancún"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 620",
            retFlightNum = "Y4 621",
            originCode = "TLC",
            destCode = "PVR",
            outDepTime = "09:05",
            outArrTime = "10:35",
            retDepTime = "11:20",
            retArrTime = "12:50",
            durationMin = 90,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 469,
            promoSource = "Referencial · Tarifa Zero Volaris Toluca-Vallarta"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 5410",
            retFlightNum = "VB 5411",
            originCode = "PBC",
            destCode = "CUN",
            outDepTime = "09:30",
            outArrTime = "12:45",
            retDepTime = "13:30",
            retArrTime = "14:55",
            durationMin = 135,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 549,
            promoSource = "Referencial · Tarifa Zero Viva Puebla-Cancún"
        ),
        RouteTemplate(
            airline = Airline.AERUS,
            outFlightNum = "ZV 310",
            retFlightNum = "ZV 311",
            originCode = "CUN",
            destCode = "CZM",
            outDepTime = "09:00",
            outArrTime = "09:35",
            retDepTime = "10:15",
            retArrTime = "10:50",
            durationMin = 35,
            aircraft = "Cessna Grand Caravan EX",
            isNonStop = true,
            minBaseFareMxn = 449,
            typicalBaseFareMxn = 799,
            promoSource = "Referencial · Aerus Puente Aéreo Cozumel"
        ),
        RouteTemplate(
            airline = Airline.AERUS,
            outFlightNum = "ZV 322",
            retFlightNum = "ZV 323",
            originCode = "VER",
            destCode = "VSA",
            outDepTime = "08:15",
            outArrTime = "09:35",
            retDepTime = "10:15",
            retArrTime = "11:35",
            durationMin = 80,
            aircraft = "Cessna Grand Caravan EX",
            isNonStop = true,
            minBaseFareMxn = 529,
            typicalBaseFareMxn = 899,
            promoSource = "Referencial · Aerus Golfo Sureste",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.TAR,
            outFlightNum = "YQ 540",
            retFlightNum = "YQ 541",
            originCode = "MZT",
            destCode = "LAP",
            outDepTime = "10:00",
            outArrTime = "11:05",
            retDepTime = "11:45",
            retArrTime = "12:50",
            durationMin = 65,
            aircraft = "Embraer ERJ-145",
            isNonStop = true,
            minBaseFareMxn = 489,
            typicalBaseFareMxn = 859,
            promoSource = "Referencial · TAR Mar de Cortés (inc. 10kg)",
            operatingStride = 2
        )
    )

    private val baseCatalog: List<FlightDeal> by lazy { build90DayCatalog() }
    private val liveFaresByKey = ConcurrentHashMap<String, ScrapedFare>()
    private val histogramWindowsByRoute = ConcurrentHashMap<String, RouteHistogramWindow>()

    @Volatile
    private var mergedCatalogCache: List<FlightDeal>? = null

    val catalog: List<FlightDeal>
        get() = mergedCatalogCache ?: synchronized(this) {
            mergedCatalogCache ?: rebuildMergedCatalog().also { mergedCatalogCache = it }
        }

    fun applyLiveFares(
        fares: List<ScrapedFare>,
        windows: List<RouteHistogramWindow> = emptyList()
    ): Int {
        if (fares.isEmpty() && windows.isEmpty()) return liveFaresByKey.size
        var changed = false

        for (window in windows) {
            if (!AirportData.isDomesticMexicanAirport(window.originCode) ||
                !AirportData.isDomesticMexicanAirport(window.destinationCode)
            ) continue
            val rKey = "${window.airline.iataCode}_${window.originCode}_${window.destinationCode}"
            val prev = histogramWindowsByRoute[rKey]
            val merged = if (prev == null) {
                window
            } else {
                prev.copy(
                    minDate = minOf(prev.minDate, window.minDate),
                    maxDate = maxOf(prev.maxDate, window.maxDate)
                )
            }
            if (prev != merged) {
                histogramWindowsByRoute[rKey] = merged
                changed = true
            }
        }

        for (fare in fares) {
            if (!AirportData.isDomesticMexicanAirport(fare.originCode) ||
                !AirportData.isDomesticMexicanAirport(fare.destinationCode)
            ) continue
            val key = fareKey(fare.airline, fare.originCode, fare.destinationCode, fare.departureDate)
            val prev = liveFaresByKey[key]
            val mergedFare = if (fare.tuaMxn == null && prev?.tuaMxn != null) {
                fare.copy(tuaMxn = prev.tuaMxn)
            } else {
                fare
            }
            if (prev == null ||
                prev.baseFareMxn != mergedFare.baseFareMxn ||
                prev.tuaMxn != mergedFare.tuaMxn ||
                prev.lastSeenLabel != mergedFare.lastSeenLabel ||
                prev.sourceUrl != mergedFare.sourceUrl
            ) {
                liveFaresByKey[key] = mergedFare
                changed = true
            }
        }
        if (changed) {
            synchronized(this) {
                mergedCatalogCache = rebuildMergedCatalog()
            }
        }
        return liveFaresByKey.size
    }

    fun liveFareCount(): Int = liveFaresByKey.size

    private fun fareKey(
        airline: Airline,
        originCode: String,
        destCode: String,
        date: TravelDate
    ): String = "${airline.iataCode}_${originCode.uppercase()}_${destCode.uppercase()}_${date.isoString}"

    private fun rebuildMergedCatalog(): List<FlightDeal> {
        if (liveFaresByKey.isEmpty() && histogramWindowsByRoute.isEmpty()) return baseCatalog

        val minLivePriceByRouteAirline = HashMap<String, Int>()
        val routeTuaByAirlineRoute = HashMap<String, Int>()
        val originTuaByAirlineOrigin = HashMap<String, Int>()
        for (scraped in liveFaresByKey.values) {
            val routeKey = "${scraped.airline.iataCode}_${scraped.originCode}_${scraped.destinationCode}"
            val currentMin = minLivePriceByRouteAirline[routeKey]
            if (currentMin == null || scraped.baseFareMxn < currentMin) {
                minLivePriceByRouteAirline[routeKey] = scraped.baseFareMxn
            }
            if (scraped.tuaMxn != null) {
                routeTuaByAirlineRoute.putIfAbsent(routeKey, scraped.tuaMxn)
                originTuaByAirlineOrigin.putIfAbsent("${scraped.airline.iataCode}_${scraped.originCode}", scraped.tuaMxn)
            }
        }

        val matchedKeys = HashSet<String>()
        val primaryUpdatedKeys = HashSet<String>()

        val updatedBase = baseCatalog.mapNotNull { deal ->
            val key = fareKey(deal.airline, deal.originCode, deal.destinationCode, deal.departureDate)
            val routeKey = "${deal.airline.iataCode}_${deal.originCode}_${deal.destinationCode}"
            val routeTua = routeTuaByAirlineRoute[routeKey]
                ?: originTuaByAirlineOrigin["${deal.airline.iataCode}_${deal.originCode}"]
            val scraped = liveFaresByKey[key]
            val liveWindow = histogramWindowsByRoute[routeKey]

            if (scraped == null) {
                // CRITICAL FIX: If the live dailyHistogram inspected this exact route & date
                // and returned 0 (no flight / no promo available), drop the phantom catalog row!
                if (liveWindow != null &&
                    deal.departureDate >= liveWindow.minDate &&
                    deal.departureDate <= liveWindow.maxDate
                ) {
                    return@mapNotNull null
                }

                val routeMinLive = minLivePriceByRouteAirline[routeKey]
                if (routeMinLive != null || routeTua != null) {
                    deal.copy(
                        baseFareMxn = if (routeMinLive != null) maxOf(deal.baseFareMxn, routeMinLive + 140) else deal.baseFareMxn,
                        originTuaMxn = routeTua ?: deal.originTuaMxn
                    )
                } else {
                    deal
                }
            } else {
                matchedKeys.add(key)
                val siteLabel = when (deal.airline) {
                    Airline.VOLARIS -> "Volaris.com"
                    Airline.VIVA_AEROBUS -> "VivaAerobus.com"
                    else -> deal.airline.displayName
                }
                val resolvedTua = scraped.tuaMxn ?: routeTua ?: deal.originTuaMxn
                val resolvedLiveUrl = scraped.sourceUrl?.takeIf {
                    it.contains("/vuelos-desde-") && it.contains("-a-")
                }
                if (primaryUpdatedKeys.add(key)) {
                    val ageNote = scraped.lastSeenLabel?.let { " ($it)" } ?: ""
                    deal.copy(
                        baseFareMxn = scraped.baseFareMxn,
                        originTuaMxn = resolvedTua,
                        isLiveVerified = true,
                        liveSourceLabel = "En vivo · $siteLabel",
                        promoSourceDescription = "Tarifa Zero real $siteLabel$ageNote",
                        liveBookingUrl = resolvedLiveUrl
                    )
                } else {
                    // Secondary flight on the same day must not undercut the live minimum fare
                    deal.copy(
                        baseFareMxn = maxOf(deal.baseFareMxn, scraped.baseFareMxn + 140),
                        originTuaMxn = resolvedTua,
                        isLiveVerified = false,
                        liveSourceLabel = null
                    )
                }
            }
        }

        val extraLiveDeals = ArrayList<FlightDeal>()
        for ((key, scraped) in liveFaresByKey) {
            if (key in matchedKeys) continue
            val originAirport = AirportData.findByCode(scraped.originCode) ?: continue
            val destAirport = AirportData.findByCode(scraped.destinationCode) ?: continue
            if (!originAirport.isMexican || !destAirport.isMexican) continue

            val distanceKm = originAirport.distanceKmTo(destAirport.latitude, destAirport.longitude)
            val estDurationMin = ((distanceKm / 720.0) * 60.0).roundToInt().coerceIn(45, 280)
            val routeKey = "${scraped.airline.iataCode}_${scraped.originCode}_${scraped.destinationCode}"
            val originTua = scraped.tuaMxn
                ?: routeTuaByAirlineRoute[routeKey]
                ?: originTuaByAirlineOrigin["${scraped.airline.iataCode}_${scraped.originCode}"]
                ?: originAirport.tuaForFlight(false)
            val depHour = 7 + (abs(scraped.originCode.hashCode() + scraped.destinationCode.hashCode()) % 12)
            val depTime = String.format(Locale.US, "%02d:15", depHour)
            val totalArrMin = depHour * 60 + 15 + estDurationMin
            val arrTime = String.format(Locale.US, "%02d:%02d", (totalArrMin / 60) % 24, totalArrMin % 60)
            val siteLabel = if (scraped.airline == Airline.VOLARIS) "Volaris.com" else "VivaAerobus.com"
            val flightNum = "${scraped.airline.iataCode} ${1100 + (abs(key.hashCode()) % 7900)}"
            val ageNote = scraped.lastSeenLabel?.let { " ($it)" } ?: ""
            val resolvedLiveUrl = scraped.sourceUrl?.takeIf {
                it.contains("/vuelos-desde-") && it.contains("-a-")
            }

            extraLiveDeals.add(
                FlightDeal(
                    id = "LIVE_${key}",
                    airline = scraped.airline,
                    flightNumber = flightNum,
                    originCode = scraped.originCode,
                    destinationCode = scraped.destinationCode,
                    departureDate = scraped.departureDate,
                    departureTime = depTime,
                    arrivalTime = arrTime,
                    departureHour = depHour,
                    durationMinutes = estDurationMin,
                    aircraftModel = if (scraped.airline == Airline.VOLARIS) "Airbus A320neo" else "Airbus A321neo",
                    isNonStop = true,
                    stopsCount = 0,
                    layoverAirportCode = null,
                    layoverDurationMinutes = null,
                    baseFareMxn = scraped.baseFareMxn,
                    originTuaMxn = originTua,
                    isInternational = false,
                    promoSourceDescription = "Tarifa Zero real $siteLabel$ageNote",
                    seatsRemaining = 4,
                    isLiveVerified = true,
                    liveSourceLabel = "En vivo · $siteLabel",
                    liveBookingUrl = resolvedLiveUrl
                )
            )
        }

        return updatedBase + extraLiveDeals
    }

    private fun build90DayCatalog(): List<FlightDeal> {
        val result = ArrayList<FlightDeal>(6000)
        for (dayOffset in 0 until 90) {
            val date = baseDate.plusDays(dayOffset)
            val dow = date.dayOfWeekMondayFirst // 1=Mon .. 7=Sun

            templates.forEachIndexed { idx, tpl ->
                if (dayOffset % tpl.operatingStride != 0) return@forEachIndexed

                val outFare = computeDynamicFare(
                    minFare = tpl.minBaseFareMxn,
                    typicalFare = tpl.typicalBaseFareMxn,
                    dayOffset = dayOffset,
                    dow = dow,
                    seed = idx
                )
                result.add(
                    createDeal(
                        id = "${tpl.outFlightNum.replace(" ", "")}_${tpl.originCode}_${tpl.destCode}_${date.isoString}",
                        airline = tpl.airline,
                        flightNumber = tpl.outFlightNum,
                        originCode = tpl.originCode,
                        destCode = tpl.destCode,
                        date = date,
                        depTime = tpl.outDepTime,
                        arrTime = tpl.outArrTime,
                        durationMin = tpl.durationMin,
                        aircraft = tpl.aircraft,
                        isNonStop = tpl.isNonStop,
                        stopsCount = tpl.stopsCount,
                        layoverCode = tpl.layoverCode,
                        layoverMin = tpl.layoverMin,
                        baseFareMxn = outFare,
                        promoSource = tpl.promoSource,
                        dayOffset = dayOffset,
                        seed = idx
                    )
                )

                val retFare = computeDynamicFare(
                    minFare = tpl.minBaseFareMxn,
                    typicalFare = tpl.typicalBaseFareMxn,
                    dayOffset = dayOffset + 1,
                    dow = dow,
                    seed = idx + 17
                )
                result.add(
                    createDeal(
                        id = "${tpl.retFlightNum.replace(" ", "")}_${tpl.destCode}_${tpl.originCode}_${date.isoString}",
                        airline = tpl.airline,
                        flightNumber = tpl.retFlightNum,
                        originCode = tpl.destCode,
                        destCode = tpl.originCode,
                        date = date,
                        depTime = tpl.retDepTime,
                        arrTime = tpl.retArrTime,
                        durationMin = tpl.durationMin,
                        aircraft = tpl.aircraft,
                        isNonStop = tpl.isNonStop,
                        stopsCount = tpl.stopsCount,
                        layoverCode = tpl.layoverCode,
                        layoverMin = tpl.layoverMin,
                        baseFareMxn = retFare,
                        promoSource = tpl.promoSource,
                        dayOffset = dayOffset,
                        seed = idx + 31
                    )
                )
            }
        }
        return result
    }

    private fun createDeal(
        id: String,
        airline: Airline,
        flightNumber: String,
        originCode: String,
        destCode: String,
        date: TravelDate,
        depTime: String,
        arrTime: String,
        durationMin: Int,
        aircraft: String,
        isNonStop: Boolean,
        stopsCount: Int,
        layoverCode: String?,
        layoverMin: Int?,
        baseFareMxn: Int,
        promoSource: String,
        dayOffset: Int,
        seed: Int
    ): FlightDeal {
        val originAirport = AirportData.findByCode(originCode)
        val originTua = originAirport?.tuaForFlight(false) ?: 520
        val depHour = depTime.substringBefore(":").toIntOrNull() ?: 10
        val seatsRemaining = 2 + (abs(dayOffset * 3 + seed) % 7)

        return FlightDeal(
            id = id,
            airline = airline,
            flightNumber = flightNumber,
            originCode = originCode,
            destinationCode = destCode,
            departureDate = date,
            departureTime = depTime,
            arrivalTime = arrTime,
            departureHour = depHour,
            durationMinutes = durationMin,
            aircraftModel = aircraft,
            isNonStop = isNonStop,
            stopsCount = stopsCount,
            layoverAirportCode = layoverCode,
            layoverDurationMinutes = layoverMin,
            baseFareMxn = baseFareMxn,
            originTuaMxn = originTua,
            isInternational = false,
            promoSourceDescription = promoSource,
            seatsRemaining = seatsRemaining
        )
    }

    private fun computeDynamicFare(
        minFare: Int,
        typicalFare: Int,
        dayOffset: Int,
        dow: Int,
        seed: Int
    ): Int {
        val isFlashWave = ((dayOffset + seed) % 5 == 0) || dow == 2 || dow == 3
        if (isFlashWave && dayOffset >= 2) {
            return minFare
        }
        val weekendMultiplier = when (dow) {
            5, 7 -> 1.15
            6 -> 0.85
            1, 4 -> 0.92
            else -> 0.78
        }
        val wave = ((abs(dayOffset * 7 + seed * 13) % 10) / 10.0)
        val interpolated = minFare + ((typicalFare - minFare) * (0.25 + 0.55 * wave) * weekendMultiplier).toInt()
        val clamped = interpolated.coerceIn(minFare, typicalFare + 180)
        return ((clamped / 10) * 10) + 9
    }

    fun getDealsForOriginsAndDestinations(
        origins: List<String>,
        destinations: List<String>
    ): List<FlightDeal> {
        val originSet = origins.map { it.uppercase() }.toSet()
        val destSet = destinations.map { it.uppercase() }.toSet()
        return catalog.filter { deal ->
            (originSet.isEmpty() || deal.originCode in originSet) &&
                (destSet.isEmpty() || deal.destinationCode in destSet)
        }
    }

    fun getDealsForRoute(originCode: String, destinationCode: String): List<FlightDeal> {
        val o = originCode.uppercase()
        val d = destinationCode.uppercase()
        return catalog.filter { it.originCode == o && it.destinationCode == d }
    }

    fun getReachableDestinations(originCode: String): List<String> {
        val o = originCode.uppercase()
        return catalog.asSequence()
            .filter { it.originCode == o }
            .map { it.destinationCode }
            .distinct()
            .toList()
    }
}
