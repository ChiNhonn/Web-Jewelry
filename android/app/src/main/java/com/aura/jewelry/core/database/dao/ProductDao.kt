package com.aura.jewelry.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aura.jewelry.core.database.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY isFeatured DESC, cachedAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE categorySlug = :categorySlug ORDER BY isFeatured DESC")
    fun getProductsByCategory(categorySlug: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE slug = :slug LIMIT 1")
    suspend fun getProductBySlug(slug: String): ProductEntity?

    @Query("SELECT * FROM products WHERE isFeatured = 1 ORDER BY cachedAt DESC")
    fun getFeaturedProducts(): Flow<List<ProductEntity>>

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()
}
