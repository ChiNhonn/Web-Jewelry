package com.aura.jewelry.data.repository

import com.aura.jewelry.core.database.dao.WishlistDao
import com.aura.jewelry.core.database.model.WishlistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

interface WishlistRepository {
    fun getWishlist(): Flow<List<WishlistEntity>>
    fun isWishlisted(productId: Long): Flow<Boolean>
    suspend fun toggleWishlist(
        productId: Long,
        name: String,
        slug: String,
        price: Double,
        thumbnailUrl: String?
    ): Boolean
    suspend fun removeFromWishlist(productId: Long)
    suspend fun clearWishlist()
}

@Singleton
class WishlistRepositoryImpl @Inject constructor(
    private val wishlistDao: WishlistDao
) : WishlistRepository {

    override fun getWishlist(): Flow<List<WishlistEntity>> = wishlistDao.getWishlist()

    override fun isWishlisted(productId: Long): Flow<Boolean> = wishlistDao.isWishlisted(productId)

    override suspend fun toggleWishlist(
        productId: Long,
        name: String,
        slug: String,
        price: Double,
        thumbnailUrl: String?
    ): Boolean {
        val exists = wishlistDao.isWishlisted(productId).first()
        return if (exists) {
            wishlistDao.removeFromWishlist(productId)
            false
        } else {
            wishlistDao.addToWishlist(
                WishlistEntity(
                    productId = productId,
                    name = name,
                    slug = slug,
                    price = price,
                    thumbnailUrl = thumbnailUrl
                )
            )
            true
        }
    }

    override suspend fun removeFromWishlist(productId: Long) {
        wishlistDao.removeFromWishlist(productId)
    }

    override suspend fun clearWishlist() {
        wishlistDao.clearWishlist()
    }
}
