package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.ProductDao
import com.rige.dulcegest.data.local.dao.SaleDao
import com.rige.dulcegest.data.local.dao.SaleItemDao
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import jakarta.inject.Inject

class SaleRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val itemDao: SaleItemDao,
    private val productDao: ProductDao
) {
    val allSales: LiveData<List<Sale>> = saleDao.getAll()

    val lastFiveSales: LiveData<List<SaleWithItems>> = saleDao.getLastFiveSales()

    val salesOfThisWeek: LiveData<List<SaleWithItems>> = saleDao.getSalesThisWeek()

    /**
     * 🟢 1. IMPLEMENTACIÓN DE LA TRANSACCIÓN (RENOMBRADO y AJUSTE DE RETORNO)
     *
     * Nota: Este método DEBE ejecutarse como una transacción atómica de Room.
     * Si no tienes acceso a la AppDatabase aquí, se asume que los DAOs o la inyección
     * de dependencias garantizan el rollback si falla.
     */
    suspend fun insertSaleTransaction(sale: Sale, items: List<SaleItem>): Long {
        // 1. Insertar la Venta y obtener el ID
        val saleId = saleDao.insert(sale)

        // 2. Preparar e Insertar los Ítems
        val itemList = items.map { it.copy(saleId = saleId) }
        itemDao.insertAll(itemList)

        // 3. Reducir Stock (la lógica ya estaba en la versión anterior)
        itemList.forEach { item ->
            // Aseguramos un valor por defecto de 1.0 para presentationQuantity
            val presentationFactor = item.presentationQuantity ?: 1.0
            val qtyToSubtract = item.qty * presentationFactor
            productDao.reduceStock(item.productId, qtyToSubtract)
        }

        return saleId // Se requiere el ID para el Use Case
    }

    // 🟢 2. NUEVO MÉTODO PARA CONSULTA POR RANGO DE FECHAS
    // Necesario para los Use Cases de totales semanales y diarios.
    fun getTotalSalesBetween(startDate: String, endDate: String): LiveData<Double?> {
        // ⚠️ Asume que SaleDao tiene el método:
        // @Query("SELECT SUM(totalAmount) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate || ' 23:59:59'")
        // fun getTotalSalesBetween(startDate: String, endDate: String): LiveData<Double?>
        return saleDao.getTotalSalesBetween(startDate, endDate)
    }

    suspend fun getSaleWithItems(id: Long) = saleDao.getSaleWithItems(id)

    suspend fun deleteSale(sale: Sale) = saleDao.delete(sale)

    // 🗑️ Estos métodos ya no son necesarios si los Use Cases de fechas son la fuente de la verdad
    // fun getTotalSalesToday() = saleDao.getTotalSalesToday()
    // fun getTotalSalesThisWeek() = saleDao.getTotalSalesThisWeek()
    // Puedes dejarlos si se usan en otros sitios, pero ya no los necesitará el ViewModel.

    fun getTotalSalesToday() = saleDao.getTotalSalesToday()
    fun getTotalSalesThisWeek() = saleDao.getTotalSalesThisWeek()


    suspend fun deleteAll() {
        saleDao.deleteAllSales()
    }
}