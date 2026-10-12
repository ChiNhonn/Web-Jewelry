package com.aura.jewelry.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aura.jewelry.core.database.model.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    fun getWishlist(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
    fun isWishlisted(productId: Long): Flow<Boolean>

    @Upsert
    suspend fun addToWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: Long)

    @Query("DELETE FROM wishlist")
    suspend fun clearWishlist()
}
