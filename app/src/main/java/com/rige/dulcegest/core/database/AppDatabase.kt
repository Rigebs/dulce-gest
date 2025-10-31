package com.rige.dulcegest.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rige.dulcegest.data.local.dao.ExpenseDao
import com.rige.dulcegest.data.local.dao.ProductDao
import com.rige.dulcegest.data.local.dao.ProductPresentationDao
import com.rige.dulcegest.data.local.dao.ProductRecipeDao
import com.rige.dulcegest.data.local.dao.ProductVariantDao
import com.rige.dulcegest.data.local.dao.ProductionBatchDao
import com.rige.dulcegest.data.local.dao.ProductionConsumptionDao
import com.rige.dulcegest.data.local.dao.PurchaseDao
import com.rige.dulcegest.data.local.dao.SaleDao
import com.rige.dulcegest.data.local.dao.SaleItemDao
import com.rige.dulcegest.data.local.dao.SettingsDao
import com.rige.dulcegest.data.local.dao.ShoppingListDao
import com.rige.dulcegest.data.local.dao.SupplyDao
import com.rige.dulcegest.data.local.entities.Expense
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductPresentation
import com.rige.dulcegest.data.local.entities.ProductRecipe
import com.rige.dulcegest.data.local.entities.ProductVariant
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption
import com.rige.dulcegest.data.local.entities.Purchase
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.local.entities.ShoppingListItem
import com.rige.dulcegest.data.local.entities.Supply

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
        Expense::class,
        ShoppingListItem::class
    ],
    version = 13,
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
    abstract fun settingsDao(): SettingsDao
    abstract fun shoppingListDao(): ShoppingListDao

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

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS shopping_list_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                supply_id INTEGER NOT NULL,
                quantity REAL,
                unit TEXT,
                priority INTEGER NOT NULL DEFAULT 0,
                purchased INTEGER NOT NULL DEFAULT 0,
                notes TEXT,
                created_at TEXT,
                FOREIGN KEY(supply_id) REFERENCES supplies(id) ON DELETE NO ACTION
            )
            """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_shopping_list_items_supply_id ON shopping_list_items(supply_id)")
            }
        }
    }
}