package com.rige.dulcegest.core.di

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
import com.rige.dulcegest.data.repository.ExpenseRepository
import com.rige.dulcegest.data.repository.ProductRepository
import com.rige.dulcegest.data.repository.ProductionRepository
import com.rige.dulcegest.data.repository.PurchaseRepository
import com.rige.dulcegest.data.repository.SaleRepository
import com.rige.dulcegest.data.repository.SettingsRepository
import com.rige.dulcegest.data.repository.ShoppingListRepository
import com.rige.dulcegest.data.repository.SupplyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExpenseRepository(
        dao: ExpenseDao
    ): ExpenseRepository = ExpenseRepository(dao)

    @Provides
    @Singleton
    fun provideSupplyRepository(
        dao: SupplyDao
    ): SupplyRepository = SupplyRepository(dao)

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao,
        recipeDao: ProductRecipeDao,
        presentationDao: ProductPresentationDao,
        variantDao: ProductVariantDao
    ): ProductRepository = ProductRepository(productDao, recipeDao, presentationDao, variantDao)

    @Provides
    @Singleton
    fun provideProductionRepository(
        batchDao: ProductionBatchDao,
        consumptionDao: ProductionConsumptionDao,
        productDao: ProductDao,
        supplyDao: SupplyDao,
    ): ProductionRepository = ProductionRepository(
        batchDao,
        consumptionDao,
        productDao,
        supplyDao
    )

    @Provides
    @Singleton
    fun providePurchaseRepository(
        dao: PurchaseDao,
        supplyDao: SupplyDao
    ): PurchaseRepository = PurchaseRepository(dao, supplyDao)

    @Provides
    @Singleton
    fun provideSaleRepository(
        saleDao: SaleDao,
        itemDao: SaleItemDao,
        productDao: ProductDao
    ): SaleRepository = SaleRepository(saleDao, itemDao, productDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        productionConsumptionDao: ProductionConsumptionDao,
        saleItemDao: SaleItemDao,
        productVariantDao: ProductVariantDao,
        productPresentationDao: ProductPresentationDao,
        productRecipeDao: ProductRecipeDao,
        shoppingListDao: ShoppingListDao,
        productionBatchDao: ProductionBatchDao,
        saleDao: SaleDao,
        purchaseDao: PurchaseDao,
        productDao: ProductDao,
        supplyDao: SupplyDao,
        expenseDao: ExpenseDao
    ): SettingsRepository {
        return SettingsRepository(
            productionConsumptionDao,
            saleItemDao,
            productVariantDao,
            productPresentationDao,
            productRecipeDao,
            shoppingListDao,
            productionBatchDao,
            saleDao,
            purchaseDao,
            productDao,
            supplyDao,
            expenseDao
        )
    }

    @Provides
    @Singleton
    fun provideShoppingListRepository(
        dao: ShoppingListDao
    ): ShoppingListRepository = ShoppingListRepository(dao)
}
