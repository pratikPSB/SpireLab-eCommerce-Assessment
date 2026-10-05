package com.pratikbharad.shoplite.testing

import com.pratikbharad.shoplite.domain.model.Cart
import com.pratikbharad.shoplite.domain.model.CartItem
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCartRepository(initialItems: List<CartItem> = emptyList()) : CartRepository {

    private val items = MutableStateFlow(initialItems)

    val currentItems: List<CartItem> get() = items.value

    override fun observeCart(): Flow<Cart> = items.map { Cart(it.sortedBy(CartItem::addedAt)) }

    override fun observeQuantity(productId: Int): Flow<Int> =
        items.map { list -> list.find { it.productId == productId }?.quantity ?: 0 }

    override suspend fun add(product: Product) {
        if (!product.isInStock) return
        items.update { list ->
            val existing = list.find { it.productId == product.id }
            if (existing == null) {
                list + CartItem(product.id, product.title, product.thumbnailUrl, product.priceCents, 1, product.stock, list.size.toLong())
            } else {
                list.replace(existing.copy(quantity = (existing.quantity + 1).coerceAtMost(product.stock), stock = product.stock))
            }
        }
    }

    override suspend fun increase(productId: Int) = items.update { list ->
        val existing = list.find { it.productId == productId } ?: return@update list
        if (existing.canIncrease) list.replace(existing.copy(quantity = existing.quantity + 1)) else list
    }

    override suspend fun decrease(productId: Int) = items.update { list ->
        val existing = list.find { it.productId == productId } ?: return@update list
        if (existing.quantity > 1) list.replace(existing.copy(quantity = existing.quantity - 1)) else list - existing
    }

    override suspend fun remove(productId: Int) = items.update { list -> list.filterNot { it.productId == productId } }

    override suspend fun restore(item: CartItem) = items.update { list -> list.filterNot { it.productId == item.productId } + item }

    private fun List<CartItem>.replace(item: CartItem) = map { if (it.productId == item.productId) item else it }
}
