package com.rige.dulcegest.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SettingsDao {
    @Query("DELETE FROM production_consumptions")
    suspend fun deleteAllProductionConsumptions()

    @Query("DELETE FROM production_batches")
    suspend fun deleteAllProductionBatches()

    @Query("DELETE FROM product_recipes")
    suspend fun deleteAllProductRecipes()

    @Query("DELETE FROM product_presentations")
    suspend fun deleteAllProductPresentations()

    @Query("DELETE FROM product_variants")
    suspend fun deleteAllProductVariants()

    @Query("DELETE FROM sale_items")
    suspend fun deleteAllSaleItems()

    @Query("DELETE FROM sales")
    suspend fun deleteAllSales()

    @Query("DELETE FROM purchases")
    suspend fun deleteAllPurchases()

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("DELETE FROM supplies")
    suspend fun deleteAllSupplies()

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    @Transaction
    suspend fun deleteAllData() {
        // 1️⃣ Tablas hijas (dependen de otras)
        deleteAllProductionConsumptions()
        deleteAllSaleItems()
        deleteAllProductVariants()
        deleteAllProductPresentations()
        deleteAllProductRecipes()

        // 2️⃣ Tablas intermedias
        deleteAllProductionBatches()
        deleteAllSales()
        deleteAllPurchases()

        // 3️⃣ Tablas principales
        deleteAllProducts()
        deleteAllSupplies()
        deleteAllExpenses()
    }
}
