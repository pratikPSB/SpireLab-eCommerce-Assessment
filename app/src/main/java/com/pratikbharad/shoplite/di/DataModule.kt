package com.pratikbharad.shoplite.di

import com.pratikbharad.shoplite.data.network.ConnectivityNetworkMonitor
import com.pratikbharad.shoplite.data.network.NetworkMonitor
import com.pratikbharad.shoplite.data.repository.DefaultProductRepository
import com.pratikbharad.shoplite.data.repository.OfflineCartRepository
import com.pratikbharad.shoplite.domain.repository.CartRepository
import com.pratikbharad.shoplite.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindProductRepository(impl: DefaultProductRepository): ProductRepository

    @Binds
    @Singleton
    fun bindCartRepository(impl: OfflineCartRepository): CartRepository

    @Binds
    @Singleton
    fun bindNetworkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor
}
