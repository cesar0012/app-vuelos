package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flight_alerts")
data class FlightAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originCode: String,
    val destinationCode: String?, // null means "Cualquier destino"
    val maxPriceMxn: Int,
    val nonStopOnly: Boolean = false,
    val isActive: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis()
)
