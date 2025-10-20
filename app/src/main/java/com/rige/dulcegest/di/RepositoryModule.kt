package com.rige.dulcegest.di

import com.rige.dulcegest.data.db.dao.*
import com.rige.dulcegest.data.repository.*
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
    fun provideIngredientRepository(
        dao: IngredientDao
    ): IngredientRepository = IngredientRepository(dao)

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao,
        recipeDao: ProductRecipeDao,
        productPresentationDao: ProductPresentationDao
    ): ProductRepository = ProductRepository(productDao, recipeDao, productPresentationDao)

    @Provides
    @Singleton
    fun provideProductionRepository(
        batchDao: ProductionBatchDao,
        consumptionDao: ProductionConsumptionDao
    ): ProductionRepository = ProductionRepository(batchDao, consumptionDao)

    @Provides
    @Singleton
    fun providePurchaseRepository(
        dao: PurchaseDao
    ): PurchaseRepository = PurchaseRepository(dao)

    @Provides
    @Singleton
    fun provideSaleRepository(
        saleDao: SaleDao,
        itemDao: SaleItemDao
    ): SaleRepository = SaleRepository(saleDao, itemDao)
}
