package com.rige.dulcegest.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import com.rige.dulcegest.domain.models.DailySalesResult
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Query("SELECT * FROM sales ORDER BY sale_date DESC")
    fun getAll(): LiveData<List<Sale>>

    @Insert
    suspend fun insert(sale: Sale): Long

    @Update
    suspend fun update(sale: Sale)

    @Delete
    suspend fun delete(sale: Sale)

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleWithItems(id: Long): SaleWithItems?

    @Query("SELECT IFNULL(SUM(total_amount), 0) FROM sales WHERE DATE(sale_date) = DATE('now', 'localtime')")
    fun getTotalSalesToday(): LiveData<Double>

    @Query("SELECT IFNULL(SUM(total_amount), 0) FROM sales WHERE DATE(sale_date) >= DATE('now', '-6 days', 'localtime')")
    fun getTotalSalesThisWeek(): LiveData<Double>

    @Transaction
    @Query("""  
        SELECT * FROM sales  
        WHERE strftime('%Y', REPLACE(sale_date, 'T', ' ')) = strftime('%Y', 'now', 'localtime')  
          AND strftime('%W', REPLACE(sale_date, 'T', ' ')) = strftime('%W', 'now', 'localtime')  
        ORDER BY sale_date DESC  
    """)
    fun getSalesThisWeek(): LiveData<List<SaleWithItems>>

    @Query("""
        SELECT IFNULL(SUM(total_amount), 0) FROM sales 
        WHERE strftime('%Y-%m-%d', sale_date) >= :startDate 
          AND strftime('%Y-%m-%d', sale_date) <= :endDate
    """)
    fun getTotalSalesBetween(startDate: String, endDate: String): LiveData<Double>

    // Dentro de @Dao interface SaleDao
    @Query("SELECT IFNULL(SUM(total_amount), 0) FROM sales WHERE sale_date BETWEEN :startDate AND :endDate || ' 23:59:59'")
    suspend fun getTotalSalesBetweenSuspend(startDate: String, endDate: String): Double

    @Transaction
    @Query("SELECT * FROM sales ORDER BY sale_date DESC LIMIT 5")
    fun getLastFiveSales(): LiveData<List<SaleWithItems>>

    @Query("DELETE FROM sales")
    suspend fun deleteAllSales()

    @Query("""
        SELECT * FROM sales 
        WHERE strftime('%Y-%m-%d', sale_date) >= :startDate 
          AND strftime('%Y-%m-%d', sale_date) <= :endDate 
        ORDER BY sale_date DESC
    """)
    fun getSalesByDateRange(startDate: String, endDate: String): Flow<List<Sale>>

    @Query("""  
        SELECT   
            strftime('%Y-%m-%d', sale_date) as sale_date,  
            SUM(total_amount) AS total_sales  
        FROM sales  
        WHERE strftime('%Y-%m-%d', sale_date) BETWEEN :startDate AND :endDate  
        GROUP BY strftime('%Y-%m-%d', sale_date)  
        ORDER BY sale_date DESC  
    """)
    suspend fun getDailySalesByDateRange(startDate: String, endDate: String): List<DailySalesResult>
}