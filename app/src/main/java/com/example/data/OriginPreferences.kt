package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AirportData
import com.example.model.PricingBaggageTier

class OriginPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSelectedOrigins(): List<String> {
        val raw = prefs.getString(KEY_SELECTED_ORIGINS, null)
        if (raw.isNullOrBlank()) return listOf("MEX", "NLU")
        return raw.split(",")
            .map { it.trim().uppercase() }
            .filter { AirportData.isDomesticMexicanAirport(it) }
            .distinct()
            .ifEmpty { listOf("MEX", "NLU") }
    }

    fun saveSelectedOrigins(codes: List<String>) {
        val clean = codes
            .map { it.trim().uppercase() }
            .filter { AirportData.isDomesticMexicanAirport(it) }
            .distinct()
        prefs.edit().putString(KEY_SELECTED_ORIGINS, clean.joinToString(",")).apply()
    }

    fun getSelectedDestinations(): List<String> {
        val raw = prefs.getString(KEY_SELECTED_DESTINATIONS, null)
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(",")
            .map { it.trim().uppercase() }
            .filter { AirportData.isDomesticMexicanAirport(it) }
            .distinct()
    }

    fun saveSelectedDestinations(codes: List<String>) {
        val clean = codes
            .map { it.trim().uppercase() }
            .filter { AirportData.isDomesticMexicanAirport(it) }
            .distinct()
        prefs.edit().putString(KEY_SELECTED_DESTINATIONS, clean.joinToString(",")).apply()
    }

    fun hasAttemptedFirstLaunchGps(): Boolean =
        prefs.getBoolean(KEY_FIRST_LAUNCH_GPS_DONE, false)

    fun markFirstLaunchGpsAttempted() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH_GPS_DONE, true).apply()
    }

    fun getDetectedAirportCode(): String? =
        prefs.getString(KEY_DETECTED_AIRPORT, null)

    fun saveDetectedAirportCode(code: String) {
        prefs.edit().putString(KEY_DETECTED_AIRPORT, code.uppercase()).apply()
    }

    fun isDarkModeEnabled(): Boolean =
        prefs.getBoolean(KEY_DARK_MODE_ENABLED, false)

    fun saveDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE_ENABLED, enabled).commit()
    }

    fun isIncludeTua(): Boolean =
        prefs.getBoolean(KEY_INCLUDE_TUA, true)

    fun saveIncludeTua(include: Boolean) {
        prefs.edit().putBoolean(KEY_INCLUDE_TUA, include).commit()
    }

    fun getBaggageTier(): PricingBaggageTier {
        val raw = prefs.getString(KEY_BAGGAGE_TIER, null) ?: return PricingBaggageTier.BASICA_10KG
        return PricingBaggageTier.entries.firstOrNull { it.name == raw } ?: PricingBaggageTier.BASICA_10KG
    }

    fun saveBaggageTier(tier: PricingBaggageTier) {
        prefs.edit().putString(KEY_BAGGAGE_TIER, tier.name).commit()
    }

    companion object {
        private const val PREFS_NAME = "cheap_travel_prefs"
        private const val KEY_SELECTED_ORIGINS = "selected_origins"
        private const val KEY_SELECTED_DESTINATIONS = "selected_destinations"
        private const val KEY_FIRST_LAUNCH_GPS_DONE = "first_launch_gps_done"
        private const val KEY_DETECTED_AIRPORT = "detected_airport_code"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_INCLUDE_TUA = "include_tua"
        private const val KEY_BAGGAGE_TIER = "baggage_tier"
    }
}
