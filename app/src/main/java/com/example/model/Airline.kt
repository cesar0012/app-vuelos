package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AeromexicoBrand
import com.example.ui.theme.MexicanaBrand
import com.example.ui.theme.VivaBrand
import com.example.ui.theme.VolarisBrand

enum class Airline(
    val iataCode: String,
    val displayName: String,
    val brandColor: Color,
    val promoTag: String,
    val carryOn10kgMxn: Int,
    val checked25kgMxn: Int,
    val standardSeatMxn: Int,
    val extraLegroomSeatMxn: Int,
    val includesCarryOnInBase: Boolean,
    val officialPromoUrl: String
) {
    VOLARIS(
        iataCode = "Y4",
        displayName = "Volaris",
        brandColor = VolarisBrand,
        promoTag = "Tarifa Zero",
        carryOn10kgMxn = 365,
        checked25kgMxn = 620,
        standardSeatMxn = 199,
        extraLegroomSeatMxn = 449,
        includesCarryOnInBase = false,
        officialPromoUrl = "https://www.volaris.com/es-mx/vuelos-en-oferta"
    ),
    VIVA_AEROBUS(
        iataCode = "VB",
        displayName = "Viva Aerobus",
        brandColor = VivaBrand,
        promoTag = "Tarifa Zero",
        carryOn10kgMxn = 345,
        checked25kgMxn = 590,
        standardSeatMxn = 185,
        extraLegroomSeatMxn = 420,
        includesCarryOnInBase = false,
        officialPromoUrl = "https://www.vivaaerobus.com/es-mx/promociones"
    ),
    AEROMEXICO(
        iataCode = "AM",
        displayName = "Aeroméxico",
        brandColor = AeromexicoBrand,
        promoTag = "Tarifa Básica",
        carryOn10kgMxn = 0, // Básica incluye equipaje de mano 10kg
        checked25kgMxn = 750,
        standardSeatMxn = 260,
        extraLegroomSeatMxn = 590,
        includesCarryOnInBase = true,
        officialPromoUrl = "https://www.aeromexico.com/es-mx/vuelos-baratos"
    ),
    MEXICANA(
        iataCode = "MX",
        displayName = "Mexicana",
        brandColor = MexicanaBrand,
        promoTag = "Tarifa AIFA",
        carryOn10kgMxn = 0, // Incluye carry-on 10kg
        checked25kgMxn = 490,
        standardSeatMxn = 140,
        extraLegroomSeatMxn = 310,
        includesCarryOnInBase = true,
        officialPromoUrl = "https://mexicana.gob.mx/"
    )
}
