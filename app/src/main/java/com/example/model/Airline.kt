package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AeromexicoBrand
import com.example.ui.theme.AerusBrand
import com.example.ui.theme.MagnichartersBrand
import com.example.ui.theme.MexicanaBrand
import com.example.ui.theme.TarBrand
import com.example.ui.theme.VivaBrand
import com.example.ui.theme.VolarisBrand

enum class Airline(
    val iataCode: String,
    val displayName: String,
    val brandColor: Color,
    val promoTag: String,
    val zeroTierLabel: String,
    val basicTierLabel: String,
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
        zeroTierLabel = "Zero (solo mochila)",
        basicTierLabel = "Básica (carry-on 10 kg)",
        carryOn10kgMxn = 365,
        checked25kgMxn = 620,
        standardSeatMxn = 199,
        extraLegroomSeatMxn = 449,
        includesCarryOnInBase = false,
        officialPromoUrl = "https://www.volaris.com/es-mx/"
    ),
    VIVA_AEROBUS(
        iataCode = "VB",
        displayName = "Viva Aerobus",
        brandColor = VivaBrand,
        promoTag = "Tarifa Zero",
        zeroTierLabel = "Zero (solo mochila)",
        basicTierLabel = "Light (carry-on 10 kg)",
        carryOn10kgMxn = 380,
        checked25kgMxn = 610,
        standardSeatMxn = 185,
        extraLegroomSeatMxn = 420,
        includesCarryOnInBase = false,
        officialPromoUrl = "https://www.vivaaerobus.com/es-mx"
    ),
    AEROMEXICO(
        iataCode = "AM",
        displayName = "Aeroméxico",
        brandColor = AeromexicoBrand,
        promoTag = "Tarifa Básica",
        zeroTierLabel = "Básica (inc. 10 kg)",
        basicTierLabel = "Básica (inc. 10 kg)",
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
        zeroTierLabel = "Base (inc. 10 kg)",
        basicTierLabel = "Base (inc. 10 kg)",
        carryOn10kgMxn = 0, // Incluye carry-on 10kg
        checked25kgMxn = 490,
        standardSeatMxn = 140,
        extraLegroomSeatMxn = 310,
        includesCarryOnInBase = true,
        officialPromoUrl = "https://mexicana.gob.mx/"
    ),
    TAR(
        iataCode = "YQ",
        displayName = "TAR Aerolíneas",
        brandColor = TarBrand,
        promoTag = "Tarifa Light Regional",
        zeroTierLabel = "Light (inc. 10 kg)",
        basicTierLabel = "Light (inc. 10 kg)",
        carryOn10kgMxn = 0, // Incluye equipaje de mano 10 kg
        checked25kgMxn = 520,
        standardSeatMxn = 160,
        extraLegroomSeatMxn = 320,
        includesCarryOnInBase = true,
        officialPromoUrl = "https://www.tarmexico.com/"
    ),
    AERUS(
        iataCode = "ZV",
        displayName = "Aerus",
        brandColor = AerusBrand,
        promoTag = "Tarifa Light Regional",
        zeroTierLabel = "Light (solo mochila)",
        basicTierLabel = "Business (carry-on 10 kg)",
        carryOn10kgMxn = 350, // Light solo incluye artículo personal
        checked25kgMxn = 550,
        standardSeatMxn = 150,
        extraLegroomSeatMxn = 290,
        includesCarryOnInBase = false,
        officialPromoUrl = "https://www.flyaerus.com/"
    ),
    MAGNICHARTERS(
        iataCode = "UJ",
        displayName = "Magnicharters",
        brandColor = MagnichartersBrand,
        promoTag = "Tarifa Turística",
        zeroTierLabel = "Turística (inc. 10 kg)",
        basicTierLabel = "Turística (inc. 10 kg)",
        carryOn10kgMxn = 0, // Incluye maleta de mano 10 kg
        checked25kgMxn = 450,
        standardSeatMxn = 150,
        extraLegroomSeatMxn = 300,
        includesCarryOnInBase = true,
        officialPromoUrl = "https://www.magnicharters.com/"
    )
}
