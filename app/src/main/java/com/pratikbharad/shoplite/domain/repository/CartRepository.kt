package com.pratikbharad.shoplite.domain.repository

import com.pratikbharad.shoplite.domain.model.Cart
import com.pratikbharad.shoplite.domain.model.CartItem
import com.pratikbharad.shoplite.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun observeCart(): Flow<Cart>

    fun observeQuantity(productId: Int): Flow<Int>

    suspend fun add(product: Product)

    suspend fun increase(productId: Int)

    suspend fun decrease(productId: Int)

    suspend fun remove(productId: Int)

    suspend fun restore(item: CartItem)
}
