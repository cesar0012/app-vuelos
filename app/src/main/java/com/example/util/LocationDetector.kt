package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.model.Airport
import com.example.model.AirportData

data class DetectedAirportResult(
    val airport: Airport,
    val distanceKm: Int,
    val providerUsed: String
)

object LocationDetector {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun detectNearestMexicanAirport(context: Context): DetectedAirportResult? {
        if (!hasLocationPermission(context)) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        var usedProvider = "GPS"
        for (provider in providers) {
            val loc = try {
                if (lm.isProviderEnabled(provider) || provider == LocationManager.PASSIVE_PROVIDER) {
                    lm.getLastKnownLocation(provider)
                } else {
                    null
                }
            } catch (_: SecurityException) {
                null
            } catch (_: IllegalArgumentException) {
                null
            }
            if (loc != null) {
                if (bestLocation == null || loc.time > bestLocation.time) {
                    bestLocation = loc
                    usedProvider = provider
                }
            }
        }

        val finalLocation = bestLocation ?: return null
        val nearest = AirportData.nearestMexicanAirport(finalLocation.latitude, finalLocation.longitude)
        val distKm = nearest.distanceKmTo(finalLocation.latitude, finalLocation.longitude).toInt()
        return DetectedAirportResult(
            airport = nearest,
            distanceKm = distKm,
            providerUsed = usedProvider
        )
    }
}
