package com.rige.dulcegest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rige.dulcegest.data.db.dao.ExpenseDao
import com.rige.dulcegest.data.db.dao.IngredientDao
import com.rige.dulcegest.data.db.dao.ProductDao
import com.rige.dulcegest.data.db.dao.ProductPresentationDao
import com.rige.dulcegest.data.db.dao.ProductRecipeDao
import com.rige.dulcegest.data.db.dao.ProductVariantDao
import com.rige.dulcegest.data.db.dao.ProductionBatchDao
import com.rige.dulcegest.data.db.dao.ProductionConsumptionDao
import com.rige.dulcegest.data.db.dao.PurchaseDao
import com.rige.dulcegest.data.db.dao.SaleDao
import com.rige.dulcegest.data.db.dao.SaleItemDao
import com.rige.dulcegest.data.db.entities.Expense
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductPresentation
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.entities.ProductVariant
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
        ProductPresentation::class,
        ProductVariant::class,
        Sale::class,
        SaleItem::class,
        Expense::class
    ],
    version = 10,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun productDao(): ProductDao
    abstract fun productRecipeDao(): ProductRecipeDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun productionBatchDao(): ProductionBatchDao
    abstract fun productionConsumptionDao(): ProductionConsumptionDao
    abstract fun productPresentationDao(): ProductPresentationDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun productVariantDao(): ProductVariantDao

    companion object {
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ingredients ADD COLUMN purchase_unit TEXT")
                db.execSQL("ALTER TABLE ingredients ADD COLUMN conversion_factor REAL")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN updated_at TEXT DEFAULT ''")
            }
        }
    }
}