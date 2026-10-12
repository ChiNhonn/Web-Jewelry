package com.aura.jewelry.core.database.di

import android.content.Context
import androidx.room.Room
import com.aura.jewelry.core.database.AuraDatabase
import com.aura.jewelry.core.database.dao.CategoryDao
import com.aura.jewelry.core.database.dao.ProductDao
import com.aura.jewelry.core.database.dao.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAuraDatabase(
        @ApplicationContext context: Context
    ): AuraDatabase {
        return Room.databaseBuilder(
            context,
            AuraDatabase::class.java,
            "aura_jewelry.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AuraDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AuraDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideWishlistDao(database: AuraDatabase): WishlistDao {
        return database.wishlistDao()
    }
}
