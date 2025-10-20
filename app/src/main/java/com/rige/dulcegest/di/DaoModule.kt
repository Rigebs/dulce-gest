package com.rige.dulcegest.di

import com.rige.dulcegest.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideIngredientDao(db: AppDatabase) = db.ingredientDao()
    @Provides fun provideProductDao(db: AppDatabase) = db.productDao()
    @Provides fun provideProductRecipeDao(db: AppDatabase) = db.productRecipeDao()
    @Provides fun providePurchaseDao(db: AppDatabase) = db.purchaseDao()
    @Provides fun provideProductionBatchDao(db: AppDatabase) = db.productionBatchDao()
    @Provides fun provideProductionConsumptionDao(db: AppDatabase) = db.productionConsumptionDao()
    @Provides fun provideProductPresentation(db: AppDatabase) = db.productPresentation()
    @Provides fun provideSaleDao(db: AppDatabase) = db.saleDao()
    @Provides fun provideSaleItemDao(db: AppDatabase) = db.saleItemDao()
    @Provides fun provideExpenseDao(db: AppDatabase) = db.expenseDao()
}