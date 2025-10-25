package com.rige.dulcegest.core.di

import com.rige.dulcegest.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides fun provideSupplyDao(db: AppDatabase) = db.supplyDao()
    @Provides fun provideProductDao(db: AppDatabase) = db.productDao()
    @Provides fun provideProductRecipeDao(db: AppDatabase) = db.productRecipeDao()
    @Provides fun providePurchaseDao(db: AppDatabase) = db.purchaseDao()
    @Provides fun provideProductionBatchDao(db: AppDatabase) = db.productionBatchDao()
    @Provides fun provideProductionConsumptionDao(db: AppDatabase) = db.productionConsumptionDao()
    @Provides fun provideProductPresentationDao(db: AppDatabase) = db.productPresentationDao()
    @Provides fun provideProductVariantDao(db: AppDatabase) = db.productVariantDao()
    @Provides fun provideSaleDao(db: AppDatabase) = db.saleDao()
    @Provides fun provideSaleItemDao(db: AppDatabase) = db.saleItemDao()
    @Provides fun provideExpenseDao(db: AppDatabase) = db.expenseDao()
}