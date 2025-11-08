package com.rige.dulcegest.data.repository

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
import com.rige.dulcegest.data.local.dao.ShoppingListDao
import com.rige.dulcegest.data.local.dao.SupplyDao
import jakarta.inject.Inject

class SettingsRepository @Inject constructor(

    private val productionConsumptionDao: ProductionConsumptionDao,
    private val saleItemDao: SaleItemDao,
    private val productVariantDao: ProductVariantDao,
    private val productPresentationDao: ProductPresentationDao,
    private val productRecipeDao: ProductRecipeDao,
    private val shoppingListDao: ShoppingListDao,
    private val productionBatchDao: ProductionBatchDao,
    private val saleDao: SaleDao,
    private val purchaseDao: PurchaseDao,
    private val productDao: ProductDao,
    private val supplyDao: SupplyDao,
    private val expenseDao: ExpenseDao
) {

    suspend fun deleteAllData() {

        productionConsumptionDao.deleteAllProductionConsumptions()
        saleItemDao.deleteAllSaleItems()
        productVariantDao.deleteAllProductVariants()
        productPresentationDao.deleteAllProductPresentations()
        productRecipeDao.deleteAllProductRecipes()
        shoppingListDao.deleteAllShoppingListItems()

        productionBatchDao.deleteAllProductions()
        saleDao.deleteAllSales()
        purchaseDao.deleteAllPurchases()

        productDao.deleteAllProducts()
        supplyDao.deleteAllSupplies()
        expenseDao.deleteAllExpenses()
    }
}