package com.example.data

import com.example.model.Airline
import com.example.model.AirportData
import com.example.model.FlightDeal
import com.example.model.TravelDate
import kotlin.math.abs

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
    val operatingStride: Int = 1 // 1 = diario, 2 = cada 2 dias, etc.
)

class FlightDealsRepository(
    private val baseDate: TravelDate = TravelDate.today()
) {
    private val templates: List<RouteTemplate> = listOf(
        // AGU <-> PVR (Ruta clave: Volaris directo 58m $199, Viva 1 escala MTY, Aeromexico 1 escala MEX)
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
            typicalBaseFareMxn = 449,
            promoSource = "Tarifa Zero Volaris · Flash Bajío-Pacífico"
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
            minBaseFareMxn = 249,
            typicalBaseFareMxn = 529,
            promoSource = "Tarifa Zero Volaris · Salida Nocturna",
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
            minBaseFareMxn = 279,
            typicalBaseFareMxn = 569,
            promoSource = "Tarifa Zero Viva · Conexión MTY"
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
            minBaseFareMxn = 649,
            typicalBaseFareMxn = 1090,
            promoSource = "Básica Aeroméxico · Conexión AICM",
            operatingStride = 2
        ),

        // AGU <-> CUN, TIJ, MEX, LAX, ORD
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
            typicalBaseFareMxn = 629,
            promoSource = "Tarifa Zero Volaris · Promo Caribe"
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
            minBaseFareMxn = 289,
            typicalBaseFareMxn = 679,
            promoSource = "Tarifa Zero Viva · Escala MTY"
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
            promoSource = "Tarifa Zero Volaris · Frontera CBX"
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
            minBaseFareMxn = 499,
            typicalBaseFareMxn = 890,
            promoSource = "Básica Aeroméxico · Puente Bajío"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 1820",
            retFlightNum = "Y4 1821",
            originCode = "AGU",
            destCode = "LAX",
            outDepTime = "11:00",
            outArrTime = "12:45",
            retDepTime = "14:00",
            retArrTime = "19:20",
            durationMin = 225,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 1190,
            typicalBaseFareMxn = 2190,
            promoSource = "Volaris Internacional · Tarifa Zero USA",
            operatingStride = 2
        ),

        // NLU (AIFA) — Rutas TUA Ultra Baja ($309 MXN)
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 489,
            promoSource = "Tarifa Zero Viva · Especial AIFA $309 TUA"
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
            typicalBaseFareMxn = 519,
            promoSource = "Tarifa Zero Volaris · Salida AIFA"
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
            minBaseFareMxn = 279,
            typicalBaseFareMxn = 599,
            promoSource = "Mexicana · Incluye Carry-on 10kg desde AIFA"
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
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 549,
            promoSource = "Tarifa Zero Viva · Caribe Sur Tulum"
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 469,
            promoSource = "Tarifa Zero Viva · AIFA Pacífico"
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
            promoSource = "Tarifa Zero Volaris · Vuelo Directo"
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
            minBaseFareMxn = 319,
            typicalBaseFareMxn = 699,
            promoSource = "Mexicana · Tarifa AIFA Baja Sur"
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
            minBaseFareMxn = 289,
            typicalBaseFareMxn = 699,
            promoSource = "Tarifa Zero Viva · Nocturno Frontera"
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 429,
            promoSource = "Tarifa Zero Viva · Escapada Oaxaca"
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
            minBaseFareMxn = 459,
            typicalBaseFareMxn = 890,
            promoSource = "Básica Aeroméxico · AIFA Mérida"
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
            minBaseFareMxn = 269,
            typicalBaseFareMxn = 589,
            promoSource = "Mexicana · Pacífico Sinaloa",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 9420",
            retFlightNum = "VB 9421",
            originCode = "NLU",
            destCode = "BOG",
            outDepTime = "22:30",
            outArrTime = "04:10",
            retDepTime = "05:15",
            retArrTime = "09:05",
            durationMin = 280,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 1490,
            typicalBaseFareMxn = 2690,
            promoSource = "Viva Internacional · AIFA - El Dorado",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 9440",
            retFlightNum = "VB 9441",
            originCode = "NLU",
            destCode = "IAH",
            outDepTime = "08:50",
            outArrTime = "11:15",
            retDepTime = "12:10",
            retArrTime = "14:30",
            durationMin = 145,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 890,
            typicalBaseFareMxn = 1690,
            promoSource = "Viva Internacional · AIFA Texas",
            operatingStride = 2
        ),

        // MEX (AICM) — Alta frecuencia y competencia directa
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 549,
            promoSource = "Tarifa Zero Volaris · Salida Temprano AICM"
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
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 569,
            promoSource = "Tarifa Zero Viva · AICM T1"
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
            typicalBaseFareMxn = 1190,
            promoSource = "Básica Aeroméxico · Terminal 2 AICM"
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 499,
            promoSource = "Tarifa Zero Volaris · Pacífico Directo"
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
            minBaseFareMxn = 229,
            typicalBaseFareMxn = 519,
            promoSource = "Tarifa Zero Viva · Pacífico Tarde"
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
            minBaseFareMxn = 249,
            typicalBaseFareMxn = 649,
            promoSource = "Tarifa Zero Volaris · Los Cabos"
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
            minBaseFareMxn = 239,
            typicalBaseFareMxn = 549,
            promoSource = "Tarifa Zero Viva · Bahías de Huatulco"
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
            minBaseFareMxn = 259,
            typicalBaseFareMxn = 599,
            promoSource = "Tarifa Zero Volaris · Nuevo Tulum"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 902",
            retFlightNum = "Y4 903",
            originCode = "MEX",
            destCode = "LAX",
            outDepTime = "07:00",
            outArrTime = "09:15",
            retDepTime = "11:00",
            retArrTime = "16:50",
            durationMin = 255,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 1290,
            typicalBaseFareMxn = 2390,
            promoSource = "Volaris Internacional · California"
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 408",
            retFlightNum = "AM 409",
            originCode = "MEX",
            destCode = "JFK",
            outDepTime = "08:45",
            outArrTime = "15:00",
            retDepTime = "16:40",
            retArrTime = "21:20",
            durationMin = 315,
            aircraft = "Boeing 737 MAX 9",
            isNonStop = true,
            minBaseFareMxn = 2290,
            typicalBaseFareMxn = 3890,
            promoSource = "Básica Aeroméxico · Nueva York Directo",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 21",
            retFlightNum = "AM 22",
            originCode = "MEX",
            destCode = "MAD",
            outDepTime = "18:20",
            outArrTime = "12:10",
            retDepTime = "14:25",
            retArrTime = "19:30",
            durationMin = 650,
            aircraft = "Boeing 787-9 Dreamliner",
            isNonStop = true,
            minBaseFareMxn = 5990,
            typicalBaseFareMxn = 8990,
            promoSource = "Aeroméxico Europa · Dreamliner Directo",
            operatingStride = 2
        ),

        // GDL — Guadalajara Hub
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
            typicalBaseFareMxn = 419,
            promoSource = "Tarifa Zero Volaris · Salto Costa Jalisco"
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
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 599,
            promoSource = "Tarifa Zero Viva · GDL - Caribe"
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 579,
            promoSource = "Tarifa Zero Volaris · Promo Flash GDL"
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 940",
            retFlightNum = "Y4 941",
            originCode = "GDL",
            destCode = "LAX",
            outDepTime = "09:20",
            outArrTime = "11:05",
            retDepTime = "12:15",
            retArrTime = "17:35",
            durationMin = 225,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 1090,
            typicalBaseFareMxn = 2090,
            promoSource = "Volaris Internacional · GDL California"
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
            minBaseFareMxn = 239,
            typicalBaseFareMxn = 599,
            promoSource = "Tarifa Zero Viva · GDL - Tijuana"
        ),

        // MTY — Monterrey Hub
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 549,
            promoSource = "Tarifa Zero Viva · Hub Monterrey"
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
            minBaseFareMxn = 209,
            typicalBaseFareMxn = 569,
            promoSource = "Tarifa Zero Volaris · Regio Caribe"
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 489,
            promoSource = "Tarifa Zero Viva · Monterrey Pacífico"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 4510",
            retFlightNum = "VB 4511",
            originCode = "MTY",
            destCode = "IAH",
            outDepTime = "11:30",
            outArrTime = "13:05",
            retDepTime = "14:00",
            retArrTime = "15:35",
            durationMin = 95,
            aircraft = "Airbus A320",
            isNonStop = true,
            minBaseFareMxn = 790,
            typicalBaseFareMxn = 1490,
            promoSource = "Viva Internacional · MTY - Houston"
        ),

        // TIJ, BJX, QRO, CUN
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
            minBaseFareMxn = 199,
            typicalBaseFareMxn = 499,
            promoSource = "Tarifa Zero Volaris · Baja Directo"
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
            typicalBaseFareMxn = 469,
            promoSource = "Tarifa Zero Volaris · Bajío Pacífico"
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
            minBaseFareMxn = 219,
            typicalBaseFareMxn = 559,
            promoSource = "Tarifa Zero Viva · Querétaro TUA Baja"
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 5600",
            retFlightNum = "VB 5601",
            originCode = "CUN",
            destCode = "MIA",
            outDepTime = "12:15",
            outArrTime = "15:00",
            retDepTime = "16:10",
            retArrTime = "17:00",
            durationMin = 105,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 890,
            typicalBaseFareMxn = 1690,
            promoSource = "Viva Internacional · Caribe - Florida",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 3910",
            retFlightNum = "Y4 3911",
            originCode = "CUN",
            destCode = "SJO",
            outDepTime = "11:20",
            outArrTime = "13:00",
            retDepTime = "13:55",
            retArrTime = "17:35",
            durationMin = 160,
            aircraft = "Airbus A320neo",
            isNonStop = true,
            minBaseFareMxn = 1190,
            typicalBaseFareMxn = 2190,
            promoSource = "Volaris Centroamérica · Costa Rica",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VIVA_AEROBUS,
            outFlightNum = "VB 1420",
            retFlightNum = "VB 1421",
            originCode = "MEX",
            destCode = "LAS",
            outDepTime = "13:45",
            outArrTime = "15:55",
            retDepTime = "17:10",
            retArrTime = "23:10",
            durationMin = 250,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 1390,
            typicalBaseFareMxn = 2490,
            promoSource = "Viva Internacional · Las Vegas Flash",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.VOLARIS,
            outFlightNum = "Y4 980",
            retFlightNum = "Y4 981",
            originCode = "MEX",
            destCode = "ORD",
            outDepTime = "23:15",
            outArrTime = "04:20",
            retDepTime = "06:00",
            retArrTime = "10:25",
            durationMin = 245,
            aircraft = "Airbus A321neo",
            isNonStop = true,
            minBaseFareMxn = 1490,
            typicalBaseFareMxn = 2690,
            promoSource = "Volaris Internacional · Chicago Nocturno",
            operatingStride = 2
        ),
        RouteTemplate(
            airline = Airline.AEROMEXICO,
            outFlightNum = "AM 690",
            retFlightNum = "AM 691",
            originCode = "MEX",
            destCode = "GUA",
            outDepTime = "09:50",
            outArrTime = "11:45",
            retDepTime = "12:40",
            retArrTime = "14:40",
            durationMin = 115,
            aircraft = "Boeing 737-800",
            isNonStop = true,
            minBaseFareMxn = 1190,
            typicalBaseFareMxn = 1990,
            promoSource = "Básica Aeroméxico · Centroamérica",
            operatingStride = 2
        )
    )

    val catalog: List<FlightDeal> by lazy { build90DayCatalog() }

    private fun build90DayCatalog(): List<FlightDeal> {
        val result = ArrayList<FlightDeal>(4500)
        for (dayOffset in 0 until 90) {
            val date = baseDate.plusDays(dayOffset)
            val dow = date.dayOfWeekMondayFirst // 1=Mon .. 7=Sun

            templates.forEachIndexed { idx, tpl ->
                if (dayOffset % tpl.operatingStride != 0) return@forEachIndexed

                // Outbound deal
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

                // Bidirectional return deal (ensures round-trip works for every route in Calendar)
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
        val destAirport = AirportData.findByCode(destCode)
        val isInternational = (originAirport?.isMexican == false) || (destAirport?.isMexican == false)
        val originTua = originAirport?.tuaForFlight(isInternational) ?: 520
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
            isInternational = isInternational,
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
        // Flash promo days: Tue(2), Wed(3), Sat(6), or deterministic promo wave every few days
        val isFlashWave = ((dayOffset + seed) % 5 == 0) || dow == 2 || dow == 3
        if (isFlashWave && dayOffset >= 2) {
            return minFare
        }
        val weekendMultiplier = when (dow) {
            5, 7 -> 1.15 // Vie / Dom ligeramente más alto
            6 -> 0.85 // Sáb intermedio
            1, 4 -> 0.92
            else -> 0.78
        }
        val wave = ((abs(dayOffset * 7 + seed * 13) % 10) / 10.0)
        val interpolated = minFare + ((typicalFare - minFare) * (0.25 + 0.55 * wave) * weekendMultiplier).toInt()
        val clamped = interpolated.coerceIn(minFare, typicalFare + 180)
        // Redondear a terminación realista (9)
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

    /**
     * Returns all supported destinations reachable from a given origin, or all airports if fallback needed.
     */
    fun getReachableDestinations(originCode: String): List<String> {
        val o = originCode.uppercase()
        return catalog.asSequence()
            .filter { it.originCode == o }
            .map { it.destinationCode }
            .distinct()
            .toList()
    }
}
