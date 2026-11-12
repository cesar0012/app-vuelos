package com.example.model

data class Airport(
    val code: String,              // e.g. "MEX", "NLU", "CUN"
    val name: String,              // e.g. "Aeropuerto Intl. Benito Juárez"
    val city: String,              // e.g. "Ciudad de México"
    val state: String,             // e.g. "CDMX"
    val country: String = "México",
    val isInternational: Boolean = false,
    val isBeachDestination: Boolean = false,
    val nationalTuaMxn: Double,    // Tarifa de Uso de Aeropuerto nacional
    val internationalTuaMxn: Double, // Tarifa TUA internacional
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    val displayTitle: String get() = "$city ($code)"
    val fullSubtitle: String get() = "$name • $country"

    /**
     * Calculates distance in kilometers using the Haversine formula
     */
    fun distanceKmTo(userLat: Double, userLon: Double): Double {
        val r = 6371.0 // Radius of earth in km
        val dLat = Math.toRadians(latitude - userLat)
        val dLon = Math.toRadians(longitude - userLon)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
