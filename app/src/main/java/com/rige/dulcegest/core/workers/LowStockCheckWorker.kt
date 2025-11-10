package com.rige.dulcegest.core.workers  
  
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rige.dulcegest.data.local.preferences.NotificationPreferences  
import com.rige.dulcegest.data.repository.SupplyRepository  
import com.rige.dulcegest.core.utils.NotificationHelper
import dagger.assisted.Assisted  
import dagger.assisted.AssistedInject

@HiltWorker
class LowStockCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val supplyRepository: SupplyRepository,
    private val notificationPreferences: NotificationPreferences
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val lowStockSupplies = supplyRepository.getAllOnce()
                .filter { it.stockQty <= 5.0 }

            if (lowStockSupplies.isNotEmpty()) {
                if (notificationPreferences.shouldShowNotification()) {
                    NotificationHelper.showLowStockNotification(
                        applicationContext,
                        lowStockSupplies
                    )
                    notificationPreferences.saveLastNotificationTime()
                }
            } else {
                NotificationHelper.cancelLowStockNotification(applicationContext)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}