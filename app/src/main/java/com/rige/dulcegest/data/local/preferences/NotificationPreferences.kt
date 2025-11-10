package com.rige.dulcegest.data.local.preferences  
  
import android.content.Context  
import android.content.SharedPreferences  
import dagger.hilt.android.qualifiers.ApplicationContext  
import jakarta.inject.Inject  
import jakarta.inject.Singleton
import androidx.core.content.edit

@Singleton  
class NotificationPreferences @Inject constructor(  
    @ApplicationContext context: Context  
) {  
    private val prefs: SharedPreferences = context.getSharedPreferences(  
        "notification_prefs",  
        Context.MODE_PRIVATE  
    )  
      
    companion object {  
        private const val KEY_LAST_NOTIFICATION_TIME = "last_notification_time"  
    }  


    fun saveLastNotificationTime(timestamp: Long = System.currentTimeMillis()) {  
        prefs.edit { putLong(KEY_LAST_NOTIFICATION_TIME, timestamp) }
    }  

    fun getLastNotificationTime(): Long {  
        return prefs.getLong(KEY_LAST_NOTIFICATION_TIME, 0L)  
    }


    fun shouldShowNotification(): Boolean {
        val lastTime = getLastNotificationTime()
        val currentTime = System.currentTimeMillis()
        val secondsSince = (currentTime - lastTime) / 1000  // ⭐ Cambiar a segundos
        return secondsSince >= 10  // ⭐ Cambiar a 10 segundos
    }
}