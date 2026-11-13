package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightAlertDao {
    @Query("SELECT * FROM flight_alerts ORDER BY createdAtMillis DESC")
    fun getAllAlerts(): Flow<List<FlightAlert>>

    @Query("SELECT * FROM flight_alerts WHERE isActive = 1 ORDER BY createdAtMillis DESC")
    fun getActiveAlerts(): Flow<List<FlightAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: FlightAlert): Long

    @Update
    suspend fun updateAlert(alert: FlightAlert)

    @Query("UPDATE flight_alerts SET isActive = :isActive WHERE id = :id")
    suspend fun setAlertActive(id: Int, isActive: Boolean)

    @Query("DELETE FROM flight_alerts WHERE id = :id")
    suspend fun deleteAlertById(id: Int)
}
