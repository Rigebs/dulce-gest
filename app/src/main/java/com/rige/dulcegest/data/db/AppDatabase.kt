package com.rige.dulcegest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rige.dulcegest.data.db.dao.ExpenseDao
import com.rige.dulcegest.data.db.dao.SupplyDao
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
import com.rige.dulcegest.data.db.entities.Supply
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
        Supply::class,
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
    version = 12,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun supplyDao(): SupplyDao
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

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ingredients ADD COLUMN avg_cost REAL NOT NULL DEFAULT 0.0")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ingredients RENAME TO supplies")

                db.execSQL("""
            CREATE TABLE purchases_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                supply_id INTEGER NOT NULL,
                quantity REAL NOT NULL,
                total_price REAL NOT NULL,
                supplier TEXT,
                date TEXT,
                notes TEXT,
                FOREIGN KEY(supply_id) REFERENCES supplies(id) ON DELETE NO ACTION
            )
        """.trimIndent())

                db.execSQL("""
            INSERT INTO purchases_new (id, supply_id, quantity, total_price, supplier, date, notes)
            SELECT id, ingredient_id, quantity, total_price, supplier, date, notes FROM purchases
        """.trimIndent())

                db.execSQL("DROP TABLE purchases")
                db.execSQL("ALTER TABLE purchases_new RENAME TO purchases")
                db.execSQL("CREATE INDEX index_purchases_supply_id ON purchases(supply_id)")

                db.execSQL("""
            CREATE TABLE production_consumptions_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                batch_id INTEGER NOT NULL,
                supply_id INTEGER NOT NULL,
                qty_used REAL NOT NULL,
                cost REAL NOT NULL,
                FOREIGN KEY(batch_id) REFERENCES production_batches(id) ON DELETE CASCADE,
                FOREIGN KEY(supply_id) REFERENCES supplies(id) ON DELETE NO ACTION
            )
        """.trimIndent())

                db.execSQL("""
            INSERT INTO production_consumptions_new (id, batch_id, supply_id, qty_used, cost)
            SELECT id, batch_id, ingredient_id, qty_used, cost FROM production_consumptions
        """.trimIndent())

                db.execSQL("DROP TABLE production_consumptions")
                db.execSQL("ALTER TABLE production_consumptions_new RENAME TO production_consumptions")
                db.execSQL("CREATE INDEX index_production_consumptions_batch_id ON production_consumptions(batch_id)")
                db.execSQL("CREATE INDEX index_production_consumptions_supply_id ON production_consumptions(supply_id)")
                db.execSQL("CREATE UNIQUE INDEX index_production_consumptions_batch_id_supply_id ON production_consumptions(batch_id, supply_id)")

                db.execSQL("""
            CREATE TABLE product_recipes_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                product_id INTEGER NOT NULL,
                supply_id INTEGER NOT NULL,
                qty_per_unit REAL,
                FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE,
                FOREIGN KEY(supply_id) REFERENCES supplies(id) ON DELETE CASCADE
            )
        """.trimIndent())

                db.execSQL("""
            INSERT INTO product_recipes_new (id, product_id, supply_id, qty_per_unit)
            SELECT id, product_id, ingredient_id, qty_per_unit FROM product_recipes
        """.trimIndent())

                db.execSQL("DROP TABLE product_recipes")
                db.execSQL("ALTER TABLE product_recipes_new RENAME TO product_recipes")
                db.execSQL("CREATE INDEX index_product_recipes_product_id ON product_recipes(product_id)")
                db.execSQL("CREATE INDEX index_product_recipes_supply_id ON product_recipes(supply_id)")
                db.execSQL("CREATE UNIQUE INDEX index_product_recipes_product_id_supply_id ON product_recipes(product_id, supply_id)")
            }
        }
    }
}