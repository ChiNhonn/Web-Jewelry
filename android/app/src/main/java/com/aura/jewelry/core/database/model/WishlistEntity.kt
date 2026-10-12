package com.aura.jewelry.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity storing user's bookmarked luxury jewelry items offline.
 */
@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val productId: Long,
    val name: String,
    val slug: String,
    val price: Double,
    val thumbnailUrl: String?,
    val addedAt: Long = System.currentTimeMillis()
)
