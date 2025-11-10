package com.rige.dulcegest.core.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rige.dulcegest.R
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.ui.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID = "low_stock_channel"
    private const val NOTIFICATION_ID = 1001

    /**
     * Crea el canal de notificación (requerido para Android 8.0+)
     * Debe llamarse al iniciar la app, típicamente en Application o MainActivity
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alertas de Stock Bajo",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones cuando los insumos están por agotarse"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra la notificación de stock bajo
     * @param context Contexto de la aplicación
     * @param supplies Lista de insumos con stock bajo
     */
    fun showLowStockNotification(context: Context, supplies: List<Supply>) {
        if (supplies.isEmpty()) return

        // Crear intent para navegar a la lista de insumos al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "supplies")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir el texto de la notificación
        val supplyNames = supplies.take(3).joinToString(", ") { it.name }
        val moreCount = if (supplies.size > 3) " y ${supplies.size - 3} más" else ""

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_warn)
            .setContentTitle("⚠️ Insumos con stock bajo")
            .setContentText("${supplies.size} insumos críticos: $supplyNames$moreCount")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Insumos críticos:\n${supplies.joinToString("\n") { "• ${it.name} (${it.stockQty} ${it.unit})" }}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Verificar permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // No tenemos permiso, no podemos mostrar la notificación
                // El permiso debe solicitarse desde una Activity, no desde aquí
                return
            }
        }

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    /**
     * Cancela la notificación de stock bajo
     */
    fun cancelLowStockNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}