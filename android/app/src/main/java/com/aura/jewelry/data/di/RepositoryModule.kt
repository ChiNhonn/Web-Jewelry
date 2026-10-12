package com.aura.jewelry.data.di

import com.aura.jewelry.data.repository.AuthRepository
import com.aura.jewelry.data.repository.AuthRepositoryImpl
import com.aura.jewelry.data.repository.CartRepository
import com.aura.jewelry.data.repository.CartRepositoryImpl
import com.aura.jewelry.data.repository.OrderRepository
import com.aura.jewelry.data.repository.OrderRepositoryImpl
import com.aura.jewelry.data.repository.ProductRepository
import com.aura.jewelry.data.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(
        wishlistRepositoryImpl: com.aura.jewelry.data.repository.WishlistRepositoryImpl
    ): com.aura.jewelry.data.repository.WishlistRepository
}
