package com.example.data

import android.content.Context
import android.content.SharedPreferences

class OriginPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSelectedOrigins(): List<String> {
        val raw = prefs.getString(KEY_SELECTED_ORIGINS, null)
        if (raw.isNullOrBlank()) return listOf("MEX", "NLU")
        return raw.split(",").map { it.trim().uppercase() }.filter { it.isNotEmpty() }.distinct()
            .ifEmpty { listOf("MEX") }
    }

    fun saveSelectedOrigins(codes: List<String>) {
        val clean = codes.map { it.trim().uppercase() }.filter { it.isNotEmpty() }.distinct()
        prefs.edit().putString(KEY_SELECTED_ORIGINS, clean.joinToString(",")).apply()
    }

    fun getSelectedDestinations(): List<String> {
        val raw = prefs.getString(KEY_SELECTED_DESTINATIONS, null)
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(",").map { it.trim().uppercase() }.filter { it.isNotEmpty() }.distinct()
    }

    fun saveSelectedDestinations(codes: List<String>) {
        val clean = codes.map { it.trim().uppercase() }.filter { it.isNotEmpty() }.distinct()
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

    companion object {
        private const val PREFS_NAME = "cheap_travel_prefs"
        private const val KEY_SELECTED_ORIGINS = "selected_origins"
        private const val KEY_SELECTED_DESTINATIONS = "selected_destinations"
        private const val KEY_FIRST_LAUNCH_GPS_DONE = "first_launch_gps_done"
        private const val KEY_DETECTED_AIRPORT = "detected_airport_code"
    }
}
