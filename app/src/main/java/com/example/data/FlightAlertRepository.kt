package com.example.data

import kotlinx.coroutines.flow.Flow

class FlightAlertRepository(private val dao: FlightAlertDao) {
    val allAlerts: Flow<List<FlightAlert>> = dao.getAllAlerts()
    val activeAlerts: Flow<List<FlightAlert>> = dao.getActiveAlerts()

    suspend fun insert(alert: FlightAlert): Long = dao.insertAlert(alert)

    suspend fun setActive(id: Int, isActive: Boolean) = dao.setAlertActive(id, isActive)

    suspend fun deleteById(id: Int) = dao.deleteAlertById(id)
}
