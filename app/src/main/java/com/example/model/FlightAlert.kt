package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flight_alerts")
data class FlightAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originCode: String,
    val originCity: String,
    val destinationCode: String,
    val destinationCity: String,
    val targetMaxPriceMxn: Double,
    val targetMonth: String = "Cualquier fecha",
    val airlinePreference: String = "Todas las mexicanas",
    val isEnabled: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val lastCheckedTimestamp: Long = System.currentTimeMillis(),
    val bestPriceDetectedMxn: Double? = null,
    val lastDealAirline: String? = null
)
