package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.FlightAlert

@Database(entities = [FlightAlert::class], version = 1, exportSchema = false)
abstract class CheapTravelDatabase : RoomDatabase() {
    abstract fun flightAlertDao(): FlightAlertDao

    companion object {
        @Volatile
        private var INSTANCE: CheapTravelDatabase? = null

        fun getDatabase(context: Context): CheapTravelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CheapTravelDatabase::class.java,
                    "cheap_travel_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
