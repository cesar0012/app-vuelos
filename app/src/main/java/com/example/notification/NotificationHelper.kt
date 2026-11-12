package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.model.FareBreakdown
import com.example.model.FlightDeal

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "cheap_travel_deals"
        const val CHANNEL_NAME = "Alertas de Vuelos Baratos"
        const val CHANNEL_DESC = "Notificaciones de tarifas ultra bajas encontradas en Volaris, Viva, etc."
        const val NOTIFICATION_ID_BASE = 5000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendDealNotification(
        deal: FlightDeal,
        targetBudgetMxn: Double? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_DEAL_ID", deal.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            deal.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val baseFormatted = FareBreakdown.formatCurrency(deal.baseFareMxn)
        val totalFormatted = FareBreakdown.formatCurrency(deal.previewTotalMxn)
        val title = "Oferta de vuelo: ${deal.origin.code} ➔ ${deal.destination.code} a $baseFormatted"

        val budgetComparison = if (targetBudgetMxn != null) {
            " (Menor a tu límite de ${FareBreakdown.formatCurrency(targetBudgetMxn)})"
        } else ""

        val bigText = "${deal.airline.displayName} tiene un vuelo el ${deal.dayAndMonthFormatted} por solo $baseFormatted de tarifa base ($totalFormatted con TUA incluida)$budgetComparison. ¡Quedan ${deal.seatsAvailableAtPromo} asientos a este precio!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("Total con TUA: $totalFormatted con ${deal.airline.displayName}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROMO)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                deal.id.hashCode() + NOTIFICATION_ID_BASE,
                notification
            )
        } catch (_: SecurityException) {
            // Permission not granted yet
        }
    }

    fun sendTestPushNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Cheap Travel: Radar Activo")
            .setContentText("Monitoreando ofertas relámpago de Volaris, Viva y Aeroméxico...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tu radar está configurado. Te notificaremos automáticamente en cuanto detectemos vuelos por debajo de tus presupuestos o promociones flash desde $199 MXN.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 200, 100, 200))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(9999, notification)
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
