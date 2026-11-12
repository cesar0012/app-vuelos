package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.FlightAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightAlertDao {
    @Query("SELECT * FROM flight_alerts ORDER BY createdTimestamp DESC")
    fun getAllAlerts(): Flow<List<FlightAlert>>

    @Query("SELECT * FROM flight_alerts WHERE isEnabled = 1")
    suspend fun getActiveAlerts(): List<FlightAlert>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: FlightAlert): Long

    @Update
    suspend fun updateAlert(alert: FlightAlert)

    @Delete
    suspend fun deleteAlert(alert: FlightAlert)

    @Query("UPDATE flight_alerts SET isEnabled = :enabled WHERE id = :id")
    suspend fun setAlertEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE flight_alerts SET bestPriceDetectedMxn = :price, lastDealAirline = :airline, lastCheckedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateBestPrice(id: Long, price: Double, airline: String, timestamp: Long = System.currentTimeMillis())
}
