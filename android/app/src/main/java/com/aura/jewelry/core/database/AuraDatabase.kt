package com.aura.jewelry.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aura.jewelry.core.database.dao.CategoryDao
import com.aura.jewelry.core.database.dao.ProductDao
import com.aura.jewelry.core.database.dao.WishlistDao
import com.aura.jewelry.core.database.model.CategoryEntity
import com.aura.jewelry.core.database.model.ProductEntity
import com.aura.jewelry.core.database.model.WishlistEntity

/**
 * Main Room Database for AURA Jewelry application.
 * Provides SQLite persistent caching and offline support.
 */
@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        WishlistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun wishlistDao(): WishlistDao
}
