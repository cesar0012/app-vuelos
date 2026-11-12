package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AeromexicoNavy
import com.example.ui.theme.MexicanaBurgundy
import com.example.ui.theme.VivaAerobusGreen
import com.example.ui.theme.VolarisPurple

enum class Airline(
    val code: String,
    val displayName: String,
    val brandColor: Color,
    val defaultCarryOnFeeMxn: Double,
    val defaultCheckedBagFeeMxn: Double,
    val defaultStandardSeatFeeMxn: Double,
    val defaultExtraLegroomFeeMxn: Double,
    val famousPromoLabel: String
) {
    VOLARIS(
        code = "Y4",
        displayName = "Volaris",
        brandColor = VolarisPurple,
        defaultCarryOnFeeMxn = 289.0,
        defaultCheckedBagFeeMxn = 650.0,
        defaultStandardSeatFeeMxn = 129.0,
        defaultExtraLegroomFeeMxn = 289.0,
        famousPromoLabel = "Vuela desde $199 MXN"
    ),
    VIVA_AEROBUS(
        code = "VB",
        displayName = "Viva Aerobus",
        brandColor = VivaAerobusGreen,
        defaultCarryOnFeeMxn = 270.0,
        defaultCheckedBagFeeMxn = 620.0,
        defaultStandardSeatFeeMxn = 119.0,
        defaultExtraLegroomFeeMxn = 269.0,
        famousPromoLabel = "Tarifa Zero desde $249 MXN"
    ),
    AEROMEXICO(
        code = "AM",
        displayName = "Aeroméxico",
        brandColor = AeromexicoNavy,
        defaultCarryOnFeeMxn = 350.0,
        defaultCheckedBagFeeMxn = 750.0,
        defaultStandardSeatFeeMxn = 160.0,
        defaultExtraLegroomFeeMxn = 380.0,
        famousPromoLabel = "Venta Azul"
    ),
    MEXICANA(
        code = "MX",
        displayName = "Mexicana",
        brandColor = MexicanaBurgundy,
        defaultCarryOnFeeMxn = 220.0,
        defaultCheckedBagFeeMxn = 550.0,
        defaultStandardSeatFeeMxn = 99.0,
        defaultExtraLegroomFeeMxn = 240.0,
        famousPromoLabel = "Rutas Sociales desde $399 MXN"
    );

    companion object {
        fun fromCode(code: String): Airline = entries.firstOrNull { it.code == code } ?: VOLARIS
    }
}
