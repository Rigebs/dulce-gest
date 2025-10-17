package com.rige.dulcegest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rige.dulcegest.data.db.dao.ExpenseDao
import com.rige.dulcegest.data.db.dao.IngredientDao
import com.rige.dulcegest.data.db.dao.ProductDao
import com.rige.dulcegest.data.db.dao.ProductRecipeDao
import com.rige.dulcegest.data.db.dao.ProductionBatchDao
import com.rige.dulcegest.data.db.dao.ProductionConsumptionDao
import com.rige.dulcegest.data.db.dao.PurchaseDao
import com.rige.dulcegest.data.db.dao.SaleDao
import com.rige.dulcegest.data.db.dao.SaleItemDao
import com.rige.dulcegest.data.db.entities.Expense
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.entities.ProductionBatch
import com.rige.dulcegest.data.db.entities.ProductionConsumption
import com.rige.dulcegest.data.db.entities.Purchase
import com.rige.dulcegest.data.db.entities.Sale
import com.rige.dulcegest.data.db.entities.SaleItem

@Database(
    entities = [
        Ingredient::class,
        Product::class,
        ProductRecipe::class,
        Purchase::class,
        ProductionBatch::class,
        ProductionConsumption::class,
        Sale::class,
        SaleItem::class,
        Expense::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun productDao(): ProductDao
    abstract fun productRecipeDao(): ProductRecipeDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun productionBatchDao(): ProductionBatchDao
    abstract fun productionConsumptionDao(): ProductionConsumptionDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun expenseDao(): ExpenseDao
}