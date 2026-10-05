package com.pratikbharad.shoplite.data.repository

import com.pratikbharad.shoplite.data.local.CartDao
import com.pratikbharad.shoplite.data.local.CartItemEntity
import com.pratikbharad.shoplite.data.local.toDomain
import com.pratikbharad.shoplite.data.local.toEntity
import com.pratikbharad.shoplite.domain.model.Cart
import com.pratikbharad.shoplite.domain.model.CartItem
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineCartRepository @Inject constructor(
    private val cartDao: CartDao,
) : CartRepository {

    override fun observeCart(): Flow<Cart> =
        cartDao.observeItems().map { items -> Cart(items.map { it.toDomain() }) }

    override fun observeQuantity(productId: Int): Flow<Int> =
        cartDao.observeQuantity(productId).map { it ?: 0 }.distinctUntilChanged()

    override suspend fun add(product: Product) {
        cartDao.addOne(
            CartItemEntity(
                productId = product.id,
                title = product.title,
                thumbnailUrl = product.thumbnailUrl,
                unitPriceCents = product.priceCents,
                quantity = 1,
                stock = product.stock,
                addedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun increase(productId: Int) = cartDao.increment(productId)

    override suspend fun decrease(productId: Int) = cartDao.decrementOrDelete(productId)

    override suspend fun remove(productId: Int) = cartDao.delete(productId)

    override suspend fun restore(item: CartItem) = cartDao.upsert(item.toEntity())
}
